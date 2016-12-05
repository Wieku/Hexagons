package xyz.hexagons.server.rank;

import com.google.gson.JsonObject;
import xyz.hexagons.server.util.Config;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Motd extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        JsonObject m = new JsonObject();
        m.addProperty("text", Config.get("motd"));
        resp.getWriter().print(m.toString());
    }
}
