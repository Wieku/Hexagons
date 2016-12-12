package xyz.hexagons.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.security.SecureRandom;

public class Settings implements Serializable {
    public static final Settings instance;
    public String siteRedir = "https://hexagons.xyz";
    public String selfUrl = "https://rankserv.hexagons.xyz";

    public byte[] signSecret = new byte[32];

    static {
        Settings s = null;
        try {
            Gson gson = new GsonBuilder().create();
            File file = new File("settings.json");
            if(!file.exists()) {
                s = new Settings();
                new SecureRandom().nextBytes(s.signSecret);
            } else
                s = gson.fromJson(new FileReader(file), Settings.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        instance = s;
    }
}