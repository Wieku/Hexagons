package xyz.hexagons.launcher.core;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import xyz.hexagons.launcher.LauncherUi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class LauncherLogic {
	private LauncherUi ui;
	private File data;
	private VersionInfo current;

	public LauncherLogic(LauncherUi ui) throws LauchException, FileNotFoundException {
		this.ui = ui;
		data = getDataDirectory();

		File currentFile = new File(data, "current.json");
		if(currentFile.exists()) {
			Gson gson = new Gson();
			current = gson.fromJson(new JsonReader(new FileReader(currentFile)), VersionInfo.class);
		}
	}

	private File getDataDirectory() throws LauchException {
		File dataDir = new File(".hexagons");
		if(dataDir.isFile())
			throw new LauchException(".hexagons is a file!");

		if(!dataDir.exists()) {
			if(dataDir.mkdirs()) {
				throw new LauchException("Couldn't create '.hexagons' directory!");
			}
		}

		return dataDir;
	}
}
