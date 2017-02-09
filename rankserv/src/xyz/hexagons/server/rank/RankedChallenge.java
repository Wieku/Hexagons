package xyz.hexagons.server.rank;

import com.google.gson.Gson;
import com.nimbusds.jose.JWSObject;
import xyz.hexagons.server.auth.AccountUtils;
import xyz.hexagons.server.auth.RuntimeSecrets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

public class RankedChallenge extends HttpServlet {
    private static final Random random;

    static {
        random = new Random(new SecureRandom().nextLong());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String token = req.getParameter("token");
        if(token == null) {
            resp.setStatus(500);
            return;
        }
        try {
            JWSObject t = JWSObject.parse(token);
            if (!RuntimeSecrets.check(t)) {
                resp.setStatus(500);
                return;
            }

            AccountUtils.SessionAccount account = new Gson().fromJson(t.getPayload().toString(), AccountUtils.SessionAccount.class);

            byte[] salt = new byte[32];
            random.nextBytes(salt);

            resp.getOutputStream().print(RuntimeSecrets.signSession(
                    "{\"salt\":\"" + Base64.getEncoder().encodeToString(salt)
                            + "\",\"id\":"+ account.id
                            + ",\"time\":" + (System.currentTimeMillis() / 1000)
                            + ",\"map\":\"" + UUID.fromString(req.getParameter("map")).toString() + "\"}"));

            System.out.println("Get ranked challenge");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
