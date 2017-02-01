import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.HColor;
import xyz.hexagons.client.api.MapScript;
import xyz.hexagons.client.api.Patterns;
import xyz.hexagons.client.api.SpeedData;
import xyz.hexagons.client.api.Wall;
import xyz.hexagons.client.map.timeline.TimelineRunnable;

import static xyz.hexagons.client.api.MapUtils.shuffle;

/**
 * @author Sebastian Krajewski on 29.03.15.
 */
public class Hexagonest implements MapScript {

	public void addPattern(int mKey) {
		if (mKey == 0) Patterns.pAltBarrage(Patterns.random(2, 4), 2);
		else if (mKey == 1) Patterns.pMirrorSpiral(Patterns.random(3, 6), 0);
		else if (mKey == 2) Patterns.pBarrageSpiral(Patterns.random(0, 3), 1, 1);
		else if (mKey == 3) Patterns.pBarrageSpiral(Patterns.random(0, 2), 1.2f, 2);
		else if (mKey == 4) Patterns.pBarrageSpiral(2, 0.7f, 1);
		else if (mKey == 5) Patterns.pInverseBarrage(0);
		else if (mKey == 6) Patterns.pTunnel(Patterns.random(1, 3));
		else if (mKey == 7) Patterns.pMirrorWallStrip(1, 0);
		else if (mKey == 8) Patterns.pWallExVortex(0, 1, 1);
		else if (mKey == 9) pSuperhexTunnel(Patterns.random(1, 1), 1.02f);
		else if (mKey == 10) pTripleWall(Patterns.random(1, 1), 1.02f);

	}

	Integer[] keys = { 0, 0, 1, 1, 2, 2, 3, 4, 5, 5, 6, 7, 7, 8, 9, 10};
	int index = 0;

	@Override
	public void onInit() {
		shuffle(keys);
		
		CurrentMap.setRotationSpeed(0.75f);
		CurrentMap.setRotationSpeedMax(1.5f);
		CurrentMap.setRotationIncrement(0f);
		CurrentMap.setFastRotate(71f);
		
		CurrentMap.setDifficulty(1f);
		CurrentMap.setLevelIncrement(9999999f);
		CurrentMap.setSpeed(2.5f);
		CurrentMap.setDelayMult(0.85f);

		CurrentMap.setBeatPulseDelay(0.17f);

		CurrentMap.setPulseMin(70);
		CurrentMap.setPulseMax(70);

		//sides
		CurrentMap.setSides(6);
		CurrentMap.setMinSides(6);
		CurrentMap.setMaxSides(6);

		//3d settings
		CurrentMap.setLayers(0);
		CurrentMap.setDepth(0f);
		CurrentMap.setSkew(0f);
		CurrentMap.setMinSkew(0f);
		CurrentMap.setMaxSkew(0.4f);
		CurrentMap.setSkewTime(3f);

		dirChangeTime = 3f;
	}

