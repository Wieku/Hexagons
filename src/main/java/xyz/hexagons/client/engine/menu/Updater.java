package xyz.hexagons.client.engine.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import xyz.hexagons.client.Main;
import xyz.hexagons.client.map.MapLoader;
import xyz.hexagons.client.utils.GUIHelper;
import xyz.hexagons.client.utils.Json;
import xyz.hexagons.client.utils.Utils;

import java.io.File;

public class Updater implements Screen {

	public static Updater instance = new Updater();
	private Stage stage;
	private Table loadTable;
	private Label status;
	private ProgressBar bar;
	private boolean ended = false;
	private String text = "Checking for updates...";

	private Updater(){

		stage = new Stage(new ScreenViewport());

		loadTable = new Table();
		loadTable.setBackground(GUIHelper.getTxRegion(new Color(0x0f0f0fff)));
		loadTable.top();
		Texture texture = new Texture(Gdx.files.internal("assets/hexlogobig.png"), true);
		texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		Image image = new Image(texture);
		image.setScaling(Scaling.fit);
		loadTable.add(image).top().size(512).padTop(64).center().row();

		loadTable.add(status = new Label("Checking for updates...", GUIHelper.getLabelStyle(Color.WHITE, 10))).left().padLeft(5).padBottom(5).bottom().expand().row();
		loadTable.add(bar = new ProgressBar(0, 100, 1, false, GUIHelper.getProgressBarStyleUB(Color.DARK_GRAY, Color.WHITE, new Color(0x02EAFAFF), 20))).fillX().center().bottom().height(20).colspan(2).row();

		stage.addActor(loadTable);
	}

	float delta1 = 1.0f;

	@Override
	public void render(float delta) {

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		if((delta1+=delta) >= 1f/60){
			status.setText(text);
			delta1 = 0;
			if(ended){
				Main.getInstance().maps = MapLoader.load();
				Main.getInstance().setScreen(MainMenu.instance);
				ended = false;
			}
		}

		stage.act(delta);
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		loadTable.setBounds(0, 0, width, height);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {}


	@Override
	public void show() {

		new Thread(()-> {

			if(!Main.noupdate){

				try {

					String infoPath = Utils.getGameDir() + ".hexUpdateInfo";

					Utils.downloadFileWithProgressBar("https://upd.hexagons.wieku.me/update.json", infoPath, bar);

					String sha1 = Files.hash(new File(Utils.getGameFile()), Hashing.sha1()).toString();
					String build = Utils.getBuildNumber();

					Utils.sleep(500);

					Json json = Json.load(new File(infoPath));

					String newBuild = json.getString("latest");
					String newSha1 = json.getString(newBuild + ".sha1");

					if(build == null) {
						setStatus("Error while reading buildNumber! Downloading newest version!");
						downloadUpdate(json, newBuild);
					} else {
						if(newBuild.equals(build)) {
							if(sha1.equals(newSha1)) {
								setStatus("Game is up to date, starting game...");
							} else {
								setStatus("Game is up to date but file is changed/corrupted, downloading new file...");
								downloadUpdate(json, newBuild);
							}
						} else {
							setStatus("You have older version, downloading newest one...");
							downloadUpdate(json, newBuild);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					setStatus("Error downloading update, starting game...");
				}

			} else {
				setStatus("Downloading update skipped, starting game...");
			}

			bar.setValue(bar.getMaxValue());
			Utils.sleep(2000);
			ended = true;

		}).start();

	}

	private void downloadUpdate(Json json, String version) throws Exception{
		Utils.sleep(1000);

		String newPath = Utils.getGameFile()+".download";

		Utils.downloadFileWithProgressBar("https://upd.hexagons.wieku.me/"+json.getString(version+".file"), newPath, bar);

		String sha1 = Files.hash(new File(newPath), Hashing.sha1()).toString();

		if(!sha1.equals(json.getString(version + ".sha1"))){
			setStatus("Downloaded file is corrupted, downloading again...");
			downloadUpdate(json, version);
		}

		setStatus("Update finished! Moving file!");

		Utils.sleep(500);

		Files.move(new File(newPath), new File(Utils.getGameFile()));
		setStatus("File moved! Restarting game!");
		Utils.restartGame();
	}

	public void setStatus(String text){
		this.text = text.replaceAll("\\[", "\\[\\[").replaceAll("\\]", "\\]\\]");
	}

}
