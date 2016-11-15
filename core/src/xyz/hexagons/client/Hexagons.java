package xyz.hexagons.client;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.wieku.animation.animations.Animation;
import xyz.hexagons.client.audio.SoundManager;
import xyz.hexagons.client.map.MapLoader;
import xyz.hexagons.client.menu.ActorAccessor;
import xyz.hexagons.client.menu.screens.Updater;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.resources.FontManager;
import xyz.hexagons.client.utils.PathUtil;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static xyz.hexagons.client.audio.MenuPlaylist.update;

public class Hexagons extends Game {

	int width, height;
	private List<Runnable> taskList = new LinkedList<>();

	public Hexagons() {
		Instance.game = this;
	}

	@Override
	public void create () {
		FontManager.init();
		Animation.addAccessor(Actor.class, new ActorAccessor());

		SoundManager.registerSound("death", PathUtil.getPathForFile("sound/death.ogg"), true);
		SoundManager.registerSound("start", PathUtil.getPathForFile("sound/go.ogg"), true);
		SoundManager.registerSound("gameover", PathUtil.getPathForFile("sound/gameOver.ogg"), true);
		SoundManager.registerSound("swap", PathUtil.getPathForFile("sound/swap.ogg"), true);
		SoundManager.registerSound("beep", PathUtil.getPathForFile("sound/beep.ogg"), true);
		SoundManager.registerSound("click", PathUtil.getPathForFile("sound/menuclick.ogg"), true);
		SoundManager.registerSound("levelup", PathUtil.getPathForFile("sound/levelUp.ogg"), true);

		setScreen(Updater.instance);
		if(Settings.instance.graphics.fullscreen){
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		} else {
			Gdx.graphics.setWindowedMode(1024, 768);
		}

		Instance.scheduleOnMain = (task) -> taskList.add(task);
	}

	@Override
	public void resize(int width, int height) {
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
			delta0 = 0;
		}

		Instance.getAnimationManager().update(Gdx.graphics.getDeltaTime());
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		if(Instance.maps != null)
			Instance.maps.forEach(m-> MapLoader.closeJar(m.file));

		if(Settings.instance != null){
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			try {
				Files.write(gson.toJson(Settings.instance), new File(Instance.storageRoot, "settings.json"), Charsets.UTF_8);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
