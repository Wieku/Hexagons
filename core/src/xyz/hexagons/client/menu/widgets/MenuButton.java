package xyz.hexagons.client.menu.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import xyz.hexagons.client.utils.GUIHelper;

public class MenuButton extends Button {

	private Color bgColor = new Color(0x272727ff);
	private Color bgFocusColor = new Color(0x4d4d4dff);
	private Color tmpColor = new Color();
	private Color lineColor = new Color(0x02eafaff);

	private boolean selected = false;

	private ShapeRenderer renderer;

	private float mixin = 0f;
	private float fluctuate = 0f;
	private int fluctDir = 1;

	Vector2 st = new Vector2();
	Vector2 lo = new Vector2();

	public MenuButton(String text){
		super(GUIHelper.getBlankButtonStyle());
		renderer = new ShapeRenderer();
		left();
		addListener(new ClickListener(){
			
		});
		add(new Label(text, GUIHelper.getLabelStyle(new Color(0xa0a0a0ff), 40))).padLeft(70);
	}

	@Override
	public void act(float delta) {
		
		super.act(delta);

		fluctuate = MathUtils.clamp((fluctuate += (delta/(5f/4)) * fluctDir), 0f, 0.4f);
		fluctDir = (fluctuate == 0.4f ? -1 : fluctuate == 0f ? 1 : fluctDir);

		mixin = MathUtils.clamp((mixin += delta * (selected?4:-4)), 0f, 1f);

		st.set(Gdx.input.getX(), Gdx.input.getY());
		lo = stageToLocalCoordinates(st);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		renderer.identity();
		renderer.translate(getX(), getY(), 0);

		renderer.begin(ShapeType.Filled);
		
		tmpColor.set(bgColor).lerp(bgFocusColor, MathUtils.clamp(mixin - fluctuate, 0f, 1f));
		renderer.setColor(tmpColor.r, tmpColor.g, tmpColor.b, tmpColor.a*getColor().a);
		renderer.triangle(4, 4, getWidth(), 4, getWidth(), getHeight() - 4);
		renderer.triangle(4, 4, 60, getHeight() - 4, getWidth(), getHeight()-4);

		renderer.setColor(lineColor.r, lineColor.g, lineColor.b, lineColor.a * getColor().a);
		renderer.rectLine(2, 2, 60, getHeight() - 2, 4);
		renderer.circle(2, 2, 2);
		renderer.circle(60, getHeight() - 2, 2);

		renderer.end();
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
		batch.begin();
		super.draw(batch, parentAlpha);
	}

	public void select(boolean select){
		selected = select;
	}

}
