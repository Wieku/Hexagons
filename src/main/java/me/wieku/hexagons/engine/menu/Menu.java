package me.wieku.hexagons.engine.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import me.wieku.hexagons.Main;
import me.wieku.hexagons.api.CurrentMap;
import me.wieku.hexagons.engine.render.Background;
import me.wieku.hexagons.engine.Game;
import me.wieku.hexagons.engine.Settings;
import me.wieku.hexagons.engine.camera.SkewCamera;
import me.wieku.hexagons.engine.menu.options.Options;
import me.wieku.hexagons.engine.render.BlurEffect;
import me.wieku.hexagons.map.Map;
import me.wieku.hexagons.resources.ArchiveFileHandle;
import me.wieku.hexagons.resources.AudioPlayer;
import me.wieku.hexagons.utils.GUIHelper;

import java.util.ArrayList;

/**
 * @author Sebastian Krajewski on 04.04.15.
 */
public class Menu implements Screen {

	public static Options options;
	static Sound beep;

	ArrayList<Map> maps;
	Stage stage;
	Map currentMap;
	Table logo, info, credits;
	Label number, name, description, author, music, creditLabel;
	Game game;
	String[] creditArray = {"Programmed by:", "Sebastian Krajewski", "Lukasz Magiera", "Original ideas by:", "Vittorio Romeo", "Terry Cavanagh"/*, "Music by:", "BOSSFIGHT", "Chipzel"*/};
	int index = -1;
	float time = 1.5f;
	float toChange = 0f;

	Table conf;

	SkewCamera camera = new SkewCamera();
	ShapeRenderer shapeRenderer;
	Background background = new Background();

	private static int mapIndex = 0;

	static Menu instance;
	public AudioPlayer audioPlayer;

	BlurEffect effect;

