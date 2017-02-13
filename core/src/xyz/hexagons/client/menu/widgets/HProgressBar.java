package xyz.hexagons.client.menu.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.utils.GUIHelper;

public class HProgressBar extends ProgressBar {
	ShapeRenderer shape;
	public HProgressBar(float min, float max, float stepSize, boolean vertical) {
		super(min, max, stepSize, vertical, GUIHelper.getProgressBarStyle(Color.BLACK, Color.BLACK, 10));
		shape = new ShapeRenderer();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();

		shape.setProjectionMatrix(batch.getProjectionMatrix());
		shape.begin(ShapeType.Filled);

		shape.setColor(CurrentMap.gameProperties.walls.r * 0.5f, CurrentMap.gameProperties.walls.g * 0.5f, CurrentMap.gameProperties.walls.b * 0.5f, CurrentMap.gameProperties.walls.a);
		shape.rect(getX()-2, getY(), getWidth() * (getValue() / getMaxValue())+4, getHeight());

		shape.setColor(CurrentMap.gameProperties.walls.r, CurrentMap.gameProperties.walls.g, CurrentMap.gameProperties.walls.b, CurrentMap.gameProperties.walls.a);
		shape.rect(getX(), getY() + 2, getWidth() * (getValue() / getMaxValue()), getHeight() - 4);

		shape.end();

		batch.begin();
	}
}
