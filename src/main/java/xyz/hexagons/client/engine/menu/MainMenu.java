package xyz.hexagons.client.engine.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import xyz.hexagons.client.Main;
import xyz.hexagons.client.animation.timeline.Timeline;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.audio.MenuPlaylist;
import xyz.hexagons.client.audio.SoundManager;
import xyz.hexagons.client.engine.ActorAccessor;
import xyz.hexagons.client.engine.camera.SkewCamera;
import xyz.hexagons.client.engine.menu.buttons.MenuButton;
import xyz.hexagons.client.engine.menu.options.Options;
import xyz.hexagons.client.engine.render.Background;
import xyz.hexagons.client.engine.render.BlurEffect;
import xyz.hexagons.client.map.Map;
import xyz.hexagons.client.utils.GUIHelper;

import java.util.ArrayList;

public class MainMenu implements Screen {

	public static MainMenu instance = new MainMenu();

	Stage stage;
	MenuButton button, button2, button3;
	ArrayList<MenuButton> list = new ArrayList<>();
	Label version, copyright;
	int currentIndex = -1;
	Image beatIHigh;
	Image beatILow;

	BlurEffect blurEffect;
	SkewCamera camera = new SkewCamera();
	ShapeRenderer shapeRenderer;
	Background background = new Background();


	Table music;
	Label title;
	ProgressBar bar;
	boolean escclick = false;

	public boolean optionsShowed;
	Options options;

