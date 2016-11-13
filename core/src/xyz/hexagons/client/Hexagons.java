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

import java.io.File;
import java.io.IOException;

import static xyz.hexagons.client.audio.MenuPlaylist.update;

public class Hexagons extends Game {

	int width, height;

	public Hexagons() {
		Instance.game = this;
	}

	@Override
	public void create () {
		FontManager.init();
		Animation.addAccessor(Actor.class, new ActorAccessor());

		String asset = (Gdx.app.getType()== ApplicationType.Android?"":"assets/");

		SoundManager.registerSound("death", asset + "sound/death.ogg", true);
		SoundManager.registerSound("start", asset + "sound/go.ogg", true);
		SoundManager.registerSound("gameover", asset + "sound/gameOver.ogg", true);
		SoundManager.registerSound("swap", asset + "sound/swap.ogg", true);
		SoundManager.registerSound("beep", asset + "sound/beep.ogg", true);
		SoundManager.registerSound("click", asset + "sound/menuclick.ogg", true);
		SoundManager.registerSound("levelup", asset + "sound/levelUp.ogg", true);

		setScreen(Updater.instance);
		if(Settings.instance.graphics.fullscreen){
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		} else {
			Gdx.graphics.setWindowedMode(1024, 768);
		}
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
				Files.write(gson.toJson(Settings.instance), new File("settings.json"), Charsets.UTF_8);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
