package xyz.hexagons.server.auth;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.apache.commons.io.IOUtils;
import xyz.hexagons.server.Settings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class SteamAuthOut extends HttpServlet {
	private static final String STEAM_ID_BASE = "http://steamcommunity.com/openid/id/";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(!checkAuth(req))
			throw new ServletException("Invalid request");

		String fullState = req.getParameter("state");
		UUID stateUuid = null;

		String oiIdentity = req.getParameter("openid.identity");
		if(!oiIdentity.startsWith(STEAM_ID_BASE))
			throw new ServletException("Invalid request");
		String steamId = oiIdentity.substring(STEAM_ID_BASE.length());
		AccountUtils.Account account = AccountUtils.getAccount(steamId, AuthType.STEAM.type);

		if(fullState.startsWith("g/"))
			AccountUtils.notifyGame(UUID.fromString(fullState.substring(2)), account);

		if(!AccountUtils.loginRedirect(account, resp, fullState))
			resp.sendRedirect(Settings.instance.siteRedir + "/error/rs/steamAuth/out/1");
	}

	private boolean checkAuth(HttpServletRequest req) throws ServletException, IOException {
		String oiNs = req.getParameter("openid.ns");
		String oiOpEndpoint = req.getParameter("openid.op_endpoint");
		String oiClaimedId = req.getParameter("openid.claimed_id");
		String oiIdentity = req.getParameter("openid.identity");
		String oiReturnTo = req.getParameter("openid.return_to");
		String oiResponseNonce = req.getParameter("openid.response_nonce");
		String oiAssocHandle = req.getParameter("openid.assoc_handle");
		String oiSigned = req.getParameter("openid.signed");
		String oiSig = req.getParameter("openid.sig");
		String oiMode = req.getParameter("openid.mode");

		if(!oiMode.equals("id_res"))
			throw new ServletException("Invalid request");

		String url = "https://steamcommunity.com/openid/login"
				+ "?openid.mode=check_authentication"
				+ "&openid.ns=" + URLEncoder.encode(oiNs, "UTF-8")
				+ "&openid.op_endpoint=" + URLEncoder.encode(oiOpEndpoint, "UTF-8")
				+ "&openid.claimed_id=" + URLEncoder.encode(oiClaimedId, "UTF-8")
				+ "&openid.identity=" + URLEncoder.encode(oiIdentity, "UTF-8")
				+ "&openid.return_to=" + URLEncoder.encode(oiReturnTo, "UTF-8")
				+ "&openid.response_nonce=" + URLEncoder.encode(oiResponseNonce, "UTF-8")
				+ "&openid.assoc_handle=" + URLEncoder.encode(oiAssocHandle, "UTF-8")
				+ "&openid.signed=" + URLEncoder.encode(oiSigned, "UTF-8")
				+ "&openid.sig=" + URLEncoder.encode(oiSig, "UTF-8");

		HttpResponse authResponse = new NetHttpTransport().createRequestFactory()
				.buildGetRequest(new GenericUrl(url)).execute();

		String auth = IOUtils.toString(authResponse.getContent(), StandardCharsets.UTF_8);
		String[] authLines = auth.split("\n");

		HashMap<String, String> authMap = new HashMap<String, String>();

		Arrays.stream(authLines)
				.map(line -> new AbstractMap.SimpleEntry<>(line.substring(0, line.indexOf(':')),
						line.substring(line.indexOf(':') + 1))).forEach(e -> authMap.put(e.getKey(), e.getValue()));

		if(!authMap.get("ns").equals("http://specs.openid.net/auth/2.0"))
			throw new ServletException("Invalid request");

		if(!authMap.get("is_valid").equals("true"))
			throw new ServletException("Invalid request");

		return true;
	}

}
