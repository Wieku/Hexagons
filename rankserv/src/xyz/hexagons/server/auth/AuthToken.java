package xyz.hexagons.server.auth;

import com.google.gson.JsonObject;
import org.eclipse.jetty.continuation.Continuation;
import xyz.hexagons.server.util.TimeoutMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class AuthToken extends HttpServlet {
	protected static final TimeoutMap<UUID, Continuation> tokenContinuations = new TimeoutMap<>();
	protected static final TimeoutMap<UUID, String> tokenChallenges = new TimeoutMap<>();

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID challenge = UUID.randomUUID();
        tokenChallenges.put(challenge, null, 5000L);

        resp.setContentType("application/json");
        JsonObject m = new JsonObject();
        m.addProperty("challenge", challenge.toString());
        resp.getWriter().print(m.toString());
    }
}
