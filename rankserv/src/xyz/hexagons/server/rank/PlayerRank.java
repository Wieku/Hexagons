package xyz.hexagons.server.rank;

import com.google.gson.Gson;
import com.nimbusds.jose.JWSObject;
import xyz.hexagons.server.auth.RuntimeSecrets;
import xyz.hexagons.server.util.SqlUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;

public class PlayerRank extends HttpServlet {
    private static final String qPlayerRankedScore = SqlUtil.getQuery("rank/playerRankedScore");
    private static final String qPlayerGlobalRank = SqlUtil.getQuery("rank/playerGlobalRank");
    private static final String qPlayerOverallScore = SqlUtil.getQuery("rank/playerOverallScore");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String token = req.getParameter("token");
        if(token == null) {
            resp.setStatus(500);
            return;
        }

        try {
            JWSObject t = JWSObject.parse(token);
            if(!RuntimeSecrets.check(t)) {
                resp.setStatus(500);
                return;
            }

            Account account = new Gson().fromJson(t.getPayload().toString(), Account.class);

            resp.getOutputStream().print("{\"rankedScore\":" + SqlUtil.getIntForQuery(qPlayerRankedScore, account.id) + "," +
                    "\"globalRank\":" + SqlUtil.getIntForQuery(qPlayerGlobalRank, account.id) + "," +
                    "\"overallScore\":" + SqlUtil.getIntForQuery(qPlayerOverallScore, account.id) + "}");

        } catch (ParseException | IOException e) {
            e.printStackTrace();
            resp.setStatus(500);
        }
    }

    private static class Account implements Serializable {
        public String account;
        public int id;
    }
}
