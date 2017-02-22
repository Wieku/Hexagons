package xyz.hexagons.client.api;

import com.badlogic.gdx.graphics.Color;
import org.luaj.vm2.LuaValue;
import xyz.hexagons.client.map.timeline.Timeline;
import xyz.hexagons.client.map.timeline.TimelineRunnable;
import xyz.hexagons.client.utils.Properties;

import java.util.ArrayList;

public class GameProperties extends Properties {
    public float rotationSpeed = 0.5f;
    public float rotationSpeedMax = 1.5f;
    public float rotationIncrement = 0.083f;
    public float rapidSpinSpeed = 70f;
    public transient boolean rapidSpin = false;

    public float difficulty = 1f;
    public float levelIncrement = 15f;
    public float delayMult = 1f;
    public float delayMultInc = 0.01f;
    public float speed = 1f;
    public float speedInc = 0.125f;
    public float currentTime = 0f;

    /**
     * sides
     */
    public int sides = 6;
    public int minSides = 5;
    public int maxSides = 7;
    public boolean mustChangeSides = false;

    /**
     * pulse
     */
    public float beatPulseMin = 1.0f;
    public float beatPulseMax = 1.2f;
    public float beatPulseDelay = 0.5f;
    public float beatPulse = 1.0f;

    /**
     * wallpulse
     */
    public float pulseMin = 70;
    public float pulseMax = 90;
    public float pulseSpeed = 1.0f;
    public float pulseSpeedR = 0.6f;
    public float pulseDelayHalfMax = 0;
    public float pulseDelayMax = 0;
    public float pulse = 75;
    public int pulseDir = 1;

    /**
     * colors
     */
    public ArrayList<HColor> backgroundColors = new ArrayList<>();
    public float colorPulseMin = 0f;
    public float colorPulseMax = 3f;
    public float colorPulseInc = 1f;
    public int colorOffset = 0;
    public float colorSwitch = 1f;
    public int colorPingPongForward = 0;
    public int colorPingPongReverse = 0;
    public HColor walls = new HColor(1, 1, 1, 1);
    public DynamicColor shadow = new DynamicColor() {
        @Override
        public void update() {
            this.set(CurrentMap.gameProperties.walls.r, CurrentMap.gameProperties.walls.g, CurrentMap.gameProperties.walls.b, CurrentMap.gameProperties.walls.a).lerp(Color.BLACK, 0.4f);
        }
    };
    /**
     * gfx settings
     */
    public int layers = 6;
    public float depth = 1.6f;
    public float skew = 0f;
    public float minSkew = 0f;
    public float maxSkew = 1f;
    public float skewTime = 5f;
    public float wallSkewLeft = 0f;
    public float wallSkewRight = 0f;
    public float alphaMultiplier = 1f;
    public float alphaFalloff = 0f;

    public Timeline<Wall> wallTimeline = new Timeline<>();
    public Timeline<TimelineRunnable> eventTimeline = new Timeline<>();
    public boolean gameCompleted = false;
    public boolean useRadians = false;


