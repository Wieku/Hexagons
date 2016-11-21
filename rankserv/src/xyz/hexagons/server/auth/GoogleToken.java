package xyz.hexagons.server.auth;

import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class GoogleToken extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID challenge = UUID.randomUUID();
        GoogleAuth.tokenChallenges.put(challenge, null, 5000L);

        resp.setContentType("application/json");
        JsonObject m = new JsonObject();
        m.addProperty("challenge", challenge.toString());
        resp.getWriter().print(m.toString());
    }
}
