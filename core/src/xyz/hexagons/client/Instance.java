package xyz.hexagons.client;

import me.wieku.animation.AnimationManager;
import xyz.hexagons.client.map.Map;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Instance {
    private static AnimationManager animationManager = new AnimationManager();
    public static ArrayList<Map> maps;
    public static float diagonal = 1600f;
    public static Hexagons game = null;
    public static boolean noupdate = false;
    public static Consumer<Integer> setForegroundFps = null;

    public static AnimationManager getAnimationManager() {
        return animationManager;
    }
}
