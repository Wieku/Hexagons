package xyz.hexagons.client.map;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.luaj.vm2.*;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.utils.Holder;
import xyz.hexagons.client.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
		new File(Instance.storageRoot, MAPS_PATH).mkdirs();
		new File(Instance.storageRoot, DATA_PATH).mkdirs();

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
			if (file.isFile() && (ext.equals("jar") || ext.equals("zip"))) {
				ZipFile zip; //TODO: Directory based(unzipped) maps
				try {
					zip = new ZipFile(file);
				} catch (IOException e) {
					System.err.println("Cannot load " + file.getName());
					e.printStackTrace();
					continue;
				}

				if (zip.getEntry("map.json") == null) {
					System.err.println("File: " + file.getName() + " doesn't contain map.json!");
					closeMap(zip);
					continue;
				}

				MapJson m;
				try {
					Gson gson = new GsonBuilder().create();
					m = gson.fromJson(new InputStreamReader(zip.getInputStream(zip.getEntry("map.json"))), MapJson.class);
				} catch (IOException e) {
					System.err.println("File map.json in map " + file.getName() + " has wrong syntax!");
					e.printStackTrace();
					closeMap(zip);
					continue;
				}

				LuaTable callbacks = null;
				if (zip.getEntry("main.lua") != null) {
					try {
						InputStreamReader r = new InputStreamReader(zip.getInputStream(zip.getEntry("main.lua")));

						Varargs pe = Instance.luaGlobals.get("prepareEnv")
								.invoke(new LuaValue[]{ LuaString.valueOf(m.name), new LuaUserdata(zip)});

						callbacks = pe.checktable(2);

						LuaValue chunk = Instance.luaGlobals.load(r, "=Map/" + file.getName() + "/main.lua", pe.checktable(1));
						chunk.call();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.err.println("File main.lua in map " + file.getName() + " doesn't exist!");
					closeMap(zip);
					continue;
				}

				Map.RankedMap rankedInfo = null;
				if(zip.getEntry("ranked.json") != null) {
					try {
						Gson gson = new GsonBuilder().create();
						RankedJson rankedJson = gson.fromJson(new InputStreamReader(zip.getInputStream(zip.getEntry("ranked.json"))), RankedJson.class);
						Holder<Boolean> valid = new Holder<>(true);
						rankedJson.essentialFiles.forEach((f, hash) -> {
							try {
								String fileHex = DigestUtils.sha256Hex(zip.getInputStream(zip.getEntry(f)));
								if(!hash.equals(fileHex)) {
									valid.value = false;
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
						if(!valid.value) {
							System.err.println("Files in map " + file.getName() + " appear to be invalid!");
							closeMap(zip);
							continue;
						}

						rankedInfo = new Map.RankedMap();
						rankedInfo.permit = rankedJson.permissionId;
						rankedInfo.hash = DigestUtils.sha256Hex(zip.getInputStream(zip.getEntry("ranked.json")));

					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				String sh1 = Utils.getFileHash(file);
				File data = new File(DATA_PATH + sh1 + ".hxd");

				maps.add(new Map(new xyz.hexagons.client.engine.lua.LuaMap(callbacks), m, zip, data, rankedInfo));

				System.out.println("Map " + m.name + " Has been loaded!");
			}
		}
		System.out.println("Loaded " + maps.size() + " map(s)");
		return maps;
	}

	public static void closeMap(ZipFile zip){
		try {
			zip.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
