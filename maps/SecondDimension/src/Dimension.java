import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.HColor;
import xyz.hexagons.client.api.MapScript;
import xyz.hexagons.client.api.Patterns;
import xyz.hexagons.client.map.timeline.TimelineRunnable;
import xyz.hexagons.client.map.Hue;

import static xyz.hexagons.client.api.MapUtils.shuffle;

/**
 * @author Sebastian Krajewski on 29.03.15.
 */
public class Dimension implements MapScript {

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
		else if (mKey == 9) Patterns.pDMBarrageSpiral(Patterns.random(4, 7), 0.4f, 1);
		else if (mKey == 10) Patterns.pRandomBarrage(Patterns.random(2, 4), 2.25f);
	}

	Integer[] keys = { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 7, 7, 8, 9, 10, 10, 10 };
	int index = 0;

	@Override
	public void onInit() {
		shuffle(keys);
		
		CurrentMap.setRotationSpeed(0.5f);
		CurrentMap.setRotationSpeedMax(1.5f);
		CurrentMap.setRotationIncrement(0.083f);
		CurrentMap.setFastRotate(70f);

		CurrentMap.setDelayMult(1.1f);
		CurrentMap.setDifficulty(1f);
		CurrentMap.setLevelIncrement(15f);
		CurrentMap.setSpeed(2.65f);

		CurrentMap.setBeatPulseDelay(0.39f);

		CurrentMap.setPulseMin(70);
		CurrentMap.setPulseMax(90);
		CurrentMap.setPulseSpeed(1f);
		CurrentMap.setPulseSpeedR(0.6f);
		CurrentMap.setPulseDelayMax(0);

		//sides
		CurrentMap.setSides(6);
		CurrentMap.setMinSides(5);
		CurrentMap.setMaxSides(7);

		//3d settings
		CurrentMap.setLayers(7);
		CurrentMap.setDepth(1.5f);
		CurrentMap.setSkew(0f);
		CurrentMap.setMinSkew(0f);
		CurrentMap.setMaxSkew(1f);
		CurrentMap.setSkewTime(10f);



	}

	@Override
	public void initEvents() {
		CurrentMap.data.eventTimeline.wait(15f);
		CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.pushText("whoa!", 1.2f);
			}
		});
		CurrentMap.data.eventTimeline.wait(45f);
		CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.pushText("may the mayhem begin!", 1.3f);
			}
		});
	}

	@Override
	public void initColors(){
		//colors
		CurrentMap.data.colors.add(new HColor(190f/255, 190f/255, 190f/255, 1).addPulse(15f/255, 15f/255, 15f/255, 0).addHue(new Hue(200, 340, 1, true, false)).addHueOffset(4.7f));
		CurrentMap.data.colors.add(new HColor(235/255f, 235/255f, 235/255f, 1));
		CurrentMap.data.walls = new HColor(0, 0, 0, 1).addHue(new Hue(200, 340, 1, true, false)).setMain(true);
		CurrentMap.setColorPulse(1.5f);
		CurrentMap.setColorPulseInc(0.025f);
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

	@Override
	public void update(float delta) {

	}
}
