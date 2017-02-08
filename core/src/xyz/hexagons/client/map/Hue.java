package xyz.hexagons.client.map;

import xyz.hexagons.client.api.CurrentMap;

public class Hue {
	public float hueMin, hueMax, hueInc, hue;
	public boolean pingPong, shared;
	public Hue(float hueMin, float hueMax, float hueInc, boolean pingPong, boolean shared) {
		this.hueMin = hue = hueMin;
		this.hueMax = hueMax;
		this.hueInc = hueInc;
		this.pingPong = pingPong;
		this.shared = shared;
	}

	public void update (float delta) {
		hue += hueInc * delta * 60f * Math.pow(CurrentMap.gameProperties.difficulty, 0.8);

		if(hue < hueMin)
		{
			if(pingPong) { hue = hueMin; hueInc *= -1.f; }
			else hue = hueMax;
		}
		if(hue > hueMax)
		{
			if(pingPong) { hue = hueMax; hueInc *= -1.f; }
			else hue = hueMin;
		}
	}
}
