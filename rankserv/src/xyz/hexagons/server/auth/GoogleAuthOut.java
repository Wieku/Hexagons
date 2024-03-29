package xyz.hexagons.server.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import org.eclipse.jetty.continuation.Continuation;

import xyz.hexagons.server.Settings;
import xyz.hexagons.server.util.Config;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class GoogleAuthOut extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullState = req.getParameter("state");

        String googleUserId = getGoogleUserId(req.getParameter("code"));
        if(googleUserId == null)
        	throw new ServletException("Invlid request");

		AccountUtils.Account account = AccountUtils.getAccount(googleUserId, AuthType.GOOGLE.type);

        if(fullState.startsWith("g/"))
            AccountUtils.notifyGame(UUID.fromString(fullState.substring(2)), account);

		if(!AccountUtils.loginRedirect(account, resp, fullState))
        	resp.sendRedirect(Settings.instance.siteRedir + "/error/rs/googleAuth/out/1");
    }

    private String getGoogleUserId(String code) {
        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(),
                    Config.get("google_oauth_id"), Config.get("google_oauth_sec"), code, Settings.instance.selfUrl + "/auth/google/out").execute();

            GoogleCredential credential = new GoogleCredential().setAccessToken(tokenResponse.getAccessToken());

            Oauth2 oauth2 = new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName("Hexagons").build();

            return oauth2.userinfo().get().execute().getId();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
