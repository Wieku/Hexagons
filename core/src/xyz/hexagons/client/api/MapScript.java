package xyz.hexagons.client.api;

/**
 * @author Sebastian Krajewski on 21.03.15.
 */
public interface MapScript {
	void init();
	void nextLevel(int levelNum); //TODO: Idea says it may not be called
	void nextPattern();
	void update(float delta);
	void initColors();
	void initEvents();
	MapEventHandler getEventHandlers();
}
