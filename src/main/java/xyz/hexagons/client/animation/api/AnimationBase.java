package xyz.hexagons.client.animation.api;

import xyz.hexagons.client.animation.AnimationManager;

public interface AnimationBase<T> {
	
	T getAnimation();
	void start(AnimationManager manager);
	void pause();
	void resume();
	void update(float delta);
	boolean hasEnded();
	boolean isFinished();
	AnimationCallback getCallback();
	T setCallback(AnimationCallback callback);
	void kill();
	
}