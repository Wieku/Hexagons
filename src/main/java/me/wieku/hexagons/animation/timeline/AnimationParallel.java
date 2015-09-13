package me.wieku.hexagons.animation.timeline;

import me.wieku.hexagons.animation.AnimationManager;
import me.wieku.hexagons.animation.api.AnimationBase;
import me.wieku.hexagons.animation.api.AnimationCallback;

public class AnimationParallel extends AnimationManager implements AnimationBase<AnimationParallel> {

	private Timeline timeline;
	private boolean finished = false;
	private boolean ended = false;
	private AnimationCallback callback;
	private boolean paused;
	
	public AnimationParallel(Timeline timeline) {
		super();
		this.timeline = timeline;
	}
	
	@Override
	public AnimationParallel getAnimation() {
		return this;
	}

	@Override
	public void start(AnimationManager manager) {
		manager.getAnimations().add(this);
	}

	@Override
	public void update(float delta) {
		
		if (paused) return;
		
		super.update(delta);
		if (getAnimations().size() == 0) {
			finished = true;
			ended = true;
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
	public AnimationParallel setCallback(AnimationCallback callback) {
		this.callback = callback;
		return this;
	}

	@Override
	public void kill() {
		finished  = true;
	}

	public AnimationParallel push(AnimationBase<?> tween) {
		tween.start(this);
		return this;
	}
	
	public Timeline end() {
		return timeline.push(this);
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
