package xyz.hexagons.client.map;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.JsePlatform;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.api.MapScript;
import xyz.hexagons.client.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

/**
 * @author Sebastian Krajewski on 28.03.15.
 */
public class MapLoader {
	private static String MAPS_PATH = "Maps" + File.separator;
	private static String DATA_PATH = "Data" + File.separator;
	//private static Logger log = LoggerFactory.getLogger(MapLoader.class);

	public static ArrayList<Map> load() {
		new File(Instance.storageRoot, MAPS_PATH).mkdirs();
		new File(Instance.storageRoot, DATA_PATH).mkdirs();

		System.out.println("Loading maps");
		ArrayList<Map> maps = new ArrayList<>();

		File dir = new File(Instance.storageRoot, MAPS_PATH);

		File files[] = dir.listFiles();

		for (File file : files) {
			if (file.isFile() && Files.getFileExtension(file.getAbsolutePath()).equals("jar")) {
				JarFile jar;
				try {
					jar = new JarFile(file);
				} catch (IOException e) {
					System.err.println("Cannot load " + file.getName());
					e.printStackTrace();
					continue;
				}

				if (jar.getEntry("map.json") == null) {
					System.err.println("File: " + file.getName() + " doesn't contain map.json!");
					closeJar(jar);
					continue;
				}

				MapJson m;
				try {
					Gson gson = new GsonBuilder().create();
					m = gson.fromJson(new InputStreamReader(jar.getInputStream(jar.getEntry("map.json"))), MapJson.class);
				} catch (IOException e) {
					System.err.println("File map.json in mod " + file.getName() + " has wrong syntax!");
					e.printStackTrace();
					closeJar(jar);
					continue;
				}

				if (jar.getEntry("main.lua") != null) {
					try {
						InputStreamReader r = new InputStreamReader(jar.getInputStream(jar.getEntry("main.lua")));

						Varargs pe = Instance.luaGlobals.get("prepareEnv").invoke(LuaString.valueOf(m.name));

						LuaTable callbacks = pe.checktable(2);

						LuaValue chunk = Instance.luaGlobals.load(r, "=Map/" + file.getName() + "/main.lua", pe.checktable(1));
						chunk.call();

						callbacks.get("init").call();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				ClassLoader loader;
				try {
					loader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()}, MapLoader.class.getClassLoader());
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
					closeJar(jar);
					continue;
				}

				Class<?> toLoad;
				try {
					toLoad = loader.loadClass(m.className);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
					closeJar(jar);
					continue;
				}

				List<Class<?>> interfaces = Arrays.asList(toLoad.getInterfaces());
				//System.err.println("fghjty " + interfaces);
				if (!interfaces.contains(MapScript.class)) {
					System.err.println("Script of " + m.name + "(" + file.getName() + ") Doesn't implement 'MapScript' interface!");
					closeJar(jar);
					continue;
				}

				try {
					String sh1 = Utils.getFileHash(file);
					File data = new File(DATA_PATH + sh1 + ".hxd");
					//if(!data.exists()) data.createNewFile();
					maps.add(new Map((MapScript) toLoad.newInstance(), m, jar, data));

				} catch (Exception e1) {
					System.err.println("Script of " + m.name + "(" + file.getName() + ") couldn't contain custom constructor!");
					e1.printStackTrace();
					closeJar(jar);
					continue;
				}

				System.out.println("Map " + m.name + " Has been loaded!");

			}
		}
		System.out.println("Loaded " + maps.size() + " maps");
		return maps;
	}

	public static void closeJar(ZipFile jar){
		try {
			jar.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
