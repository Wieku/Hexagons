package xyz.hexagons.server.auth;

import xyz.hexagons.server.Settings;
import xyz.hexagons.server.util.Config;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GoogleAuthSite extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(301);
        resp.sendRedirect("https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id="
                + Config.get("google_oauth_id") + "&redirect_uri=" + Settings.instance.selfUrl + "/auth/google/out&scope=profile&state=s/");
    }
}
