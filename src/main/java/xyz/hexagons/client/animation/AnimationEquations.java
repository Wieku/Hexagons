package xyz.hexagons.client.animation;

import xyz.hexagons.client.animation.api.AnimationEquation;

public interface AnimationEquations {

	float back_s = 1.70158f;
	float elastic_a = 0;
	float elastic_p = 0;
	boolean elastic_setA = false;
	boolean elastic_setP = false;
	
	/*
	 * Back equations
	 */
	
	AnimationEquation easeInBack = t -> {
		float s = back_s;
		return t * t * ((s + 1) * t - s);
	};

	AnimationEquation easeOutBack = t -> {
		float s = back_s;
		return (t -= 1) * t * ((s + 1) * t + s) + 1;
	};

	AnimationEquation easeInOutBack = t -> {
		float s = back_s;
		if ((t *= 2) < 1) return 0.5f * (t * t * (((s *= 1.525f) + 1) * t - s));
		return 0.5f * ((t -= 2) * t * (((s *= 1.525f) + 1) * t + s) + 2);
	};

	/*
	 * Bounce equations
	 */

	AnimationEquation easeOutBounce = t -> {
		if (t < (1 / 2.75)) {
			return 7.5625f * t * t;
		} else if (t < (2 / 2.75)) {
			return 7.5625f * (t -= 1.5f / 2.75f) * t + .75f;
		} else if (t < (2.5 / 2.75)) {
			return 7.5625f * (t -= 2.25f / 2.75f) * t + .9375f;
		} else {
			return 7.5625f * (t -= 2.625f / 2.75f) * t + .984375f;
		}
	};

	AnimationEquation easeInBounce = t -> 1 - easeOutBounce.compute(1 - t);

	AnimationEquation easeInOutBounce = t -> {
		if (t < 0.5f) return easeInBounce.compute(t * 2) * .5f;
		else return easeOutBounce.compute(t * 2 - 1) * .5f + 0.5f;
	};
	
	/*
	 * Circ equations
	 */
	
	AnimationEquation easeInCirc = t -> (float) -Math.sqrt(1 - t * t) + 1;
	
	AnimationEquation easeOutCirc = t -> (float) Math.sqrt(1 - (t -= 1) * t);
	
	AnimationEquation easeInOutCirc = t -> {
		if ((t *= 2) < 1) return -0.5f * ((float) Math.sqrt(1 - t * t) - 1);
		return 0.5f * ((float) Math.sqrt(1 - (t -= 2) * t) + 1);
	};
	
	/*
	 * Cubic equations
	 */
	
	AnimationEquation easeInCubic = t -> t * t * t;
	
	AnimationEquation easeOutCubic = t -> (t -= 1) * t * t + 1;
	
	AnimationEquation easeInOutCubic = t -> {
		if ((t *= 2) < 1) return 0.5f * t * t * t;
		return 0.5f * ((t -= 2) * t * t + 2);
	};
	
	/*
	 * Elastic equations
	 */
	
	AnimationEquation easeInElastic = (t) -> {
		float a = elastic_a;
		float p = elastic_p;
		if (t == 0) return 0;
		if (t == 1) return 1;
		if (!elastic_setP) p = .3f;
		float s;
		if (!elastic_setA || a < 1) {
			a = 1;
			s = p / 4;
		} else
			s = p / (2 * (float) Math.PI) * (float) Math.asin(1 / a);
		return -(a * (float) Math.pow(2, 10 * (t - 1)) * (float) Math.sin((t - s) * (2 * Math.PI) / p));
	};
	
	AnimationEquation easeOutElastic = (t) -> {
		float a = elastic_a;
		float p = elastic_p;
		if (t == 0) return 0;
		if (t == 1) return 1;
		if (!elastic_setP) p = .3f;
		float s;
		if (!elastic_setA || a < 1) {
			a = 1;
			s = p / 4;
		} else
			s = p / (2 * (float) Math.PI) * (float) Math.asin(1 / a);
		return a * (float) Math.pow(2, -10 * t) * (float) Math.sin((t - s) * (2 * Math.PI) / p) + 1;
	};
	
