package xyz.hexagons.client.menu.settings;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import xyz.hexagons.client.Instance;

import java.io.File;
import java.io.IOException;

public class SettingsManager {
    public static void saveSettings() {
        if(Settings.instance != null) {
            System.out.println("Saving settings");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try {
                Files.write(gson.toJson(Settings.instance), new File(Instance.storageRoot, "settings.json"), Charsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
