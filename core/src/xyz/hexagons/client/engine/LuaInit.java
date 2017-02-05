package xyz.hexagons.client.engine;

import com.badlogic.gdx.Gdx;
import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.Patterns;
import xyz.hexagons.client.utils.PathUtil;
import xyz.hexagons.client.utils.function.Function;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.function.Supplier;
import java.util.zip.ZipFile;

public class LuaInit {
    static Random random = new Random(); //TODO: CENTRAL SEED

    public static void init() {
        Instance.luaGlobals.set("game", getGame());
        Instance.luaGlobals.set("standardPattern", getStandardPatterns());

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

        return game;
    }

    private static LuaValue getStandardPatterns() {
        LuaTable standardPatterns = new LuaTable();

        standardPatterns.set("alternatingBarrage", getStandardPatternGenerator(new AlternatingBarrageGenerator()));
        standardPatterns.set("mirrorSpiral", getStandardPatternGenerator(new MirrorSpiralGenerator()));
        standardPatterns.set("barrageSpiral", getStandardPatternGenerator(new BarrageSpiralGenerator()));
        standardPatterns.set("inverseBarrage", getStandardPatternGenerator(new InverseBarrageGenerator()));
        standardPatterns.set("tunnel", getStandardPatternGenerator(new TunnelGenerator()));
        standardPatterns.set("mirroredWallStrip", getStandardPatternGenerator(new MirroredWallStripGenerator()));

        return standardPatterns;
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

    private static LuaValue wrapLuaParam(final LuaValue lv) {
        if(lv.isfunction()) {
            return lv;
        }
        return new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return lv;
            }
        };
    }

    private interface PatternGenerator extends Function<LuaValue, Supplier<LuaValue>> {}

    private static class AlternatingBarrageGenerator implements PatternGenerator {

        @Override
        public Supplier<LuaValue> apply(LuaValue arg) {
            if(!arg.istable()) {
                throw new RuntimeException("Arg to pattern constructor must be a table!");
            }

            final LuaValue times = wrapLuaParam(arg.get("times"));
            final LuaValue step = wrapLuaParam(arg.get("step"));

            return new Supplier<LuaValue>() {
                @Override
                public LuaValue get() {
                    Patterns.pAltBarrage(times.call().optint(1), step.call().optint(1));
                    return LuaValue.NIL;
                }
            };
        }
    }

    private static class MirrorSpiralGenerator implements PatternGenerator {

        @Override
        public Supplier<LuaValue> apply(LuaValue arg) {
            if(!arg.istable()) {
                throw new RuntimeException("Arg to pattern constructor must be a table!");
            }

            final LuaValue times = wrapLuaParam(arg.get("times"));
            final LuaValue extra = wrapLuaParam(arg.get("extra"));

            return new Supplier<LuaValue>() {
                @Override
                public LuaValue get() {
                    Patterns.pMirrorSpiral(times.call().optint(1), extra.call().optint(1));
                    return LuaValue.NIL;
                }
            };
        }
    }

    private static class BarrageSpiralGenerator implements PatternGenerator {

        @Override
        public Supplier<LuaValue> apply(LuaValue arg) {
            if(!arg.istable()) {
                throw new RuntimeException("Arg to pattern constructor must be a table!");
            }

            final LuaValue times = wrapLuaParam(arg.get("times"));
            final LuaValue delayMult = wrapLuaParam(arg.get("delayMult"));
            final LuaValue step = wrapLuaParam(arg.get("step"));

            return new Supplier<LuaValue>() {
                @Override
                public LuaValue get() {
                    Patterns.pBarrageSpiral(times.call().optint(1), (float) delayMult.call().optdouble(1), step.call().optint(1));
                    return LuaValue.NIL;
                }
            };
        }
    }

    private static class InverseBarrageGenerator implements PatternGenerator {

        @Override
        public Supplier<LuaValue> apply(LuaValue arg) {
            if(!arg.istable()) {
                throw new RuntimeException("Arg to pattern constructor must be a table!");
            }

            final LuaValue times = wrapLuaParam(arg.get("times"));

            return new Supplier<LuaValue>() {
                @Override
                public LuaValue get() {
                    Patterns.pInverseBarrage(times.call().optint(1));
                    return LuaValue.NIL;
                }
            };
        }
    }

    private static class TunnelGenerator implements PatternGenerator {

        @Override
        public Supplier<LuaValue> apply(LuaValue arg) {
            if(!arg.istable()) {
                throw new RuntimeException("Arg to pattern constructor must be a table!");
            }

            final LuaValue times = wrapLuaParam(arg.get("times"));

            return new Supplier<LuaValue>() {
                @Override
                public LuaValue get() {
                    Patterns.pTunnel(times.call().optint(1));
                    return LuaValue.NIL;
                }
            };
        }
    }

    private static class MirroredWallStripGenerator implements PatternGenerator {

        @Override
        public Supplier<LuaValue> apply(LuaValue arg) {
            if(!arg.istable()) {
                throw new RuntimeException("Arg to pattern constructor must be a table!");
            }

            final LuaValue times = wrapLuaParam(arg.get("times"));
            final LuaValue extra = wrapLuaParam(arg.get("extra"));

            return new Supplier<LuaValue>() {
                @Override
                public LuaValue get() {
                    Patterns.pMirrorWallStrip(times.call().optint(1), extra.call().optint(1));
                    return LuaValue.NIL;
                }
            };
        }
    }

}
