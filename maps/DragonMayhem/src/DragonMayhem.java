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
public class DragonMayhem implements MapScript {

	public void addPattern(int mKey) {
		if (mKey == 0) Patterns.pAltBarrage(Patterns.random(2, 4), 2);
		else if (mKey == 1) Patterns.pMirrorSpiral(Patterns.random(3, 6), 0);
		else if (mKey == 2) Patterns.pBarrageSpiral(Patterns.random(0, 3), 1, 1);
		else if (mKey == 3) Patterns.pBarrageSpiral(Patterns.random(0, 2), 1.2f, 2);
		else if (mKey == 4) Patterns.pBarrageSpiral(2, 0.7f, 1);
		else if (mKey == 5) Patterns.pInverseBarrage(0);
		else if (mKey == 6) Patterns.pTunnel(Patterns.random(1, 3));
		else if (mKey == 7) Patterns.pMirrorWallStrip(1, 0);
	}

	Integer[] keys = { 0, 0, 1, 1, 2, 2, 3, 4, 5, 5, 6, 7, 7 };
	int index = 0;

	@Override
	public void onInit() {
		shuffle(keys);

		CurrentMap.setRotationSpeed(0f);
		CurrentMap.setRotationSpeedMax(1.375f);
		CurrentMap.setRotationIncrement(1.375f); //(0.75)
		CurrentMap.setFastRotate(100f);

		CurrentMap.setDelayMult(1.00f);
		CurrentMap.setDelayMultInc(-0.01f);
		CurrentMap.setDifficulty(1f);
		CurrentMap.setLevelIncrement(65.4f);
		CurrentMap.setSpeed(1f);
		CurrentMap.setSpeedInc(0.125f);

		CurrentMap.setBeatPulseMax(1);

		CurrentMap.setPulseMin(75);
		CurrentMap.setPulseMax(100);
		CurrentMap.setPulseSpeed(5f);
		CurrentMap.setPulseSpeedR(2.5f);
		CurrentMap.setPulseDelayMax(0.172f);

		//sides
		CurrentMap.setSides(6);
		CurrentMap.setMinSides(6);
		CurrentMap.setMaxSides(6);

		//3d settings
		CurrentMap.setLayers(5);
		CurrentMap.setDepth(10f);
		CurrentMap.setSkew(0f);
		CurrentMap.setMinSkew(0f);
		CurrentMap.setMaxSkew(0.85f);
		CurrentMap.setSkewTime(5f);

	}

	@Override
	public void initEvents() {
		CurrentMap.data.eventTimeline.wait(65.4f);
		CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.data.colors.clear();
				CurrentMap.data.colors.add(new HColor(1f, 0f, 0f, 1f));
				CurrentMap.data.colors.add(new HColor(225f/255, 0f, 0f, 1f));
				CurrentMap.data.colors.add(new HColor(195f / 255, 0f, 0f, 1f));
				CurrentMap.data.colors.add(new HColor(195f / 255, 0f, 0f, 1f));
				CurrentMap.data.colors.add(new HColor(225f/255, 0f, 0f, 1f));
				CurrentMap.data.colors.add(new HColor(1f, 0f, 0f, 1f));
			}
		});
		CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.pushText("Try this now!", 150f / 60);
			}
		});
		CurrentMap.data.eventTimeline.wait(85f);
		CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.data.colors.clear();
				CurrentMap.data.colors.add(new HColor(1f, 0f, 0f, 1f).addHue(new Hue(0, 100, 0.541f, true, false)));
				CurrentMap.data.colors.add(new HColor(225f/255, 0f, 0f, 1f));
				CurrentMap.data.colors.add(new HColor(195f / 255, 0f, 0f, 1f).addHue(new Hue(0, 100, 0.541f, true, false)));
				CurrentMap.data.colors.add(new HColor(195f / 255, 0f, 0f, 1f));
				CurrentMap.data.colors.add(new HColor(225f/255, 0f, 0f, 1f).addHue(new Hue(0, 100, 0.541f, true, false)));
				CurrentMap.data.colors.add(new HColor(1f, 0f, 0f, 1f));
				CurrentMap.data.walls = new HColor(1f, 0f, 0f, 1f).addPulse(0f, 1f, 1f, 0f);
				CurrentMap.setColorPulseInc(0.541f);
			}
		});
		CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.pushText("How you can do this? Impossible!", 150f / 60);
			}
		});
	}

	@Override
	public void initColors(){
		CurrentMap.data.colors.add(new HColor(255f / 255, 0, 0, 1f).addHue(new Hue(0, 25, 0.345f, true, false)));
		CurrentMap.data.colors.add(new HColor(180f / 255, 0, 0, 1f));
		CurrentMap.data.walls = new HColor(1,1,1,1);
		CurrentMap.setColorPulseMax(2.5f);
		CurrentMap.setColorPulseInc(0.345f);
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
