package xyz.hexagons.client.menu.screens;

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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.map.MapLoader;
import xyz.hexagons.client.utils.GUIHelper;
import xyz.hexagons.client.utils.Glider;
import xyz.hexagons.client.utils.PathUtil;
import xyz.hexagons.client.utils.Utils;

public class Splash implements Screen {

	public static Splash instance = new Splash();
	private Stage stage;
	private Table loadTable;
	private ProgressBar bar;
	private boolean ended = false;
	private String text = "Checking for updates...";

	String[] colors = {"b6f6faff", "82f2faff", "56effaff", "02eafaff", "56effaff", "82f2faff", "b6f6faff"};
	
	Label loadLabel;
	char[] chars = "Loading awesomeness...".toCharArray();
	int currentIndex = 0;
	
	Glider glider = new Glider(0);
	
	private Splash(){

		stage = new Stage(new ScreenViewport());

		loadTable = GUIHelper.getTable(new Color(0x1a1a1aff));
		loadTable.top();

		Texture texture = new Texture(Gdx.files.internal(PathUtil.getPathForFile("hexlogobig.png")), true);
		texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);

		Image image = new Image(texture);
		image.setScaling(Scaling.fit);
		loadTable.add(GUIHelper.text("EPILEPSY WARNING! You play this game at your own risk!", new Color(0xfac466ff), 9)).left().pad(5).colspan(3).row();
		loadTable.add(image).top()/*.size(512)*/.pad(64).padBottom(96).padTop(48).center().colspan(3).row();
		
		loadLabel = GUIHelper.text("Loading awesomeness...", Color.WHITE, 24);
		loadLabel.setAlignment(Align.center);
		
		loadLabel.getStyle().font.getData().markupEnabled = true;
		
		Table subTable = new Table();
		loadTable.add(new Table()).expandX();
		subTable.add(loadLabel).center().bottom().row();
		subTable.add(bar = new ProgressBar(0, 100, 1, false, GUIHelper.getProgressBarStyle(Color.DARK_GRAY, new Color(0x02EAFAFF), 2))).fillX().center().bottom().height(2).padBottom(62).row();
		loadTable.add(subTable);
		loadTable.add(new Table()).expandX().padBottom(62).row();

		stage.addActor(loadTable);
		glider.glide(100, 0.5f);
	}

	float delta1 = 1.0f;
	float delta2 = 0;

	@Override
	public void render(float delta) {

		Gdx.gl20.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
		glider.update(delta);
		if((delta1+=delta) >= 1f/60){
			
			delta1 = 0;
			if(ended){
				Instance.game.setScreen(MainMenu.instance);
				ended = false;
			}

			bar.setValue(glider.getValue());
		}
		
		
		if((delta2+=delta) >= 1f/20){
			
			currentIndex+=1;
			
			String e = "";
			
			for(int i = 0; i < chars.length; i++) {
				
				int h = currentIndex-i+colors.length/2;
				
				if(h>=0 && h<=colors.length-1) {
					e += "[#"+colors[h]+"]"+chars[i]+"[]";
				} else {
					e+="[#ffffff]"+chars[i]+"[]";
				}
			}
			
			loadLabel.setText(e);
			
			if(currentIndex>= chars.length+colors.length/2) currentIndex=-colors.length/2;
			
			delta2 = 0;
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
	public void show() {

		Instance.cachedExecutor.submit(()->{
			Instance.maps = MapLoader.load();
			Utils.sleep(1000);
			ended = true;
		});

	}

	public void setStatus(String text){
		this.text = text.replaceAll("\\[", "\\[\\[").replaceAll("\\]", "\\]\\]");
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
