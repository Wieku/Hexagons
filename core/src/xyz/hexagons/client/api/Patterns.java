package xyz.hexagons.client.api;

import java.util.Random;

/**
 * @author Sebastian Krajewski on 21.03.15.
 */
public abstract class Patterns {

	static Random random = new Random(); //TODO: CENTRAL SEED
	static public int random (int start, int end) {
		return start + random.nextInt(end - start + 1);
	}
	//getRandomDir: returns either 1 or -1

	public static final float THICKNESS = 40f;

	public static int getRandomDir(){
		if (random(0, 100) > 50) return 1;
			return -1;
	}

	//getHalfSides: returns half the number of sides (integer)
	public static int getHalfSides() { return (int) Math.ceil(CurrentMap.gameProperties.sides / 2f); }

	//getRandomSide: returns random mSide
	public static int getRandomSide() { return random(0, CurrentMap.gameProperties.sides - 1); }

	public static float getBaseSpeed() { return CurrentMap.gameProperties.speed * (float)(Math.pow(CurrentMap.gameProperties.difficulty, 0.65f)); }

	public static float getDelayMultDM() { return CurrentMap.gameProperties.delayMult / (float)(Math.pow(CurrentMap.gameProperties.difficulty, 0.10f)); }

	//getPerfectDelayDM: returns getPerfectDelay calculated with difficulty mutliplier
	public static float getPerfectDelay(float mThickness) { return mThickness / (5.02f * getBaseSpeed()) * getDelayMultDM(); }

	//getPerfectThickness: returns a good THICKNESS value in relation to human reflexes
	public static float getPerfectThickness(float mThickness) { return mThickness * getBaseSpeed(); }

	//getSideDistance: returns shortest distance from a side to another
	public static int getSideDistance(int mSide1, int mSide2){
		int start = mSide1;
		int rightSteps = 0;
		while (start != mSide2) {
			rightSteps = rightSteps + 1;
			start = start + 1;
			if (start > CurrentMap.gameProperties.sides - 1 ){ start = 0;}
		}

		start = mSide1;
		int leftSteps = 0;
		while (start != mSide2) {
			leftSteps = leftSteps + 1;
			start = start - 1;
			if (start < 0 ) { start = CurrentMap.gameProperties.sides - 1; }
		}

		if (rightSteps < leftSteps) { return rightSteps; }
		return leftSteps;
	}

	public static void wallAcc(int side, float thickness, float speed, float acceleration, float speedMin, float speedMax) {
		CurrentMap.gameProperties.wallTimeline.submit(new Wall(side, thickness, new SpeedData(speed * getBaseSpeed(), acceleration, speedMin * getBaseSpeed(), speedMax * getBaseSpeed())));
	}

	//cWall: creates a wall with the common THICKNESS
	public static void cWall(int mSide, float thickness){
		CurrentMap.gameProperties.wallTimeline.submit(new Wall(mSide, thickness, new SpeedData(getBaseSpeed())));
	}

	//oWall: creates a wall opposite to the mSide passed
	public static void oWall(int mSide, float thickness){
		cWall(mSide + getHalfSides(), thickness);
	}

	//rWall: union of cwall and owall (created 2 walls facing each other)
	public static void rWall(int mSide, float thickness) {
		cWall(mSide, thickness);
		oWall(mSide, thickness);
	}

	//cWallEx: creates a wall with mExtra walls attached to it
	public static void cWallEx(int mSide, int mExtra, float thickness) {
		cWall(mSide, thickness);
		int loopDir = 1;

		if (mExtra < 0) { loopDir = -1; }
		for (int i = 0; i < mExtra; i+=loopDir) {
			cWall(mSide + i, thickness);
		}
	}

	//oWallEx: creates a wall with mExtra walls opposite to mSide
	public static void oWallEx(int mSide, int mExtra, float thickness) {
		cWallEx(mSide + getHalfSides(), mExtra, thickness);
	}

	//rWallEx: union of cwallex and owallex
	public static void  rWallEx(int mSide, int mExtra, float thickness) {
		cWallEx(mSide, mExtra, thickness);
		oWallEx(mSide, mExtra, thickness);
	}

