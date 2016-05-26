package me.wieku.animation.timeline;

import me.wieku.animation.AnimationManager;
import me.wieku.animation.animations.PauseAnimation;
import me.wieku.animation.api.AnimationBase;
import me.wieku.animation.api.AnimationCallback;

public class Timeline implements AnimationBase<Timeline> {

	private AnimationManager manager;
	private AnimationCallback callback;
	private boolean finished = false;
	private boolean ended = false;
	private int currentTween = 0;
	private float delay = 0.0f;
	private boolean paused;
	
	public Timeline() {
		manager = new AnimationManager();
	}
	
	@Override
	public Timeline getAnimation() {
		return this;
	}

	@Override
	public void start(AnimationManager manager) {
		manager.getAnimations().add(this);
	}

	public Timeline delay(float delay) {
		this.delay = delay;
		return this;
	}
	
	@Override
	public void update(float delta) {
		
		if (paused) return;
		
		if (delay == 0) {
			
			updateElement(delta);
			
		} else {
			if ((delay -= delta) < 0) {
				updateElement(Math.abs(delay));
				delay = 0;
			}
		}
	}

	private void updateElement(float delta) {
		if (manager.getAnimations().size() <= currentTween) {
			finished = true;
			ended = true;
			manager.dispose();
			return;
		}
		
		AnimationBase<?> tween = manager.getAnimations().get(currentTween);
		
		if (tween.isFinished()) {
			if (tween.hasEnded()) {
				if (tween.getCallback() != null) {
					tween.getCallback().onEvent(tween);
				}
			}
			
			++currentTween;
			
		} else {
			tween.update(delta);
		}
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
	public AnimationCallback getCallback() {
		return callback;
	}

	@Override
	public Timeline setCallback(AnimationCallback callback) {
		this.callback = callback;
		return this;
	}

	@Override
	public void kill() {
		finished = true;
	}

	public Timeline push(AnimationParallel parallel) {
		parallel.start(manager);
		return this;
	}
	
	public Timeline push(AnimationSequence sequence) {
		sequence.start(manager);
		return this;
	}
	
	public Timeline pushPause(float time) {
		PauseAnimation pause = new PauseAnimation(time);
		pause.start(manager);
		return this;
	}
	
	public AnimationSequence beginSequence() {
		return new AnimationSequence(this);
	}
	
	public AnimationParallel beginParallel() {
		return new AnimationParallel(this);
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
