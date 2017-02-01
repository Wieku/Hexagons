package xyz.hexagons.client.api;

import com.badlogic.gdx.utils.GdxRuntimeException;
import xyz.hexagons.client.map.timeline.TimelineRunnable;
import xyz.hexagons.client.utils.Utils;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author Sebastian Krajewski on 21.03.15.
 */
public abstract class CurrentMap {


	public transient static GameProperties gameProperties = new GameProperties();
	
	public static void reset(){
		gameProperties = new GameProperties();
	}
	
	public static class TextInfo {
		public String text;
		public float duration;
		public boolean visible = false;
		public TextInfo(String text, float duration) {
			this.text = text;
			this.duration = duration;
		}
	}
	
	public static void useRadians(boolean b) {
		gameProperties.useRadians = b;
	}
	
	public transient static TextInfo currentText = null;

	public static void pushText(String text, float duration){
		currentText = new TextInfo(text, duration);
	}

	public static void setRotationSpeed(float rotationSpeed) {
		gameProperties.rotationSpeed = rotationSpeed;
	}

	public static void setRotationSpeedMax(float rotationSpeedMax) {
		gameProperties.rotationSpeedMax = rotationSpeedMax;
	}

	public static void setRotationIncrement(float rotationIncrement) {
		gameProperties.rotationIncrement = rotationIncrement;
	}

	public static void setFastRotate(float fastRotate) {
		gameProperties.fastRotate = fastRotate;
	}

	public static void setIsFastRotation(boolean isFastRotation) {
		gameProperties.isFastRotation = isFastRotation;
	}

	public static void setDifficulty(float difficulty) {
		gameProperties.difficulty = difficulty;
	}

	public static void setLevelIncrement(float levelIncrement) {
		gameProperties.levelIncrement = levelIncrement;
	}

	public static void setDelayMult(float delay) {
		gameProperties.delayMult = delay;
	}

	public static void setDelayMultInc(float delayInc) {
		gameProperties.delayMultInc = delayInc;
	}

	public static void setSpeed(float speed) {
		gameProperties.speed = speed;
	}

	public static void setSpeedInc(float speedInc) {
		gameProperties.speedInc = speedInc;
	}

	public static void setCurrentTime(float currentTime) {
		gameProperties.currentTime = currentTime;
	}

	public static void setSides(int sides) {
		gameProperties.sides = sides;
	}

	public static void setMinSides(int minSides) {
		gameProperties.minSides = minSides;
	}

	public static void setMaxSides(int maxSides) {
		gameProperties.maxSides = maxSides;
	}

	public static void setMustChangeSides(boolean mustChangeSides) {
		gameProperties.mustChangeSides = mustChangeSides;
	}

	public static void setBeatPulseMin(float beatPulseMin) {
		gameProperties.beatPulseMin = beatPulseMin;
	}

	public static void setBeatPulseMax(float beatPulseMax) {
		gameProperties.beatPulseMax = beatPulseMax;
	}

	public static void setBeatPulseDelay(float beatPulseDelay) {
		gameProperties.beatPulseDelay = beatPulseDelay;
	}

	public static void setPulseMin(float pulseMin) {
		gameProperties.pulseMin = pulseMin;
	}

	public static void setPulseMax(float pulseMax) {
		gameProperties.pulseMax = pulseMax;
		gameProperties.pulse = pulseMax;
		gameProperties.pulseDir = 1;
	}

	public static void setPulseSpeed(float pulseSpeed) {
		gameProperties.pulseSpeed = pulseSpeed;
	}

	public static void setPulseSpeedR(float pulseSpeedR) {
		gameProperties.pulseSpeedR = pulseSpeedR;
	}

	public static void setPulseDelayMax(float pulseDelayMax) {
		gameProperties.pulseDelayMax = pulseDelayMax;
		gameProperties.pulseDelayHalfMax = pulseDelayMax / 2;
	}

	public static void setColors(ArrayList<HColor> colors) {
		gameProperties.backgroundColors = colors;
	}
	
	public static void setColorPulseMin(float colorPulse) {
		gameProperties.colorPulseMin = colorPulse;
	}
	
	public static void setColorPulseMax(float colorPulse) {
		gameProperties.colorPulseMax = colorPulse;
	}

	public static void setColorPulseInc(float colorPulseInc) {
		gameProperties.colorPulseInc = colorPulseInc;
	}

	public static void setColorOffset(int colorOffset) {
		gameProperties.colorOffset = colorOffset;
	}

	public static void setColorSwitch(float colorSwitch) {
		gameProperties.colorSwitch = colorSwitch;
	}

	public static void setWalls(HColor walls) {
		gameProperties.walls = walls;
	}

	public static void setLayers(int layers) {
		gameProperties.layers = layers;
	}

	public static void setDepth(float depth) {
		gameProperties.depth = depth;
	}

	public static void setSkew(float skew) {
		gameProperties.skew = skew;
	}

	public static void setMinSkew(float minSkew) {
		gameProperties.minSkew = minSkew;
	}

	public static void setMaxSkew(float maxSkew) {
		gameProperties.maxSkew = maxSkew;
	}

	public static void setSkewTime(float skewTime) {
		gameProperties.skewTime = skewTime;
	}

	public static void setWallSkewLeft(float wallSkewLeft) {
		gameProperties.wallSkewLeft = wallSkewLeft;
	}

	public static void setWallSkewRight(float wallSkewRight) {
		gameProperties.wallSkewRight = wallSkewRight;
	}

	public static void setAlphaMultiplier(float alphaMultiplier) {
		gameProperties.alphaMultiplier = alphaMultiplier;
	}

	public static void setAlphaFalloff(float alphaFalloff) {
		gameProperties.alphaFalloff = alphaFalloff;
	}
	
	public static void killPlayer() {
		gameProperties.gameCompleted = true;
	}
	
	public static void pushEvent(float time, String name, Object... data) {
		Runnable runnable = null;
		
		switch(name) {
			case "kill_player":
				runnable = CurrentMap::killPlayer;
				break;
			case "change_direction":
				runnable = () -> CurrentMap.setRotationSpeed(-CurrentMap.gameProperties.rotationSpeed);
				break;
			case "set_style":
				//TODO
				break;
			case "push_text":
				runnable = ()->pushText((String)data[0], (float)data[1]);
				break;
			default:
				if(data.length == 0) throw new GdxRuntimeException("Wrong argument size!");
				
				String methodName = "set" + name.substring(0,1).toUpperCase() + name.substring(1);
				
				for (Method method : CurrentMap.class.getDeclaredMethods()) {
					if(method.getName().equals(methodName)) {
						method.setAccessible(true);
						runnable = ()-> Utils.tryOr(()->method.invoke(null, data), null);
						break;
					}
				}
				break;
		}
		
		if(runnable != null) {
			final Runnable runnable1 = runnable;
			
			CurrentMap.gameProperties.eventTimeline.wait(time);
			CurrentMap.gameProperties.eventTimeline.submit(new TimelineRunnable() {
				@Override
				public void run() {
					runnable1.run();
				}
			});
		}
		
	}
	
}
