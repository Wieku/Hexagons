package me.wieku.hexagons.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;

/**
 * @author Sebastian Krajewski (LudziE12)
 */
public class Hocon {

	private com.typesafe.config.Config config;
	private ConfigRenderOptions options;
	private File file;
	private ArrayList<Hocon> configs = new ArrayList<Hocon>();

	private Hocon(String string) {
		init();
		config = ConfigFactory.parseString(string);
	}

	private Hocon(InputStream stream) {
		init();
		config = ConfigFactory.parseReader(new InputStreamReader(stream, Charsets.UTF_8));
	}

	private Hocon(File file, boolean isDirectory) {
		this();

		if (file.isDirectory()) {
			for (File file1 : file.listFiles()) {
				try {
					if ("text/plain".equals(Files.probeContentType(file1.toPath()))) {
						mergeWith(load(file1));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {

			this.file = file;

			if ("dat".equals(com.google.common.io.Files.getFileExtension(file.getName()))) {
				try {
					DataInputStream reader = new DataInputStream(new FileInputStream(file));
					String g = reader.readUTF();
					reader.close();
					config = ConfigFactory.parseString(g);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					config = ConfigFactory.parseReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
				} catch (FileNotFoundException e) {
					config = ConfigFactory.empty();
					e.printStackTrace();
				}
			}

		}
	}

	public Hocon() {
		init();
		config = ConfigFactory.empty();
	}

	public Hocon(com.typesafe.config.Config config) {
		init();
		this.config = config;
	}

	public static Hocon load(File file) throws FileNotFoundException {
		return new Hocon(file, file.isDirectory());
	}

	public static Hocon load(InputStream stream) {
		return new Hocon(stream);
	}

	public static Hocon load(String conf) {
		return new Hocon(conf);
	}

	public static Hocon clear() {
		return new Hocon();
	}

	private void init() {
		options = ConfigRenderOptions.defaults().setOriginComments(false).setJson(false).setFormatted(true);
	}

	public Object getObject(String path) {
		return config.getAnyRef(path);
	}

	public String getString(String path) {
		if (!config.hasPath(path))
			return null;
		return config.getString(path);
	}

	private Number getNumber(String path) {
		return config.getNumber(path);
	}

	// FIXME: Don't know if this is correct

	@SuppressWarnings("null")
	public int getInteger(String path) {
		if (!config.hasPath(path))
			return (Integer) null;
		return config.getInt(path);
	}

	@SuppressWarnings("null")
	public float getFloat(String path) {
		if (!config.hasPath(path))
			return (Float) null;
		return (float) config.getDouble(path);
	}

	@SuppressWarnings("null")
	public double getDouble(String path) {
		if (!config.hasPath(path))
			return (Double) null;
		return config.getDouble(path);
	}

	@SuppressWarnings("null")
	public short getShort(String path) {
		if (!config.hasPath(path))
			return (Short) null;
		return (short) getNumber(path);
	}

	@SuppressWarnings("null")
	public long getLong(String path) {
		if (!config.hasPath(path))
			return (Long) null;
		return config.getLong(path);
	}

	@SuppressWarnings("null")
	public byte getByte(String path) {
		if (!config.hasPath(path))
			return (Byte) null;
		return (byte) getNumber(path);
	}

	public List<String> getStringArray(String path) {
		if (!config.hasPath(path))
			return null;
		return config.getStringList(path);
	}

	public List<Integer> getIntArray(String path){
		if (!config.hasPath(path))
			return null;
		return config.getIntList(path);
	}

	public List<Double> getDoubleArray(String path){
		if (!config.hasPath(path))
			return null;
		return config.getDoubleList(path);
	}

	public List<Float> getFloatArray(String path){
		List<Float> list = new ArrayList<>();
		if (!config.hasPath(path))
			return null;
		config.getDoubleList(path).forEach(e->list.add(e.floatValue()));
		return list;
	}

	public HashMap<String, Hocon> getHoconArray(String path){
		HashMap<String, Hocon> map = new HashMap<>();
		for(Map.Entry<String, ConfigValue> entry: getChild(path).config.entrySet()){
			map.put(entry.getKey(), new Hocon(((ConfigObject)entry.getValue()).toConfig()));
		}
		return map;
	}

	public void set(String path, Object value) {
		if (value == null)
			config = config.withoutPath(path);
		else
			config = config.withValue(path, ConfigValueFactory.fromAnyRef(value));
	}

	public void saveAll() throws IOException {
		for (Hocon c : configs) {
			c.save();
		}
	}

	public void save() throws IOException {
		if (file != null) {
			save(file);
		}
	}

	public void save(File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
		if (this.file == null) this.file = file;

		if ("dat".equals(com.google.common.io.Files.getFileExtension(file.getName()))) {
			try {
				DataOutputStream writer = new DataOutputStream(new FileOutputStream(file));
				writer.writeUTF(getCompiled());
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			com.google.common.io.Files.write(getCompiled().getBytes(Charsets.UTF_8), file);
		}
	}

	public com.typesafe.config.Config getUnwrapped() {
		return config;
	}

	public String getCompiled() {
		return config.root().render(options);
	}

	public Hocon getChild(String path) {
		return new Hocon(config.atPath(path));
	}

	public void mergeWith(Hocon config) {
		for (Hocon conf : config.configs) {
			configs.add(conf);
		}

		this.config = this.config.withFallback(config.getUnwrapped());
	}

	public boolean contains(String nodeName) {
		return config.hasPath(nodeName);
	}

	public boolean getBoolean(String path) {
		if (!config.hasPath(path))
			return false;
		return config.getBoolean(path);
	}
}
