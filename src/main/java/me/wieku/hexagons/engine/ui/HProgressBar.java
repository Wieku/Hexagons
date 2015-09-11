package me.wieku.hexagons.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import me.wieku.hexagons.api.CurrentMap;
import me.wieku.hexagons.utils.GUIHelper;

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

		float scale = getHeight() / 6f;

		shape.setColor(CurrentMap.walls.r * 0.5f, CurrentMap.walls.g * 0.5f, CurrentMap.walls.b * 0.5f, CurrentMap.walls.a);
		shape.rect(getX() + scale, getY(), (getWidth() - scale) * (getValue() / getMaxValue()), getHeight() - scale);

		shape.setColor(CurrentMap.walls.r, CurrentMap.walls.g, CurrentMap.walls.b, CurrentMap.walls.a);
		shape.rect(getX(), getY() + scale, (getWidth() - scale) * (getValue() / getMaxValue()), getHeight() - scale);

		shape.end();

		batch.begin();
		//super.draw(batch, parentAlpha);
	}
}
