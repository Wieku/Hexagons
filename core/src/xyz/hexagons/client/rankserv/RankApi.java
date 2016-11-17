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

public class RankApi {
    public static final RankApi instance = new RankApi();

    private RankApi() {}

    public LeaderBoard getScoreForMap(Map map, int count) {
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet req = new HttpGet(Settings.instance.ranking.server + "/v0/leaders?uuid="
                + map.info.uuid + "&count=" + String.valueOf(count) + "&nick=" + Settings.instance.ranking.nickname);

        try {
            HttpResponse response = httpclient.execute(req);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream in = entity.getContent();

                LeaderBoard lb = new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), LeaderBoard.class);
                in.close();
                return lb;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class LeaderBoard implements Serializable {
        public String state = "";
        public List<Leader> list;
        public String ownBest = "";
        public String mapPlayers = "";
        public String ownPlayCount = "";
    }

    public static class Leader implements Serializable {
        public String nick = "";
        public String score = "";
    }
}
