import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.Patterns;
import xyz.hexagons.client.api.SpeedData;
import xyz.hexagons.client.api.Wall;

import static xyz.hexagons.client.api.Patterns.getPerfectThickness;
import static xyz.hexagons.client.api.Patterns.getRandomSide;

public class SynapticPatterns {
    public static float BARRAGE_THICKNESS = 25f;

    public static void cWall(int mSide, float thickness) {
        CurrentMap.data.wallTimeline.submit(new Wall(mSide, thickness, new SpeedData(Patterns.getSpeedMultDM())));
    }

    public static void cBarrageN(int mSide, int mNeighbors, float thickness) {
        for (int i = mNeighbors; i <= CurrentMap.data.sides - 2 - mNeighbors; ++i) {
            cWall(mSide + i + 1, thickness);
        }
    }

    public static void  cBarrage(int mSide, float thickness) {
        cBarrageN(mSide, 0, thickness);
    }

    public static void patternBarrageSpiral(int mTimes, float mDelayMult, int mStep) {
        float delay = Patterns.getPerfectDelayDM(BARRAGE_THICKNESS) * 5.6f * mDelayMult;
        int startSide = getRandomSide();
        int loopDir = mStep * Patterns.getRandomDir();
        int j = 0;

        for (int i = 0; i <= mTimes; ++i) {
            cBarrage(startSide + j, BARRAGE_THICKNESS);
            j = j + loopDir;
            Patterns.t_wait(delay);
            if(CurrentMap.data.sides < 6) { Patterns.t_wait(delay * 0.6f); }
        }

        Patterns.t_wait(Patterns.getPerfectDelayDM(BARRAGE_THICKNESS) * 6.1f);
    }

    public static void patternTunnelFastAlt(int times){
        float thickness = BARRAGE_THICKNESS;
        float delay = Patterns.getPerfectDelay(getPerfectThickness(BARRAGE_THICKNESS)) * 2;
        int startSide = getRandomSide();
        int loopDir = Patterns.getRandomDir();

        for (int i = 0; i < times; ++i) {
            CurrentMap.data.wallTimeline.submit(new Wall(startSide, thickness + 5 * Patterns.getSpeedMultDM() * delay, new SpeedData(Patterns.getSpeedMultDM())));

            cBarrage(startSide + loopDir * ((int)(CurrentMap.data.sides / 2)), thickness);
            Patterns.t_wait(delay);
            loopDir = loopDir * -1;
        }

        Patterns.t_wait(delay);
    }
}
