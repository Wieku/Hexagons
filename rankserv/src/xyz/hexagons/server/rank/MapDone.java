package xyz.hexagons.server.rank;

import xyz.hexagons.server.Launcher;
import xyz.hexagons.server.util.SqlUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.time.Instant;

public class MapDone extends HttpServlet {
    public static final String qInstertGame = SqlUtil.getQuery("rank/insertGame");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            String nick = req.getParameter("nick");
            String mapid = req.getParameter("mapid");
            long score = Long.valueOf(req.getParameter("score"));
            System.out.println("New score report "+mapid+"/"+nick+": "+score);

            PreparedStatement statement = Launcher.connection.prepareStatement(qInstertGame);
            statement.setString(1, mapid);
            statement.setLong(2, score);
            statement.setString(3, nick);
            statement.setLong(4, Instant.now().getEpochSecond());
            statement.executeUpdate();

            resp.getWriter().print("{\"state\": \"OK\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().print("{\"state\": \"ERROR\"}");
        }


    }
}
