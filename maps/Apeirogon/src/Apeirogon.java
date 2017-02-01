import xyz.hexagons.client.api.*;

import static xyz.hexagons.client.api.MapUtils.shuffle;

/**
 * @author Sebastian Krajewski on 29.03.15.
 */
public class Apeirogon implements MapScript {

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

	Integer[] keys = { 0, 0, 1, 1, 2, 2, 3, 4, 4, 5, 6, 7, 7, 7, 8, 9, 9 };
	int index = 0;

	@Override
	public void onInit() {
		shuffle(keys);
		
		CurrentMap.setRotationSpeed(0.55f);
		CurrentMap.setRotationSpeedMax(1.5f);
		CurrentMap.setRotationIncrement(0.083f);
		CurrentMap.setFastRotate(71f);

		CurrentMap.setDelayMult(1.1f);
		CurrentMap.setDifficulty(1f);
		CurrentMap.setLevelIncrement(15f);
		CurrentMap.setSpeed(2.9f);

		CurrentMap.setBeatPulseDelay(0.25f);

		CurrentMap.setPulseMin(64);
		CurrentMap.setPulseMax(84);
		CurrentMap.setPulseSpeed(2.0f);
		CurrentMap.setPulseSpeedR(1.5f);
		CurrentMap.setPulseDelayMax(9);

		//sides
		CurrentMap.setSides(6);
		CurrentMap.setMinSides(5);
		CurrentMap.setMaxSides(7);

		//3d settings
		CurrentMap.setLayers(8);
		CurrentMap.setDepth(1.5f);
		CurrentMap.setSkew(0f);
		CurrentMap.setMinSkew(0f);
		CurrentMap.setMaxSkew(1f);
		CurrentMap.setSkewTime(5f);

		dirChangeTime = 1.666666f;
	}

	@Override
	public void initEvents() {

	}

	@Override
	public void initColors(){
		//colors
		CurrentMap.gameProperties.backgroundColors.add(new HColor(90f / 255, 90f / 255, 90f / 255, 1f).addPulse(75f/255, 15f/255, 15f/255, 0));
		CurrentMap.gameProperties.backgroundColors.add(new HColor(60f / 255, 60f / 255, 60f / 255, 1f));

		CurrentMap.gameProperties.walls = new HColor(253f/255, 253f/255, 253f/255, 1);
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

	float dirChangeTime = 1.666666f;
	@Override
	public void update(float delta) {
		dirChangeTime = dirChangeTime - delta;
		if (dirChangeTime < 0) {
			if (!CurrentMap.gameProperties.isFastRotation) {
				CurrentMap.setRotationSpeed(CurrentMap.gameProperties.rotationSpeed * -1.0f);
				dirChangeTime = 300f/60;
			}
		}
	}
}
