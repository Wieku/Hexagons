package me.wieku.hexagons.api;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import me.wieku.hexagons.Main;
import me.wieku.hexagons.engine.Game;
import me.wieku.hexagons.engine.timeline.TimelineObject;

/**
 * @author Sebastian Krajewski on 21.03.15.
 */
public class Wall extends TimelineObject {

	public static float pulseSpeed = 1f;

	public float angle1;
	public float angle2;
	public float thickness;
	public float position;

	public boolean visible = false;

	SpeedData speed;
	SpeedData curve;

	public Vector2 tmp = new Vector2(), tmp2 = new Vector2(), tmp3 = new Vector2(), tmp4 = new Vector2();
	public Array<Vector2> vecs = new Array<>();

	public Wall(int side, float thickness, SpeedData speedData, SpeedData curveData) {
		angle1 = side / (float) CurrentMap.data.sides * 360f;
		angle2 = (side+1) / (float) CurrentMap.data.sides * 360f;

		this.thickness = thickness;
		this.speed = speedData;
		this.curve = curveData;

		vecs.add(tmp);
		vecs.add(tmp2);
		vecs.add(tmp4);
		vecs.add(tmp3);
	}

	public Wall(int side, float thickness, SpeedData speedData) {
		this(side, thickness, speedData, new SpeedData(0f));
	}

	@Override
	public void update(float delta){

		pulseSpeed = CurrentMap.data.pulse / CurrentMap.data.pulseMin;

		if(!visible){
			position = Main.diagonal;
			visible = true;
		}

		speed.update(delta);
		curve.update(delta);

		position -= speed.getSpeed() * 5 * 60f * delta;

		if (position + thickness <= 0){
			setToRemove(true);
		}

		angle1 += delta * curve.getSpeed();
		angle2 += delta * curve.getSpeed();

		float gsc = Main.diagonal * 0.048f * Game.scale;

		tmp.set(0, Math.max(gsc, position * pulseSpeed)).rotate(-angle1);
		tmp2.set(0, Math.max(gsc, (position + thickness + CurrentMap.data.wallSkewLeft) * pulseSpeed)).rotate(-angle1);
		tmp3.set(0, Math.max(gsc, position * pulseSpeed)).rotate(-angle2);
		tmp4.set(0, Math.max(gsc, (position + thickness + CurrentMap.data.wallSkewRight) * pulseSpeed)).rotate(-angle2);
	}

}
