package xyz.hexagons.server.servlets;

import xyz.hexagons.server.Launcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class MapDone extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
        String nick = req.getParameter("nick");
        String mapid = req.getParameter("mapid");
        long score = Long.valueOf(req.getParameter("score"));


            PreparedStatement statement = Launcher.connection.prepareStatement("INSERT INTO `games` (`map_id`, `score`, `nick`) VALUES (?, ?, ?)");
            statement.setString(1, mapid);
            statement.setLong(2, score);
            statement.setString(3, nick);
            statement.executeUpdate();

            resp.getWriter().print("{\"state\": \"OK\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().print("{\"state\": \"ERROR\"}");
        }


    }
}
