package xyz.hexagons.client.menu.screens;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.google.common.eventbus.Subscribe;
import me.wieku.animation.timeline.AnimationParallel;
import me.wieku.animation.timeline.Timeline;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.Version;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.HColor;
import xyz.hexagons.client.audio.MenuPlaylist;
import xyz.hexagons.client.audio.SoundManager;
import xyz.hexagons.client.menu.ActorAccessor;
import xyz.hexagons.client.engine.camera.SkewCamera;
import xyz.hexagons.client.menu.widgets.MenuButton;
import xyz.hexagons.client.menu.settings.ConfigEngine;
import xyz.hexagons.client.menu.settings.SettingsTab;
import xyz.hexagons.client.engine.render.BlurEffect;
import xyz.hexagons.client.engine.render.MapRenderer;
import xyz.hexagons.client.map.Map;
import xyz.hexagons.client.menu.widgets.PlayerRank;
import xyz.hexagons.client.rankserv.EventLogin;
import xyz.hexagons.client.rankserv.MotdApi;
import xyz.hexagons.client.rankserv.RankApi;
import xyz.hexagons.client.rankserv.RankApi.PlayerRankInfo;
import xyz.hexagons.client.utils.*;

import java.util.ArrayList;

public class MainMenu implements Screen {

	public static MainMenu instance = new MainMenu();

	private Stage stage;
	private MenuButton buttonStart, buttonOptions, buttonExit;
	private ArrayList<MenuButton> list = new ArrayList<>();
	private ArrayList<Actor> list2 = new ArrayList<>();
	private Label version, copyright;
	private int currentIndex = -1;
	private Image beatIHigh;
	private Image beatILow;

	private BlurEffect blurEffect;
	private SkewCamera camera = new SkewCamera();
	private ShapeRenderer shapeRenderer;
	private MapRenderer mapRenderer = new MapRenderer();
	private Table music;
	private Label title;
	private boolean escclick = false;

	private float[] dfg = new float[60];
	
	private static float COUNT = 10f;
	private float countDown = COUNT;
	private boolean visible = true;
	private boolean activity = false;
	private Timeline uiAnimation;
	
	SettingsTab sTab;
	
	PlayerRank rank;

	public MapSelect sl;

