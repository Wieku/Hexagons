package me.wieku.hexagons.api;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import me.wieku.hexagons.Main;
import me.wieku.hexagons.engine.timeline.TimelineObject;

/**
 * @author Sebastian Krajewski on 21.03.15.
 */
public class Wall extends TimelineObject {

	public int side;
	public float thickness;
	public float speed;
	public float acceleration;
	public float speedMin;
	public float speedMax;
	public boolean pingPong;
	public float position;

	private float delta0;
	public boolean visible = false;

	public Vector2 tmp = new Vector2(), tmp2 = new Vector2(), tmp3 = new Vector2(), tmp4 = new Vector2();
	public Array<Vector2> vecs = new Array<>();

	public Wall(int side, float thickness, float speed, float acceleration, float speedMin, float speedMax, boolean pingPong) {
		this.side = side;
		this.thickness = thickness;
		this.speed = speed;
		this.acceleration = acceleration;
		this.speedMin = speedMin;
		this.speedMax = speedMax;
		this.pingPong = pingPong;

		vecs.add(tmp);
		vecs.add(tmp2);
		vecs.add(tmp4);
		vecs.add(tmp3);
	}

	public Wall(int side, float thickness, float speed, float acceleration, float speedMin, float speedMax){
		this(side, thickness, speed, acceleration, speedMin, speedMax, true);
	}

	public Wall(int side, float thickness, float speed) {
		this(side, thickness, speed, 0, 0, 0, true);
	}

	public static float pulseSpeed = 1f;
	private static float scale = 0f;

	@Override
	public void update(float delta){

		pulseSpeed = CurrentMap.pulse / CurrentMap.pulseMin;

		//delta0 += delta;

		if(!visible){
			position = Main.diagonal;
			visible = true;
		}

		//while(delta0 >= 1f / 60){

			if(acceleration > 0) {
				speed += acceleration * delta * 60f;
				if(speed > speedMax) {
					speed = speedMax;
					if(pingPong)
						acceleration *= -1f;
				}
				else if(speed < speedMin) {
					speed = speedMin;
					if(pingPong)
						acceleration *= -1f;
				}
			}

			position -= speed * 5 * 60f * delta;

			if (position + thickness <= 0){
				setToRemove(true);
			}

			float angle1 = side / (float) CurrentMap.sides * 360f;
			float angle2 = (side + 1) / (float) CurrentMap.sides * 360f;

			tmp.set(0, Math.max(0, position) * pulseSpeed).rotate(-angle1);
			tmp2.set(0, Math.max(0, position + thickness + CurrentMap.wallSkewLeft) * pulseSpeed).rotate(-angle1);
			tmp3.set(0, Math.max(0, position) * pulseSpeed).rotate(-angle2);
			tmp4.set(0, Math.max(0, position + thickness + CurrentMap.wallSkewRight) * pulseSpeed).rotate(-angle2);

		/*	delta0 -= 0.016666668f;
		}*/


	}

}
