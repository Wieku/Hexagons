import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.HColor;
import xyz.hexagons.client.api.MapScript;
import static xyz.hexagons.client.api.MapUtils.shuffle;
import xyz.hexagons.client.api.Patterns;
import xyz.hexagons.client.map.timeline.TimelineRunnable;

/**
 * @author Sebastian Krajewski on 29.03.15.
 */
public class Fairydust implements MapScript {

	public void addPattern(int mKey) {
		if (mKey == 0) Patterns.pAltBarrage(Patterns.random(2, 4), 2);
		else if (mKey ==  1) Patterns.pMirrorSpiral(Patterns.random(3, 6), 0);
		else if (mKey ==  2) Patterns.pBarrageSpiral(Patterns.random(0, 3), 1, 1);
		else if (mKey ==  3) Patterns.pBarrageSpiral(Patterns.random(0, 2), 1.2f, 2);
		else if (mKey ==  4) Patterns.pBarrageSpiral(2, 0.7f, 1);
		else if (mKey ==  5) Patterns.pInverseBarrage(0);
		else if (mKey ==  6) Patterns.pTunnel(Patterns.random(1, 3));
		else if (mKey ==  7) Patterns.pMirrorWallStrip(1, 0);
		else if (mKey ==  8) Patterns.pWallExVortex(0, 1, 1);
		else if (mKey ==  9) Patterns.pDMBarrageSpiral(Patterns.random(4, 7), 0.4f, 1);
		else if (mKey == 10) Patterns.pRandomBarrage(Patterns.random(2, 4), 2.25f);
	}

	Integer[] keys = { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 7, 7, 8, 9, 10, 10, 10 };
	int index = 0;

	@Override
	public void onInit() {
		shuffle(keys);

		CurrentMap.useRadians(true);
		CurrentMap.setRotationSpeed(0f);
		CurrentMap.setRotationSpeedMax(1.25f);
		CurrentMap.setRotationIncrement(0f); //(0.75)
		CurrentMap.setFastRotate(100f);

		CurrentMap.setDelayMult(1.00f);
		CurrentMap.setDelayMultInc(-0.01f);
		CurrentMap.setDifficulty(1f);
		CurrentMap.setLevelIncrement(54.2f);
		CurrentMap.setSpeed(1.92f);
		CurrentMap.setSpeedInc(0.125f);

		CurrentMap.setBeatPulseDelay(0);
		CurrentMap.setBeatPulseMax(0);

		CurrentMap.setPulseMin(75);
		CurrentMap.setPulseMax(100);
		CurrentMap.setPulseSpeed(2f);
		CurrentMap.setPulseSpeedR(2.5f);
		CurrentMap.setPulseDelayMax(0.172f);

		//sides
		CurrentMap.setSides(6);
		CurrentMap.setMinSides(6);
		CurrentMap.setMaxSides(6);

		//3d settings
		CurrentMap.setLayers(8);
		CurrentMap.setDepth(150f);
		CurrentMap.setAlphaMultiplier(2);
		CurrentMap.setAlphaFalloff(12f/255);
		CurrentMap.setSkew(0f);
		CurrentMap.setMinSkew(0f);
		CurrentMap.setMaxSkew(0.8f);
		CurrentMap.setSkewTime(1.5f);
	}

	@Override
	public void initEvents() {
		CurrentMap.data.eventTimeline.wait(10.4f);
		CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.setRotationSpeed(0.25f);
			}
		});
		CurrentMap.data.eventTimeline.wait(32.6f-10.4f);
		CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.setRotationSpeed(0.75f);
			}
		});
		CurrentMap.data.eventTimeline.wait(65.5f-32.6f);
		CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.setRotationSpeed(0.75f);
			}
		});
		CurrentMap.data.eventTimeline.wait(87.4f-65.5f);
		CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.setRotationSpeed(0.5f);
			}
		});
		CurrentMap.data.eventTimeline.wait(121f-87.4f);
		CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.setRotationSpeed(1.25f);
			}
		});
		CurrentMap.data.eventTimeline.wait(175.8f-121f);
		CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.setRotationSpeed(0.5f);
			}
		});
		
		CurrentMap.data.eventTimeline.wait(192f-175.8f);
		CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.killPlayer();
			}
		});
		
	}

	@Override
	public void initColors(){
		CurrentMap.data.colors.add(new HColor(255f / 255, 128f/255, 1, 60f/255).addPulse(253f/255, 0, 253f/255, 60f/255));
		CurrentMap.data.colors.add(new HColor(253f / 255, 0, 253f / 255, 60f/255).addPulse(255f/255, 128f/255, 255f/255, 60f/255));
		CurrentMap.data.walls = new HColor(1,1,1,1).setMain(true);
		CurrentMap.setColorPulseMax(2f);
		CurrentMap.setColorPulseMin(2f);
		CurrentMap.setColorPulseInc(0.03f);
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
