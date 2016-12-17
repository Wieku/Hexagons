package xyz.hexagons.server.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
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
    private static final String qUserByAuth = SqlUtil.getQuery("user/userByAuth");
    private static final String qInsertAuthUser = SqlUtil.getQuery("user/insertAuth");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullState = req.getParameter("state");
        UUID stateUuid = null;

        String googleUserId = getGoogleUserId(req.getParameter("code"));

        Account account = getAccount(googleUserId);

        if(fullState.startsWith("g/"))
            notifyGame(UUID.fromString(fullState.substring(2)), account);

        JWSObject userToken = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(account.id));
        try {
            userToken.sign(new MACSigner(Settings.instance.signSecret));
            String next = "/profile";
            if(fullState.startsWith("g/"))
                next = "/welcome";

            if(account.name.matches("^u\\d+$")) {
                resp.sendRedirect(Settings.instance.siteRedir + "/start/register/" + userToken.serialize() + next);
            } else {
                resp.sendRedirect(Settings.instance.siteRedir + "/login/rankserv/" + userToken.serialize() + next);
            }
            return;
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        resp.sendRedirect(Settings.instance.siteRedir + "/error/rs/gauth/out/1");
    }

    private void notifyGame(UUID stateUuid, Account account) {
        try {
            if (GoogleAuthGame.tokenContinuations.containsKey(stateUuid)) {
                Continuation c = GoogleAuthGame.tokenContinuations.get(stateUuid);
                GoogleAuthGame.tokenContinuations.remove(stateUuid);

                System.out.println("{\"account\":\"" + account.name + "\", id: " + account.id + "}");
                c.getServletResponse().getWriter().print(RuntimeSecrets.signSession("{\"account\":\"" + account.name + "\", id: " + account.id + "}"));
                c.complete();
            } else if (GoogleAuthGame.tokenChallenges.containsKey(stateUuid)) {
                GoogleAuthGame.tokenChallenges.remove(stateUuid);
                GoogleAuthGame.tokenChallenges.put(stateUuid, account.name, 16000L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Account getAccount(String googleUserId) {
        try {
            return Launcher.withConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement(qUserByAuth);
                statement.setInt(1, AuthType.GOOGLE.type);
                statement.setString(2, googleUserId);
                ResultSet rs = statement.executeQuery();
                if(rs.next()) {
                    Account account = new Account();
                    account.name = rs.getString("nick");
                    account.id = rs.getString("id");
                    return account;
                } else {
                    PreparedStatement istatement = connection.prepareStatement(qInsertAuthUser);
                    istatement.setInt(1, AuthType.GOOGLE.type);
                    istatement.setString(2, googleUserId);
                    ResultSet res = istatement.executeQuery();
                    if(res.next()) {
                        Account account = new Account();
                        account.name = "u" + res.getString("user_id");
                        account.id = res.getString("user_id");
                        return account;
                    }
                }
                return null;
            });
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return null;
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

    private static class Account {
        String name;
        String id;
    }

}
