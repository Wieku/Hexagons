package xyz.hexagons.client.engine;

import com.badlogic.gdx.Gdx;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.api.Patterns;
import xyz.hexagons.client.utils.PathUtil;
import xyz.hexagons.client.utils.function.Function;

import java.util.function.Supplier;

public class LuaInit {
    public static void init() {
        Instance.luaGlobals.set("game", getGame());
        Instance.luaGlobals.set("standardPattern", getStandardPatterns());

        LuaValue chunk = Instance.luaGlobals.load(Gdx.files.internal(PathUtil.getPathForFile("lua/boot.lua")).readString("UTF-8"), "boot.lua");
        chunk.call();
    }

    private static LuaValue getGame() {
        LuaTable game = new LuaTable();

        game.set("newPatternQueue", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                return null;
            }
        });

        game.set("randomParam", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                return new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf(9); //TODO: NO, ITS NOT RNG.. YET
                    }
                };
            }
        });
        return game;
    }

    private static LuaValue getStandardPatterns() {
        LuaTable standardPatterns = new LuaTable();

        standardPatterns.set("alternatingBarrage", getStandardPatternGenerator(new AlternatingBarrageGenerator()));
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

    private interface PatternGenerator extends Function<LuaValue, Supplier<LuaValue>> {}

    private static class AlternatingBarrageGenerator implements PatternGenerator {

        @Override
        public Supplier<LuaValue> apply(LuaValue arg) {
            if(!arg.istable()) {
                throw new RuntimeException("Arg to pattern constructor must be a table!");
            }

            final int times = arg.get("times").optint(1); //TODO: Should be func, wrap into that, call on get
            final int step = arg.get("step").optint(1);

            return new Supplier<LuaValue>() {
                @Override
                public LuaValue get() {
                    Patterns.pAltBarrage(times, step);
                    return null;
                }
            };
        }
    }

}
