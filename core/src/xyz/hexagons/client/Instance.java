package xyz.hexagons.client;

import com.google.common.eventbus.EventBus;
import me.wieku.animation.AnimationManager;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;
import xyz.hexagons.client.audio.AudioPlayer.IAudioPlayerFactory;
import xyz.hexagons.client.map.Map;
import xyz.hexagons.client.rankserv.AccountManager;
import xyz.hexagons.client.utils.function.Consumer;
import xyz.hexagons.client.utils.function.Function;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Instance {
    private static AnimationManager animationManager = new AnimationManager();
    public static IAudioPlayerFactory audioPlayerFactory;

    //MUST BE SINGLE THREADED, @see RankApi
    public static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static EventBus eventBus = new EventBus();
    public static ExecutorService cachedExecutor = Executors.newCachedThreadPool();
    public static Consumer<Runnable> scheduleOnMain = null;

    public static Globals luaGlobals = JsePlatform.standardGlobals();
    public static ArrayList<Map> maps;
    public static Hexagons game = null;

    public static File storageRoot = null;
    public static float diagonal = 1600f;
    public static boolean noupdate = false;

    public static Consumer<Integer> setForegroundFps = null;
    public static Consumer<Integer> setSamples = null;

    public static AccountManager accountManager = null;
    public static AccountManager.Account currentAccount = null;


    public static Function<String, File> cacheFile; //Android only for now

	public static AnimationManager getAnimationManager() {
        return animationManager;
    }
}
