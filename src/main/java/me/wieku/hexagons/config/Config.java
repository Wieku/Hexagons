/*
package me.wieku.hexagons.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValueFactory;

public class Config {

	private com.typesafe.config.Config config;
	private ConfigRenderOptions options;
	private File file;
	private ArrayList<Config> configs = new ArrayList<Config>();
	
	private Config(String string) {
		init();
		config = ConfigFactory.parseString(string);
	}
	
	private Config(InputStream stream){
		init();
		config = ConfigFactory.parseReader(new InputStreamReader(stream,Charsets.UTF_8));
	}
	
	private Config(File file, boolean isDirectory){
		this();
		
		if(file.isDirectory()){
			for(File file1 : file.listFiles()){
				try {
					if(Files.probeContentType(file1.toPath()).equals("text/plain")){
						mergeWith(load(file1));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			
			this.file = file;
			
			try {
				config = ConfigFactory.parseReader(new InputStreamReader(new FileInputStream(file),Charsets.UTF_8));
			} catch (FileNotFoundException e) {
				config = ConfigFactory.empty();
				e.printStackTrace();
			}
			
		}
	}
	
	public Config() {
		init();
		config = ConfigFactory.empty();
	}
	
	public Config(com.typesafe.config.Config config) {
		init();
		this.config = config;
	}
	
	public static Config load(File file) throws FileNotFoundException {
		return new Config(file, file.isDirectory());
	}
	
	public static Config load(InputStream stream) {
		return new Config(stream);
	}
	
	public static Config load(String conf) {
		return new Config(conf);
	}
	
	public static Config clear(){
		return new Config();
	}
	
	private void init(){
		options = ConfigRenderOptions.defaults();//.setJson(false);
	}
	
	public Object getObject(String path){
		return config.getObject(path);
	}
	
	public String getString(String path){
		return config.getString(path);
	}
	
	private Number getNumber(String path){
		return config.getNumber(path);
	}
	
	public int getInteger(String path){
		return config.getInt(path);
	}
	
	public float getFloat(String path){
		return (float) getNumber(path);
	}
	
	public double getDouble(String path){
		return (double) getNumber(path);
	}
	
	public short getShort(String path){
		return (short) getNumber(path);
	}
	
	public long getLong(String path){
		return (long) getNumber(path);
	}
	
	public byte getByte(String path){
		return (byte) getNumber(path);
	}
	
	public List<String> getStringArray(String path){
		return config.getStringList(path);
	}
	
	public void set(String path, Object value){
		config = config.withValue(path, ConfigValueFactory.fromAnyRef(value));
	}
	
	public void saveAll() throws IOException{
		for(Config config : configs){
			config.save();
		}
	}
	
	public void save() throws IOException{
		if(file != null){
			save(file);
		}
	}
	
	
	public void save(File file) throws IOException{
		if(!file.exists()){
			file.createNewFile();
		}

		com.google.common.io.Files.write(getCompiled().getBytes(Charsets.UTF_8),file);
	}
	
	public com.typesafe.config.Config getUnwrapped(){
		return config;
	}
	
	public String getCompiled(){
		return config.root().render(options);
	}
	
	public Config getChild(String path){
		return new Config(config.atPath(path));
	}
	
	public void mergeWith(Config config){
		for(Config conf : config.configs){
			configs.add(conf);
		}
		
		this.config = this.config.withFallback(config.getUnwrapped());
	}
	
	public boolean contains(String nodeName) {
		return config.hasPath(nodeName);
	}
	
}
*/
