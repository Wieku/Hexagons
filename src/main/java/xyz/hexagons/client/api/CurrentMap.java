package xyz.hexagons.client.api;

import xyz.hexagons.client.engine.timeline.Timeline;
import xyz.hexagons.client.engine.timeline.TimelineRunnable;

import java.util.ArrayList;

/**
 * @author Sebastian Krajewski on 21.03.15.
 */
public abstract class CurrentMap {

	public static class Data {
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



	public static Data data = new Data();
	/** rotations/s *//*
	public static float rotationSpeed = 0.5f;
	public static float rotationSpeedMax = 1.5f;
	public static float rotationIncrement = 0.083f;
	public static float fastRotate = 70f;
	public static boolean isFastRotation = false;

	public static float difficulty = 1f;
	public static float levelIncrement = 15f;
	public static float delayMult = 1f;
	public static float delayMultInc = 0.01f;
	public static float speed = 1f ;
	public static float speedInc = 0.125f;
	public static float currentTime = 0f;

	*//** sides *//*
	public static int sides = 6;
	public static int minSides = 5;
	public static int maxSides = 7;
	public static boolean mustChangeSides = false;

	*//**pulse*//*
	public static float beatPulseMin = 1.0f;
	public static float beatPulseMax = 1.2f;
	public static float beatPulseDelay = 0.5f;
	public static float beatPulse = 1.0f;

	*//**wallpulse*//*
	public static float pulseMin = 70;
	public static float pulseMax = 90;
	public static float pulseSpeed = 1.0f;
	public static float pulseSpeedR = 0.6f;
	public static float pulseDelayMax = 0;
	public static float pulse = 75;
	public static int pulseDir = 1;

	*//**colors*//*
	public static ArrayList<HColor> colors = new ArrayList<>();
	//public static float menuColor = 0f;
	public static float colorPulse = 3f;
	public static float colorPulseInc = 1f;
	public static int colorOffset = 0;
	public static float colorSwitch = 1f;
	public static HColor walls = new HColor(1, 1, 1, 1);

	*//**gfx settings *//*
	public static int layers = 6;
	public static float depth = 1.6f;
	public static float skew = 0f;
	public static float minSkew = 0f;
	public static float maxSkew = 1f;
	public static float skewTime = 5f;
	public static float wallSkewLeft = 0f;
	public static float wallSkewRight = 0f;
	public static float alphaMultiplier = 1f;
	public static float alphaFalloff = 0f;

	public static Timeline<Wall> wallTimeline = new Timeline<>();
	public static Timeline<TimelineRunnable> eventTimeline = new Timeline<>();*/


	public static void reset(){
		//wallTimeline.reset();
		//eventTimeline.reset();
		//resetValues();
		data = new Data();
	}

	/*private static void resetValues(){
		rotationSpeed = 0.25f;
		rotationSpeedMax = 0.5f;
		rotationIncrement = 0.01f;
		fastRotate = 70f;
		isFastRotation = false;
		
		difficulty = 1f;
		levelIncrement = 15f;
		delayMult = 1f;
		delayMultInc = 0.00f;
		speed = 1f;
		speedInc = 0f;
		currentTime = 0f;
		
		*//** sides *//*
		sides = 6;
		minSides = 5;
		maxSides = 7;
		mustChangeSides = false;

		*//**pulse*//*
		beatPulseMin = 1.0f;
		beatPulseMax = 1.2f;
		beatPulseDelay = 0.5f;
		beatPulse = 1.0f;

		*//**wallpulse*//*
		pulseMin = 70;
		pulseMax = 90;
		pulseSpeed = 1.0f;
		pulseSpeedR = 0.6f;
		pulseDelayMax = 0;
		pulse = 75;
		pulseDir = 1;

		*//**colors*//*
		colors.clear();
		colorPulse = 3f;
		colorPulseInc = 1f;
		colorOffset = 0;
		colorSwitch = 1f;
		walls = new HColor(1, 1, 1, 1);
		
		*//**gfx settings *//*
		layers = 6;
		depth = 1.6f;
		skew = 0f;
		minSkew = 0f;
		maxSkew = 1f;
		skewTime = 5f;
		wallSkewLeft = 0f;
		wallSkewRight = 0f;
		alphaMultiplier = 1f;
		alphaFalloff = 0f;
	}*/
	
	
	public static class TextInfo {
		public String text;
		public float duration;
		public boolean visible = false;
		public TextInfo(String text, float duration) {
			this.text = text;
			this.duration = duration;
		}
	}

