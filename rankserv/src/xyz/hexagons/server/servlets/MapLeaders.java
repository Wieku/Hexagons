package xyz.hexagons.server.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xyz.hexagons.server.Launcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MapLeaders extends HttpServlet {
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
                PreparedStatement statement = Launcher.connection.prepareStatement("SELECT `nick`, MAX(`score`) as `sc` FROM `games` WHERE `map_id`=? GROUP BY `nick` ORDER BY `sc` DESC LIMIT ?");
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
                PreparedStatement statement = Launcher.connection.prepareStatement("SELECT MAX(`score`) AS `sc` FROM `games` WHERE `nick`=? AND `map_id`=?");
                statement.setString(1, nick);
                statement.setString(2, mapId);
                ResultSet rs = statement.executeQuery();
                if(rs.next()) {
                    result.addProperty("ownBest", rs.getLong("sc"));
                }
            }

            {
                PreparedStatement statement = Launcher.connection.prepareStatement("SELECT COUNT(DISTINCT `nick`) as `players` FROM `games` WHERE `map_id`=?");
                statement.setString(1, mapId);
                ResultSet rs = statement.executeQuery();
                if(rs.next()) {
                    result.addProperty("mapPlayers", rs.getLong("players"));
                }
            }

            {
                PreparedStatement statement = Launcher.connection.prepareStatement("SELECT COUNT(`nick`) as `playCount` FROM `games` WHERE `nick`=?");
                statement.setString(1, nick);
                ResultSet rs = statement.executeQuery();
                if(rs.next()) {
                    result.addProperty("ownPlayCount", rs.getLong("playCount"));
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

