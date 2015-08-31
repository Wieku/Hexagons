package me.wieku.hexagons.engine.camera;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import me.wieku.hexagons.api.CurrentMap;

/**
 * @author Sebastian Krajewski on 21.03.15.
 */
public class SkewCamera extends PerspectiveCamera {

	private float currentRotation;
	private float rumbleX;
	private float rumbleZ;
	private float rumbleTime = 0;
	private float currentRumbleTime = 1;
	private float rumblePower = 0;
	private float currentRumblePower = 0;


	public SkewCamera(){
		viewportWidth = 4;
		viewportHeight = 3;
		far = 1000000f;
		near = 0.00001f;
		fieldOfView = 45f;
		update();
	}


	public void update(float delta){

		if(currentRumbleTime <= rumbleTime) {
			currentRumblePower = rumblePower * ((rumbleTime - currentRumbleTime) / rumbleTime);

			rumbleX = (MathUtils.random(1.0f) - 0.5f) * 2 * currentRumblePower;
			rumbleZ = (MathUtils.random(1.0f) - 0.5f) * 2 * currentRumblePower;

			currentRumbleTime += delta;
		} else {
			rumbleX = 0;
			rumbleZ = 0;
		}

		position.set(0, 1200f, 0).rotate(Vector3.X, Math.max(0.00001f, 50f * CurrentMap.skew)).rotate(Vector3.Y, currentRotation);

		float realRumbleX = (position.x < 0 ? Math.max(position.x, rumbleX) : Math.min(position.x, rumbleX));
		float realRumbleZ = (position.z < 0 ? Math.max(position.z, rumbleZ) : Math.min(position.z, rumbleZ));

		lookAt(realRumbleX, 0, realRumbleZ);
		up.set(Vector3.Y);

		update();
	}

	public void rotate(float angle){
		currentRotation += angle;
		currentRotation = (currentRotation >= 360f ? currentRotation - 360f : (currentRotation >= 360f? 360f - currentRotation : currentRotation));
	}

	public void rumble(float power, float time) {
		rumblePower = power;
		rumbleTime = time;
		currentRumbleTime = 0;
	}

	public void reset(){
		rumblePower = 0;
		rumbleTime = 1;
		currentRumbleTime = 0;
	}

}
