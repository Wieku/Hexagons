package xyz.hexagons.launcher.core;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import xyz.hexagons.launcher.LauncherUi;
import xyz.hexagons.launcher.core.models.UpdateInfo;
import xyz.hexagons.launcher.core.models.VersionInfo;
import xyz.hexagons.launcher.util.HTTPUtil;

import java.io.*;
import java.util.Set;
import java.util.StringJoiner;

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
		} else {
			current = VersionInfo.defaultInfo;
		}
	}

	public String getBranch() {
		return current.branch;
	}

	public String getVersion() {
		return current.version;
	}

	public void launch() throws Exception {
		UpdateInfo updateInfo = HTTPUtil.getJson(Settings.updateBase + "/verinfo-" + current.branch + ".json", UpdateInfo.class);
		ui.reportStatus("LV: " + String.valueOf(current.version));
		ui.reportStatus("RV: " + String.valueOf(updateInfo.version));
		if(!String.valueOf(current.version).equals(updateInfo.version)) {
			ui.reportStatus("Update Required!");
			Settings.objectCdnBase = updateInfo.updateBaseUrl;
			VersionInfo update = HTTPUtil.getJson(Settings.objectCdnBase + "/" + updateInfo.updateId, VersionInfo.class);
			try (Writer writer = new FileWriter(new File(data, "current.json"))) {
				Gson gson = new Gson();
				gson.toJson(update, writer);
			}


		}
	}

	private File getDataDirectory() throws LauchException {
		File dataDir = new File(".hexagons");
		if(dataDir.isFile())
			throw new LauchException(".hexagons is a file!");

		if(!dataDir.exists()) {
			if(!dataDir.mkdirs()) {
				throw new LauchException("Couldn't create '.hexagons' directory!");
			}
		}

		return dataDir;
	}
}
