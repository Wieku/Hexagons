package xyz.hexagons.client.api;


import xyz.hexagons.client.utils.function.Consumer;

import java.util.ArrayList;

public abstract class Properties {
    abstract void registerProperties();

    protected void registerFloat(String path, Consumer<Float> setter) {

    }

    protected void registerInteger(String path, Consumer<Integer> setter) {

    }

    protected void registerBoolean(String path, Consumer<Boolean> setter) {

    }

    protected void registerHColor(String path, HColor color) {

    }

    protected void registerHColorArray(String path, ArrayList<HColor> array) {

    }

}
