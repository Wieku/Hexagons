package xyz.hexagons.server.auth;

import org.eclipse.jetty.continuation.Continuation;
import xyz.hexagons.server.util.TimeoutMap;

import java.util.UUID;

public class GoogleAuth {
    protected static final TimeoutMap<UUID, Continuation> tokenContinuations = new TimeoutMap<>();
    protected static final TimeoutMap<UUID, String> tokenChallenges = new TimeoutMap<>();
}
