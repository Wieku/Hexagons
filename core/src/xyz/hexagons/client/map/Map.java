package xyz.hexagons.client.map;

import xyz.hexagons.client.api.MapScript;
import xyz.hexagons.client.rankserv.RankApi;

import java.io.File;
import java.util.zip.ZipFile;

/**
 * @author Sebastian Krajewski on 28.03.15.
 */
public class Map {

	public final MapScript script;
	public final MapJson info;
	public final ZipFile file;
	public final File dataFile;
	public final RankedMap rankedMap;

	public Map(MapScript script, MapJson info, ZipFile file, File dataFile, RankedMap rankedMap) {
		this.script = script;
		this.info = info;
		this.file = file;
		this.dataFile = dataFile;
		this.rankedMap = rankedMap;
	}

	public static class RankedMap {
		public String hash;
		public int permit;
	}
}
