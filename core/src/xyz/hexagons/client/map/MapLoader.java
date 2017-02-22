package xyz.hexagons.client.map;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.luaj.vm2.*;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.engine.lua.LuaMapScript;
import xyz.hexagons.client.utils.Holder;
import xyz.hexagons.client.utils.LFAgonisticInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.ZipFile;

/**
 * @author Sebastian Krajewski on 28.03.15.
 */
public class MapLoader {
	private static String MAPS_PATH = "Maps" + File.separator;
	private static String DATA_PATH = "Data" + File.separator;
	//private static Logger log = LoggerFactory.getLogger(MapLoader.class);

	public static ArrayList<Map> load() {
		if(!new File(Instance.storageRoot, MAPS_PATH).mkdirs())
			System.out.println("Couldn't create Maps directory!");

		if(!new File(Instance.storageRoot, DATA_PATH).mkdirs())
			System.out.println("Couldn't create Data directory!");

		System.out.println("Loading maps");
		ArrayList<Map> maps = new ArrayList<>();

		File dir = new File(Instance.storageRoot, MAPS_PATH);

		File files[] = dir.listFiles();

		if(files == null) {
			System.err.println("Error loading maps: file list is null");
			return maps;
		}

		for (File file : files) {
			String ext = Files.getFileExtension(file.getAbsolutePath());
			if ((file.isFile() && (ext.equals("jar") || ext.equals("zip"))) || file.isDirectory()) {
				MapFile mapFile;
				try {
					if(file.isFile())
						mapFile = new DirectoryMapFile(new ZipFile(file), file);
					else
						mapFile = new DirectoryMapFile(file);

				} catch (IOException e) {
					System.err.println("Cannot load " + file.getName());
					e.printStackTrace();
					continue;
				}

				if (!mapFile.contains("map.json")) {
					System.err.println("File: " + file.getName() + " doesn't contain map.json!");
					closeMap(mapFile);
					continue;
				}

				MapJson m;
				try {
					Gson gson = new GsonBuilder().create();
					m = gson.fromJson(new InputStreamReader(mapFile.getInputStream("map.json")), MapJson.class);
				} catch (IOException e) {
					System.err.println("File map.json in map " + file.getName() + " has wrong syntax!");
					e.printStackTrace();
					closeMap(mapFile);
					continue;
				}

				LuaTable callbacks = null;
				if (mapFile.contains("main.lua")) {
					try {
						InputStreamReader r = new InputStreamReader(mapFile.getInputStream("main.lua"));

						Varargs pe = Instance.luaGlobals.get("prepareEnv")
								.invoke(new LuaValue[]{ LuaString.valueOf(m.name), new LuaUserdata(mapFile)});

						callbacks = pe.checktable(2);

						LuaValue chunk = Instance.luaGlobals.load(r, "=Map/" + file.getName() + "/main.lua", pe.checktable(1));
						chunk.call();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.err.println("File main.lua in map " + file.getName() + " doesn't exist!");
					closeMap(mapFile);
					continue;
				}

				Map.RankedMap rankedInfo = null;
				if(mapFile.contains("ranked.json")) {
					try {
						Gson gson = new GsonBuilder().create();
						RankedJson rankedJson = gson.fromJson(new InputStreamReader(mapFile.getInputStream("ranked.json")), RankedJson.class);
						Holder<Boolean> valid = new Holder<>(true);
						for (java.util.Map.Entry<String, String> entry : rankedJson.essentialFiles.entrySet()) {
							try {
								String fileHex = new String(Hex.encodeHex(DigestUtils.sha256(new LFAgonisticInputStream(mapFile.getInputStream(entry.getKey())))));
								if(!entry.getValue().equals(fileHex)) {
									valid.value = false;
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						if(!valid.value) {
							System.err.println("Files in map " + file.getName() + " appear to be invalid!");
							closeMap(mapFile);
							continue;
						}

						rankedInfo = new Map.RankedMap();
						rankedInfo.permit = rankedJson.permissionId;
						rankedInfo.hash = new String(Hex.encodeHex(DigestUtils.sha256(new LFAgonisticInputStream(mapFile.getInputStream("ranked.json")))));

					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				File data = new File(new File(DATA_PATH), m.uuid + ".hxd");

				maps.add(new Map(new LuaMapScript(callbacks), m, mapFile, data, rankedInfo));

				System.out.println("Map " + m.name + " Has been loaded!");
			}
		}
		System.out.println("Loaded " + maps.size() + " map(s)");
		return maps;
	}

	public static void closeMap(MapFile zip){
		try {
			zip.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