	//cBarrageN: spawns a barrage of walls, with a free mSide plus mNeighbors
	public static void cBarrageN(int mSide, int mNeighbors, float thickness) {
		for (int i = mNeighbors; i <= CurrentMap.gameProperties.sides - 2 - mNeighbors; ++i) {
			cWall(mSide + i + 1, thickness);
		}
	}

	//cBarrage: spawns a barrage of walls, with a single free mSide
	public static void  cBarrage(int mSide, float thickness) { cBarrageN(mSide, 0, thickness); }

	//cBarrageOnlyN: spawns a barrage of wall, with only free mNeighbors
	public static void  cBarrageOnlyN(int mSide, int mNeighbors, float thickness){
		cWall(mSide, thickness);
		cBarrageN(mSide, mNeighbors, thickness);
	}

	//cAltBarrage: spawns a barrage of alternate walls
	public static void  cAltBarrage(int mSide, int mStep, float thickness) {
		for (int i = 0; i <= CurrentMap.gameProperties.sides / mStep; ++i ) {
			cWall(mSide + i * mStep, thickness);
		}
	}
	
	public static void  pAltBarrage(int mTimes, int mStep) {
		float delay = getPerfectDelay(THICKNESS) * 5.6f;
		for (int i = 0; i<= mTimes; ++i) {
			cAltBarrage(i, mStep, THICKNESS);
			timelineWait(delay);
		}

		timelineWait(delay);
	}
	
	//pMirrorSpiral: spawns a spiral of rWallEx
	public static void  pMirrorSpiral(int mTimes, int mExtra) {
		float thickness = getPerfectThickness(THICKNESS);
		float delay = getPerfectDelay(thickness);
		int startSide = getRandomSide();
		int loopDir = getRandomDir();
		int j = 0;

		for (int i = 0; i <= mTimes; ++i ) {
			rWallEx(startSide + j, mExtra, thickness);
			j = j + loopDir;
			timelineWait(delay);
		}

		timelineWait(getPerfectDelay(THICKNESS) * 6.5f);
	}
	
	//pMirrorSpiralDouble: spawns a spiral of rWallEx where you need to change direction
	public static void  pMirrorSpiralDouble(int mTimes, int mExtra) {
		float thickness = getPerfectThickness(THICKNESS);
		float delay = getPerfectDelay(thickness);
		int startSide = getRandomSide();
		int currentSide = startSide;
		int loopDir = getRandomDir();
		int j = 0;

		for (int i = 0; i <= mTimes; ++i) {
			rWallEx(startSide + j, mExtra, thickness);
			j = j + loopDir;
			timelineWait(delay);
		}
	
		rWallEx(startSide + j, mExtra, thickness);
		timelineWait(delay * 0.9f);

		rWallEx(startSide + j, mExtra, thickness);
		timelineWait(delay * 0.9f);

		loopDir *= -1;

		for (int i = 0; i <= mTimes + 1; ++i) {
			currentSide = currentSide + loopDir;
			rWallEx(startSide + j, mExtra, thickness);
			j = j + loopDir;
			timelineWait(delay);
		}

		timelineWait(getPerfectDelay(THICKNESS) * 7.5f);
	}
	
	//pBarrageSpiral: spawns a spiral of cBarrage
	public static void  pBarrageSpiral(int mTimes, float mDelayMult, int mStep) {
		float delay = getPerfectDelay(THICKNESS) * 5.6f * mDelayMult;
		int startSide = getRandomSide();
		int loopDir = mStep * getRandomDir();
		int j = 0;

		for (int i = 0; i <= mTimes; ++i) {
			cBarrage(startSide + j, THICKNESS);
			j = j + loopDir;
			timelineWait(delay);
			if(CurrentMap.gameProperties.sides < 6) { timelineWait(delay * 0.6f); }
		}

		timelineWait(getPerfectDelay(THICKNESS) * 6.1f);
	}
	
