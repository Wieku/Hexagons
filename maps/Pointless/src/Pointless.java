import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.HColor;
import xyz.hexagons.client.api.MapScript;
import xyz.hexagons.client.api.Patterns;
import xyz.hexagons.client.map.Hue;

/**
 * @author Sebastian Krajewski on 29.03.15.
 */
public class Pointless implements MapScript {

	public void addPattern(int mKey) {
		if (mKey == 0) Patterns.pAltBarrage(Patterns.random(2, 4), 2);
		else if (mKey == 1) Patterns.pMirrorSpiral(Patterns.random(2, 5), Patterns.getHalfSides() - 3);
		else if (mKey == 2) Patterns.pBarrageSpiral(Patterns.random(0, 3), 1, 1);
		else if (mKey == 3) Patterns.pInverseBarrage(0);
		else if (mKey == 4) Patterns.pTunnel(Patterns.random(1, 3));

	}

	Integer[] keys = { 0, 0, 1, 1, 2, 2, 3, 3, 4 };
	int index = 0;

	@Override
	public void onInit() {
		shuffle(keys);

		CurrentMap.setRotationSpeed(42 / 360f);
		CurrentMap.setRotationSpeedMax(240 / 360f);
		CurrentMap.setRotationIncrement(24 / 360f);
		CurrentMap.setFastRotate(1f);

		CurrentMap.setDelayMult(1.0f);
		CurrentMap.setDelayMultInc(-0.01f);
		CurrentMap.setDifficulty(1f);
		CurrentMap.setLevelIncrement(15f);
		CurrentMap.setSpeed(1.55f);
		CurrentMap.setSpeedInc(0.125f);

		CurrentMap.setBeatPulseDelay(0.25f);

		CurrentMap.setPulseMin(75);
		CurrentMap.setPulseMax(91);
		CurrentMap.setPulseSpeed(1.2f);
		CurrentMap.setPulseSpeedR(1f);
		CurrentMap.setPulseDelayMax(23.9f);

		//sides
		CurrentMap.setSides(6);
		CurrentMap.setMinSides(5);
		CurrentMap.setMaxSides(6);

		//3d settings
		CurrentMap.setLayers(8);
		CurrentMap.setDepth(1.5f);
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
		CurrentMap.data.colors.add(new HColor(0, 0, 0, 1f).addDynamicDarkness(2.7f).addHue(new Hue(0, 360, 0.7f, false, false)));
		CurrentMap.data.colors.add(new HColor(0, 0, 0, 1f).addDynamicDarkness(3.5f).addHue(new Hue(0, 360, 0.7f, false, false))/*.addHueOffset(5.0f)*/);

		CurrentMap.data.walls = new HColor(1f, 0, 0, 1f).addPulse(-80f/255, 75f/255, 65f/255, 0f).addHue(new Hue(0, 360, 0.7f, false, false)).setMain(true);
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
	public void update(float delta) {}
}
