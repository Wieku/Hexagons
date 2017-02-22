package xyz.hexagons.client.engine.lua;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import xyz.hexagons.client.api.MapEventHandler;
import xyz.hexagons.client.api.MapScript;

public class LuaMapScript implements MapScript {
    private final LuaFunction lInit;
    private final LuaFunction lInitEvents;
    private final LuaFunction lNextLevel;
    private final LuaFunction lNextPattern;
    private final LuaFunction lUpdate;
    private final LuaFunction lInitColors;
    private final MapEventHandler eventHandler;

    public LuaMapScript(LuaTable callbacks) {
        lInit = callbacks.get("init").isfunction() ? callbacks.get("init").checkfunction() : null;
        lInitColors = callbacks.get("initColors").isfunction() ? callbacks.get("initColors").checkfunction() : null;
        lInitEvents = callbacks.get("initEvents").isfunction() ? callbacks.get("initEvents").checkfunction() : null;
        lNextLevel = callbacks.get("nextLevel").isfunction() ? callbacks.get("nextLevel").checkfunction() : null;
        lNextPattern = callbacks.get("nextPattern").isfunction() ? callbacks.get("nextPattern").checkfunction() : null;
        lUpdate = callbacks.get("update").isfunction() ? callbacks.get("update").checkfunction() : null;

        eventHandler = new LuaMapEventHandler(callbacks);
    }

    @Override
    public void init() {
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
            lNextLevel.call(LuaValue.valueOf(levelNum));
    }

    @Override
    public void nextPattern() {
        if(lNextPattern != null)
            lNextPattern.call();
    }

    @Override
    public void update(float delta) {
        if(lUpdate != null)
            lUpdate.call(LuaValue.valueOf(delta));
    }

    @Override
    public MapEventHandler getEventHandlers() {
        return eventHandler;
    }

    private class LuaMapEventHandler implements MapEventHandler {
        private final LuaFunction keyDown;
        private final LuaFunction keyUp;
        private final LuaFunction mouseDown;
        private final LuaFunction mouseUp;
        private final LuaFunction mouseMove;

        public LuaMapEventHandler(LuaTable callbacks) {
            LuaTable event = callbacks.get("event").checktable();

            keyDown = event.get("keyDown").isfunction() ? event.get("keyDown").checkfunction() : null;
            keyUp = event.get("keyUp").isfunction() ? event.get("keyUp").checkfunction() : null;
            mouseDown = event.get("mouseDown").isfunction() ? event.get("mouseDown").checkfunction() : null;
            mouseUp = event.get("mouseUp").isfunction() ? event.get("mouseUp").checkfunction() : null;
            mouseMove = event.get("mouseMove").isfunction() ? event.get("mouseMove").checkfunction() : null;
        }

        @Override
        public void keyDown(int keyCode) {
            if(keyDown != null)
                keyDown.call(LuaValue.valueOf(keyCode));
        }

        @Override
        public void keyUp(int keyCode) {
            if(keyUp != null)
                keyUp.call(LuaValue.valueOf(keyCode));
        }

        @Override
        public void mouseDown(float x, float y, int pointer, int button) {
            if(mouseDown != null)
                mouseDown.invoke(new LuaValue[]{LuaValue.valueOf(x), LuaValue.valueOf(y), LuaValue.valueOf(pointer), LuaValue.valueOf(button)});
        }

        @Override
        public void mouseUp(float x, float y, int pointer, int button) {
            if(mouseUp != null)
                mouseUp.invoke(new LuaValue[]{LuaValue.valueOf(x), LuaValue.valueOf(y), LuaValue.valueOf(pointer), LuaValue.valueOf(button)});
        }

        @Override
        public void mouseMove(float x, float y, int pointer) {
            if(mouseMove != null)
                mouseMove.invoke(LuaValue.valueOf(x), LuaValue.valueOf(y), LuaValue.valueOf(pointer));
        }
    }
}
