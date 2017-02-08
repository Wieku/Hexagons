package xyz.hexagons.client.engine.lua;

import com.badlogic.gdx.Gdx;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.*;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.Patterns;
import xyz.hexagons.client.api.SpeedData;
import xyz.hexagons.client.api.Wall;
import xyz.hexagons.client.utils.PathUtil;
import xyz.hexagons.client.utils.function.Function;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.function.Supplier;
import java.util.zip.ZipFile;

//TODO: check prebata, see if everything matches
public class LuaInit {
    static Random random = new Random(); //TODO: CENTRAL SEED

    public static void init() {
        Instance.luaGlobals.set("game", getGame());
        Instance.luaGlobals.set("standardPattern", getStandardPatterns());
        Instance.luaGlobals.set("patterns", getPatterns());
        Instance.luaGlobals.set("timeline", getTimeline());

        LuaValue chunk = Instance.luaGlobals.load(Gdx.files.internal(PathUtil.getPathForFile("lua/boot.lua")).readString("UTF-8"), "boot.lua");
        chunk.call();
    }

    private static LuaValue getGame() {
        LuaTable game = new LuaTable();

        game.set("randomParam", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                final int start = arg1.checkint();
                final int end = arg2.checkint();

                return new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf(start + random.nextInt(end - start + 1));
                    }
                };
            }
        });

        game.set("random", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                final int bound = arg.checkint();
                return LuaValue.valueOf(random.nextInt(bound));
            }
        });

        game.set("randomSide", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(Patterns.getRandomSide() + 1);
            }
        });

        game.set("randomDir", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(random.nextBoolean() ? 1 : -1);
            }
        });

        game.set("getHalfSides", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(Patterns.getHalfSides());
            }
        });

        game.set("loadProperties", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue map, LuaValue file) {
                try {
                    ZipFile zip = (ZipFile) map.checkuserdata();
                    InputStream in = zip.getInputStream(zip.getEntry(file.checkjstring()));
                    CurrentMap.gameProperties.loadConfig(in);
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return LuaValue.NIL;
            }
        });

        game.set("setProperty", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue path, LuaValue value) {
                CurrentMap.gameProperties.setProperty(path.checkjstring(), value);
                return LuaValue.NIL;
            }
        });

        game.set("setAll", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue base, LuaValue path, LuaValue value) {
                CurrentMap.gameProperties.setAll(base.checkjstring(), path.checkjstring(), value);
                return LuaValue.NIL;
            }
        });

        game.set("getProperty", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                return CurrentMap.gameProperties.getProperty(arg.checkjstring());
            }
        });

        game.set("pushEvent", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if(args.arg(2).isstring())
                    CurrentMap.pushEvent(args.arg(1).tofloat(), args.arg(2).checkjstring(), args.subargs(3));
                else if(args.arg(2).isfunction())
                    CurrentMap.pushEvent(args.arg(1).tofloat(), "function", args.subargs(2));
                return LuaValue.NIL;
            }
        });

        return game;
    }

    private static LuaValue getPatterns() {
        LuaTable patterns = new LuaTable();

        patterns.set("THICKNESS", LuaValue.valueOf(Patterns.THICKNESS));

        patterns.set("getPerfectThickness", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                return LuaValue.valueOf(Patterns.getPerfectThickness(arg.tofloat()));
            }
        });

        patterns.set("getPerfectDelay", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                return LuaValue.valueOf(Patterns.getPerfectDelay(arg.tofloat()));
            }
        });

        patterns.set("getBaseSpeed", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(Patterns.getBaseSpeed());
            }
        });

        ////////
        // Pattern components

        patterns.set("barrage", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue side, LuaValue neighbours, LuaValue thickness) {
                Patterns.cBarrageN(side.toint(), neighbours.toint(), thickness.tofloat());
                return LuaValue.NIL;
            }
        });

        return patterns;
    }

    private static LuaValue getStandardPatterns() {
        LuaTable standardPatterns = new LuaTable();

        standardPatterns.set("alternatingBarrage", getStandardPatternGenerator(new LuaStandardPatterns.AlternatingBarrageGenerator()));
        standardPatterns.set("mirrorSpiral", getStandardPatternGenerator(new LuaStandardPatterns.MirrorSpiralGenerator()));
        standardPatterns.set("doubleMirrorSpiral", getStandardPatternGenerator(new LuaStandardPatterns.DoubleMirrorSpiralGenerator()));
        standardPatterns.set("barrageSpiral", getStandardPatternGenerator(new LuaStandardPatterns.BarrageSpiralGenerator()));
        standardPatterns.set("inverseBarrage", getStandardPatternGenerator(new LuaStandardPatterns.InverseBarrageGenerator()));
        standardPatterns.set("tunnel", getStandardPatternGenerator(new LuaStandardPatterns.TunnelGenerator()));
        standardPatterns.set("mirroredWallStrip", getStandardPatternGenerator(new LuaStandardPatterns.MirroredWallStripGenerator()));
        standardPatterns.set("vortex", getStandardPatternGenerator(new LuaStandardPatterns.VortexGenerator()));
        standardPatterns.set("fixedDelayBarrageSpiral", getStandardPatternGenerator(new LuaStandardPatterns.FixedDelayBarrageSpiralGenerator()));
        standardPatterns.set("randomBarrage", getStandardPatternGenerator(new LuaStandardPatterns.RandomBarrageGenerator()));

        return standardPatterns;
    }

    private static LuaValue getTimeline() {
        LuaTable timeline = new LuaTable();

        timeline.set("wait", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Patterns.timelineWait(arg.tofloat());
                return LuaValue.NIL;
            }
        });

        timeline.set("addWall", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                int side = arg.get("side").toint();
                float thickness = arg.get("thickness").tofloat();

                Wall wall = new Wall(side, thickness, luaSpeedData(arg));
                CurrentMap.gameProperties.wallTimeline.submit(wall);
                return LuaValue.NIL;
            }
        });

        return timeline;
    }

    private static SpeedData luaSpeedData(LuaValue lval) {
        if(lval.isnumber())
            return new SpeedData(lval.tofloat());

        return new SpeedData(
                lval.get("speed").tofloat(),
                lval.get("acceleration").tofloat(),
                lval.get("speedMin").tofloat(),
                lval.get("speedMax").tofloat(),
                lval.get("pingPong").toboolean()
        );
    }

    private static LuaValue getStandardPatternGenerator(final Function<LuaValue, Supplier<LuaValue>> patternGenerator) {
        return new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                final Supplier<LuaValue> generate = patternGenerator.apply(arg);
                return new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return generate.get();
                    }
                };
            }
        };
    }
}
