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
            long count = Long.valueOf(req.getParameter("count"));

            PreparedStatement statement = Launcher.connection.prepareStatement("SELECT `nick`, MAX(`score`) as `sc` FROM `games` WHERE `map_id`=? GROUP BY `nick` ORDER BY `sc` DESC LIMIT ?");
            statement.setString(1, mapId);
            statement.setLong(2, count);
            ResultSet rs = statement.executeQuery();

            JsonArray res = new JsonArray();
            while (rs.next()) {
                JsonObject result = new JsonObject();
                result.addProperty("nick", rs.getString("nick"));
                result.addProperty("score", rs.getString("sc"));
                res.add(result);
            }

            JsonObject result = new JsonObject();
            result.addProperty("state", "OK");
            result.add("list", res);
            resp.getWriter().print(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().print("{\"state\": \"ERROR\"}");
        }

    }
}

