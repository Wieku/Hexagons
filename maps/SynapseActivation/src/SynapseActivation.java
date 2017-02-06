import xyz.hexagons.client.api.*;

import static xyz.hexagons.client.api.MapUtils.shuffle;

public class SynapseActivation implements MapScript {

	private void addPattern(int mKey) {
		if (mKey == 0) SynapticPatterns.patternBarrageSpiral(10, 0.5f, 1);
		else if (mKey == 1) SynapticPatterns.patternTunnelFastAlt(Patterns.random(4, 10));


	}

	//Integer[] keys = { 0, 0, 1, 1, 2, 2, 3, 4, 4, 5, 6, 7, 7, 7, 8, 9, 9 };
	Integer[] keys = { 0, 0, 1, 1, 1 };
	int index = 0;

	@Override
	public void onInit() {
		shuffle(keys);
		
		CurrentMap.setRotationSpeed(0.35f);
		CurrentMap.setRotationSpeedMax(1.5f);
		CurrentMap.setRotationIncrement(0.083f);
		CurrentMap.setFastRotate(71f);

		CurrentMap.setDelayMult(1.1f);
		CurrentMap.setDifficulty(1f);
		CurrentMap.setLevelIncrement(15f);
		CurrentMap.setSpeed(2f);

		CurrentMap.setBeatPulseDelay(1f);

		CurrentMap.setPulseMin(100);
		CurrentMap.setPulseMax(100);
		CurrentMap.setPulseSpeed(1.03f);
		CurrentMap.setPulseSpeedR(1.01f);
		CurrentMap.setPulseDelayMax(9);

		//sides
		CurrentMap.setSides(5);
		CurrentMap.setMinSides(5);
		CurrentMap.setMaxSides(5);

		//3d settings
		CurrentMap.setLayers(8);
		CurrentMap.setDepth(0.5f);
		CurrentMap.setSkew(0.2f);
		CurrentMap.setMinSkew(0.2f);
		CurrentMap.setMaxSkew(1f);
		CurrentMap.setSkewTime(10f);

		dirChangeTime = 1.666666f;
	}

	@Override
	public void initEvents() {

	}

	@Override
	public void initColors(){
		//colors
		CurrentMap.gameProperties.backgroundColors.add(new HColor(160f/255, 160f/255, 160f/255, 1));//.addPulse(75f/255, 15f/255, 15f/255, 0));
		//CurrentMap.gameProperties.colors.add(new HColor(60f / 255, 60f / 255, 60f / 255, 1f));
		CurrentMap.gameProperties.shadow = new DynamicColor.StaticDynamicColor(0x02eafaff);

		CurrentMap.gameProperties.walls = new HColor(39f / 255, 39f / 255, 39f / 255, 1f);

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
			if (!CurrentMap.gameProperties.rapidSpin) {
				CurrentMap.setRotationSpeed(CurrentMap.gameProperties.rotationSpeed * -1.0f);
				dirChangeTime = 300f/60;
			}
		}
	}
}
