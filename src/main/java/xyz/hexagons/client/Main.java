package xyz.hexagons.client;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.wieku.animation.AnimationManager;
import me.wieku.animation.animations.Animation;
import xyz.hexagons.client.audio.MenuPlaylist;
import xyz.hexagons.client.audio.SoundManager;
import xyz.hexagons.client.menu.ActorAccessor;
import xyz.hexagons.client.menu.screens.Updater;
import xyz.hexagons.client.map.MapLoader;
import xyz.hexagons.client.resources.FontManager;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.map.Map;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Sebastian Krajewski on 20.03.15.
 */
public class Main extends Game{

	public static LwjglApplicationConfiguration config;
	int width, height;
	public ArrayList<Map> maps;
	public static float diagonal = 1600f;
	static Main instance = new Main();
	static LwjglApplication app;
	public static boolean noupdate = false;
	public static final String version = "0.1.3";
	private AnimationManager animationManager = new AnimationManager();

	public static Main getInstance(){
		return instance;
	}

	@Override
	public void create() {
		FontManager.init();
		Animation.addAccessor(Actor.class, new ActorAccessor());

		SoundManager.registerSound("death", "assets/sound/death.ogg", true);
		SoundManager.registerSound("start", "assets/sound/go.ogg", true);
		SoundManager.registerSound("gameover", "assets/sound/gameOver.ogg", true);
		SoundManager.registerSound("swap", "assets/sound/swap.ogg", true);
		SoundManager.registerSound("beep", "assets/sound/beep.ogg", true);
		SoundManager.registerSound("click", "assets/sound/menuclick.ogg", true);
		SoundManager.registerSound("levelup", "assets/sound/levelUp.ogg", true);

		setScreen(Updater.instance);
		if(Settings.instance.graphics.fullscreen){
			Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode());
		} else {
			Gdx.graphics.setDisplayMode(1024, 768, false);
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
	public void render() {

		if((delta0+=Gdx.graphics.getDeltaTime()) >=1f/60){
			MenuPlaylist.update(delta0);
			delta0 = 0;
		}

		animationManager.update(Gdx.graphics.getDeltaTime());
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
		if(maps != null)
			maps.forEach(m-> MapLoader.closeJar(m.file));

		if(Settings.instance != null){
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			try {
				Files.write(gson.toJson(Settings.instance), new File("settings.json"), Charsets.UTF_8);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}


	public static void main(String[] args) {

		if(args.length > 0 && args[0].equals("noupdate")) noupdate = true;

		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

		try {
			Gson gson = new GsonBuilder().create();
			File file = new File("settings.json");
			if(!file.exists())
				Settings.instance = new Settings();
			else
				Settings.instance = gson.fromJson(new FileReader(file), Settings.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Globals globals = JsePlatform.standardGlobals();
		LuaValue chunk = globals.load("print 'hello, world'");
		chunk.call();


		config = new LwjglApplicationConfiguration();

		config.width = 1024;
		config.height = 768;
		config.fullscreen = false;
		config.title = "Hexagons! " + Main.version;
		config.foregroundFPS = 120;
		config.addIcon("assets/hexlogo.png", FileType.Internal);
		config.samples = Settings.instance.graphics.msaa;
		config.vSyncEnabled = Settings.instance.graphics.vSync;
		app = new LwjglApplication(instance, config);

	}

	public AnimationManager getAnimationManager() {
		return animationManager;
	}

}
