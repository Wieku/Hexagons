package xyz.hexagons.server.rank;

import com.google.gson.Gson;
import com.nimbusds.jose.JWSObject;
import org.apache.commons.codec.binary.Hex;
import xyz.hexagons.server.Launcher;
import xyz.hexagons.server.auth.RuntimeSecrets;
import xyz.hexagons.server.util.SqlUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.UUID;

public class RankedToken extends HttpServlet {
    public static final String qGetMapHash = SqlUtil.getQuery("rank/getMapHash");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String challengeJws = req.getParameter("challenge");
        String hash = req.getParameter("hash");
        String permitId = req.getParameter("permitId");
        if(challengeJws == null || hash == null || permitId == null) {
            resp.setStatus(500);
            return;
        }


        try {
            JWSObject t = JWSObject.parse(challengeJws);
            if (!RuntimeSecrets.check(t)) {
                resp.setStatus(500);
                return;
            }

            Challenge challenge = new Gson().fromJson(t.getPayload().toString(), Challenge.class);
            if((System.currentTimeMillis()/1000) - challenge.time > 10) {
                resp.setStatus(500);
                return;
            }

            Launcher.withConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement(qGetMapHash);
                statement.setInt(1, Integer.valueOf(permitId));
                ResultSet rs = statement.executeQuery();
                if(!rs.next()) {
                    resp.setStatus(500);
                    return 0;
                }

                if(!rs.getString(1).equals(challenge.map)) {
                    resp.setStatus(500);
                    return 0;
                }

                String dbHash = rs.getString(2);
                try {
                    byte[] saltBytes = Base64.getDecoder().decode(challenge.salt);

                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    digest.update(dbHash.getBytes());
                    digest.update(String.valueOf(challenge.id).getBytes());
                    digest.update(saltBytes);
                    byte[] dbSalted = digest.digest();

                    if(!hash.equals(String.valueOf(Hex.encodeHex(dbSalted, true)))) {
                        resp.setStatus(500);
                        return 0;
                    }

                    resp.getOutputStream().print(RuntimeSecrets.signSession(
                            "{\"ranked\":true"
                                    + ",\"at\": "+ (System.currentTimeMillis() / 1000)
                                    + ", \"permId\":\"" + UUID.nameUUIDFromBytes(saltBytes) + "\"}"));

                    System.out.println("Get ranked token OK!");

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                return 0;
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Challenge {
        public String salt;
        public int id; //user id
        public long time;
        public String map;
    }
}
