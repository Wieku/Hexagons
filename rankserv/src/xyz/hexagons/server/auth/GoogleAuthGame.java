package xyz.hexagons.server.auth;

import org.eclipse.jetty.continuation.Continuation;
import xyz.hexagons.server.Settings;
import xyz.hexagons.server.util.Config;
import xyz.hexagons.server.util.TimeoutMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class GoogleAuthGame extends HttpServlet {
    protected static final TimeoutMap<UUID, Continuation> tokenContinuations = new TimeoutMap<>();
    protected static final TimeoutMap<UUID, String> tokenChallenges = new TimeoutMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(301);
        resp.sendRedirect("https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id="
                + Config.get("google_oauth_id") + "&redirect_uri=" + Settings.instance.selfUrl + "/auth/google/out&scope=profile&state=g/"
                + req.getParameter("challenge"));
    }
}