	AnimationEquation easeInOutElastic = (t) -> {
		float a = elastic_a;
		float p = elastic_p;
		if (t == 0) return 0;
		if ((t * 2) == 2) return 1;
		if (!elastic_setP) p = .3f * 1.5f;
		float s;
		if (!elastic_setA || a < 1) {
			a = 1;
			s = p / 4;
		} else
			s = p / (2 * (float) Math.PI) * (float) Math.asin(1 / a);
		if (t < 1) return -.5f * (a * (float) Math.pow(2, 10 * (t - 1)) * (float) Math.sin((t - s) * (2 * Math.PI) / p));
		return a * (float) Math.pow(2, -10 * (t - 1)) * (float) Math.sin((t - s) * (2 * Math.PI) / p) * .5f + 1;
	};
	
	/*
	 * Expo equations
	 */
	
	AnimationEquation easeInExpo = t -> (t == 0) ? 0 : (float) Math.pow(2, 10 * (t - 1));
	
	AnimationEquation easeOutExpo = t -> (t == 1) ? 1 : -(float) Math.pow(2, -10 * t) + 1;
	
	AnimationEquation easeInOutExpo = t -> {
		if (t == 0) return 0;
		if (t == 1) return 1;
		if ((t *= 2) < 1) return 0.5f * (float) Math.pow(2, 10 * (t - 1));
		return 0.5f * (-(float) Math.pow(2, -10 * --t) + 2);
	};
	
	/*
	 * Quad equations
	 */
	
	AnimationEquation easeInQuad = t -> t * t;
	
	AnimationEquation easeOutQuad = t -> -t * (t - 2);
	
	AnimationEquation easeInOutQuad = t -> {
		if ((t *= 2) < 1) return 0.5f * t * t;
		return -0.5f * ((--t) * (t - 2) - 1);
	};
	
	/*
	 * Quart equations
	 */
	
	AnimationEquation easeInQuart = t -> t * t * t * t;
	
	AnimationEquation easeOutQuart = t -> -((t -= 1) * t * t * t - 1);
	
	AnimationEquation easeInOutQuart = t -> {
		if ((t *= 2) < 1) return 0.5f * t * t * t * t;
		return -0.5f * ((t -= 2) * t * t * t - 2);
	};
	
	/*
	 * Quint equations
	 */
	
	AnimationEquation easeInQuint = t -> t * t * t * t * t;
	
	AnimationEquation easeOutQuint = t -> (t -= 1) * t * t * t * t + 1;
	
	AnimationEquation easeInOutQuint = t -> {
		if ((t *= 2) < 1) return 0.5f * t * t * t * t * t;
		return 0.5f * ((t -= 2) * t * t * t * t + 2);
	};
	
	/*
	 * Sine equations
	 */
	
	AnimationEquation easeInSine = t -> (float) -Math.cos(t * (Math.PI  / 2)) + 1;
	
	AnimationEquation easeOutSine = t -> (float) Math.sin(t * (Math.PI / 2));
	
	AnimationEquation easeInOutSine = t -> -0.5f * ((float) Math.cos(Math.PI * t) - 1);
	
    /*
	 * Linear equations
	 */
    
	AnimationEquation easeLinear = t -> t;
	

	enum Equations{
		Linear(easeLinear),
		InBack(easeInBack),
		OutBack(easeOutBack),
		InOutBack(easeInOutBack),
		InBounce(easeInBounce),
		OutBounce(easeOutBounce),
		InOutBounce(easeInOutBounce),
		InCirc(easeInCirc),
		OutCirc(easeOutCirc),
		InOutCirc(easeInOutCirc),
		InCubic(easeInCubic),
		OutCubic(easeOutCubic),
		InOutCubic(easeInOutCubic),
		InElastic(easeInElastic),
		OutElastic(easeOutElastic),
		InOutElastic(easeInOutElastic),
		InExpo(easeInExpo),
		OutExpo(easeOutExpo),
		InOutExpo(easeInOutExpo),
		InQuad(easeInQuad),
		OutQuad(easeOutQuad),
		InOutQuad(easeInOutQuad),
		InQuart(easeInQuart),
		OutQuart(easeOutQuart),
		InOutQuart(easeInOutQuart),
		InQuint(easeInQuint),
		OutQuint(easeOutQuint),
		InOutQuint(easeInOutQuint),
		InSine(easeInSine),
		OutSine(easeOutSine),
		InOutSine(easeInOutSine);

		AnimationEquation eq;

		Equations(AnimationEquation e) {
			eq = e;
		}
		

		
		AnimationEquation getEquation() {
			return eq;
		}
	}
	
}
