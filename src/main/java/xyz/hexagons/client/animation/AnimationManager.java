package xyz.hexagons.client.animation;

import xyz.hexagons.client.animation.api.AnimationBase;

import java.util.ArrayList;
import java.util.List;

public class AnimationManager {

	List<AnimationBase<?>> animations;
	
	public AnimationManager() {
		animations = new ArrayList<>();
	}
	
	public void update(float delta) {
		
		for (int i = 0; i < animations.size(); i++) {
			AnimationBase<?> tween = animations.get(i);
			if (tween.isFinished()) {
				if (tween.hasEnded()) {
					if (tween.getCallback() != null) {
						tween.getCallback().onEvent(tween);
					}
				}
				
				animations.remove(tween);
				--i;
			} else {
				tween.update(delta);
			}
		}
	}
	
	public List<AnimationBase<?>> getAnimations() {
		return animations;
	}
	
	public void dispose() {
		animations.clear();
	}
	
}
