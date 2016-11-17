package xyz.hexagons.server.servlets;

import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Motd extends HttpServlet {
    public static String motd = "Welcome to Hexagons!";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        JsonObject m = new JsonObject();
        m.addProperty("text", motd);
        resp.getWriter().print(m.toString());
    }
}