	public MainMenu(){
		stage = new Stage(new ExtendViewport(1024, 768));
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		
		blurEffect = new BlurEffect(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		blurEffect.setPower(5f);
		blurEffect.setDarkness(1.5f);

		shapeRenderer = new ShapeRenderer();
		
		Instance.eventBus.register(this);
		
		ConfigEngine.register();

		sTab = SettingsTab.getInstance();

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
					if(currentIndex == 0)
						Instance.game.setScreen((sl!=null ? sl : (sl=new MapSelect(Instance.maps))));

					if(currentIndex == 1 && !sTab.isShowed())
						sTab.show();

					if(currentIndex == 2)
						Gdx.app.exit();

				}

				if(keycode == Keys.ESCAPE || keycode == Keys.BACK)
					escclick = true;
				
				
				activity = true;
				return false;
			}

			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if(keycode == Keys.ESCAPE || keycode == Keys.BACK){
					if(escclick) {
						Gdx.app.exit();
					}
					escclick = false;
				}
				return false;
			}
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				
				for (int i = 0; i < list.size(); i++) {
					if(list.get(i).isPressed()) {
						selectIndex(i);
						list.get(i).getClickListener().touchUp(event, x, y, pointer, button);

						if(currentIndex == 0)
							Instance.scheduleOnMain.accept(()->Instance.game.setScreen((sl!=null ? sl : (sl=new MapSelect(Instance.maps)))));
						
						if(currentIndex == 1 && !sTab.isShowed())
								sTab.show();
						
						if(currentIndex == 2)
							Gdx.app.exit();

					}
				}
				activity = true;
				return super.touchDown(event, x, y, pointer, button);
			}
			
			@Override
			public boolean mouseMoved(InputEvent event, float x, float y) {
				activity = true;
				return super.mouseMoved(event, x, y);
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				activity = true;
				super.touchDragged(event, x, y, pointer);
			}
		});

		version = new Label("Build: " + Version.version, GUIHelper.getLabelStyle(new Color(0xa0a0a0ff), 8));
		version.pack();
		version.setPosition(5, stage.getHeight() - version.getHeight() - 7);
		stage.addActor(version);

		copyright = new Label("Hexagons! 2017 Written by Wieku and Magik6k", GUIHelper.getLabelStyle(new Color(0xa0a0a0ff), 10));
		copyright.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y,int pointer, int button) {
				Gdx.net.openURI("https://hexagons.xyz/");
				return true;
			}
		});
		copyright.pack();
		copyright.setPosition(stage.getWidth() - copyright.getWidth() - 5, 5);
		stage.addActor(copyright);

		Texture tex = new Texture(Gdx.files.internal(PathUtil.getPathForFile("hexlogobig.png")), true);
		tex.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		beatIHigh = new Image(tex);
		beatIHigh.setScaling(Scaling.fit);

		beatILow = new Image(tex);
		beatILow.setColor(1, 1, 1, 0.3f);
		beatILow.setScaling(Scaling.fit);

		list2.add(beatIHigh);
		list2.add(beatILow);

		stage.addActor(beatIHigh);
		stage.addActor(beatILow);

		music = GUIHelper.getTable(new Color(0.1f, 0.1f, 0.1f, 0.5f));

		music.add(title = GUIHelper.text("", new Color(0xa0a0a0ff), 12)).pad(5).row();
		//music.add(bar = new ProgressBar(0f, 100f, 1f, false, GUIHelper.getProgressBarStyle(Color.DARK_GRAY, new Color(0x02eafaff), 10)));
		music.pack();

		stage.addActor(music);

		list.add(buttonStart = new MenuButton("Start"));
		list.add(buttonOptions = new MenuButton("Options"));
		list.add(buttonExit = new MenuButton("Exit"));

		buttonStart.setBounds(stage.getWidth() - 328, 252, 512, 100);
		buttonOptions.setBounds(stage.getWidth() - 394, 142, 512, 100);
		buttonExit.setBounds(stage.getWidth() - 460, 32, 512, 100);
		stage.addActor(buttonStart);
		stage.addActor(buttonOptions);
		stage.addActor(buttonExit);
		list2.add(sTab);
		stage.addActor(sTab);
		selectIndex(0);
		
		rank = new PlayerRank();
		stage.addActor(rank);
		
		CurrentMap.reset();
		
		Instance.accountManager.loginSaved();
		Instance.game.showNotify(MotdApi.instance.getMotd().getText(), 5f);
	}

	private boolean first = false;


	private Map currentPlaying;
	@Override
	public void show() {
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		Gdx.input.setInputProcessor(stage);

		Instance.setForegroundFps.accept(0);
		if(!first){
			MenuPlaylist.start();

			first = true;
			if(Instance.maps.isEmpty()) {
				CurrentMap.gameProperties.backgroundColors.add(new HColor(36f/255, 36f/255, 36f/255, 1f).addPulse(20f / 255, 20f / 255, 20f / 255, 0f));
				CurrentMap.gameProperties.backgroundColors.add(new HColor(20f / 255, 20f / 255, 20f / 255, 1f).addPulse(20f / 255, 20f / 255, 20f / 255, 0f));
			}
		}

		if(!Instance.maps.isEmpty()){
			try {
				CurrentMap.reset();
				MenuPlaylist.getCurrent().script.initColors();
				MenuPlaylist.getCurrent().script.init();
			} catch (Exception e) {
				System.err.println("MAP INIT FAILED:");
				e.printStackTrace();
			}
		}

		MenuPlaylist.setLooping(false);
		currentPlaying = MenuPlaylist.getCurrent();
		
		countDown = COUNT;
		
		Instance.cachedExecutor.execute(()->{
			if(Instance.currentAccount != null) {
				PlayerRankInfo info = RankApi.instance.getPlayerRankInfo();
				rank.update(Instance.currentAccount.nick(), info.globalRank, info.rankedScore);
			}
		});
	}

	private Timeline beatHigh;
	private Timeline beatLow;
	private float delta0 = 0;


	private boolean lo = false;
	
	private Glider darknessGlider = new Glider(1.5f);
	private Glider alphaGlider = new Glider(0.1f);

	@Override
	public void render(float delta) {

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		camera.rotate(CurrentMap.gameProperties.rotationSpeed * 360f * delta);
		camera.update(delta);
		if((delta0 += delta)>=1f/60) {

			darknessGlider.update(1f/60);
			alphaGlider.update(1f/60);
			
			CurrentMap.gameProperties.walls.update(1f/60);
			CurrentMap.gameProperties.skew = 1f;
			CurrentMap.setMinSkew(0.9999f);
			CurrentMap.setMaxSkew(1);
			CurrentMap.setSkewTime(1);

			if(currentPlaying != MenuPlaylist.getCurrent()){

				currentPlaying = MenuPlaylist.getCurrent();
				if(!Instance.maps.isEmpty()){
					CurrentMap.reset();
					MenuPlaylist.getCurrent().script.initColors();
					MenuPlaylist.getCurrent().script.init();
					camera.reset();
				}
			}

			if(MenuPlaylist.getCurrent() != null) {
				title.setText(MenuPlaylist.getCurrent().info.songAuthor + " - " + MenuPlaylist.getCurrent().info.songName);
				float[] cv = MenuPlaylist.getCurrentPlayer().getFFT();
				for(int i=0;i<40;i++) {
					dfg[i] = Math.max(2, Math.max(Math.min(MathUtils.log2(cv[i] * 2) * 50 * (1.5f/darknessGlider.getValue()), dfg[i] + delta0 * 800), dfg[i] - delta0 * 300));
				}
			} else title.setText("No maps available");

			music.pack();
			music.setPosition(stage.getWidth() - music.getWidth(), stage.getHeight() - music.getHeight());
			
			boolean cd = false;
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i).isOver()) {
					cd=true;
					selectIndex(i);
				}
			}
			if(!cd && Gdx.app.getType() == ApplicationType.Android) selectIndex(-1);

			boolean isNull = MenuPlaylist.getCurrentPlayer() == null;
			if((!isNull && !lo && MenuPlaylist.getCurrentPlayer().isOnset()) || (isNull && (beatLow == null || beatLow.isFinished()))){
				lo=true;

				float duration = (MenuPlaylist.getCurrentPlayer() != null?0.2f:1f);

				if(beatLow != null) beatLow.kill();
				beatLow = new Timeline().beginSequence().push(ActorAccessor.createSineTween(beatILow, ActorAccessor.SIZEC, 0.1f*(1.025f/beatILow.getScaleX()), 1.025f, 0))
						.push(ActorAccessor.createSineTween(beatILow, ActorAccessor.SIZEC, duration, 1f, 0)).end();
				beatLow.start(Instance.getAnimationManager());

				if(beatHigh != null) beatHigh.kill();

				beatHigh = new Timeline().beginSequence().push(ActorAccessor.createSineTween(beatIHigh, ActorAccessor.SIZEC, 0.1f/2*(0.96f/ beatIHigh.getScaleX()), 0.96f, 0))
						.push(ActorAccessor.createSineTween(beatIHigh, ActorAccessor.SIZEC, duration, 1f, 0)).end();
				beatHigh.start(Instance.getAnimationManager());

			}

			if(isNull || !MenuPlaylist.getCurrentPlayer().isOnset()) lo = false;

			countDown -= 1f/60;
			
			if(countDown < 0 && visible) {
				
				if(uiAnimation != null) uiAnimation.kill();

				AnimationParallel parr = new Timeline().beginParallel();

				for(Actor actor : stage.getActors()) {
					if(!list2.contains(actor))
						parr.push(ActorAccessor.createFadeTween(actor, 5f, 0f, 0f));
				}
				uiAnimation = parr.end();
				uiAnimation.start(Instance.getAnimationManager());
				
				visible = false;
				
				sTab.hide();
				
				darknessGlider.glide(1f, 5f);
				alphaGlider.glide(0.4f, 5f);
			}
			
			if(activity) {
				
				if(!visible) {
					if(uiAnimation != null) uiAnimation.kill();

					AnimationParallel parr = new Timeline().beginParallel();

					for(Actor actor : stage.getActors()) {
						if(!list2.contains(actor))
							parr.push(ActorAccessor.createFadeTween(actor, 1f*(1f-actor.getColor().a), 0f, 1f));
					}
					uiAnimation = parr.end();
					uiAnimation.start(Instance.getAnimationManager());
					
					darknessGlider.glide(1.5f, (1f - stage.getRoot().getColor().a));
					alphaGlider.glide(0.1f, (1f - stage.getRoot().getColor().a));
				}
				
				countDown = COUNT;
				visible = true;
				activity = false;
			}
			
			blurEffect.setDarkness(darknessGlider.getValue());
			
			rank.setPosition(stage.getWidth() - 300, stage.getHeight() - music.getHeight() - 5 - rank.getHeight());
			
			delta0 -= 1f/60;
		}

		blurEffect.bind();

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.identity();
		shapeRenderer.rotate(1, 0, 0, 90);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		mapRenderer.renderBackground(shapeRenderer, delta, true, 0);
		shapeRenderer.end();

		blurEffect.unbind();

		blurEffect.render(stage.getBatch());

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
		shapeRenderer.identity();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		shapeRenderer.setColor(CurrentMap.gameProperties.walls.r, CurrentMap.gameProperties.walls.g, CurrentMap.gameProperties.walls.b, alphaGlider.getValue());
		float g = stage.getHeight()/40f;
		for(int i = 0; i < 40; i++){
			shapeRenderer.rect(0, i*g, dfg[i], g-1);
			shapeRenderer.rect(stage.getWidth()-dfg[i], stage.getHeight()-g-i*g, dfg[i], g-1);
		}
		shapeRenderer.end();

		Gdx.gl.glDisable(GL20.GL_BLEND);

		stage.act(delta);
		stage.draw();
		
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);

		version.setPosition(5, stage.getHeight() - version.getHeight() - 7);
		copyright.setPosition(stage.getWidth() - copyright.getWidth() - 5, 5);
		
		beatIHigh.setSize((508f / 1024) * stage.getWidth(), (508f / 768) * stage.getHeight());
		beatIHigh.setPosition((401f / 1024) * stage.getWidth() - beatIHigh.getWidth() / 2, ((768f - 301f) / 768) * stage.getHeight() - beatIHigh.getHeight() / 2);

		beatILow.setSize((508f / 1024) * stage.getWidth(), (508f / 768) * stage.getHeight());
		beatILow.setPosition((401f / 1024) * stage.getWidth() - beatIHigh.getWidth() / 2, ((768f - 301f) / 768) * stage.getHeight() - beatIHigh.getHeight() / 2);


		buttonStart.setBounds(stage.getWidth() - 328 - (list.indexOf(buttonStart) == currentIndex ? 20 : 0), 252, 512, 100);
		buttonOptions.setBounds(stage.getWidth() - 394 - (list.indexOf(buttonOptions) == currentIndex ? 20 : 0), 142, 512, 100);
		buttonExit.setBounds(stage.getWidth() - 460 - (list.indexOf(buttonExit) == currentIndex ? 20 : 0), 32, 512, 100);

		blurEffect.resize(width, height);
		music.setPosition(stage.getWidth() - music.getWidth(), stage.getHeight() - music.getHeight());
		music.layout();
	}

	private void selectIndex(int index){
		if(currentIndex == index) return;

		for (int i = 0; i < list.size(); i++) {
			list.get(i).select(i==index);
			float x=(i==0?stage.getWidth() - 328:i==1?stage.getWidth() - 394:stage.getWidth() - 460);
			ActorAccessor.startTween(ActorAccessor.createCircleOutTween(list.get(i), ActorAccessor.SLIDEX, 0.5f, x , 0f));
		}

		if((currentIndex = index) == -1) return;

		SoundManager.playSound("click");

		float x = (currentIndex == 0 ? stage.getWidth() - 328:currentIndex==1?stage.getWidth() - 394:stage.getWidth() - 460);
		ActorAccessor.startTween(ActorAccessor.createCircleOutTween(list.get(currentIndex), ActorAccessor.SLIDEX, 0.5f, x - 20, 0f));
	}
	
	@Subscribe
	public void onLogin(EventLogin event) {
		if(Instance.currentAccount != null) {
			PlayerRankInfo info = RankApi.instance.getPlayerRankInfo();
			rank.update(event.getAccount().nick(), info.globalRank, info.rankedScore);
		}
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
