package xyz.hexagons.server.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;
import org.eclipse.jetty.continuation.Continuation;
import xyz.hexagons.server.Launcher;
import xyz.hexagons.server.Settings;
import xyz.hexagons.server.util.Config;
import xyz.hexagons.server.util.SqlUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class GoogleAuthOut extends HttpServlet {
    private static final String qAuthExists = SqlUtil.getQuery("user/hasAuth");
    private static final String qInsertAuthUser = SqlUtil.getQuery("user/insertAuth");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID state = UUID.fromString(req.getParameter("state"));
        String code = req.getParameter("code");

        String accountName = "";

        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(),
                Config.get("google_oauth_id"), Config.get("google_oauth_sec"), code, Config.get("url") + "/auth/google/out").execute();

        GoogleCredential credential = new GoogleCredential().setAccessToken(tokenResponse.getAccessToken());

        Oauth2 oauth2 = new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName("Hexagons").build();
        Userinfoplus ui = oauth2.userinfo().get().execute();
        accountName = ui.getName();

        boolean redirAuth = false;

        try {
            PreparedStatement statement = Launcher.connection.prepareStatement(qAuthExists);
            statement.setInt(1, AuthType.GOOGLE.type);
            statement.setString(2, ui.getId());
            ResultSet rs = statement.executeQuery();
            if(rs.next() && rs.getBoolean(1)) {
                accountName = "registered";
            } else {
                PreparedStatement istatement = Launcher.connection.prepareStatement(qInsertAuthUser);
                istatement.setString(1, "u" + ui.getId());
                istatement.setInt(2, AuthType.GOOGLE.type);
                istatement.setString(3, ui.getId());
                istatement.executeUpdate();
                accountName = "u" + ui.getId();
                redirAuth = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if(GoogleAuth.tokenContinuations.containsKey(state)) {
            Continuation c = GoogleAuth.tokenContinuations.get(state);
            GoogleAuth.tokenContinuations.remove(state);

            System.out.println("{\"account\":\"" + accountName +"\"}");
            c.getServletResponse().getWriter().print("{\"account\":\"" + accountName +"\"}");
            c.complete();

        } else if(GoogleAuth.tokenChallenges.containsKey(state)) {
            GoogleAuth.tokenChallenges.remove(state);
            GoogleAuth.tokenChallenges.put(state, accountName, 16000L);
        }

        if(redirAuth) {
            resp.sendRedirect(Settings.siteRedir + "/start/register");
        } else {
            resp.getWriter().print("OK, close the tab");
        }
    }
}