	public static TextInfo currentText = null;

	public static void pushText(String text, float duration){
		currentText = new TextInfo(text, duration);
	}

	public static void setRotationSpeed(float rotationSpeed) {
		data.rotationSpeed = rotationSpeed;
	}

	public static void setRotationSpeedMax(float rotationSpeedMax) {
		data.rotationSpeedMax = rotationSpeedMax;
	}

	public static void setRotationIncrement(float rotationIncrement) {
		data.rotationIncrement = rotationIncrement;
	}

	public static void setFastRotate(float fastRotate) {
		data.fastRotate = fastRotate;
	}

	public static void setIsFastRotation(boolean isFastRotation) {
		data.isFastRotation = isFastRotation;
	}

	public static void setDifficulty(float difficulty) {
		data.difficulty = difficulty;
	}

	public static void setLevelIncrement(float levelIncrement) {
		data.levelIncrement = levelIncrement;
	}

	public static void setDelayMult(float delay) {
		data.delayMult = delay;
	}

	public static void setDelayMultInc(float delayInc) {
		data.delayMultInc = delayInc;
	}

	public static void setSpeed(float speed) {
		data.speed = speed;
	}

	public static void setSpeedInc(float speedInc) {
		data.speedInc = speedInc;
	}

	public static void setCurrentTime(float currentTime) {
		data.currentTime = currentTime;
	}

	public static void setSides(int sides) {
		data.sides = sides;
	}

	public static void setMinSides(int minSides) {
		data.minSides = minSides;
	}

	public static void setMaxSides(int maxSides) {
		data.maxSides = maxSides;
	}

	public static void setMustChangeSides(boolean mustChangeSides) {
		data.mustChangeSides = mustChangeSides;
	}

	public static void setBeatPulseMin(float beatPulseMin) {
		data.beatPulseMin = beatPulseMin;
	}

	public static void setBeatPulseMax(float beatPulseMax) {
		data.beatPulseMax = beatPulseMax;
	}

	public static void setBeatPulseDelay(float beatPulseDelay) {
		data.beatPulseDelay = beatPulseDelay;
	}

	public static void setPulseMin(float pulseMin) {
		data.pulseMin = pulseMin;
	}

	public static void setPulseMax(float pulseMax) {
		data.pulseMax = pulseMax;
		data.pulse = pulseMax;
		data.pulseDir = 1;
	}

	public static void setPulseSpeed(float pulseSpeed) {
		data.pulseSpeed = pulseSpeed;
	}

	public static void setPulseSpeedR(float pulseSpeedR) {
		data.pulseSpeedR = pulseSpeedR;
	}

	public static void setPulseDelayMax(float pulseDelayMax) {
		data.pulseDelayMax = pulseDelayMax;
		data.pulseDelayHalfMax = pulseDelayMax / 2;
	}

	public static void setColors(ArrayList<HColor> colors) {
		data.colors = colors;
	}

	public static void setColorPulse(float colorPulse) {
		data.colorPulse = colorPulse;
	}

	public static void setColorPulseInc(float colorPulseInc) {
		data.colorPulseInc = colorPulseInc;
	}

	public static void setColorOffset(int colorOffset) {
		data.colorOffset = colorOffset;
	}

	public static void setColorSwitch(float colorSwitch) {
		data.colorSwitch = colorSwitch;
	}

	public static void setWalls(HColor walls) {
		data.walls = walls;
	}

	public static void setLayers(int layers) {
		data.layers = layers;
	}

	public static void setDepth(float depth) {
		data.depth = depth;
	}

	public static void setSkew(float skew) {
		data.skew = skew;
	}

	public static void setMinSkew(float minSkew) {
		data.minSkew = minSkew;
	}

	public static void setMaxSkew(float maxSkew) {
		data.maxSkew = maxSkew;
	}

	public static void setSkewTime(float skewTime) {
		data.skewTime = skewTime;
	}

	public static void setWallSkewLeft(float wallSkewLeft) {
		data.wallSkewLeft = wallSkewLeft;
	}

	public static void setWallSkewRight(float wallSkewRight) {
		data.wallSkewRight = wallSkewRight;
	}

	public static void setAlphaMultiplier(float alphaMultiplier) {
		data.alphaMultiplier = alphaMultiplier;
	}

	public static void setAlphaFalloff(float alphaFalloff) {
		data.alphaFalloff = alphaFalloff;
	}
}