	//pDMBarrageSpiral: spawns a spiral of cBarrage, with static delay
	public static void  pDMBarrageSpiral(int mTimes, float mDelayMult, int mStep) {
		float delay = (getPerfectDelay(THICKNESS) * 5.42f) * (mDelayMult / (float) Math.pow(CurrentMap.gameProperties.difficulty, 0.4)) * (float)Math.pow(getBaseSpeed(), 0.35);
		int  startSide = getRandomSide();
		int loopDir = mStep * getRandomDir();
		int j = 0;

		for (int i = 0; i <= mTimes; ++i) {
			cBarrage(startSide + j, THICKNESS);
			j = j + loopDir;
			timelineWait(delay);
			if(CurrentMap.gameProperties.sides < 6) { timelineWait(delay * 0.49f); }
		}

		timelineWait(getPerfectDelay(THICKNESS) * (6.7f * (float)Math.pow(CurrentMap.gameProperties.difficulty, 0.7)));
	}
	
	//pWallExVortex: spawns left-left right-right spiral patters
	public static void  pWallExVortex(int mTimes, int  mStep, int mExtraMult) {
		float delay = getPerfectDelay(THICKNESS) * 5.0f;
		int startSide = getRandomSide();
		int loopDir = getRandomDir();
		int currentSide = startSide;

		for (int j = 0; j <= mTimes; ++j) {
			for (int i = 0; i <= mStep; ++i) {
				currentSide = currentSide + loopDir;
				rWallEx(currentSide, loopDir * mExtraMult, THICKNESS);
				timelineWait(delay);
			}

			loopDir = loopDir * -1;

			for (int i = 0; i <= mStep + 1; ++i) {
				currentSide = currentSide + loopDir;
				rWallEx(currentSide, loopDir * mExtraMult, THICKNESS);
				timelineWait(delay);
			}
		}

		timelineWait(getPerfectDelay(THICKNESS) * 5.5f);
	}
	
	//pInverseBarrage: spawns two barrages who force you to turn 180 degrees
	public static void  pInverseBarrage(int mTimes) {
		float delay = getPerfectDelay(THICKNESS) * 9.9f;
		int startSide = getRandomSide();

		for (int i = 0; i<= mTimes; ++i) {
			cBarrage(startSide, THICKNESS);
			timelineWait(delay);
			if(CurrentMap.gameProperties.sides < 6) { timelineWait(delay * 0.8f); }
			cBarrage(startSide + getHalfSides(), THICKNESS);
			timelineWait(delay);
		}

		timelineWait(getPerfectDelay(THICKNESS) * 2.5f);
	}
	
	//pRandomBarrage: spawns barrages with random side, and waits humanly-possible times dep}ing on the sides distance
	public static void  pRandomBarrage(int mTimes, float mDelayMult) {
		int side = getRandomSide();
		int oldSide = 0;

		for (int i = 0; i < mTimes; ++i) {
			cBarrage(side, THICKNESS);
			oldSide = side;
			side = getRandomSide();
			timelineWait(getPerfectDelay(THICKNESS) * (2 + (getSideDistance(side, oldSide) * mDelayMult)));
		}

		timelineWait(getPerfectDelay(THICKNESS) * 5.6f);
	}
	
	//pMirrorWallStrip: spawns rWalls close to one another on the same side
	public static void  pMirrorWallStrip(int mTimes, int mExtra) {
		float delay = getPerfectDelay(THICKNESS) * 3.65f;
		int startSide = getRandomSide();

		for (int i = 0; i < mTimes; ++i) {
			rWallEx(startSide, mExtra, THICKNESS);
			timelineWait(delay);
		}

		timelineWait(getPerfectDelay(THICKNESS) * 5.00f);
	}
	
	//pTunnel: forces you to circle around a very thick wall
	public static void pTunnel(int mTimes){
		float thickness = getPerfectThickness(THICKNESS);
		float delay = getPerfectDelay(thickness) * 5;
		int startSide = getRandomSide();
		int loopDir = getRandomDir();

		for (int i = 0; i < mTimes; ++i) {
			CurrentMap.gameProperties.wallTimeline.submit(new Wall(startSide, thickness + 5 * getBaseSpeed() * delay, new SpeedData(getBaseSpeed())));

			cBarrage(startSide + loopDir, thickness);
			timelineWait(delay);
			loopDir = loopDir * -1;
		}

		timelineWait(delay);
	}

	public static void timelineWait(float delay){
		CurrentMap.gameProperties.wallTimeline.wait(delay / 60);
	}

}
