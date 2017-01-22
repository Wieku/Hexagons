package xyz.hexagons.client.rankserv;

import com.google.gson.Gson;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import xyz.hexagons.client.menu.settings.Settings;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.StandardCharsets;

public class MotdApi {
    public static final MotdApi instance = new MotdApi();

    private MotdApi() {}

    public Motd getMotd() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(Settings.instance.ranking.server + "/motd").openConnection();
            InputStream in = conn.getInputStream();

            if (conn.getResponseCode() == 200) {
                Motd lb = new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), Motd.class);
                in.close();
                return lb;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Motd("RankServ is down");
    }

    public static class Motd implements Serializable {
        public String text = "";

        public String getText() {
            return text != null ? text : "MOTD Error";
        }

        protected Motd(String text) {
            this.text = text;
        }
    }
}