	public MainMenu(){
		stage = new Stage(new ScreenViewport());
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		blurEffect = new BlurEffect(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		blurEffect.setPower(5f);
		blurEffect.setDarkness(1.5f);

		shapeRenderer = new ShapeRenderer();

		options = new Options();

		stage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.UP) {
					int index = (currentIndex == 0 ? list.size() - 1 : currentIndex - 1);
					selectIndex(index);
				}

				if (keycode == Keys.DOWN) {
					int index = (currentIndex == list.size() - 1 ? 0 : currentIndex + 1);
					selectIndex(index);
				}

				if(keycode == Keys.LEFT){
					SoundManager.playSound("click");
					MenuPlaylist.previousSong();
				}

				if(keycode == Keys.RIGHT){
					SoundManager.playSound("click");
					MenuPlaylist.nextSong();
				}

				if(keycode == Keys.ENTER){
					if(currentIndex == 0){
						Main.getInstance().setScreen(new MapSelect(Main.getInstance().maps));
					}

					if(currentIndex == 1) {
						optionsShowed = true;
						options.show();
						/*Main.getInstance().setScreen(options);*/
					}

					if(currentIndex == 2){
						Gdx.app.exit();
					}
				}
				if(keycode == Keys.ESCAPE)
					escclick = true;
				return false;
			}

			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if(keycode == Keys.ESCAPE){
					if(escclick == true) {
						Gdx.app.exit();
					}
					escclick = false;
				}
				return false;
			}
		});

		version = new Label("Version: "+Main.version, GUIHelper.getLabelStyle(new Color(0xa0a0a0ff), 10));
		version.pack();
		version.setPosition(5, stage.getHeight() - version.getHeight() - 5);
		stage.addActor(version);

		copyright = new Label("Hexagons! 2015 Created by: Magik6k and Wieku",GUIHelper.getLabelStyle(new Color(0xa0a0a0ff), 10));
		copyright.pack();
		copyright.setPosition(stage.getWidth() - copyright.getWidth() - 5, 5);
		stage.addActor(copyright);

		Texture tex = new Texture(Gdx.files.internal("assets/hexlogobig.png"), true);
		tex.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		beatIHigh = new Image(tex);
		beatIHigh.setScaling(Scaling.fit);

		beatILow = new Image(tex);
		beatILow.setColor(1, 1, 1, 0.5f);
		beatILow.setScaling(Scaling.fit);

		stage.addActor(beatIHigh);
		stage.addActor(beatILow);


		music = new Table();
		music.setBackground(GUIHelper.getTxRegion(new Color(0.1f, 0.1f, 0.1f, 0.5f)));

		music.add(title = new Label("", GUIHelper.getLabelStyle(new Color(0xa0a0a0ff), 12))).pad(5).row();
		//music.add(bar = new ProgressBar(0f, 100f, 1f, false, GUIHelper.getProgressBarStyle(Color.DARK_GRAY, new Color(0x02eafaff), 10)));
		music.pack();

		stage.addActor(music);

		list.add(button = new MenuButton("Start"));
		list.add(button2 = new MenuButton("Options"));
		list.add(button3 = new MenuButton("Exit"));

		button.setBounds(stage.getWidth() - 309, 252, 512, 100);
		button2.setBounds(stage.getWidth() - 379,142,512,100);
		button3.setBounds(stage.getWidth() - 449, 32, 512, 100);
		stage.addActor(button);
		stage.addActor(button2);
		stage.addActor(button3);

		selectIndex(0);

		CurrentMap.reset();

	}

	boolean first = false;


	Map currentPlaying;
	@Override
	public void show() {
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		Gdx.input.setInputProcessor(stage);

		if(!first){
			MenuPlaylist.start();
			first = true;
		}

		if(!Main.getInstance().maps.isEmpty()){
			CurrentMap.reset();
			MenuPlaylist.getCurrent().script.initColors();
			MenuPlaylist.getCurrent().script.onInit();
		}

		MenuPlaylist.setLooping(false);
		currentPlaying = MenuPlaylist.getCurrent();

	}

	Timeline beatHigh;
	Timeline beatLow;
	float delta0 = 0;
	@Override
	public void render(float delta) {

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		camera.rotate(CurrentMap.data.rotationSpeed * 360f * delta);
		camera.update(delta);
		if((delta0 += delta)>=1f/60) {
			background.update(delta0);
			CurrentMap.data.walls.update(delta0);
			CurrentMap.data.skew = 1f;
			CurrentMap.setMinSkew(0.9999f);
			CurrentMap.setMaxSkew(1);
			CurrentMap.setSkewTime(1);


			if(currentPlaying != MenuPlaylist.getCurrent()){

				currentPlaying = MenuPlaylist.getCurrent();
				if(!Main.getInstance().maps.isEmpty()){
					CurrentMap.reset();
					MenuPlaylist.getCurrent().script.initColors();
					MenuPlaylist.getCurrent().script.onInit();
				}
			}


			title.setText(MenuPlaylist.getCurrent().info.songAuthor + " - " + MenuPlaylist.getCurrent().info.songName);
			music.pack();

			music.setPosition(Gdx.graphics.getWidth()-music.getWidth(), Gdx.graphics.getHeight()-music.getHeight());

			if(beatHigh == null || beatHigh.isFinished()){
				
				beatHigh = new Timeline().beginSequence().push(ActorAccessor.createSineTween(beatIHigh, ActorAccessor.SIZEC, 0.04f/2*(0.96f/ beatIHigh.getScaleX()), 0.96f, 0))
						.push(ActorAccessor.createSineTween(beatIHigh, ActorAccessor.SIZEC, 0.3f, 1f, 0)).end();
				beatHigh.start(Main.getInstance().getAnimationManager());
			}
			
			if(beatLow == null || beatLow.isFinished()){
				
				beatLow = new Timeline().beginSequence().push(ActorAccessor.createSineTween(beatILow, ActorAccessor.SIZEC, 0.1f*(1.025f/beatILow.getScaleX()), 1.025f, 0))
						.push(ActorAccessor.createSineTween(beatILow, ActorAccessor.SIZEC, 0.3f, 1f, 0)).end();
				beatLow.start(Main.getInstance().getAnimationManager());
			}

			delta0 = 0;
		}

		blurEffect.bind();

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.identity();
		shapeRenderer.rotate(1, 0, 0, 90);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		background.render(shapeRenderer, delta, true, 0);
		shapeRenderer.end();

		blurEffect.unbind();

		blurEffect.render(stage.getBatch());

		stage.act(delta);
		stage.draw();

		if(optionsShowed)
			options.render(delta);
		else
			if(!Gdx.input.getInputProcessor().equals(stage)) Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		version.setPosition(5, stage.getHeight() - version.getHeight() - 5);
		copyright.setPosition(stage.getWidth() - copyright.getWidth() - 5, 5);
		
		beatIHigh.setSize((508f / 1024) * stage.getWidth(), (508f / 768) * stage.getHeight());
		beatIHigh.setPosition((401f / 1024) * stage.getWidth() - beatIHigh.getWidth() / 2, ((768f - 301f) / 768) * stage.getHeight() - beatIHigh.getHeight() / 2);


		beatILow.setSize((508f / 1024) * stage.getWidth(), (508f / 768) * stage.getHeight());
		beatILow.setPosition((401f / 1024) * stage.getWidth() - beatIHigh.getWidth() / 2, ((768f - 301f) / 768) * stage.getHeight() - beatIHigh.getHeight() / 2);

		//beatIHigh.setOrigin((401f / 1024) * stage.getWidth(), ((768f - 301f) / 768) * stage.getHeight());
		button.setBounds(stage.getWidth() - 309 - (list.indexOf(button) == currentIndex ? 20 : 0), 252, 512, 100);
		button2.setBounds(stage.getWidth() - 379 - (list.indexOf(button2) == currentIndex ? 20 : 0), 142, 512, 100);
		button3.setBounds(stage.getWidth() - 449 - (list.indexOf(button3) == currentIndex ? 20 : 0), 32, 512, 100);
		blurEffect.resize(width, height);
		music.setPosition(Gdx.graphics.getWidth() - music.getWidth(), Gdx.graphics.getHeight() - music.getHeight());

		if(optionsShowed)
			options.resize(width, height);

	}

	private void selectIndex(int index){
		if(currentIndex != -1){
			list.get(currentIndex).select(false);
			ActorAccessor.startTween(ActorAccessor.createSineTween(list.get(currentIndex), ActorAccessor.SLIDEX, 0.05f, list.get(currentIndex).getX()+20 , 0f));
		}
		currentIndex = index;
		SoundManager.playSound("click");
		list.get(currentIndex).select(true);
		ActorAccessor.startTween(ActorAccessor.createSineTween(list.get(currentIndex), ActorAccessor.SLIDEX, 0.05f, list.get(currentIndex).getX() - 20, 0f));
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}
}
