package xyz.hexagons.server.rank;

import com.google.gson.Gson;
import com.nimbusds.jose.JWSObject;
import xyz.hexagons.server.Launcher;
import xyz.hexagons.server.auth.AccountUtils;
import xyz.hexagons.server.auth.RuntimeSecrets;
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
			String token = req.getParameter("token");
			if(token == null) {
				resp.setStatus(500);
				return;
			}
			JWSObject t = JWSObject.parse(token);
			if(!RuntimeSecrets.check(t)) {
				resp.setStatus(500);
				return;
			}

			AccountUtils.SessionAccount account = new Gson().fromJson(t.getPayload().toString(), AccountUtils.SessionAccount.class);

			boolean ranked = false;
            String rankedToken = req.getParameter("rankedToken");
			if(rankedToken != null) {
                JWSObject rt = JWSObject.parse(rankedToken);
                if(!RuntimeSecrets.check(t)) {
                    resp.setStatus(500);
                    return;
                }
                RankedToken rtok = new Gson().fromJson(rt.getPayload().toString(), RankedToken.class);
                if(rtok.ranked) {
                    System.out.println("Ranked game!");
                    ranked = true;
                    //TODO: this fucking sucks
                    //If you are reading this, please remind us that this code is terrible
                    //Also, please don't cheat, we appreciate (:
                }
            }

            final boolean _ranked = ranked;
            String nick = account.account;
            String mapid = req.getParameter("mapid");
            long score = Long.valueOf(req.getParameter("score"));
            System.out.println("New score report " + mapid + "/" + nick + ": " + score);

            Launcher.withConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement(qInstertGame);
                statement.setString(1, mapid);
                statement.setLong(2, score);
                statement.setLong(3, account.id);
                statement.setLong(4, Instant.now().getEpochSecond());
                statement.setBoolean(5, _ranked);
                statement.executeUpdate();
                return null;
            });
            resp.getWriter().print("{\"state\": \"OK\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().print("{\"state\": \"ERROR\"}");
        }
    }

    private static class RankedToken {
        public boolean ranked;
        public long at;
        public String permId;
    }
}
