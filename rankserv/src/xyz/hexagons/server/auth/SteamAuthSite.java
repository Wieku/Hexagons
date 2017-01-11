package xyz.hexagons.server.auth;

import xyz.hexagons.server.Settings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SteamAuthSite extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setStatus(301);
		resp.sendRedirect("http://steamcommunity.com/openid/login"
				+ "?openid.ns=http://specs.openid.net/auth/2.0&openid.mode=checkid_setup"
				+ "&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select"
				+ "&openid.identity=http://specs.openid.net/auth/2.0/identifier_select"
				+ "&openid.realm=" + Settings.instance.selfUrl
				+ "&openid.return_to=" + Settings.instance.selfUrl + "/auth/steam/out%3Fstate=s/");

	}
}
