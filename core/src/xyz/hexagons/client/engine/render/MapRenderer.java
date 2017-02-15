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
import xyz.hexagons.client.engine.camera.SkewCamera;

import java.util.List;

public class MapRenderer {
	private Vector2 tmp = new Vector2();
	private Vector2 tmp2 = new Vector2();

	public void renderObjects(ShapeRenderer renderer, float delta, Camera camera, Player player, List<Wall> walls) {

		renderer.setProjectionMatrix(camera.combined);
		renderer.identity();
		renderer.translate(((SkewCamera)camera).rumbleX*1.5f, 0, ((SkewCamera)camera).rumbleZ*1.5f);
		((ObjRender) renderer).setHeight(0);
		renderer.begin(ObjRender.ShapeType.Filled);
		renderBackground(renderer, delta, true, 0);
		
		float pulseSpeed = CurrentMap.gameProperties.pulse / CurrentMap.gameProperties.pulseMin;
		for (int j = 1; j <= CurrentMap.gameProperties.layers; ++j) {
			((ObjRender) renderer).setHeight(-j * CurrentMap.gameProperties.depth * 1.4f * Math.abs(CurrentMap.gameProperties.skew / CurrentMap.gameProperties.maxSkew) * Math.abs(pulseSpeed));
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

		for(HColor o: CurrentMap.gameProperties.backgroundColors) o.update(delta);

		if((delta1 += delta) >= CurrentMap.gameProperties.colorSwitch){

			++CurrentMap.gameProperties.colorOffset;

			if(CurrentMap.gameProperties.colorOffset == CurrentMap.gameProperties.backgroundColors.size()){
				CurrentMap.gameProperties.colorOffset = 0;
			}

			delta1 = 0;
		}

		if(shadows)
			for(float i = 0; i < CurrentMap.gameProperties.sides; ++i) {

				if(CurrentMap.gameProperties.backgroundColors.size() > 0){
					HColor col = CurrentMap.gameProperties.backgroundColors.get(((int)i + CurrentMap.gameProperties.colorOffset) % CurrentMap.gameProperties.backgroundColors.size());
					if(i+1==CurrentMap.gameProperties.sides && CurrentMap.gameProperties.sides % 2 == 1)
						renderer.setColor(col.r / 1.4f, col.g / 1.4f, col.b / 1.4f, col.a);
					else
						renderer.setColor(col.r, col.g, col.b, col.a);
				} else {
					renderer.setColor(0, 0, 0, 1);
				}

				tmp.set(0, Instance.diagonal * 2).rotate(i / CurrentMap.gameProperties.sides * -360f);
				tmp2.set(0, Instance.diagonal * 2).rotate((i + 1) / CurrentMap.gameProperties.sides * -360f);

				renderer.triangle(0, 0, tmp.x, tmp.y, tmp2.x, tmp2.y);

				
				if(shadLev==-10) {
					renderer.setColor(CurrentMap.gameProperties.walls.r, CurrentMap.gameProperties.walls.g, CurrentMap.gameProperties.walls.b, 1);
					renderer.line(0,0, tmp.x, tmp.y);
				}
				
			}

	}


	public void renderCenter(ShapeRenderer renderer, float delta, boolean shadows, int shadLev) {

		CurrentMap.gameProperties.shadow.update();
		float pulseSpeed =  CurrentMap.gameProperties.pulseMin / CurrentMap.gameProperties.pulse;
		for (float i = 0; i < CurrentMap.gameProperties.sides; ++i) {

			if(!shadows)
				if(CurrentMap.gameProperties.backgroundColors.size() > 0){
					HColor col = CurrentMap.gameProperties.backgroundColors.get(CurrentMap.gameProperties.colorOffset);
					renderer.setColor(col.r, col.g, col.b, col.a);
				} else
					renderer.setColor(Color.WHITE);
			else
				renderer.setColor(CurrentMap.gameProperties.shadow.r, CurrentMap.gameProperties.shadow.g, CurrentMap.gameProperties.shadow.b, (CurrentMap.gameProperties.shadow.a/CurrentMap.gameProperties.alphaMultiplier)-shadLev*CurrentMap.gameProperties.alphaFalloff);


			tmp.set(0, Instance.diagonal * 0.048f * CurrentMap.gameProperties.beatPulse).rotate(i / CurrentMap.gameProperties.sides * -360f);
			tmp2.set(tmp).rotate(-360f/CurrentMap.gameProperties.sides);
			tmp2.set(0, Instance.diagonal * 0.048f * CurrentMap.gameProperties.beatPulse).rotate((i + 1) / CurrentMap.gameProperties.sides * -360f);

			if(!shadows){
				renderer.triangle(0, 0, tmp.x, tmp.y, tmp2.x, tmp2.y);
				renderer.setColor(CurrentMap.gameProperties.walls.r, CurrentMap.gameProperties.walls.g, CurrentMap.gameProperties.walls.b, CurrentMap.gameProperties.walls.a);
			}

			renderer.circle(tmp.x, tmp.y, 3 * pulseSpeed);
			renderer.rectLine(tmp, tmp2, 6 * pulseSpeed);

		}

	}


	public void renderWalls(ShapeRenderer renderer, float delta, boolean shadows, int shadLev, List<Wall> walls) {

		if(!shadows)
			renderer.setColor(CurrentMap.gameProperties.walls.r, CurrentMap.gameProperties.walls.g, CurrentMap.gameProperties.walls.b, CurrentMap.gameProperties.walls.a);
		else {
			CurrentMap.gameProperties.shadow.update();
			renderer.setColor(CurrentMap.gameProperties.shadow.r, CurrentMap.gameProperties.shadow.g, CurrentMap.gameProperties.shadow.b, (CurrentMap.gameProperties.shadow.a/CurrentMap.gameProperties.alphaMultiplier)-shadLev*CurrentMap.gameProperties.alphaFalloff);
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
			renderer.setColor(CurrentMap.gameProperties.walls.r, CurrentMap.gameProperties.walls.g, CurrentMap.gameProperties.walls.b, CurrentMap.gameProperties.walls.a);
		else {
			CurrentMap.gameProperties.shadow.update();
			renderer.setColor(CurrentMap.gameProperties.shadow.r, CurrentMap.gameProperties.shadow.g, CurrentMap.gameProperties.shadow.b, (CurrentMap.gameProperties.shadow.a/CurrentMap.gameProperties.alphaMultiplier)-shadLev*CurrentMap.gameProperties.alphaFalloff);
		}

		renderer.triangle(player.tmp.x, player.tmp.y, player.tmp2.x, player.tmp2.y, player.tmp3.x, player.tmp3.y);
	}

}
