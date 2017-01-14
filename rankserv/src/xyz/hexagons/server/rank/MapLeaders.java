package xyz.hexagons.server.rank;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nimbusds.jose.JWSObject;
import xyz.hexagons.server.Launcher;
import xyz.hexagons.server.auth.AccountUtils;
import xyz.hexagons.server.auth.RuntimeSecrets;
import xyz.hexagons.server.util.SqlUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Holder;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MapLeaders extends HttpServlet {
    private static final String qLeaders = SqlUtil.getQuery("rank/leaders");
    private static final String qMapPlayerCount = SqlUtil.getQuery("rank/mapPlayerCount");
    private static final String qPlayerBest = SqlUtil.getQuery("rank/playerBest");
    private static final String qPlayerPlayCount = SqlUtil.getQuery("rank/playerPlayCount");
    private static final String qPlayerRank = SqlUtil.getQuery("rank/playerRank");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            System.out.println("Get score report");

            String mapId = req.getParameter("uuid");
            long count = Long.valueOf(req.getParameter("count"));

			String token = req.getParameter("token");
            Holder<AccountUtils.SessionAccount> account = new Holder<>();

			if (token != null) {
                JWSObject t = JWSObject.parse(token);
                if(!RuntimeSecrets.check(t)) {
                    resp.setStatus(500);
                    return;
                }

                account.value = new Gson().fromJson(t.getPayload().toString(), AccountUtils.SessionAccount.class);
			}


            JsonObject sres = Launcher.withConnection(connection -> {
                JsonObject result = new JsonObject();

                {
                    PreparedStatement statement = connection.prepareStatement(qLeaders);
                    statement.setString(1, mapId);
                    statement.setLong(2, count);
                    ResultSet rs = statement.executeQuery();

                    JsonArray res = new JsonArray();
                    while (rs.next()) {
                        JsonObject r = new JsonObject();
                        r.addProperty("nick", rs.getString("nick"));
                        r.addProperty("score", rs.getLong("sc"));
                        res.add(r);
                    }
                    result.add("list", res);
                }

                if (account.value != null) {
                    PreparedStatement statement = connection.prepareStatement(qPlayerBest);
                    statement.setLong(1, account.value.id);
                    statement.setString(2, mapId);
                    ResultSet rs = statement.executeQuery();
                    if(rs.next()) {
                        result.addProperty("ownBest", rs.getLong("sc"));
                    }
                }

                {
                    PreparedStatement statement = connection.prepareStatement(qMapPlayerCount);
                    statement.setString(1, mapId);
                    ResultSet rs = statement.executeQuery();
                    if(rs.next()) {
                        result.addProperty("mapPlayers", rs.getLong("players"));
                    }
                }

                if (account.value != null) {
                    PreparedStatement statement = connection.prepareStatement(qPlayerPlayCount);
                    statement.setLong(1, account.value.id);
                    ResultSet rs = statement.executeQuery();
                    if(rs.next()) {
                        result.addProperty("ownPlayCount", rs.getLong("playCount"));
                    }
                }

                if (account.value != null) {
                    PreparedStatement statement = connection.prepareStatement(qPlayerRank);
                    statement.setString(1, mapId);
                    statement.setLong(2, account.value.id);
                    statement.setString(3, mapId);
                    ResultSet rs = statement.executeQuery();

                    if(rs.next()) {
                        result.addProperty("position", rs.getLong("rank") + 1);
                    }
                }

                return result;
            });

			sres.addProperty("state", "OK");
            resp.getWriter().print(sres.toString());
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().print("{\"state\": \"ERROR\"}");
        }

    }
}

