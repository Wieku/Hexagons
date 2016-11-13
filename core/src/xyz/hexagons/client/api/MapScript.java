package xyz.hexagons.client.api;

import java.util.Random;

/**
 * @author Sebastian Krajewski on 21.03.15.
 */
public interface MapScript {
	public void onInit();
	public void initEvents();
	public void nextLevel(int levelNum);
	public void nextPattern();
	public void update(float delta);
	public void initColors();
}
