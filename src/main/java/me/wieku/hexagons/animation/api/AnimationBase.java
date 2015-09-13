package me.wieku.hexagons.animation.api;

import me.wieku.hexagons.animation.AnimationManager;

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