package xyz.hexagons.client.utils;

import com.google.common.base.Charsets;
import com.typesafe.config.*;
import org.javatuples.Pair;
import org.luaj.vm2.LuaValue;
import xyz.hexagons.client.api.HColor;
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

    protected void registerHColor(String path, HColor color) {

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
                System.out.println("C: has root: " + root);
                for (String sub : pathList.get(root)) {
                    String path = root + "." + sub;
                    if(config.hasPath(path)) {
                        System.out.println("C: HAS " + path);
                        Pair<PropertyType, Consumer<Object>> setter = setters.get(path);
                        if(setter.getValue0() == PropertyType.FLOAT)
                            setter.getValue1().accept((float) config.getDouble(path));

                        if(setter.getValue0() == PropertyType.FLOAT)
                            System.out.println("SET " + path + " to " + (float) config.getDouble(path));
                    }
                }
            }
        }
    }

    private enum PropertyType {
        FLOAT,
        INTEGER,
    }
}
