package me.wieku.hexagons.engine.menu.buttons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import me.wieku.hexagons.utils.GUIHelper;

public class MenuButton extends Table {

	Color color = new Color(0x272727ff);
	Color colorh = new Color(0x4d4d4dff);
	Color color1 = new Color(0x02eafaff);
	boolean selected = false;
	Vector2 vec = new Vector2().set(0, 4).rotate(22.5f);
	ShapeRenderer renderer;
	String text;
	public MenuButton(String text){
		this.text = text;
		renderer = new ShapeRenderer();
		left();
		add(new Label(text, GUIHelper.getLabelStyle(new Color(0xa0a0a0ff), 40))).padLeft(70);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {


		batch.end();

		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		renderer.identity();
		renderer.translate(getX(), getY(), 0);
		renderer.begin(ShapeType.Filled);
		renderer.setColor(selected?colorh:color);

		renderer.triangle(2 + vec.x, 2 + vec.y, getWidth(), 2 + vec.y, getWidth(), getHeight() - 2 - vec.y);
		renderer.triangle(getWidth(), getHeight() - 2 - vec.y, 60 - vec.x, getHeight()-2-vec.y, 2 + vec.x, 2 + vec.y);
		//renderer.rect(60-vec.x, 2 + vec.y, getWidth() - 60 - 2, getHeight() - 4 - vec.y*2);
		renderer.setColor(color1);
		renderer.rectLine(2, 2, 60, getHeight() - 2, 4);
		renderer.circle(2, 2, 2);
		renderer.circle(60, getHeight() - 2, 2);

		renderer.end();
		batch.begin();
		super.draw(batch, parentAlpha);
	}

	public void select(boolean select){
		selected = select;
	}

}
