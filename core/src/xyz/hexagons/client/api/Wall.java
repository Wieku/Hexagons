package xyz.hexagons.client.api;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.engine.Game;
import xyz.hexagons.client.map.timeline.TimelineObject;

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

	public Vector2 tmp = new Vector2();
	//public Array<Vector2> vecs = new Array<>();

	float[] temp = new float[8];

	Polygon polygon = new Polygon();

	public Wall(int side, float thickness, SpeedData speedData, SpeedData curveData) {
		angle1 = side / (float) CurrentMap.gameProperties.sides * 360f;
		angle2 = (side+1) / (float) CurrentMap.gameProperties.sides * 360f;

		this.thickness = thickness;
		this.speed = speedData;
		this.curve = curveData;
	}

	public Wall(int side, float thickness, SpeedData speedData) {
		this(side, thickness, speedData, new SpeedData(0f));
	}

	@Override
	public void update(float delta){

		pulseSpeed = CurrentMap.gameProperties.pulse / CurrentMap.gameProperties.pulseMin;

		if(!visible){
			position = Instance.diagonal;
			visible = true;
		}

		speed.update(delta);
		curve.update(delta);

		position -= speed.getSpeed() * 300f * delta;

		if (position + thickness <= 0){
			setToRemove(true);
		}

		angle1 += delta * curve.getSpeed();
		angle2 += delta * curve.getSpeed();

		float gsc = Instance.diagonal * 0.048f * CurrentMap.gameProperties.beatPulse;

		tmp.set(0, Math.max(gsc, position * pulseSpeed)).rotate(-angle1);
		temp[0] = tmp.x; temp[1] = tmp.y;
		tmp.set(0, Math.max(gsc, (position + thickness + CurrentMap.gameProperties.wallSkewLeft) * pulseSpeed)).rotate(-angle1);
		temp[2] = tmp.x; temp[3] = tmp.y;
		tmp.set(0, Math.max(gsc, (position + thickness + CurrentMap.gameProperties.wallSkewRight) * pulseSpeed)).rotate(-angle2);
		temp[4] = tmp.x; temp[5] = tmp.y;
		tmp.set(0, Math.max(gsc, position * pulseSpeed)).rotate(-angle2);
		temp[6] = tmp.x; temp[7] = tmp.y;

		polygon.setVertices(temp);
	}

	public Polygon getPolygon() {
		return polygon;
	}

}
