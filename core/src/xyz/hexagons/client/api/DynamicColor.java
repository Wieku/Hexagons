package xyz.hexagons.client.api;

import com.badlogic.gdx.graphics.Color;

public abstract class DynamicColor extends Color {
    public abstract void update();

    public DynamicColor() {
        super();
    }

    public DynamicColor(int rgba8888) {
        super(rgba8888);
    }

    public DynamicColor(float r, float g, float b, float a) {
        super(r, g, b, a);
    }

    public DynamicColor(Color color) {
        super(color);
    }

    public static class StaticDynamicColor extends DynamicColor {
        public StaticDynamicColor() {
            super();
        }

        public StaticDynamicColor(int rgba8888) {
            super(rgba8888);
        }

        public StaticDynamicColor(float r, float g, float b, float a) {
            super(r, g, b, a);
        }

        public StaticDynamicColor(Color color) {
            super(color);
        }
        @Override
        public void update() {}
    }
}
