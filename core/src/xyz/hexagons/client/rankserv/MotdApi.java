package xyz.hexagons.client.rankserv;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import xyz.hexagons.client.map.Map;
import xyz.hexagons.client.menu.settings.Settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MotdApi {
    public static final MotdApi instance = new MotdApi();

    private MotdApi() {}

    public Motd getMotd() {
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet req = new HttpGet(Settings.instance.ranking.server + "/motd");

        try {
            HttpResponse response = httpclient.execute(req);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream in = entity.getContent();

                Motd lb = new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), Motd.class);
                in.close();
                return lb;
            }
        } catch (IOException e) {
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
