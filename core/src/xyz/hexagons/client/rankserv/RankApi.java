package xyz.hexagons.client.rankserv;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.javatuples.Pair;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.map.Map;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.utils.REST;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class RankApi {
    public static final RankApi instance = new RankApi();
    private volatile RankedGameTokenStatus tokenStatus = RankedGameTokenStatus.INIT;
    private volatile long gameId = 0;
    private String rankedToken = null;

    private RankApi() {}

    public void setupRankedMap(Map map) {
        if(map.rankedMap != null) {
            final long gid = ++gameId;
            gameId = gid;
            tokenStatus = RankedGameTokenStatus.GETTING_CHALLENGE;
            prepareRankedGame(gid, map);
        } else {
            tokenStatus = RankedGameTokenStatus.UNRANKED;
        }
    }

    public void sendScore(Map map, float score) {
        Instance.executor.execute(() -> {
            try {
                System.out.println("Sending score");

                HttpClient httpclient = HttpClients.createDefault();
                HttpPost httppost = new HttpPost(Settings.instance.ranking.server + "/v1/game");

                List<NameValuePair> params = new ArrayList<>(3);
                params.add(new BasicNameValuePair("token", Instance.currentAccount.authToken().toString()));
                params.add(new BasicNameValuePair("score", String.valueOf((long) score)));
                params.add(new BasicNameValuePair("mapid", map.info.uuid));
                if(tokenStatus == RankedGameTokenStatus.RANKED) {
                    params.add(new BasicNameValuePair("rankedToken", rankedToken));
                    tokenStatus = RankedGameTokenStatus.INIT;
                    rankedToken = null;
                }
                httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    InputStream in = entity.getContent();
                    System.out.println(CharStreams.toString(new InputStreamReader(in, StandardCharsets.UTF_8)));
                    in.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

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

    private void prepareRankedGame(final long gid, Map map) {
        Instance.executor.execute(() -> {
            try {
                if(gid != gameId) return;
                Pair<RankedChallenge, String> challenge = REST.getJWS(getRankChallengeUri(map), RankedChallenge.class);

                if(gid != gameId) return;
                if (challenge == null) {
                    System.out.println("Error getting challenge!");
                    tokenStatus = RankedGameTokenStatus.UNRANKED;
                    return;
                }
                tokenStatus = RankedGameTokenStatus.GETTING_TOKEN;

                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.update(map.rankedMap.hash.getBytes());
                digest.update(String.valueOf(challenge.getValue0().id).getBytes());
                digest.update(Base64.getDecoder().decode(challenge.getValue0().salt));
                String salted = String.valueOf(Hex.encodeHex(digest.digest(), true));

                Pair<RankedToken, String> token = REST.getJWS(getRankTokenUri(challenge.getValue1(), salted, map), RankedToken.class);
                if(gid != gameId) return;
                if(token == null) {
                    System.out.println("Failed to get ranked token");
                    tokenStatus = RankedGameTokenStatus.UNRANKED;
                    return;
                }
                tokenStatus = RankedGameTokenStatus.RANKED;
                rankedToken = token.getValue1();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private String getRankChallengeUri(Map map) {
        return Settings.instance.ranking.server + "/v1/game/ranked/challenge?token=" + Instance.currentAccount.authToken().toString()
                + "&map=" + map.info.uuid;
    }

    private String getRankTokenUri(String challenge, String hash, Map map) {
        return Settings.instance.ranking.server + "/v1/game/ranked/token?challenge=" + challenge
                + "&hash=" + hash + "&permitId=" + map.rankedMap.permit;
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

    private static class RankedChallenge {
        public String salt;
        public int id;
        public long time;
        public String map;
    }

    private static class RankedToken {
        public boolean ranked;
        public long at;
        public String permId;
    }

    private enum RankedGameTokenStatus {
        INIT,
        GETTING_CHALLENGE,
        GETTING_TOKEN,
        RANKED,
        UNRANKED
    }
}
