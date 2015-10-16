package me.wieku.hexagons.engine.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.wieku.hexagons.engine.Game;
import me.wieku.hexagons.Main;
import me.wieku.hexagons.api.CurrentMap;
import me.wieku.hexagons.api.HColor;

/**
 * @author Sebastian Krajewski on 21.03.15.
 */
public class Center implements Renderer {

	Color shadow = new Color();

	Vector2 tmp = new Vector2();
	Vector2 tmp2 = new Vector2();

	@Override
	public void render(ShapeRenderer renderer, float delta, boolean shadows, int shadLev) {

		shadow.set(CurrentMap.data.walls.r, CurrentMap.data.walls.g, CurrentMap.data.walls.b, CurrentMap.data.walls.a).lerp(Color.BLACK, 0.4f);
		float pulseSpeed =  CurrentMap.data.pulseMin / CurrentMap.data.pulse;
		for (float i = 0; i < CurrentMap.data.sides; ++i) {

			if(!shadows)
				if(CurrentMap.data.colors.size() > 0){
					HColor col = CurrentMap.data.colors.get(CurrentMap.data.colorOffset);
					renderer.setColor(col.r, col.g, col.b, col.a);
				} else
					renderer.setColor(Color.WHITE);
			else
				renderer.setColor(shadow.r, shadow.g, shadow.b, (shadow.a/CurrentMap.data.alphaMultiplier)-shadLev*CurrentMap.data.alphaFalloff);


			tmp.set(0, Main.diagonal * 0.048f * Game.scale).rotate(i / CurrentMap.data.sides * -360f);
			tmp2.set(0, Main.diagonal * 0.048f * Game.scale).rotate((i + 1) / CurrentMap.data.sides * -360f);

			if(!shadows){
				renderer.triangle(0, 0, tmp.x, tmp.y, tmp2.x, tmp2.y);
				renderer.setColor(CurrentMap.data.walls.r, CurrentMap.data.walls.g, CurrentMap.data.walls.b, CurrentMap.data.walls.a);
			}

			renderer.circle(tmp.x, tmp.y, 3 * pulseSpeed);
			renderer.rectLine(tmp, tmp2, 6 * pulseSpeed);

		}

	}

	@Override
	public void update(float delta){}

	@Override
	public int getIndex(){
		return 2;
	}

}
