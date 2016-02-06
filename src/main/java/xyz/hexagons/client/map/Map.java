package xyz.hexagons.client.map;

import xyz.hexagons.client.api.MapScript;

import java.io.File;
import java.util.zip.ZipFile;

/**
 * @author Sebastian Krajewski on 28.03.15.
 */
public class Map {

	public MapScript script;
	public MapJson info;
	public ZipFile file;
	public File dataFile;

	public Map(MapScript script, MapJson info, ZipFile file, File dataFile) {
		this.script = script;
		this.info = info;
		this.file = file;
		this.dataFile = dataFile;
	}
}
