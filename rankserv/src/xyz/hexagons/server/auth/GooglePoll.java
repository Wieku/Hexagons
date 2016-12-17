package xyz.hexagons.server.auth;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class GooglePoll extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID challenge = UUID.fromString(req.getParameter("challenge"));
        if(GoogleAuthGame.tokenChallenges.containsKey(challenge)) {
            String token = GoogleAuthGame.tokenChallenges.get(challenge);
            GoogleAuthGame.tokenChallenges.remove(challenge);

            if(token == null) {
                Continuation continuation = ContinuationSupport.getContinuation(req);
                continuation.suspend(resp);
                GoogleAuthGame.tokenContinuations.put(challenge, continuation, 120000L);
            } else {
                System.out.println("{\"account\":\"" + token +"\"}");
                resp.getWriter().print("{\"account\":\"" + token +"\"}");
            }
        } else {
            System.out.println("INVTOK!!!!!!");
            resp.getWriter().print("{}");
        }
    }
}
