import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.HColor;
import xyz.hexagons.client.api.MapScript;
import xyz.hexagons.client.api.Patterns;
import xyz.hexagons.client.map.Hue;

import static xyz.hexagons.client.api.MapUtils.shuffle;

/**
 * @author Sebastian Krajewski on 29.03.15.
 */
public class Pi implements MapScript {

	public void addPattern(int mKey) {
		if (mKey == 0)  {Patterns.cWallEx(Patterns.random(0, CurrentMap.data.sides), Patterns.random(1, 2)); Patterns.t_wait(Patterns.getPerfectDelay(Patterns.THICKNESS) * 2.5f);}
		else if (mKey == 1) Patterns.pMirrorSpiralDouble(Patterns.random(1, 2), 4);
		else if (mKey == 2) {Patterns.rWallEx(Patterns.random(0, CurrentMap.data.sides), Patterns.random(1, 2)); Patterns.t_wait(Patterns.getPerfectDelay(Patterns.THICKNESS) * 2.8f);}
		else if (mKey == 3) Patterns.pMirrorWallStrip(1, 2);
		else if (mKey == 4) {Patterns.rWallEx(Patterns.random(0, CurrentMap.data.sides), 1); Patterns.t_wait(Patterns.getPerfectDelay(Patterns.THICKNESS) * 2.3f);}
		else if (mKey == 5) {Patterns.cWallEx(Patterns.random(0, CurrentMap.data.sides), 7); Patterns.t_wait(Patterns.getPerfectDelay(Patterns.THICKNESS) * 2.7f);}

	}

	Integer[] keys = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 4, 4, 5, 5, 5, 5 };
	int index = 0;

	@Override
	public void onInit() {
		shuffle(keys);
		
		CurrentMap.setRotationSpeed(0.416666667f);
		CurrentMap.setRotationSpeedMax(0.666666667f);
		CurrentMap.setRotationIncrement(0.0666666667f);
		CurrentMap.setFastRotate(80f);

		CurrentMap.setDelayMult(1.0f);
		CurrentMap.setDifficulty(1f);
		CurrentMap.setLevelIncrement(15f);
		CurrentMap.setSpeed(3.4f);
		CurrentMap.setSpeedInc(0.10f);

		CurrentMap.setBeatPulseDelay(0.25f);

		CurrentMap.setPulseMin(68);
		CurrentMap.setPulseMax(80);
		CurrentMap.setPulseSpeed(3.6f);
		CurrentMap.setPulseSpeedR(1.4f);
		CurrentMap.setPulseDelayMax(7f);

		//sides
		CurrentMap.setSides(24);
		CurrentMap.setMinSides(20);
		CurrentMap.setMaxSides(28);

		//3d settings
		CurrentMap.setLayers(4);
		CurrentMap.setDepth(4.5f);
		CurrentMap.setSkew(0f);
		CurrentMap.setMinSkew(0f);
		CurrentMap.setMaxSkew(1f);
		CurrentMap.setSkewTime(5f);

		dirChangeTime = 2.5f;
	}

	@Override
	public void initEvents() {

	}

	@Override
	public void initColors(){
		//colors
		CurrentMap.data.colors.add(new HColor(190f / 255, 74f / 255, 190f / 255, 1f).addHue(new Hue(0, 255, 1f, false, false)).addHueOffset(4.2f).addHueShift(25f));
		CurrentMap.data.colors.add(new HColor(190f / 255, 190f / 255, 190f / 255, 1f).addHue(new Hue(0, 255, 1f, false, false)).addHueOffset(4.2f).addHueShift(50f));
		CurrentMap.data.colors.add(new HColor(190f / 255, 115f / 255, 190f / 255, 1f).addHue(new Hue(0, 255, 1f, false, false)).addHueOffset(4.2f).addHueShift(75f));
		CurrentMap.data.colors.add(new HColor(190f / 255, 190f / 255, 98f / 255, 1f).addHue(new Hue(0, 255, 1f, false, false)).addHueOffset(4.2f).addHueShift(100f));
		CurrentMap.data.colors.add(new HColor(250f / 255, 190f / 255, 190f / 255, 1f).addHue(new Hue(0, 255, 1f, false, false)).addHueOffset(4.2f).addHueShift(125f));
		CurrentMap.data.colors.add(new HColor(163f / 255, 190f / 255, 190f / 255, 1f).addHue(new Hue(0, 255, 1f, false, false)).addHueOffset(4.2f).addHueShift(150f));


		CurrentMap.data.walls = new HColor(0, 0, 0, 225/255f).addPulse(99/255f, 30/255f, 165/255f, 0f).setMain(true);
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

	float dirChangeTime = 2.5f;
	@Override
	public void update(float delta) {
		dirChangeTime = dirChangeTime - delta;
		if (dirChangeTime < 0) {
			if (!CurrentMap.data.isFastRotation) {
				CurrentMap.setRotationSpeed(CurrentMap.data.rotationSpeed * -1.0f);
				dirChangeTime = 100f/60;
			}
		}
	}
}
