package me.wieku.hexagons.animation.timeline;

import me.wieku.hexagons.animation.AnimationManager;
import me.wieku.hexagons.animation.animations.PauseAnimation;
import me.wieku.hexagons.animation.api.AnimationBase;
import me.wieku.hexagons.animation.api.AnimationCallback;

import java.util.ArrayList;
import java.util.List;

public class AnimationSequence implements AnimationBase<AnimationSequence> {

	private int currentTween = 0;
	private AnimationCallback callback;
	private boolean finished = false;
	private boolean ended = false;
	private Timeline timeline;
	private List<AnimationBase<?>> tweens = new ArrayList<>();
	private boolean paused;
	
	
	public AnimationSequence(Timeline timeline) {
		this.timeline = timeline;
	}
	
	@Override
	public AnimationSequence getAnimation() {
		return this;
	}

	@Override
	public void start(AnimationManager manager) {
		manager.getAnimations().add(this);
		if (tweens.size() > 0) {
			tweens.get(0).start(null);
		}
	}
	
	@Override
	public void update(float delta) {
		
		if (paused) return;
		
		if (tweens.size() <= currentTween) {
			
			finished = true;
			ended = true;
			tweens.clear();
			return;
		}
		
		AnimationBase<?> tween = tweens.get(currentTween);
		
		if (tween.isFinished()) {
			if (tween.hasEnded()) {
				if (tween.getCallback() != null) {
					tween.getCallback().onEvent(tween);
				}
			}
			
			++currentTween;
			
			if (tweens.size() > currentTween) {
				tweens.get(currentTween).start(null);
			}
			
		} else {
			tween.update(delta);
		}
		
	}

	public AnimationSequence push(AnimationBase<?> tween) {
		tweens.add(tween);
		return this;
	}
	
	public AnimationSequence pushPause(float time) {
		push(new PauseAnimation(time));
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
	public AnimationCallback getCallback() {
		return callback;
	}

	@Override
	public AnimationSequence setCallback(AnimationCallback callback) {
		this.callback = callback;
		return this;
	}

	@Override
	public void kill() {
		finished = true;
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
