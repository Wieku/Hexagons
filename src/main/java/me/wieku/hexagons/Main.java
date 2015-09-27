package me.wieku.hexagons;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.wieku.hexagons.animation.AnimationManager;
import me.wieku.hexagons.animation.animations.Animation;
import me.wieku.hexagons.audio.MenuPlaylist;
import me.wieku.hexagons.engine.ActorAccessor;
import me.wieku.hexagons.engine.menu.Updater;
import me.wieku.hexagons.map.MapLoader;
import me.wieku.hexagons.resources.FontManager;
import me.wieku.hexagons.engine.Settings;
import me.wieku.hexagons.map.Map;

import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
	public static final String version = "0.1.1";
	private AnimationManager animationManager = new AnimationManager();

	private Main(){
	}

	public static Main getInstance(){
		return instance;
	}

	@Override
	public void create() {
		FontManager.init();
		Animation.addAccessor(Actor.class, new ActorAccessor());
		setScreen(Updater.instance);
		if(Settings.instance.fullscreen){
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

		config = new LwjglApplicationConfiguration();

		for (DisplayMode current : LwjglApplicationConfiguration.getDisplayModes()) {
			System.out.println(current.width + "x" + current.height + "x" +
					current.bitsPerPixel + " " + current.refreshRate + "Hz");
		}



		config.width = 1024;
		config.height = 768;
		config.fullscreen = false;
		config.title = "Hexagons! " + Main.version;
		config.foregroundFPS = 120;
		config.addIcon("assets/hexlogo.png", FileType.Internal);
		config.samples = Settings.instance.msaa;
		config.vSyncEnabled = Settings.instance.vSync;
		app = new LwjglApplication(instance, config);

	}

	public AnimationManager getAnimationManager() {
		return animationManager;
	}

}
