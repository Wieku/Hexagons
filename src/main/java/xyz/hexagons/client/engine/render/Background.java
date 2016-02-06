package xyz.hexagons.client.engine.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import xyz.hexagons.client.Main;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.HColor;

/**
 * @author Sebastian Krajewski on 21.03.15.
 */
public class Background implements Renderer {

	public Background(){}

	Vector2 tmp = new Vector2();
	Vector2 tmp2 = new Vector2();
	Color tmpC = new Color();

	float delta1;

	@Override
	public void render(ShapeRenderer renderer, float delta, boolean shadows, int shadLev) {

		if(shadows)
			for(float i = 0; i < CurrentMap.data.sides; ++i) {

				if(CurrentMap.data.colors.size() > 0){
					HColor col = CurrentMap.data.colors.get(((int)i + CurrentMap.data.colorOffset) % CurrentMap.data.colors.size());
					if(i+1==CurrentMap.data.sides && CurrentMap.data.sides % 2 == 1)
						tmpC.set(col.r / 1.4f, col.g / 1.4f, col.b / 1.4f, col.a);
					else
						tmpC.set(col.r, col.g, col.b, col.a);
					renderer.setColor(tmpC);
				} else {
					renderer.setColor(0, 0, 0, 1);
				}

				tmp.set(0, Main.diagonal * 2).rotate(i / CurrentMap.data.sides * -360f);
				tmp2.set(0, Main.diagonal * 2).rotate((i + 1) / CurrentMap.data.sides * -360f);

				renderer.triangle(0, 0, tmp.x, tmp.y, tmp2.x, tmp2.y);

			}

	}

	@Override
	public void update(float delta){
		CurrentMap.data.colors.forEach(o -> o.update(delta));

		if((delta1 += delta) >= CurrentMap.data.colorSwitch){

			++CurrentMap.data.colorOffset;

			if(CurrentMap.data.colorOffset == CurrentMap.data.colors.size()){
				CurrentMap.data.colorOffset = 0;
			}

			delta1 = 0;
		}
	}

	@Override
	public int getIndex(){
		return 0;
	}

}