	public Menu(ArrayList<Map> maps){
		this.maps = maps;

		effect = new BlurEffect(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		effect.setPower(5f);
		effect.setDarkness(1.5f);
		instance = this;
		beep = Gdx.audio.newSound(Gdx.files.internal("assets/sound/beep.ogg"));
		shapeRenderer = new ShapeRenderer();

		options = new Options();
		stage = new Stage(new ScreenViewport());
		stage.addListener(new InputListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

				if(!maps.isEmpty()) {

					if (button == Buttons.LEFT || button == Buttons.RIGHT) {

						if (button == Buttons.LEFT && --mapIndex < 0) mapIndex = maps.size() - 1;
						if (button == Buttons.RIGHT && ++mapIndex > maps.size() - 1) mapIndex = 0;

						selectIndex(mapIndex);

						playBeep();
					}
				}

				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public boolean keyDown(InputEvent event, int keycode) {

				if(!maps.isEmpty()){

					if(keycode == Keys.LEFT || keycode == Keys.RIGHT){

						if(keycode == Keys.LEFT && --mapIndex < 0) mapIndex = maps.size() - 1;
						if (keycode == Keys.RIGHT && ++mapIndex > maps.size() - 1) mapIndex = 0;

						selectIndex(mapIndex);

						//CurrentMap.reset();
						//maps.get(mapIndex).script.initColors();

						playBeep();
					}

					if(keycode == Keys.ENTER){
						playBeep();
						Gdx.input.setInputProcessor(null);
						audioPlayer.pause();
						Main.getInstance().setScreen(game = new Game(maps.get(mapIndex)));
					}

				}

				if(keycode == Keys.F3){
					playBeep();
					Main.getInstance().setScreen(options);
				}

				if(keycode == Keys.ESCAPE){
					playBeep();
					Gdx.app.exit();
				}

				return false;
			}
		});


		info = new Table();
		info.add(number = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 12))).left().row();
		info.add(name = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 18))).left().row();
		info.add(description = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 16))).left().row();
		info.add(author = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 14))).left().row();
		info.add(music = new Label("No maps available!", GUIHelper.getLabelStyle(Color.WHITE, 14))).left().row();

		info.setPosition(5, 5);
		stage.addActor(info);

		conf = GUIHelper.getTable(Color.BLACK);
		conf.add(new Label("Press F3 to open settings", GUIHelper.getLabelStyle(Color.BLACK, Color.WHITE, 8))).pad(5);
		conf.pack();
		stage.addActor(conf);

		logo = GUIHelper.getTable(Color.BLACK);
		logo.add(new Label("[#A0A0A0]He[#02EAFA]x[]agons![]", GUIHelper.getLabelStyle(Color.WHITE, 40))).pad(5).padBottom(0).row();
		logo.add(new Label(Main.version, GUIHelper.getLabelStyle(Color.WHITE, 12))).pad(5).padTop(0).right();
		logo.pack();
		stage.addActor(logo);

		credits = GUIHelper.getTable(Color.BLACK);
		credits.add(creditLabel = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 14))).pad(5).padBottom(10);
		credits.pack();

		stage.addActor(credits);

		selectIndex(mapIndex);
		if(Settings.instance.fullscreen == true)
			Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode());
	}

	float delta0 = 0;

	Color tmpC = new Color();
	@Override
	public void render(float delta) {

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		updateSkew(delta);
		camera.rotate(CurrentMap.rotationSpeed * 360f * delta);
		camera.update(delta);

		if((delta0 += delta)>=1f/60){
			background.update(delta0);
			CurrentMap.walls.update(delta0);

			CurrentMap.setMinSkew(0.9999f);
			CurrentMap.setMaxSkew(1);
			CurrentMap.setSkewTime(1);
			tmpC.set(CurrentMap.walls.r, CurrentMap.walls.g, CurrentMap.walls.b, CurrentMap.walls.a);

			number.getStyle().fontColor = tmpC;
			name.getStyle().fontColor = tmpC;
			description.getStyle().fontColor = tmpC;
			author.getStyle().fontColor = tmpC;
			music.getStyle().fontColor = tmpC;

			delta0 = 0;
		}

		effect.bind();


		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.identity();
		shapeRenderer.rotate(1, 0, 0, 90);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		background.render(shapeRenderer, delta, true);
		shapeRenderer.end();

		effect.unbind();

		effect.render(stage.getBatch());

		if((toChange -= delta) <= 0){

			++index;
			System.out.println(Gdx.graphics.getFramesPerSecond());
			if(index == creditArray.length) index = 0;

			creditLabel.setText(creditArray[index]);
			credits.pack();

			credits.setWidth(Math.max(credits.getWidth(), logo.getWidth()));

			credits.setPosition(Gdx.graphics.getWidth() - 5 - credits.getWidth(), Gdx.graphics.getHeight() - 10 - logo.getHeight() - credits.getHeight());
			toChange = time;
		}

		stage.act(delta);
		stage.draw();
	}

	int inc;
	float delta2;
	public void updateSkew(float delta) {
		inc = (delta2 == 0 ? 1 : (delta2 == CurrentMap.skewTime ? -1 : inc));
		delta2 += delta * inc;
		delta2 = Math.min(CurrentMap.skewTime, Math.max(delta2, 0));
		float percent = delta2 / CurrentMap.skewTime;
		CurrentMap.skew = CurrentMap.minSkew + (CurrentMap.maxSkew - CurrentMap.minSkew) * percent;
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		conf.setPosition(2, height - 2 - conf.getHeight());
		logo.setPosition(width - 5 - logo.getWidth(), height - 5 - logo.getHeight());
		credits.setPosition(width - 5 - credits.getWidth(), height - 10 - logo.getHeight() - credits.getHeight());
		effect.resize(width, height);
	}

	@Override
	public void show() {

		Main.config.foregroundFPS = 120;

		//CurrentMap.reset();

		selectIndex(mapIndex);

		if(game != null){
			audioPlayer.play();
			audioPlayer.setPosition(game.exitPosition);
			game = null;
		}

		//if(!maps.isEmpty())
			//maps.get(mapIndex).script.initColors();

		Gdx.input.setInputProcessor(stage);
	}


	public void selectIndex(int index){
		if(maps.isEmpty()){
			number.setText("");
			name.setText("");
			description.setText("");
			author.setText("");
			music.setText("No maps available!");
			info.pack();
			info.setPosition(5, 5);
		} else {
			Map map = maps.get(index);
			CurrentMap.reset();
			maps.get(mapIndex).script.initColors();
			maps.get(mapIndex).script.onInit();
			camera.reset();

			if (audioPlayer == null || !currentMap.equals(map)) {
				if(audioPlayer != null){
					audioPlayer.stop();
					audioPlayer.dispose();
				}

				audioPlayer = new AudioPlayer(new ArchiveFileHandle(map.file,map.info.audioFileName));
				audioPlayer.setVolume(((float) Settings.instance.masterVolume * (float) Settings.instance.menuMusicVolume / 10000f) / 2f);
				audioPlayer.play(map.info.previewTime);
			}

			number.setText("[" + (index + 1) + "/" + maps.size() + "] Pack: " + map.info.pack);
			name.setText(map.info.name);
			description.setText(map.info.description);
			author.setText("Author: " + map.info.author);
			music.setText("Music: " + map.info.songName + " by " + map.info.songAuthor);
			info.pack();
			info.setPosition(5, 5);
			currentMap = map;
		}

	}

	public static void playBeep(){
		long id = beep.play();
		beep.setVolume(id, (float) Settings.instance.masterVolume * (float) Settings.instance.effectVolume / 10000f);
	}

	public static Menu getInstance() {
		return instance;
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {}

}
