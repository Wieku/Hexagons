package xyz.hexagons.server.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Instance {
    public static Executor executor = Executors.newSingleThreadExecutor();
}
