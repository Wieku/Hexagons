package xyz.hexagons.client.api;

public interface MapEventHandler {
    void keyDown(int keyCode);
    void keyUp(int keyCode);
    void mouseDown(float x, float y, int pointer, int button);
    void mouseUp(float x, float y, int pointer, int button);
    void mouseMove(float x, float y, int pointer);
}
