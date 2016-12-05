package xyz.hexagons.server.auth;

import com.google.gson.Gson;
import com.nimbusds.jose.JWSObject;
import xyz.hexagons.server.Launcher;
import xyz.hexagons.server.util.SqlUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class GetNick extends HttpServlet {
    private static final String qUserByAuth = SqlUtil.getQuery("user/userById");

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

            PreparedStatement statement = Launcher.connection.prepareStatement(qUserByAuth);
            statement.setInt(1, account.id);
            ResultSet rs = statement.executeQuery();
            if(rs.next()) {
                String name = rs.getString(1);
                resp.getOutputStream().print("{\"account\":\"" + name +"\", id: "+ account.id +"}");
            }
        } catch (ParseException | SQLException | IOException e) {
            e.printStackTrace();
            resp.setStatus(500);
        }
    }

    private static class Account implements Serializable {
        public String account;
        public int id;
    }
}
