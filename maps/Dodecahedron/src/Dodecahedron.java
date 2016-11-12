import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.HColor;
import xyz.hexagons.client.api.MapScript;
import xyz.hexagons.client.api.Patterns;
import xyz.hexagons.client.engine.Game;
import xyz.hexagons.client.map.timeline.TimelineRunnable;

/**
 * @author Sebastian Krajewski on 29.03.15.
 */
public class Dodecahedron implements MapScript {

	public void addPattern(int mKey) {
		if (mKey == 0) Patterns.pAltBarrage(Patterns.random(2, 3), 2);
		else if (mKey == 1) Patterns.pBarrageSpiral(3, 0.6f, 1);
		else if (mKey == 2) Patterns.pInverseBarrage(0);
		else if (mKey == 3) Patterns.pTunnel(Patterns.random(1, 3));
		else if (mKey == 4) Patterns.pMirrorWallStrip(1, 0);
		else if (mKey == 5) Patterns.pWallExVortex(0, Patterns.random(1, 2), 1);
		else if (mKey == 6) Patterns.pDMBarrageSpiral(Patterns.random(4, 7), 0.4f, 1);
		else if (mKey == 7) Patterns.pRandomBarrage(Patterns.random(2, 5), 2.25f);
		else if (mKey == 8) Patterns.pMirrorSpiralDouble(Patterns.random(4, 6), 0);
		else if (mKey == 9) Patterns.pMirrorSpiral(Patterns.random(2, 4), 0);

	}

	Integer[] keys = { 0, 0, 1, 1, 1, 1, 2, 2, 3, 4, 4, 5, 6, 7, 7, 7, 8, 9, 9 };
	int index = 0;

	@Override
	public void onInit() {
		shuffle(keys);
		
		CurrentMap.setRotationSpeed(0.65f);
		CurrentMap.setRotationSpeedMax(1.5f);
		CurrentMap.setRotationIncrement(0.053f);
		CurrentMap.setFastRotate(81f);

		CurrentMap.setDelayMult(1.1f);
		CurrentMap.setDifficulty(1f);
		CurrentMap.setLevelIncrement(24f);
		CurrentMap.setSpeed(2.2f);

		CurrentMap.setBeatPulseDelay(0.25f);

		CurrentMap.setPulseMin(64);
		CurrentMap.setPulseMax(104);
		CurrentMap.setPulseSpeed(2.0f);
		CurrentMap.setPulseSpeedR(1.5f);
		CurrentMap.setPulseDelayMax(1);

		//sides
		CurrentMap.setSides(6);
		CurrentMap.setMinSides(4);
		CurrentMap.setMaxSides(6);

		//3d settings
		CurrentMap.setLayers(12);
		CurrentMap.setDepth(5f);
		CurrentMap.setSkew(0f);
		CurrentMap.setMinSkew(0f);
		CurrentMap.setMaxSkew(1f);
		CurrentMap.setSkewTime(4f);

		dirChangeTime = 2.666666f;
	}

	@Override
	public void initEvents() {
		CurrentMap.data.eventTimeline.wait(2f);
		CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.pushText("Are you ready?", 0.5f);
			}
		});
		CurrentMap.data.eventTimeline.wait(0.5f);
		for (int i=0; i< 52;i++) {
			CurrentMap.data.eventTimeline.wait(0.4f);
			CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
				@Override
				public void run() {
					CurrentMap.pushText("EH", 0.2f);
				}
			});
		}
		CurrentMap.data.eventTimeline.wait(1.5f);
		CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.pushText("Are you ready?", 0.5f);
			}
		});
		CurrentMap.data.eventTimeline.wait(0.5f);
		for (int i=0; i< 52;i++) {
			CurrentMap.data.eventTimeline.wait(0.4f);
			CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
				@Override
				public void run() {
					CurrentMap.pushText("EH", 0.2f);
				}
			});
		}

		lyric("Adrenaline is pumping", 2f, 1.5f);
		lyric("Adrenaline is pumping", 2f, 1.5f);
		lyric("Generator", 2.5f, 1f);
		lyric("Automatic lover", 2f, 1.5f);
		lyric("Atomic", 4f, 0.5f);
		lyric("Atomic", 1.5f, 0.5f);
		lyric("Overdrive", 1.5f, 1.0f);
		lyric("Blockbuster", 2.0f, 1f);
		lyric("Brainpower", 3f, 1f);

		lyric("Call me leader", 3f, 1f);
		lyric("Cocaine", 2f, 1f);
		lyric("Don't you try it", 3.5f, 1.5f);
		lyric("Don't you try it", 2.5f, 1.5f);

		lyric("Innovator", 3f, 1f);
		lyric("Killing machine", 2f, 1.5f);
		lyric("There's no fate", 3f, 1.5f);
		lyric("Take control", 3f, 1.5f);
		lyric("BRAINPOWER", 2.5f, 1.5f);

		lyric("LET THE BASE KICK!", 2.0f, 1.5f);

	}

	public void lyric(String text, float wait, float duration) {
		CurrentMap.data.eventTimeline.wait(wait);
		CurrentMap.data.eventTimeline.submit(new TimelineRunnable() {
			@Override
			public void run() {
				CurrentMap.pushText(text, duration);
			}
		});

	}

	@Override
	public void initColors(){
		//colors
		CurrentMap.data.colors.add(new HColor(61f / 255, 0f / 255, 62f / 255, 1f).addPulse(68f/255, 0f/255, 69f/255, 0));
		CurrentMap.data.colors.add(new HColor(195f / 255, 67f / 255, 152f / 255, 1f));

		CurrentMap.data.walls = new HColor(243f/255, 148f/255, 183f/255, 1).setMain(true).addPulse(60f/255, 60f/255, 60f/255, 0f);
		CurrentMap.setAlphaFalloff(0.1f);
		CurrentMap.setColorPulse(1.8f);
		CurrentMap.setColorPulseInc(0.047f);
		CurrentMap.setColorSwitch(0.6f);


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

	float dirChangeTime = 2.666666f;
	@Override
	public void update(float delta) {
		dirChangeTime = dirChangeTime - delta;
		if (dirChangeTime < 0) {
			if (!CurrentMap.data.isFastRotation) {
				CurrentMap.setRotationSpeed(CurrentMap.data.rotationSpeed * -1.0f);
				dirChangeTime = 300f/60;
			}
		}
	}
}
