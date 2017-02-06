package xyz.hexagons.client.engine.lua;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import xyz.hexagons.client.api.Patterns;
import xyz.hexagons.client.utils.function.Function;

import java.util.function.Supplier;

public class LuaStandardPatterns {
    interface PatternGenerator extends Function<LuaValue, Supplier<LuaValue>> {}

    static class AlternatingBarrageGenerator implements PatternGenerator {

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

    static class MirrorSpiralGenerator implements PatternGenerator {

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

    static class DoubleMirrorSpiralGenerator implements PatternGenerator {

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
                    Patterns.pMirrorSpiralDouble(times.call().optint(1), extra.call().optint(1));
                    return LuaValue.NIL;
                }
            };
        }
    }

    static class BarrageSpiralGenerator implements PatternGenerator {

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

    static class InverseBarrageGenerator implements PatternGenerator {

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

    static class TunnelGenerator implements PatternGenerator {

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

    static class MirroredWallStripGenerator implements PatternGenerator {

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

    static class VortexGenerator implements PatternGenerator {

        @Override
        public Supplier<LuaValue> apply(LuaValue arg) {
            if(!arg.istable()) {
                throw new RuntimeException("Arg to pattern constructor must be a table!");
            }

            final LuaValue times = wrapLuaParam(arg.get("times"));
            final LuaValue step = wrapLuaParam(arg.get("step"));
            final LuaValue extra = wrapLuaParam(arg.get("extra"));

            return new Supplier<LuaValue>() {
                @Override
                public LuaValue get() {
                    Patterns.pWallExVortex(times.call().optint(1), step.call().optint(1), extra.call().optint(1));
                    return LuaValue.NIL;
                }
            };
        }
    }

    static class FixedDelayBarrageSpiralGenerator implements PatternGenerator {

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
                    Patterns.pDMBarrageSpiral(times.call().optint(1), (float) delayMult.call().optdouble(1), step.call().optint(1));
                    return LuaValue.NIL;
                }
            };
        }
    }

    static class RandomBarrageGenerator implements PatternGenerator {

        @Override
        public Supplier<LuaValue> apply(LuaValue arg) {
            if(!arg.istable()) {
                throw new RuntimeException("Arg to pattern constructor must be a table!");
            }

            final LuaValue times = wrapLuaParam(arg.get("times"));
            final LuaValue delayMult = wrapLuaParam(arg.get("delayMult"));

            return new Supplier<LuaValue>() {
                @Override
                public LuaValue get() {
                    Patterns.pRandomBarrage(times.call().optint(1), (float) delayMult.call().optdouble(1));
                    return LuaValue.NIL;
                }
            };
        }
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
}
