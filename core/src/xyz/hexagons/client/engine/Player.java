package xyz.hexagons.client.engine;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.Wall;
import xyz.hexagons.client.menu.settings.Settings;

/**
 * @author Sebastian Krajewski on 22.03.15.
 */
public class Player {

	float rot;
	public Vector2 tmp = new Vector2();
	public Vector2 tmp2 = new Vector2();
	public Vector2 tmp3 = new Vector2();

	Vector2 fCh = new Vector2();
	Vector2 lCh = new Vector2();
	Vector2 rCh = new Vector2();
	public boolean dead = false;
	int dir = 0;
	Color shadow = new Color();

	/*@Override
	public void render(ShapeRenderer renderer, float delta, boolean shadows, int shadLev) {
		if(!shadows)
			renderer.setColor(CurrentMap.data.walls.r, CurrentMap.data.walls.g, CurrentMap.data.walls.b, CurrentMap.data.walls.a);
		else {
			shadow.set(CurrentMap.data.walls.r, CurrentMap.data.walls.g, CurrentMap.data.walls.b, CurrentMap.data.walls.a).lerp(Color.BLACK, 0.4f);
			renderer.setColor(shadow.r, shadow.g, shadow.b, (shadow.a/CurrentMap.data.alphaMultiplier)-shadLev*CurrentMap.data.alphaFalloff);
		}

		renderer.triangle(tmp.x, tmp.y, tmp2.x, tmp2.y, tmp3.x, tmp3.y);
		*//*renderer.setColor(Color.CYAN);
		renderer.circle(lCh.x, lCh.y, 1);
		renderer.setColor(Color.RED);
		renderer.circle(rCh.x, rCh.y, 1);*//*
	}*/

	float delta;
	//@Override
	public void update(float delta){
		this.delta += delta;

		float oldRot = rot;

		if(!dead)
			if(Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT) || (Gdx.app.getType() == ApplicationType.Desktop && Gdx.input.isButtonPressed(Buttons.LEFT))
					|| (Gdx.app.getType() == ApplicationType.Android && Gdx.input.getX() <= Gdx.graphics.getWidth()/2 && Gdx.input.isTouched())){
				rot -= (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)? 4.725f : 9.45f) * 60f * delta;
				dir = -1;
			} else if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT) || (Gdx.app.getType() == ApplicationType.Desktop && Gdx.input.isButtonPressed(Buttons.RIGHT))
					|| (Gdx.app.getType() == ApplicationType.Android && Gdx.input.getX() >= Gdx.graphics.getWidth()/2 && Gdx.input.isTouched())){
				rot += (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)? 4.725f : 9.45f) * 60f * delta;
				dir = 1;
			}

		rot = (rot < 0 ? rot + 360f : (rot > 360f ? rot - 360f : rot));

		fCh.set(0, 0.061f * Instance.diagonal * Game.scale).rotate(rot);
		lCh.set(0, 0.01f).rotate(rot-90).add(fCh);
		rCh.set(0, 0.01f).rotate(rot+90).add(fCh);

		for(Wall wall : CurrentMap.data.wallTimeline.getObjects()){

			if(Settings.instance.gameplay.invincibility) continue;

			if((dir == -1 && (Intersector.intersectSegmentPolygon(fCh, lCh, wall.getPolygon()) || Intersector.isPointInPolygon(wall.getPolygon().getVertices(), 0, 8, lCh.x, lCh.y)))
					|| (dir == 1 && (Intersector.intersectSegmentPolygon(fCh, rCh, wall.getPolygon()) || Intersector.isPointInPolygon(wall.getPolygon().getVertices(), 0, 8, rCh.x, rCh.y)))) {
				rot = oldRot;
			}

			if(Intersector.isPointInPolygon(wall.getPolygon().getVertices(), 0, 8, tmp.x, tmp.y)){
				dead = true;
			}
		}

		tmp.set(0, 0.061f * Instance.diagonal * Game.scale).rotate(rot);
		tmp2.set(0, 0.055f * Instance.diagonal * Game.scale).rotate(rot - 6);
		tmp3.set(0, 0.055f * Instance.diagonal * Game.scale).rotate(rot + 6);

	}

	//@Override
	public int getIndex(){
		return 3;
	}

	public void reset(){
		dead = false;
	}

}
