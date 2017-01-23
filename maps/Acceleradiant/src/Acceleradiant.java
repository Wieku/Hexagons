import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.HColor;
import xyz.hexagons.client.api.MapScript;
import xyz.hexagons.client.api.Patterns;
import xyz.hexagons.client.map.Hue;

import static xyz.hexagons.client.api.MapUtils.shuffle;

public class Acceleradiant implements MapScript {

	public void wallSAcc(int mSide, float mAdj, float mAcc, float mMinSpd, float mMaxSpd){
		Patterns.wallAcc(mSide, Patterns.THICKNESS, mAdj, mAcc * (CurrentMap.data.difficulty), mMinSpd, mMaxSpd);
	}

	public void pACBarrage() {
		int currentSides = CurrentMap.data.sides;
		float delay = Patterns.getPerfectDelayDM(Patterns.THICKNESS) * 3.7f;
		int startSide = Patterns.random(0, 10);
		for (int i = 0; i <= currentSides - 2; i++) {
			int currentSide = startSide + i;
			wallSAcc(currentSide, 9 + Patterns.random(0, 1), -1.1f, 1, 12);
		}
		Patterns.t_wait(delay * 2.5f);
	}


	public void pACBarrageMulti(){
		int currentSides = CurrentMap.data.sides;
		float delay = Patterns.getPerfectDelayDM(Patterns.THICKNESS) * 3.7f;
		int startSide = Patterns.random(0, 10);
		for (int i = 0; i <= currentSides - 2; i++) {
			int currentSide = startSide + i;
			wallSAcc(currentSide, 10, -1.09f, 0.31f, 10);
			wallSAcc(currentSide, 0, 0.05f, 0, 4.0f);
			wallSAcc(currentSide, 0, 0.09f, 0, 4.0f);
			wallSAcc(currentSide, 0, 0.12f, 0, 4.0f);
		}
		Patterns.t_wait(delay * 8);
	}

	public void pACBarrageMultiAltDir() {
		int currentSides = CurrentMap.data.sides;
		float delay = Patterns.getPerfectDelayDM(Patterns.THICKNESS) * 4;
		float mdiff = 1 + Math.abs(1 - CurrentMap.data.difficulty);
		int startSide = Patterns.random(0, 10);
		int loopDir = Patterns.getRandomDir();
		for (int i = 0; i <= currentSides + Patterns.getHalfSides(); i++) {
			int currentSide = startSide + i * loopDir;
			wallSAcc(currentSide, 10, -1.095f, 0.40f, 10f);
			Patterns.t_wait((delay / 2.21f) * (mdiff * 1.29f));
			wallSAcc(currentSide + (Patterns.getHalfSides() * loopDir), 0, 0.128f, 0, 1.4f);
		}
		Patterns.t_wait(delay * 8);
	}

	public void addPattern(int mKey) {
		if (mKey == 0)  pACBarrage();
		else if (mKey == 1) pACBarrageMulti();
		else if (mKey == 2) pACBarrageMultiAltDir();
	}

	Integer[] keys = { 0, 0, 1, 1, 2, 2, 0, 0, 0, 0, 0 };
	int index = 0;

	@Override
	public void onInit() {
		shuffle(keys);

		CurrentMap.setRotationSpeed(0.495f);
		CurrentMap.setRotationSpeedMax(0.73f);
		CurrentMap.setRotationIncrement(0.08f);
		CurrentMap.setFastRotate(71f);

		CurrentMap.setDelayMult(1.1f);
		CurrentMap.setDelayMultInc(-0.01f);
		CurrentMap.setDifficulty(1f);
		CurrentMap.setLevelIncrement(15f);
		CurrentMap.setSpeed(2.25f);
		CurrentMap.setSpeedInc(0.045f);

		//CurrentMap.setBeatPulseDelay(2.18f);

		CurrentMap.setBeatPulseMax(1.2f);
		//CurrentMap.setBeatPulseDelayMax(21.8f);

		CurrentMap.setPulseMin(64);
		CurrentMap.setPulseMax(84);
		CurrentMap.setPulseSpeed(1.05f);
		CurrentMap.setPulseSpeedR(1.34f);
		CurrentMap.setPulseDelayMax(7);

		//sides
		CurrentMap.setSides(6);
		CurrentMap.setMinSides(5);
		CurrentMap.setMaxSides(7);

		//3d settings
		CurrentMap.setLayers(3);
		CurrentMap.setDepth(153.5f);
		CurrentMap.setSkew(0.83f);
		CurrentMap.setMinSkew(0f);
		CurrentMap.setMaxSkew(0.83f);
		CurrentMap.setSkewTime(5.5f);

	}

	@Override
	public void initEvents() {

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

	float dirChangeTime = 6.666666f;
	float hueIMin = 0.0f;
	float hueIMax = 22.0f;
	float hueIStep = 0.0065f;
	@Override
	public void update(float delta) {
		dirChangeTime = dirChangeTime - delta;
		if (dirChangeTime < 0) {
			if (!CurrentMap.data.isFastRotation) {
				CurrentMap.setRotationSpeed(CurrentMap.data.rotationSpeed * -1.0f);
				dirChangeTime = 400f / 60;
			}
		}

		for (HColor color : CurrentMap.data.colors) {
			color.setHueInc(color.getHueInc() + hueIStep);
		}
		CurrentMap.data.walls.setHueInc(CurrentMap.data.walls.getHueInc() + hueIStep);

		if(CurrentMap.data.walls.getHueInc() > hueIMax) hueIStep *= -1;
		if(CurrentMap.data.walls.getHueInc() < hueIMin) hueIStep *= -1;

	}

	@Override
	public void initColors() {
		CurrentMap.data.colors.add(new HColor(0, 0, 0, 1f).addPulse(45f / 255, 25f / 255, 1f / 255, 0f).addHue(new Hue(0, 360, 0.5f, false, false)).addDynamicDarkness(8.7f));
		CurrentMap.data.colors.add(new HColor(0, 0, 0, 1f).addPulse(1f / 255, 25f / 255, 45f / 255, 0f).addHue(new Hue(0, 360, 0.5f, false, false)).addDynamicDarkness(9.5f));

		CurrentMap.data.walls = new HColor(1, 155f/255, 155f/255, 230f/255).addPulse(50f/255, -75f/255, 125f/255, 0).addHue(new Hue(0, 360, 0.5f, false, false)).setMain(true);
		CurrentMap.setColorPulseMax(1.5f);
		CurrentMap.setColorPulseInc(0.025f);
		CurrentMap.setAlphaMultiplier(5.9f);
		CurrentMap.setAlphaFalloff(0.06f);
		CurrentMap.setColorSwitch(1f);
	}
}