    @Override
    protected void registerProperties() {
        mkpath("rotation");
        mkpath("difficulty");
        mkpath("sides");
        mkpath("beatPulse");
        mkpath("pulse");
        mkpath("color");
        mkpath("view");

        registerBoolean("rotation.useRadians", b -> useRadians = b, () -> LuaValue.valueOf(useRadians));
        registerFloat("rotation.speed", f -> rotationSpeed = f, () -> LuaValue.valueOf(rotationSpeed));
        registerFloat("rotation.maxSpeed", f -> rotationSpeedMax = f, () -> LuaValue.valueOf(rotationSpeedMax));
        registerFloat("rotation.increment", f -> rotationIncrement = f, () -> LuaValue.valueOf(rotationIncrement));
        registerFloat("rotation.rapidSpinSpeed", f -> rapidSpinSpeed = f, () -> LuaValue.valueOf(rapidSpinSpeed));
        registerGetter("rotation.rapidSpin", () -> LuaValue.valueOf(rapidSpin));

        registerFloat("difficulty.difficulty", f -> difficulty = f, () -> LuaValue.valueOf(difficulty));
        registerFloat("difficulty.delayMultiplier", f -> delayMult = f, () -> LuaValue.valueOf(delayMult));
        registerFloat("difficulty.delayMultiplierIncrement", f -> delayMultInc = f, () -> LuaValue.valueOf(delayMultInc));
        registerFloat("difficulty.levelIncrement", f -> levelIncrement = f, () -> LuaValue.valueOf(levelIncrement));
        registerFloat("difficulty.speed", f -> speed = f, () -> LuaValue.valueOf(speed));
        registerFloat("difficulty.speedIncrement", f -> speedInc = f, () -> LuaValue.valueOf(speedInc));

        registerInteger("sides.start", i -> sides = i, () -> LuaValue.valueOf(sides)); //TODO: Find better name
        registerInteger("sides.min", i -> minSides = i, () -> LuaValue.valueOf(minSides));
        registerInteger("sides.max", i -> maxSides = i, () -> LuaValue.valueOf(maxSides));

        registerFloat("beatPulse.min", f -> beatPulseMin = f, () -> LuaValue.valueOf(beatPulseMin));
        registerFloat("beatPulse.max", f -> beatPulseMax = f, () -> LuaValue.valueOf(beatPulseMax));
        registerFloat("beatPulse.delay", f -> beatPulseDelay = f, () -> LuaValue.valueOf(beatPulseDelay));

        registerFloat("pulse.min", f -> pulseMin = f, () -> LuaValue.valueOf(pulseMin));
        registerFloat("pulse.max", f -> {pulseMax = f; pulse = f; pulseDir = 1;}, () -> LuaValue.valueOf(pulseMax));
        registerFloat("pulse.speed", f -> pulseSpeed = f, () -> LuaValue.valueOf(pulseSpeed));
        registerFloat("pulse.speedReverse", f -> pulseSpeedR = f, () -> LuaValue.valueOf(pulseSpeedR));
        registerFloat("pulse.delayMax", f -> {pulseDelayMax = f; pulseDelayHalfMax = f / 2;}, () -> LuaValue.valueOf(pulseDelayMax));

        registerHColorArray("color.background", a -> backgroundColors = a, () -> backgroundColors);
        registerFloat("color.pulseMin", f -> colorPulseMin = f, () -> LuaValue.valueOf(colorPulseMin));
        registerFloat("color.pulseMax", f -> colorPulseMax = f, () -> LuaValue.valueOf(colorPulseMax));
        registerFloat("color.pulseIncrement", f -> colorPulseInc = f, () -> LuaValue.valueOf(colorPulseInc));
        registerInteger("color.offset", i -> colorOffset = i, () -> LuaValue.valueOf(colorOffset));
        registerFloat("color.switch", f -> colorSwitch = f, () -> LuaValue.valueOf(colorSwitch));
        registerInteger("color.pingPongForward", i -> colorPingPongForward = i, () -> LuaValue.valueOf(colorPingPongForward));
        registerInteger("color.pingPongReverse", i -> colorPingPongReverse = i, () -> LuaValue.valueOf(colorPingPongReverse));
        registerHColor("color.walls", c -> walls = c, () -> walls);
        registerDynamicColor("color.shadow", c -> shadow = c, () -> shadow);

        registerInteger("view.layers", f -> layers = f, () -> LuaValue.valueOf(layers));
        registerFloat("view.depth", f -> depth = f, () -> LuaValue.valueOf(depth));
        registerFloat("view.skew", f -> skew = f, () -> LuaValue.valueOf(skew));
        registerFloat("view.minSkew", f -> minSkew = f, () -> LuaValue.valueOf(minSkew));
        registerFloat("view.maxSkew", f -> maxSkew = f, () -> LuaValue.valueOf(maxSkew));
        registerFloat("view.skewTime", f -> skewTime = f, () -> LuaValue.valueOf(skewTime));
        registerFloat("view.wallSkewLeft", f -> wallSkewLeft = f, () -> LuaValue.valueOf(wallSkewLeft));
        registerFloat("view.wallSkewRight", f -> wallSkewRight = f, () -> LuaValue.valueOf(wallSkewRight));
        registerFloat("view.alphaMultiplier", f -> alphaMultiplier = f, () -> LuaValue.valueOf(alphaMultiplier));
        registerFloat("view.alphaFalloff", f -> alphaFalloff = f, () -> LuaValue.valueOf(alphaFalloff));
    }
}
