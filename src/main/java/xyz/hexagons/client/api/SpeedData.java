package xyz.hexagons.client.api;

public class SpeedData {

	private float speed;
	private float acceleration;
	private float speedMin;
	private float speedMax;
	private boolean pingPong;
	private boolean accel;

	public SpeedData(float speed) {
		this.speed = speed;
		this.accel = false;
	}

	public SpeedData(float speed, float acceleration, float speedMin, float speedMax) {
		this.speed = speed;
		this.acceleration = acceleration;
		this.speedMin = speedMin;
		this.speedMax = speedMax;
		this.pingPong = false;
		this.accel = true;
	}

	public SpeedData(float speed, float acceleration, float speedMin, float speedMax, boolean pingPong) {
		this.speed = speed;
		this.acceleration = acceleration;
		this.speedMin = speedMin;
		this.speedMax = speedMax;
		this.pingPong = pingPong;
		this.accel = true;
	}

	public void update(float delta) {
		if(accel) {
			speed += acceleration * 60f * delta;

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
	}

	public boolean isPingPong() {
		return pingPong;
	}

	public float getSpeed() {
		return speed;
	}

	public float getAcceleration() {
		return acceleration;
	}

	public float getSpeedMin() {
		return speedMin;
	}

	public float getSpeedMax() {
		return speedMax;
	}
}
