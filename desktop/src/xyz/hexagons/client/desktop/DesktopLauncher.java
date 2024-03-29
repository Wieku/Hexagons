package xyz.hexagons.client.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import xyz.hexagons.client.Hexagons;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.Version;
import xyz.hexagons.client.menu.settings.Settings;

import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class DesktopLauncher {
	static LwjglApplicationConfiguration config;
	static LwjglApplication app;

	public static void main (String[] args) {
		System.out.println("PWD: " + System.getProperty("user.dir"));

		if(args.length > 0 && args[0].equals("noupdate")) Instance.noupdate = true;

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

		Instance.storageRoot = new File(".");
		Instance.accountManager = new DesktopAccountManager();

		config = new LwjglApplicationConfiguration();

		Instance.setForegroundFps = fps -> config.foregroundFPS = fps;
		Instance.audioPlayerFactory = new DesktopAudioPlayerFactory();

		config.width = 1024;
		config.height = 768;
		config.fullscreen = false;
		config.title = "Hexagons! " + Version.version;
		config.foregroundFPS = 120;
		config.addIcon("assets/hexlogo.png", Files.FileType.Internal);

		String msaaSamples = Settings.instance.graphics.msaa;
		if(!msaaSamples.contains("x") && !msaaSamples.equals("OFF")) Settings.instance.graphics.msaa = msaaSamples += "x";
		config.samples = (msaaSamples.equals("OFF")?0:Integer.parseInt(msaaSamples.substring(0, msaaSamples.length()-1)));

		Instance.setSamples = samples -> config.samples = samples;

		config.vSyncEnabled = Settings.instance.graphics.vSync;
		app = new LwjglApplication(new Hexagons(), config);
	}
}
