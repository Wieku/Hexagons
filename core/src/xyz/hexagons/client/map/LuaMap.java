package xyz.hexagons.client.map;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import xyz.hexagons.client.api.MapScript;

public class LuaMap implements MapScript {
    private LuaFunction lInit = null;
    private LuaFunction lInitEvents = null;
    private LuaFunction lNextLevel = null;
    private LuaFunction lNextPattern = null;
    private LuaFunction lUpdate = null;
    private LuaFunction lInitColors = null;

    public LuaMap(LuaTable callbacks) {
        if(callbacks.get("init").isfunction()) {
            lInit = callbacks.get("init").checkfunction();
        }
        if(callbacks.get("initColors").isfunction()) {
            lInitColors = callbacks.get("initColors").checkfunction();
        }
        if(callbacks.get("initEvents").isfunction()) {
            lInitEvents = callbacks.get("initEvents").checkfunction();
        }
        if(callbacks.get("nextLevel").isfunction()) {
            lNextLevel = callbacks.get("nextLevel").checkfunction();
        }
        if(callbacks.get("nextPattern").isfunction()) {
            lNextPattern = callbacks.get("nextPattern").checkfunction();
        }
        if(callbacks.get("update").isfunction()) {
            lUpdate = callbacks.get("update").checkfunction();
        }
    }

    @Override
    public void onInit() {
        if(lInit != null)
            lInit.call();
    }

    @Override
    public void initEvents() {
        if(lInitEvents != null)
            lInitEvents.call();
    }

    @Override
    public void initColors() {
        if(lInitColors != null)
            lInitColors.call();
    }

    @Override
    public void nextLevel(int levelNum) {
        if(lNextLevel != null)
            lNextLevel.call();
    }

    @Override
    public void nextPattern() {
        if(lNextPattern != null)
            lNextPattern.call();
    }

    @Override
    public void update(float delta) {
        if(lUpdate != null)
            lUpdate.call();
    }
}
