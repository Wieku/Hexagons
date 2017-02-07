package xyz.hexagons.client.utils;

import com.google.common.base.Charsets;
import com.typesafe.config.*;
import org.javatuples.Pair;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.ast.Str;
import xyz.hexagons.client.api.HColor;
import xyz.hexagons.client.map.Hue;
import xyz.hexagons.client.utils.function.Consumer;
import xyz.hexagons.client.utils.function.Supplier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Properties {
    protected abstract void registerProperties();

    private HashMap<String, HashSet<String>> pathList = new HashMap<>();
    private HashMap<String, Pair<PropertyType, Consumer<Object>>> setters = new HashMap<>();
    private HashMap<String, Supplier<LuaValue>> getters = new HashMap<>();
    private HashMap<String, Supplier<?>> rawGetters = new HashMap<>();
    private Pattern basePathPattern = Pattern.compile("^([a-z]+\\.[a-z]+)");

    public Properties() {
        registerProperties();
    }

    protected void mkpath(String path) {
        pathList.put(path, new HashSet<String>());
    }

    protected void registerBoolean(String path, final Consumer<Boolean> setter, Supplier<LuaValue> getter) {
        String[] pathParts = path.split("\\.");
        if(pathParts.length != 2)
            throw new RuntimeException("Config path len should always be 2!");

        pathList.get(pathParts[0]).add(pathParts[1]);
        setters.put(path, new Pair<>(PropertyType.BOOLEAN, cval -> setter.accept((Boolean) cval)));
        getters.put(path, getter);
    }

    protected void registerFloat(String path, final Consumer<Float> setter, Supplier<LuaValue> getter) {
        String[] pathParts = path.split("\\.");
        if(pathParts.length != 2)
            throw new RuntimeException("Config path len should always be 2!");

        pathList.get(pathParts[0]).add(pathParts[1]);
        setters.put(path, new Pair<>(PropertyType.FLOAT, cval -> setter.accept((Float) cval)));
        getters.put(path, getter);
    }

    protected void registerInteger(String path, Consumer<Integer> setter, Supplier<LuaValue> getter) {
        String[] pathParts = path.split("\\.");
        if(pathParts.length != 2)
            throw new RuntimeException("Config path len should always be 2!");

        pathList.get(pathParts[0]).add(pathParts[1]);
        setters.put(path, new Pair<>(PropertyType.INTEGER, cval -> setter.accept((Integer) cval)));
        getters.put(path, getter);
    }

    protected void registerHColor(String path, final Consumer<HColor> setter, Supplier<HColor> getter) {
        String[] pathParts = path.split("\\.");
        if(pathParts.length != 2)
            throw new RuntimeException("Config path len should always be 2!");

        pathList.get(pathParts[0]).add(pathParts[1]);
        setters.put(path, new Pair<>(PropertyType.HCOLOR, cval -> setter.accept((HColor) cval)));
        rawGetters.put(path, getter);
    }

    protected void registerHColorArray(String path, Consumer<ArrayList<HColor>> setter, Supplier<ArrayList<HColor>> getter) {
        String[] pathParts = path.split("\\.");
        if(pathParts.length != 2)
            throw new RuntimeException("Config path len should always be 2!");

        pathList.get(pathParts[0]).add(pathParts[1]);
        setters.put(path, new Pair<>(PropertyType.HCOLOR_ARRAY, cval -> setter.accept((ArrayList<HColor>) cval)));
        rawGetters.put(path, getter);
    }

    protected void registerGetter(String path, Supplier<LuaValue> getter) {
        getters.put(path, getter);
    }

    public boolean checkPath(String path) {
        Matcher baseMatcher = basePathPattern.matcher(path);
        if(!baseMatcher.find())
            return false;

        String basePath = baseMatcher.group(1);
        return setters.containsKey(basePath);
    }

    public void setProperty(String path, LuaValue value) {
        Matcher baseMatcher = basePathPattern.matcher(path);
        if(!baseMatcher.find())
            throw new RuntimeException("Invalid path!");

        String basePath = baseMatcher.group(1);

        if(!setters.containsKey(basePath))
            throw new RuntimeException("Property at path " + basePath + "doesn't exist!");

        Pair<PropertyType, Consumer<Object>> setter = setters.get(basePath);

        switch(setter.getValue0()) {
            case BOOLEAN:
                setter.getValue1().accept(value.toboolean());
                break;
            case FLOAT:
                setter.getValue1().accept(value.tofloat());
                break;
            case INTEGER:
                setter.getValue1().accept(value.toint());
                break;
            case HCOLOR:
                String[] pathParts = path.split("\\.");
                setColorProperty((HColor) rawGetters.get(basePath).get(), 2, pathParts, value);
                break;
            case HCOLOR_ARRAY:
                throw new RuntimeException("Use game.setAll");
            default:
                throw new RuntimeException("Not Implemented");
        }
    }

    public void setAll(String basePath, String path, LuaValue value) {
        if(!setters.containsKey(basePath))
            throw new RuntimeException("Property at path " + basePath + " doesn't exist!");

        PropertyType type = setters.get(basePath).getValue0();
        switch (type) {
            case HCOLOR_ARRAY:
                break;
            default:
                setProperty(basePath + "." + path, value); //TODO: Normalize path?
        }

        List<?> toSet = (List<?>) rawGetters.get(basePath).get();

        switch (type) {
            case HCOLOR_ARRAY:
                ArrayList<HColor> colors = (ArrayList<HColor>) toSet;
                colors.forEach(color -> setColorProperty(color, 0, path.split("\\."), value));
                break;
            default:
                throw new RuntimeException("WTF code 77e7620a-5d4b-4d5e-8159-01fd9e0cb3b2, please report.");
        }
    }

    public LuaValue getProperty(String path) {
        Supplier<LuaValue> getter = getters.get(path);
        if(getter == null)
            return LuaValue.NIL;
        return getter.get();
    }

    public void loadConfig(InputStream in) {
        Config config = ConfigFactory.parseReader(new InputStreamReader(in, Charsets.UTF_8));
        for(String root : pathList.keySet()) {
            if(config.hasPath(root)) {
                for (String sub : pathList.get(root)) {
                    String path = root + "." + sub;
                    if(config.hasPath(path)) {
                        Pair<PropertyType, Consumer<Object>> setter = setters.get(path);
                        switch (setter.getValue0()) {
                            case BOOLEAN:
                                setter.getValue1().accept(config.getBoolean(path));
                                break;
                            case FLOAT:
                                setter.getValue1().accept((float) config.getDouble(path));
                                break;
                            case INTEGER:
                                setter.getValue1().accept(config.getInt(path));
                                break;
                            case HCOLOR:
                                // { r: 0, b: 0, g: 0, a: 1, dynamicDarkness: 2.7, hue: { min: 0, max: 360, increment: 0.7, pingPong: false } }
                                setter.getValue1().accept(parseColor(config, path + "."));
                                break;
                            case HCOLOR_ARRAY:
                                ArrayList<HColor> colors = new ArrayList<HColor>();
                                for (Config c : config.getConfigList(path)) {
                                    colors.add(parseColor(c, ""));
                                }
                                setter.getValue1().accept(colors);
                                break;
                        }
                    }
                }
            }
        }
    }

    private void setColorProperty(HColor color, int pathBase, String[] pathParts, LuaValue value) {
        if(pathParts.length == pathBase)
            throw new RuntimeException("Full color setting is not implemented yet");

        switch (pathParts[pathBase]) {
            case "r":
                color.r = value.checknumber().tofloat();
                break;
            case "g":
                color.g = value.checknumber().tofloat();
                break;
            case "b":
                color.b = value.checknumber().tofloat();
                break;
            case "a":
                color.a = value.checknumber().tofloat();
                break;
            case "dynamicDarkness":
                color.addDynamicDarkness(value.checknumber().tofloat());
                break;
            case "main":
                color.setMain(value.checkboolean());
                break;
            case "hue":
                if(pathParts.length == pathBase + 1)
                    throw new RuntimeException("Full color.hue setting is not implemented yet");
                switch (pathParts[pathBase + 1]) {
                    case "min":
                        color.setHueMin(value.checknumber().tofloat());
                        break;
                    case "max":
                        color.setHueMax(value.checknumber().tofloat());
                        break;
                    case "increment":
                        color.setHueInc(value.checknumber().tofloat());
                        break;
                }
                break;
            case "pulse":
                if(pathParts.length == pathBase + 1)
                    throw new RuntimeException("Full color.pulse setting is not implemented yet");
                switch (pathParts[pathBase + 1]) {
                    case "r":
                        color.r = value.checknumber().tofloat();
                        break;
                    case "g":
                        color.g = value.checknumber().tofloat();
                        break;
                    case "b":
                        color.b = value.checknumber().tofloat();
                        break;
                    case "a":
                        color.a = value.checknumber().tofloat();
                        break;
                }
                break;
            default:
                throw new RuntimeException("Invalid path");
        }
    }

    private HColor parseColor(Config config, String path) {
        float r, g, b, a;
        r = b = g = a = 1f;

        if(config.hasPath(path + "r"))
            r = (float) config.getDouble(path + "r");
        if(config.hasPath(path + "g"))
            g = (float) config.getDouble(path + "g");
        if(config.hasPath(path + "b"))
            b = (float) config.getDouble(path + "b");
        if(config.hasPath(path + "a"))
            a = (float) config.getDouble(path + "a");

        HColor color = new HColor(r, g, b, a);

        if(config.hasPath(path + "main"))
            color.setMain(config.getBoolean(path + "main"));
        if(config.hasPath(path + "dynamicDarkness"))
            color.addDynamicDarkness((float) config.getDouble(path + "dynamicDarkness"));
        if(config.hasPath(path + "hue")) {
            Hue hue = new Hue(0f,360f,1f, false, false);
            if(config.hasPath(path + "hue.min"))
                hue.hueMin = (float) config.getDouble(path + "hue.min");
            if(config.hasPath(path + "hue.max"))
                hue.hueMax = (float) config.getDouble(path + "hue.max");
            if(config.hasPath(path + "hue.increment"))
                hue.hueInc = (float) config.getDouble(path + "hue.increment");
            if(config.hasPath(path + "hue.pingPong"))
                hue.pingPong = config.getBoolean(path + "hue.pingPong");
            if(config.hasPath(path + "hue.shared"))
                hue.shared = config.getBoolean(path + "hue.shared");
            color.addHue(hue);
        }
        if(config.hasPath(path + "pulse")) {
            float pr, pg, pb, pa;
            pr = pb = pg = pa = 1f;
            if(config.hasPath(path + "pulse.r"))
                pr = (float) config.getDouble(path + "pulse.r");
            if(config.hasPath(path + "pulse.g"))
                pg = (float) config.getDouble(path + "pulse.g");
            if(config.hasPath(path + "pulse.b"))
                pb = (float) config.getDouble(path + "pulse.b");
            if(config.hasPath(path + "pulse.a"))
                pa = (float) config.getDouble(path + "pulse.a");
            color.addPulse(pr, pg, pb, pa);
        }
        return color;
    }

    private enum PropertyType {
        BOOLEAN,
        FLOAT,
        INTEGER,
        HCOLOR,
        HCOLOR_ARRAY,
    }
}
