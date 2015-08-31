package net.wieku.jhexagon.engine.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import net.wieku.jhexagon.api.CurrentMap;
import net.wieku.jhexagon.api.Wall;
import net.wieku.jhexagon.engine.render.Renderer;

/**
 * @author Sebastian Krajewski on 21.03.15.
 */
public class WallRenderer implements Renderer {
	String h = "»";
	Color shadow = new Color();

	@Override
	public void render(ShapeRenderer renderer, float delta, boolean shadows){

		if(!shadows)
			renderer.setColor(CurrentMap.walls.r, CurrentMap.walls.g, CurrentMap.walls.b, CurrentMap.walls.a);
		else
			renderer.setColor(shadow.set(CurrentMap.walls.r, CurrentMap.walls.g, CurrentMap.walls.b, CurrentMap.walls.a).lerp(Color.BLACK, 0.4f));

		for(Wall wall : CurrentMap.wallTimeline.getObjects()){

			if(!wall.visible) continue;
			/*if(wall.position < 500) {
				if(!shadows)
					renderer.setColor(CurrentMap.walls.r, CurrentMap.walls.g, CurrentMap.walls.b, CurrentMap.walls.a * (Math.max(wall.position - 400, 0) / 100f));
				else
					renderer.setColor(shadow.r, shadow.g, shadow.b, shadow.a * (Math.max(wall.position - 400, 0) / 100f));
			}*/
			renderer.triangle(wall.tmp.x, wall.tmp.y, wall.tmp2.x, wall.tmp2.y, wall.tmp4.x, wall.tmp4.y);
			renderer.triangle(wall.tmp4.x, wall.tmp4.y, wall.tmp3.x, wall.tmp3.y, wall.tmp.x, wall.tmp.y);
			/*if(wall.position < 500) {
				if (!shadows)
					renderer.setColor(CurrentMap.walls.r, CurrentMap.walls.g, CurrentMap.walls.b, CurrentMap.walls.a);
				else
					renderer.setColor(shadow.r, shadow.g, shadow.b, shadow.a);
			}*/
		}

	}

	@Override
	public void update(float delta){}

	@Override
	public int getIndex(){
		return 1;
	}

}
