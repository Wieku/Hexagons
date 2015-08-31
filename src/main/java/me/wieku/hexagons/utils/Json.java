package me.wieku.hexagons.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class Json {

	private Gson gson;
	private JsonObject json;
	private static Logger log = LoggerFactory.getLogger(Json.class);

	private Json(String json) throws JsonSyntaxException, JsonIOException{
		init();
		log.info("Loading json");
		this.json = gson.fromJson(json, JsonObject.class);
		log.info("Json loaded");
	}

	private Json(InputStream stream){
		init();
		log.info("Loading json");
		try {
			json = gson.fromJson(new BufferedReader(new InputStreamReader(stream,"UTF8")), JsonObject.class);
			log.info("Json loaded");
		} catch (Exception e) {
			log.error("Json loading error");
			e.printStackTrace();
			json = new JsonObject();
		}

	}

	public Json() {
		init();
		json = new JsonObject();
	}

	public static Json load(File file) throws JsonSyntaxException, JsonIOException, FileNotFoundException{
		return new Json(new FileInputStream(file));
	}

	public static Json load(InputStream stream) throws JsonSyntaxException, JsonIOException{
		return new Json(stream);
	}

	public static Json load(String json) throws JsonSyntaxException, JsonIOException{
		return new Json(json);
	}

	public static Json clear(){
		return new Json();
	}

	private void init(){
		gson = new GsonBuilder().setPrettyPrinting().create();
	}

	private JsonElement getElement(String path){

		JsonElement el = gson.toJsonTree(json);

		if(path.contains(".")){
			String[] pathSplit = path.split("\\.");
			for(int i=0;i<pathSplit.length;i++){
				if(el != null){
					if(el.isJsonObject()){
						el = el.getAsJsonObject().get(pathSplit[i]);
					} else {
						return null;
					}
				} else {
					return null;
				}
			}
		} else {
			el = el.getAsJsonObject().get(path);
		}

		return el;
	}

	public Object getObject(String path){
		JsonElement el = getElement(path);
		return (el==null?null:(Object)el);
	}

	public String getString(String path){
		JsonElement el = getElement(path);
		return (el==null?null:el.getAsString());
	}

	public int getInteger(String path){
		JsonElement el = getElement(path);
		return (el==null?null:el.getAsInt());
	}

	public float getFloat(String path){
		JsonElement el = getElement(path);
		return (el==null?null:el.getAsFloat());
	}
	public double getDouble(String path){
		JsonElement el = getElement(path);
		return (el==null?null:el.getAsDouble());
	}

	public short getShort(String path){
		JsonElement el = getElement(path);
		return (el==null?null:el.getAsShort());
	}

	public long getLong(String path){
		JsonElement el = getElement(path);
		return (el==null?null:el.getAsLong());
	}

	public byte getByte(String path){
		JsonElement el = getElement(path);
		return (el==null?null:el.getAsByte());
	}

	public char getCharacter(String path){
		JsonElement el = getElement(path);
		return (el==null?null:el.getAsCharacter());
	}

	public JsonArray getArray(String path){
		JsonElement el = getElement(path);
		return (el==null?null:el.getAsJsonArray());
	}

	public void set(String path, Object value){
		if(!path.contains(".")){
			json.add(path, gson.toJsonTree(value));
		} else {
			JsonObject el = json;
			String[] pathSplit = path.split("\\.");
			for(int i=0;i<pathSplit.length;i++){
				if(i == pathSplit.length-1){
					el.add(pathSplit[i], gson.toJsonTree(value));
				} else {
					if(!el.has(pathSplit[i]) || !el.get(pathSplit[i]).isJsonObject()){
						JsonObject el1 = new JsonObject();
						el.add(pathSplit[i],el1);
						el = el1;
					} else {
						el = el.get(pathSplit[i]).getAsJsonObject();
					}
				}
			}
		}
	}

	public void save(File file) throws IOException{
		if(!file.exists()){
			file.createNewFile();
		}

		PrintWriter writer = new PrintWriter(file);
		writer.println(getCompiled());
		writer.flush();
		writer.close();

	}

	public JsonObject getJsonRoot(){
		return json;
	}

	public String getCompiled(){
		return gson.toJson(json);
	}

	public boolean contains(String nodeName) {
		return getElement(nodeName) != null;
	}

}
