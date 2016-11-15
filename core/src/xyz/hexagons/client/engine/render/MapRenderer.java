package xyz.hexagons.client.engine.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.HColor;
import xyz.hexagons.client.api.Wall;
import xyz.hexagons.client.engine.Game;
import xyz.hexagons.client.engine.Player;

import java.util.List;

import static jdk.nashorn.internal.objects.NativeArray.forEach;

public class MapRenderer {

	private Color shadow = new Color();

	private Vector2 tmp = new Vector2();
	private Vector2 tmp2 = new Vector2();


	public void renderObjects(ShapeRenderer renderer, float delta, Camera camera, /*Style style,*/ Player player, List<Wall> walls) {

		renderer.setProjectionMatrix(camera.combined);
		renderer.identity();
		((ObjRender) renderer).setHeight(0);
		renderer.begin(ObjRender.ShapeType.Filled);
		renderBackground(renderer, delta, true, 0);


		for (int j = 1; j <= CurrentMap.data.layers; ++j) {
			((ObjRender) renderer).setHeight(-j * CurrentMap.data.depth * 1.4f * Math.abs(CurrentMap.data.skew / CurrentMap.data.maxSkew));
			renderPlayer(renderer, delta, true, j - 1, player);
			renderWalls(renderer, delta, true, j - 1, walls);
			renderCenter(renderer, delta, true, j - 1);
		}

		((ObjRender) renderer).setHeight(0);

		renderPlayer(renderer, delta, false, 0, player);
		renderWalls(renderer, delta, false, 0, walls);
		renderCenter(renderer, delta, false, 0);
		renderer.end();

	}


	private float delta1;
	public void renderBackground(ShapeRenderer renderer, float delta, boolean shadows, int shadLev) {

		for(HColor o: CurrentMap.data.colors) o.update(delta);

		if((delta1 += delta) >= CurrentMap.data.colorSwitch){

			++CurrentMap.data.colorOffset;

			if(CurrentMap.data.colorOffset == CurrentMap.data.colors.size()){
				CurrentMap.data.colorOffset = 0;
			}

			delta1 = 0;
		}

		if(shadows)
			for(float i = 0; i < CurrentMap.data.sides; ++i) {

				if(CurrentMap.data.colors.size() > 0){
					HColor col = CurrentMap.data.colors.get(((int)i + CurrentMap.data.colorOffset) % CurrentMap.data.colors.size());
					if(i+1==CurrentMap.data.sides && CurrentMap.data.sides % 2 == 1)
						renderer.setColor(col.r / 1.4f, col.g / 1.4f, col.b / 1.4f, col.a);
					else
						renderer.setColor(col.r, col.g, col.b, col.a);
				} else {
					renderer.setColor(0, 0, 0, 1);
				}

				tmp.set(0, Instance.diagonal * 2).rotate(i / CurrentMap.data.sides * -360f);
				tmp2.set(0, Instance.diagonal * 2).rotate((i + 1) / CurrentMap.data.sides * -360f);

				renderer.triangle(0, 0, tmp.x, tmp.y, tmp2.x, tmp2.y);

			}

	}


	public void renderCenter(ShapeRenderer renderer, float delta, boolean shadows, int shadLev) {

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


			tmp.set(0, Instance.diagonal * 0.048f * Game.scale).rotate(i / CurrentMap.data.sides * -360f);
			tmp2.set(tmp).rotate(-360f/CurrentMap.data.sides);
			tmp2.set(0, Instance.diagonal * 0.048f * Game.scale).rotate((i + 1) / CurrentMap.data.sides * -360f);

			if(!shadows){
				renderer.triangle(0, 0, tmp.x, tmp.y, tmp2.x, tmp2.y);
				renderer.setColor(CurrentMap.data.walls.r, CurrentMap.data.walls.g, CurrentMap.data.walls.b, CurrentMap.data.walls.a);
			}

			renderer.circle(tmp.x, tmp.y, 3 * pulseSpeed);
			renderer.rectLine(tmp, tmp2, 6 * pulseSpeed);

		}

	}


	public void renderWalls(ShapeRenderer renderer, float delta, boolean shadows, int shadLev, List<Wall> walls){

		if(!shadows)
			renderer.setColor(CurrentMap.data.walls.r, CurrentMap.data.walls.g, CurrentMap.data.walls.b, CurrentMap.data.walls.a);
		else {
			shadow.set(CurrentMap.data.walls.r, CurrentMap.data.walls.g, CurrentMap.data.walls.b, CurrentMap.data.walls.a).lerp(Color.BLACK, 0.4f);
			renderer.setColor(shadow.r, shadow.g, shadow.b, (shadow.a/CurrentMap.data.alphaMultiplier)-shadLev*CurrentMap.data.alphaFalloff);
		}

		for(Wall wall : walls) {
			if(wall.visible) {
				float[] vert = wall.getPolygon().getVertices();
				renderer.triangle(vert[0], vert[1], vert[2], vert[3], vert[4], vert[5]);
				renderer.triangle(vert[4], vert[5], vert[6], vert[7], vert[0], vert[1]);
			}
		}
	}

	public Vector2 tmp11 = new Vector2();
	public Vector2 tmp22 = new Vector2();
	public Vector2 tmp33 = new Vector2();

	public void renderPlayer(ShapeRenderer renderer, float delta, boolean shadows, int shadLev, Player player) {
		if(!shadows)
			renderer.setColor(CurrentMap.data.walls.r, CurrentMap.data.walls.g, CurrentMap.data.walls.b, CurrentMap.data.walls.a);
		else {
			shadow.set(CurrentMap.data.walls.r, CurrentMap.data.walls.g, CurrentMap.data.walls.b, CurrentMap.data.walls.a).lerp(Color.BLACK, 0.4f);
			renderer.setColor(shadow.r, shadow.g, shadow.b, (shadow.a/CurrentMap.data.alphaMultiplier)-shadLev*CurrentMap.data.alphaFalloff);
		}

		renderer.triangle(player.tmp.x, player.tmp.y, player.tmp2.x, player.tmp2.y, player.tmp3.x, player.tmp3.y);
	}

}
