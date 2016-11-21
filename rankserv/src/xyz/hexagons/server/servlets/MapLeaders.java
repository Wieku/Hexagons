package xyz.hexagons.server.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xyz.hexagons.server.Launcher;
import xyz.hexagons.server.util.SqlUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MapLeaders extends HttpServlet {
    private static final String qLeaders = SqlUtil.getQuery("rank/leaders.sql");
    private static final String qMapPlayerCount = SqlUtil.getQuery("rank/mapPlayerCount.sql");
    private static final String qPlayerBest = SqlUtil.getQuery("rank/playerBest.sql");
    private static final String qPlayerPlayCount = SqlUtil.getQuery("rank/playerPlayCount.sql");
    private static final String qPlayerRank = SqlUtil.getQuery("rank/playerRank.sql");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            System.out.println("Get score report");

            String mapId = req.getParameter("uuid");
            String nick = req.getParameter("nick");
            long count = Long.valueOf(req.getParameter("count"));

            JsonObject result = new JsonObject();

            {
                PreparedStatement statement = Launcher.connection.prepareStatement(qLeaders);
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

            {
                PreparedStatement statement = Launcher.connection.prepareStatement(qPlayerBest);
                statement.setString(1, nick);
                statement.setString(2, mapId);
                ResultSet rs = statement.executeQuery();
                if(rs.next()) {
                    result.addProperty("ownBest", rs.getLong("sc"));
                }
            }

            {
                PreparedStatement statement = Launcher.connection.prepareStatement(qMapPlayerCount);
                statement.setString(1, mapId);
                ResultSet rs = statement.executeQuery();
                if(rs.next()) {
                    result.addProperty("mapPlayers", rs.getLong("players"));
                }
            }

            {
                PreparedStatement statement = Launcher.connection.prepareStatement(qPlayerPlayCount);
                statement.setString(1, nick);
                ResultSet rs = statement.executeQuery();
                if(rs.next()) {
                    result.addProperty("ownPlayCount", rs.getLong("playCount"));
                }
            }

            {
                PreparedStatement statement = Launcher.connection.prepareStatement(qPlayerRank);
                statement.setString(1, mapId);
                statement.setString(2, nick);
                statement.setString(3, mapId);
                ResultSet rs = statement.executeQuery();

                if(rs.next()) {
                    result.addProperty("position", rs.getLong("rank") + 1);
                }
            }

            result.addProperty("state", "OK");
            resp.getWriter().print(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().print("{\"state\": \"ERROR\"}");
        }

    }
}

