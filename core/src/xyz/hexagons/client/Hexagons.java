package xyz.hexagons.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.google.common.eventbus.Subscribe;
import me.wieku.animation.animations.Animation;
import me.wieku.animation.timeline.Timeline;
import org.lwjgl.opengl.Display;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.audio.SoundManager;
import xyz.hexagons.client.map.Map;
import xyz.hexagons.client.map.MapLoader;
import xyz.hexagons.client.menu.ActorAccessor;
import xyz.hexagons.client.menu.screens.MapSelect;
import xyz.hexagons.client.menu.screens.Splash;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.menu.settings.SettingsManager;
import xyz.hexagons.client.menu.settings.event.SettingsChanged;
import xyz.hexagons.client.resources.FontManager;
import xyz.hexagons.client.utils.FpsCounter;
import xyz.hexagons.client.utils.GUIHelper;
import xyz.hexagons.client.utils.PathUtil;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static xyz.hexagons.client.audio.MenuPlaylist.update;

public class Hexagons extends Game {

	DecimalFormat delayFormat = new DecimalFormat("0.00");
	Color color = new Color(Color.WHITE);
	FpsCounter fpsCounter = new FpsCounter(60);
	Stage stage;
	public Label fps;

	int width, height;
	private Queue<Runnable> taskList = new ConcurrentLinkedQueue<>();

	private Table notifyTable;
	private Label notifyLabel;
	private Timeline notifyAnimation;

	public Hexagons() {
		Instance.game = this;
		Instance.eventBus.register(this);
	}

	@Override
	public void create () {
		FontManager.init();
		Animation.addAccessor(Actor.class, new ActorAccessor());
		Gdx.input.setCatchBackKey(true);

		SoundManager.registerSound("death", PathUtil.getPathForFile("sound/death.ogg"), true);
		SoundManager.registerSound("start", PathUtil.getPathForFile("sound/go.ogg"), true);
		SoundManager.registerSound("gameover", PathUtil.getPathForFile("sound/gameOver.ogg"), true);
		SoundManager.registerSound("swap", PathUtil.getPathForFile("sound/swap.ogg"), true);
		SoundManager.registerSound("beep", PathUtil.getPathForFile("sound/beep.ogg"), true);
		SoundManager.registerSound("click", PathUtil.getPathForFile("sound/menuclick.ogg"), true);
		SoundManager.registerSound("change", PathUtil.getPathForFile("sound/menuhit.ogg"), true);
		SoundManager.registerSound("levelup", PathUtil.getPathForFile("sound/levelUp.ogg"), true);

		setScreen(Splash.instance);
		if(Settings.instance.graphics.fullscreen){
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		} else {
			Gdx.graphics.setWindowedMode(1024, 768);
		}

		Instance.scheduleOnMain = (task) -> taskList.add(task);

		stage = new Stage(new ExtendViewport(1024, 768));

		fps = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 10));
		fps.getStyle().font.setFixedWidthGlyphs("01234567890");
		fps.layout();
		fps.setX(2);
		stage.addActor(fps);

		notifyTable = GUIHelper.getTable(new Color(0,0,0,0.8f));
		notifyLabel = GUIHelper.text("", Color.WHITE, 20);
		notifyLabel.setAlignment(Align.center);
		notifyTable.add(notifyLabel).expand();
		notifyTable.pack();
		notifyTable.setWidth(stage.getWidth());
		notifyTable.setPosition(0, 1f/3 * 768);
		notifyTable.setTouchable(Touchable.disabled);
		notifyTable.setColor(1, 1, 1, 0f);

		stage.addActor(notifyTable);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		this.width = width;
		this.height = height;
		super.resize(width, height);
	}

	float delta0;
	@Override
	public void render () {
		if(!taskList.isEmpty()) {
			Iterator<Runnable> it = taskList.iterator();
			while (it.hasNext()) {
				Runnable r = it.next();
				try {
					r.run();
				} catch (Exception e) {
					e.printStackTrace();
				}

				it.remove();
			}
		}

		if((delta0 += Gdx.graphics.getDeltaTime()) >=1f/60){
			update(delta0);
			fpsCounter.update(Gdx.graphics.getDeltaTime());
			if(getScreen() instanceof MapSelect)
				color.set(Color.WHITE);
			else
				color.set(CurrentMap.gameProperties.walls.r, CurrentMap.gameProperties.walls.g, CurrentMap.gameProperties.walls.b, 1);
			fps.getStyle().fontColor = color;
			fps.setText((int)(fpsCounter.getFPS()) + "FPS\n" + delayFormat.format(1000f/fpsCounter.getFPS())+"ms");
			fps.pack();
			delta0 = 0;
		}

		Instance.getAnimationManager().update(Gdx.graphics.getDeltaTime());
		super.render();
		if(!(getScreen() instanceof Splash)) {
			notifyTable.setWidth(stage.getWidth());
			notifyTable.layout();
			stage.act();
			stage.draw();
		}
	}
	
	@Override
	public void dispose () {
		super.dispose();
		if(Instance.maps != null) {
			for (Map map : Instance.maps) {
				MapLoader.closeMap(map.file);
			}
		}

		SettingsManager.saveSettings();
	}


	int x=-1, y=-1, windowWidth = 1024, windowHeight = 768;
	@Subscribe
	public void settingsChanged(SettingsChanged e) {
		if(e.getElement().getId().equals("fullscreen")) {
			if(Settings.instance.graphics.fullscreen){
				x = Display.getX();
				y = Display.getY();
				windowWidth = Display.getWidth();
				windowHeight = Display.getHeight();
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
			} else {
				if(x==-1) {
					x = (Display.getWidth()-windowWidth)/2;
					y = (Display.getHeight()-windowHeight)/2;
				}

				Gdx.graphics.setWindowedMode(windowWidth, windowHeight);
				Display.setLocation(x, y);
			}
		} else if(e.getElement().getId().equals("vSync")) {
			Display.setVSyncEnabled(Settings.instance.graphics.vSync);
		} else if(e.getElement().getId().equals("msaa")) {
			String sam = Settings.instance.graphics.msaa;
			Instance.setSamples.accept(sam.equals("OFF")?0:Integer.parseInt(sam.substring(0, sam.length()-1)));
			showNotify("You need to restart the game\nto see MSAA changes", 3f);
		}
	}

	public void showNotify(String notify, float time) {
		notifyLabel.setText(notify);
		if(notifyAnimation != null) notifyAnimation.kill();
		notifyTable.pack();
		notifyAnimation = new Timeline().beginSequence().push(ActorAccessor.createFadeTableTween(notifyTable, 2f, 0, 1f))
				.pushPause(time).push(ActorAccessor.createFadeTableTween(notifyTable, 2f, 0, 0f)).end();
		notifyAnimation.start(Instance.getAnimationManager());
	}

}