	@Override
	public void initEvents() {
		CurrentMap.gameProperties.eventTimeline.wait(10f);
		CurrentMap.gameProperties.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.pushText("LINE", 1.2f);
			}
		});
		CurrentMap.gameProperties.eventTimeline.wait(10f);
		CurrentMap.gameProperties.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.pushText("TRIANGLE", 1.2f);
			}
		});
		CurrentMap.gameProperties.eventTimeline.wait(10f);
		CurrentMap.gameProperties.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.pushText("SQUARE", 1.2f);
			}
		});
		CurrentMap.gameProperties.eventTimeline.wait(15f);
		CurrentMap.gameProperties.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.pushText("PENTAGON", 1.2f);
			}
		});
		CurrentMap.gameProperties.eventTimeline.wait(15f);
		CurrentMap.gameProperties.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.pushText("HEXAGON", 1.2f);
			}
		});

	}

	@Override
	public void initColors(){
		//colors
		CurrentMap.gameProperties.colors.add(new HColor(96f/255, 96f/255, 96f/255, 1f).addPulse(20f / 255, 20f / 255, 20f / 255, 0f));
		CurrentMap.gameProperties.colors.add(new HColor(128f / 255, 128f / 255, 128f / 255, 1f).addPulse(20f / 255, 20f / 255, 20f / 255, 0f));

		CurrentMap.gameProperties.walls = new HColor(1,1,1,1);
		CurrentMap.setColorPulseMax(1.8f);
		CurrentMap.setColorPulseInc(0.027f);
		CurrentMap.setColorSwitch(1f);
	}

	@Override
	public void nextLevel(int levelNum) {

	}

	@Override
	public void nextPattern() {
		addPattern(keys[index]);
		++index;

		if (index == keys.length) {
			index = 1;
		}
	}

	float dirChangeTime = 3f;
	@Override
	public void update(float delta) {
		dirChangeTime = dirChangeTime - delta;
		if (dirChangeTime < 0) {
			if (!CurrentMap.gameProperties.isFastRotation) {
				CurrentMap.setRotationSpeed(CurrentMap.gameProperties.rotationSpeed * -1.0f);
				dirChangeTime = 7f;
			}
		}
	}
	
	public void pSuperhexTunnel(int mTimes, float mDelayMult){
		float oldThickness = Patterns.THICKNESS;
		float myThickness = Patterns.getPerfectThickness(Patterns.THICKNESS);
		float delay = Patterns.getPerfectDelay(myThickness) * 5.5f;
		int startSide = Patterns.getRandomSide();

		Patterns.THICKNESS = myThickness;

		for (int i = 0; i < mTimes; ++i) {
			CurrentMap.gameProperties.wallTimeline.submit(new Wall(startSide + 1, myThickness + 9 * Patterns.getSpeedMultDM() * delay, new SpeedData(Patterns.getSpeedMultDM())));
			CurrentMap.gameProperties.wallTimeline.submit(new Wall(startSide + 2, myThickness + 9 * Patterns.getSpeedMultDM() * delay, new SpeedData(Patterns.getSpeedMultDM())));
			CurrentMap.gameProperties.wallTimeline.submit(new Wall(startSide + 3, myThickness + 9 * Patterns.getSpeedMultDM() * delay, new SpeedData(Patterns.getSpeedMultDM())));
		}

		Patterns.THICKNESS = oldThickness;

		Patterns.cBarrage(startSide + 4);
		Patterns.t_wait(delay * 0.65f * mDelayMult);
		Patterns.cBarrage(startSide + 6);
		Patterns.t_wait(delay * 0.65f * mDelayMult);

		Patterns.t_wait(delay);
	}
	
	// pTripleWall
	public void pTripleWall(int mTimes, float mDelayMult) {
		float oldThickness = Patterns.THICKNESS;
		float myThickness = Patterns.getPerfectThickness(Patterns.THICKNESS);
		float delay = Patterns.getPerfectDelay(myThickness) * 5.5f;
		int startSide = Patterns.getRandomSide();

			Patterns.THICKNESS = myThickness;

		for (int i = 0; i < mTimes; ++i) {
			CurrentMap.gameProperties.wallTimeline.submit(new Wall(startSide + 1, myThickness + 0.15f * Patterns.getSpeedMultDM() * delay, new SpeedData(Patterns.getSpeedMultDM())));
			CurrentMap.gameProperties.wallTimeline.submit(new Wall(startSide + 3, myThickness + 0.15f * Patterns.getSpeedMultDM() * delay, new SpeedData(Patterns.getSpeedMultDM())));
			CurrentMap.gameProperties.wallTimeline.submit(new Wall(startSide + 5, myThickness + 0.15f * Patterns.getSpeedMultDM() * delay, new SpeedData(Patterns.getSpeedMultDM())));
		}

		Patterns.t_wait(delay);
		Patterns.THICKNESS = oldThickness;
	}
	
}
