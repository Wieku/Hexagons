import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.HColor;
import xyz.hexagons.client.api.MapScript;
import xyz.hexagons.client.api.Patterns;
import xyz.hexagons.client.map.Hue;

import static xyz.hexagons.client.api.MapUtils.shuffle;

/**
 * @author Sebastian Krajewski on 29.03.15.
 */
public class FlatteringShape implements MapScript {

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

		CurrentMap.setRotationSpeed(78 / 360f);
		CurrentMap.setRotationSpeedMax(240 / 360f);
		CurrentMap.setRotationIncrement(24 / 360f);
		CurrentMap.setFastRotate(1f);

		CurrentMap.setDelayMult(1.0f);
		CurrentMap.setDelayMultInc(-0.03f);
		CurrentMap.setDifficulty(1f);
		CurrentMap.setLevelIncrement(15f);
		CurrentMap.setSpeed(1.74f);
		CurrentMap.setSpeedInc(0.18f);

		CurrentMap.setBeatPulseDelay(0.25f);

		CurrentMap.setPulseMin(75);
		CurrentMap.setPulseMax(91);
		CurrentMap.setPulseSpeed(1.5f);
		CurrentMap.setPulseSpeedR(0.6f);
		CurrentMap.setPulseDelayMax(9);

		//sides
		CurrentMap.setSides(6);
		CurrentMap.setMinSides(5);
		CurrentMap.setMaxSides(6);

		//3d settings
		CurrentMap.setLayers(7);
		CurrentMap.setDepth(1.2f);
		CurrentMap.setSkew(0f);
		CurrentMap.setMinSkew(0f);
		CurrentMap.setMaxSkew(1f);
		CurrentMap.setSkewTime(5f);

	}

	@Override
	public void initEvents() {

	}

	@Override
	public void initColors(){
		//colors
		CurrentMap.gameProperties.backgroundColors.add(new HColor(0, 0, 0, 1f).addDynamicDarkness(2.7f).addHue(new Hue(0, 160, 0.7f, true, false)));
		CurrentMap.gameProperties.backgroundColors.add(new HColor(45f / 255, 60f / 255, 45f / 255, 1f).addPulse(25f/255, 25f/255, 25f/255, 0f).addDynamicDarkness(2.5f)/*.addHueOffset(5.0f)*/);

		CurrentMap.gameProperties.walls = new HColor(1f, 0, 0, 1f).addPulse(0, 50f/255, 0f, 0f).addHue(new Hue(0, 160, 0.7f, true, false)).setMain(true);
		CurrentMap.setColorPulseMax(1.2f);
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
	public void update(float delta) {}
}
