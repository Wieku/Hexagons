package xyz.hexagons.client.rankserv;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.map.Map;
import xyz.hexagons.client.menu.settings.Settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RankApi {
    public static final RankApi instance = new RankApi();

    private RankApi() {}

    public LeaderBoard getScoreForMap(Map map, int count) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(getLeadersUri(map, count)).openConnection();
            InputStream in = conn.getInputStream();

            if (conn.getResponseCode() == 200) {
                LeaderBoard lb = new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), LeaderBoard.class);
                in.close();
                return lb;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PlayerRankInfo getPlayerRankInfo() {
        if(Instance.currentAccount == null) return null;

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(Settings.instance.ranking.server + "/v1/rank?token=" + Instance.currentAccount.authToken()).openConnection();
            InputStream in = conn.getInputStream();

            if (conn.getResponseCode() == 200) {
                PlayerRankInfo info = new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), PlayerRankInfo.class);
                return info;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getLeadersUri(Map map, int count) {
        if (Instance.currentAccount != null) {
            return Settings.instance.ranking.server + "/v1/leaders?uuid="
                    + map.info.uuid + "&count=" + String.valueOf(count) + "&token=" + Instance.currentAccount.authToken().toString();
        } else {
            return Settings.instance.ranking.server + "/v1/leaders?uuid="
                    + map.info.uuid + "&count=" + String.valueOf(count);
        }
    }

    public static class PlayerRankInfo implements Serializable {
        public int rankedScore = 0;
        public int globalRank = 0;
        public int overallScore = 0;
    }

    public static class LeaderBoard implements Serializable {
        public String state = "";
        public List<Leader> list;
        public int ownBest = 0;
        public int mapPlayers = 0;
        public int ownPlayCount = 0;
        public int position = 0;
    }

    public static class Leader implements Serializable {
        public String nick = "";
        public int score = 0;
    }
}
