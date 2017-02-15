package xyz.hexagons.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import me.wieku.animation.animations.Animation;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.audio.SoundManager;
import xyz.hexagons.client.engine.lua.LuaInit;
import xyz.hexagons.client.map.Map;
import xyz.hexagons.client.map.MapLoader;
import xyz.hexagons.client.menu.ActorAccessor;
import xyz.hexagons.client.menu.screens.MapSelect;
import xyz.hexagons.client.menu.screens.Splash;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.menu.settings.SettingsManager;
import xyz.hexagons.client.resources.FontManager;
import xyz.hexagons.client.utils.FpsCounter;
import xyz.hexagons.client.utils.GUIHelper;
import xyz.hexagons.client.utils.PathUtil;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static xyz.hexagons.client.audio.MenuPlaylist.update;

public class Hexagons extends Game {

	DecimalFormat delayFormat = new DecimalFormat("0.00");
	Color color = new Color(Color.WHITE);
	FpsCounter fpsCounter = new FpsCounter(60);
	Stage stage;
	public Label fps;

	int width, height;
	private List<Runnable> taskList = new LinkedList<>();

	public Hexagons() {
		Instance.game = this;
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
		SoundManager.registerSound("levelup", PathUtil.getPathForFile("sound/levelUp.ogg"), true);

		setScreen(Splash.instance);
		if(Settings.instance.graphics.fullscreen){
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		} else {
			Gdx.graphics.setWindowedMode(1024, 768);
		}

		Instance.scheduleOnMain = (task) -> taskList.add(task);

		LuaInit.init();

		stage = new Stage(new ExtendViewport(1024, 768));

		fps = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 10));
		fps.getStyle().font.setFixedWidthGlyphs("01234567890");
		fps.layout();
		fps.setX(2);
		stage.addActor(fps);

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

}
