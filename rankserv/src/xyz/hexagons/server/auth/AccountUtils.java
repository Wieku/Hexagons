package xyz.hexagons.server.auth;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import org.eclipse.jetty.continuation.Continuation;
import xyz.hexagons.server.Launcher;
import xyz.hexagons.server.Settings;
import xyz.hexagons.server.util.SqlUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AccountUtils {
	private static final String qUserByAuth = SqlUtil.getQuery("user/userByAuth");
	private static final String qInsertAuthUser = SqlUtil.getQuery("user/insertAuth");

	static boolean loginRedirect(Account account, HttpServletResponse resp, String fullState) throws IOException {
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
			return true;
		} catch (JOSEException e) {
			e.printStackTrace();
		}
		return false;
	}

	static void notifyGame(UUID stateUuid, AccountUtils.Account account) {
		try {
			if (AuthToken.tokenContinuations.containsKey(stateUuid)) {
				Continuation c = AuthToken.tokenContinuations.get(stateUuid);
				AuthToken.tokenContinuations.remove(stateUuid);

				System.out.println("{\"account\":\"" + account.name + "\", id: " + account.id + "}");
				c.getServletResponse().getWriter().print(RuntimeSecrets.signSession("{\"account\":\"" + account.name + "\", id: " + account.id + "}"));
				c.complete();
			} else if (AuthToken.tokenChallenges.containsKey(stateUuid)) {
				AuthToken.tokenChallenges.remove(stateUuid);
				AuthToken.tokenChallenges.put(stateUuid, account.name, 16000L);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static AccountUtils.Account getAccount(String userId, int accountType) {
		try {
			return Launcher.withConnection(connection -> {
				PreparedStatement statement = connection.prepareStatement(qUserByAuth);
				statement.setInt(1, accountType);
				statement.setString(2, userId);
				ResultSet rs = statement.executeQuery();
				if(rs.next()) {
					AccountUtils.Account account = new AccountUtils.Account();
					account.name = rs.getString("nick");
					account.id = rs.getString("id");
					return account;
				} else {
					PreparedStatement istatement = connection.prepareStatement(qInsertAuthUser);
					istatement.setInt(1, accountType);
					istatement.setString(2, userId);
					ResultSet res = istatement.executeQuery();
					if(res.next()) {
						AccountUtils.Account account = new AccountUtils.Account();
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

	// NOTE: Used for JWT tokens!
	public static class SessionAccount {
		public String account;
		public int id;
	}

	static class Account {
		public String name;
		public String id;
	}
}
