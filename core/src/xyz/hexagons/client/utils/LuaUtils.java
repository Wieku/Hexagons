package xyz.hexagons.client.utils;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import xyz.hexagons.client.utils.function.Supplier;

public class LuaUtils {
    public static LuaValue supplierGetter(final Supplier<LuaValue> s) {
        return new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return s.get();
            }
        };
    }
}
