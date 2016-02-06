package xyz.hexagons.client.engine.render;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * @author Sebastian Krajewski on 05.04.15.
 */
public interface Renderer {
	public void render(ShapeRenderer renderer, float delta, boolean shadow, int shadLev);
	public int getIndex();
	public void update(float delta);
}
