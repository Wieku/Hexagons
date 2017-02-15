package xyz.hexagons.client.engine.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.menu.settings.Settings;

/**
 * @author Sebastian Krajewski on 21.03.15.
 */
public class SkewCamera extends PerspectiveCamera {

	private float currentRotation;
	public float rumbleX;
	public float rumbleZ;
	private float rumbleTime = 0;
	private float currentRumbleTime = 1;
	private float rumblePower = 0;
	private float currentRumblePower = 0;


	public SkewCamera(){
		far = 1000000f;
		near = 0.00001f;
		fieldOfView = 45f;
		update();
	}

	public void update(float delta){

		if(Settings.instance.graphics.fixedratio) {
			viewportWidth = 4;
			viewportHeight = 3;
		} else {
			viewportWidth = Gdx.graphics.getWidth();
			viewportHeight = Gdx.graphics.getHeight();
		}

		if(currentRumbleTime <= rumbleTime) {
			currentRumblePower = rumblePower * ((rumbleTime - currentRumbleTime) / rumbleTime);

			rumbleX = (MathUtils.random(1.0f) - 0.5f) * 2 * currentRumblePower;
			rumbleZ = (MathUtils.random(1.0f) - 0.5f) * 2 * currentRumblePower;

			currentRumbleTime += delta;
		} else {
			rumbleX = 0;
			rumbleZ = 0;
		}

		position.set(0, 1200f, 0).rotate(Vector3.X, MathUtils.clamp(50f * CurrentMap.gameProperties.skew, 0.00001f, 50f)).rotate(Vector3.Y, currentRotation);

		lookAt(0, 0, 0);
		up.set( 0, (CurrentMap.gameProperties.skew >= 0 ? 1 : -1), 0);

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
		currentRotation = 0;
	}
}
