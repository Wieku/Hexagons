package xyz.hexagons.client.engine;

import xyz.hexagons.client.api.HColor;
import xyz.hexagons.client.api.Wall;
import xyz.hexagons.client.map.timeline.Timeline;
import xyz.hexagons.client.map.timeline.TimelineRunnable;

import java.util.ArrayList;

public class Map {




}

class Data {
	public float rotationSpeed = 0.5f;
	public float rotationSpeedMax = 1.5f;
	public float rotationIncrement = 0.083f;
	public float fastRotate = 70f;
	public boolean isFastRotation = false;

	public float difficulty = 1f;
	public float levelIncrement = 15f;
	public float delayMult = 1f;
	public float delayMultInc = 0.01f;
	public float speed = 1f ;
	public float speedInc = 0.125f;
	public float currentTime = 0f;

	/** sides */
	public int sides = 6;
	public int minSides = 5;
	public int maxSides = 7;
	public boolean mustChangeSides = false;

	/**pulse*/
	public float beatPulseMin = 1.0f;
	public float beatPulseMax = 1.2f;
	public float beatPulseDelay = 0.5f;
	public float beatPulse = 1.0f;

	/**wallpulse*/
	public float pulseMin = 70;
	public float pulseMax = 90;
	public float pulseSpeed = 1.0f;
	public float pulseSpeedR = 0.6f;
	public float pulseDelayHalfMax = 0;
	public float pulseDelayMax = 0;
	public float pulse = 75;
	public int pulseDir = 1;

	/**colors*/
	public ArrayList<HColor> colors = new ArrayList<>();
	//public float menuColor = 0f;
	public float colorPulse = 3f;
	public float colorPulseInc = 1f;
	public int colorOffset = 0;
	public float colorSwitch = 1f;
	public HColor walls = new HColor(1, 1, 1, 1);

	/**gfx settings */
	public int layers = 6;
	public float depth = 1.6f;
	public float skew = 0f;
	public float minSkew = 0f;
	public float maxSkew = 1f;
	public float skewTime = 5f;
	public float wallSkewLeft = 0f;
	public float wallSkewRight = 0f;
	public float alphaMultiplier = 1f;
	public float alphaFalloff = 0f;

	public Timeline<Wall> wallTimeline = new Timeline<>();
	public Timeline<TimelineRunnable> eventTimeline = new Timeline<>();
}
class Status {

}