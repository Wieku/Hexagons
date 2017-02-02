package xyz.hexagons.client.utils;

import com.google.common.base.Charsets;
import com.typesafe.config.*;
import org.javatuples.Pair;
import org.luaj.vm2.LuaValue;
import xyz.hexagons.client.api.HColor;
import xyz.hexagons.client.map.Hue;
import xyz.hexagons.client.utils.function.Consumer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Properties {
    protected abstract void registerProperties();

    private HashMap<String, HashSet<String>> pathList = new HashMap<>();
    private HashMap<String, Pair<PropertyType, Consumer<Object>>> setters = new HashMap<>();

    public Properties() {
        registerProperties();
    }

    protected void mkpath(String path) {
        pathList.put(path, new HashSet<String>());
    }

    protected void registerFloat(String path, final Consumer<Float> setter) {
        String[] pathParts = path.split("\\.");
        pathList.get(pathParts[0]).add(pathParts[1]);
        setters.put(path, new Pair<>(PropertyType.FLOAT, cval -> setter.accept((Float) cval)));
    }

    protected void registerInteger(String path, Consumer<Integer> setter) {
        String[] pathParts = path.split("\\.");
        pathList.get(pathParts[0]).add(pathParts[1]);
        setters.put(path, new Pair<>(PropertyType.INTEGER, cval -> setter.accept((Integer) cval)));
    }

    protected void registerHColor(String path, final Consumer<HColor> setter) {
        String[] pathParts = path.split("\\.");
        pathList.get(pathParts[0]).add(pathParts[1]);
        setters.put(path, new Pair<>(PropertyType.HCOLOR, cval -> setter.accept((HColor) cval)));
    }

    protected void registerHColorArray(String path, ArrayList<HColor> array) {

    }

    public void setProperty(String path, LuaValue value) {
        if(!setters.containsKey(path))
            throw new RuntimeException("Property at path " + path + "doesn't exist!");

        Pair<PropertyType, Consumer<Object>> setter = setters.get(path);

        if(setter.getValue0() == PropertyType.FLOAT)
            setter.getValue1().accept(value.tofloat());
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
                            case FLOAT:
                                setter.getValue1().accept((float) config.getDouble(path));
                                break;
                            case INTEGER:
                                setter.getValue1().accept(config.getInt(path));
                                break;
                            case HCOLOR:
                                // { r: 0, b: 0, g: 0, a: 1, dynamicDarkness: 2.7, hue: { min: 0, max: 360, increment: 0.7, pingPong: false } }
                                float r, g, b, a;
                                r = b = g = a = 1f;
                                System.out.println("COL: " + path);
                                System.out.println("COLH: " + path + ".hue.min");

                                if(config.hasPath(path + ".r"))
                                    r = (float) config.getDouble(path + ".r");
                                if(config.hasPath(path + ".g"))
                                    g = (float) config.getDouble(path + ".g");
                                if(config.hasPath(path + ".b"))
                                    b = (float) config.getDouble(path + ".b");
                                if(config.hasPath(path + ".a"))
                                    a = (float) config.getDouble(path + ".a");

                                HColor color = new HColor(r, g, b, a);

                                if(config.hasPath(path + ".main"))
                                    color.setMain(config.getBoolean(path + ".main"));
                                if(config.hasPath(path + ".dynamicDarkness"))
                                    color.addDynamicDarkness((float) config.getDouble(path + ".dynamicDarkness"));
                                if(config.hasPath(path + ".hue")) {
                                    Hue hue = new Hue(0f,360f,1f, false, false);
                                    if(config.hasPath(path + ".hue.min"))
                                        hue.hueMin = (float) config.getDouble(path + ".hue.min");
                                    if(config.hasPath(path + ".hue.max"))
                                        hue.hueMax = (float) config.getDouble(path + ".hue.max");
                                    if(config.hasPath(path + ".hue.increment"))
                                        hue.hueInc = (float) config.getDouble(path + ".hue.increment");
                                    if(config.hasPath(path + ".hue.pingPong"))
                                        hue.pingPong = config.getBoolean(path + ".hue.pingPong");
                                    if(config.hasPath(path + ".hue.shared"))
                                        hue.shared = config.getBoolean(path + ".hue.shared");
                                    color.addHue(hue);
                                }
                                if(config.hasPath(path + ".pulse")) {
                                    float pr, pg, pb, pa;
                                    pr = pb = pg = pa = 1f;
                                    if(config.hasPath(path + ".pulse.r"))
                                        pr = (float) config.getDouble(path + ".pulse.r");
                                    if(config.hasPath(path + ".pulse.g"))
                                        pg = (float) config.getDouble(path + ".pulse.g");
                                    if(config.hasPath(path + ".pulse.b"))
                                        pb = (float) config.getDouble(path + ".pulse.b");
                                    if(config.hasPath(path + ".pulse.a"))
                                        pa = (float) config.getDouble(path + ".pulse.a");
                                    color.addPulse(pr, pg, pb, pa);
                                }
                                setter.getValue1().accept(color);
                                break;
                        }

                    }
                }
            }
        }
    }

    private enum PropertyType {
        FLOAT,
        INTEGER,
        HCOLOR,
    }
}
