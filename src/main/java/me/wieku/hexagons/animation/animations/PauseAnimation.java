package me.wieku.hexagons.animation.animations;

import me.wieku.hexagons.animation.AnimationManager;
import me.wieku.hexagons.animation.api.AnimationBase;
import me.wieku.hexagons.animation.api.AnimationCallback;

public class PauseAnimation implements AnimationBase<PauseAnimation> {

	private float elapsed = 0.0f;
	private float duration = 0.0f;
	private boolean paused = false;
	private boolean ended = false;
	private boolean finished = false;
	private AnimationCallback callback;
	
	
	public PauseAnimation(float time) {
		duration = time;
	}
	
	@Override
	public PauseAnimation getAnimation() {
		return this;
	}
	
	@Override
	public void start(AnimationManager manager) {
		if (manager != null) {
			manager.getAnimations().add(this);
		}
	}

	@Override
	public void update(float delta) {
		if (paused) return;
		elapsed += delta;
		
		if (elapsed >= duration) {
			finished = true;
			ended = true;
			
		}
		
	}

	@Override
	public AnimationCallback getCallback() {
		return callback;
	}
	
	@Override
	public PauseAnimation setCallback(AnimationCallback callback) {
		this.callback = callback;
		return this;
	}

	@Override
	public boolean hasEnded() {
		return ended;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	@Override
	public void kill() {
		finished = true;
		
	}

	@Override
	public void pause() {
		paused = true;
	}

	@Override
	public void resume() {
		paused = false;
	}

}
