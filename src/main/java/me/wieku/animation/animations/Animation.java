package me.wieku.animation.animations;

import com.google.common.base.Preconditions;
import me.wieku.animation.AnimationEquations;
import me.wieku.animation.AnimationManager;
import me.wieku.animation.api.AnimationAccessor;
import me.wieku.animation.api.AnimationBase;
import me.wieku.animation.api.AnimationCallback;
import me.wieku.animation.api.AnimationEquation;

import java.util.HashMap;


public class Animation implements AnimationBase<Animation> {
	private static float[] buffer = new float[1000];
	private static HashMap<Class<?>, AnimationAccessor<?>> accessors = new HashMap<>();

	AnimationCallback callBack = null;

	private boolean started = false;
	private boolean pause = false;
	private boolean finished = false;

	private float duration = 0.0f;
	private float elapsed = 0.0f;
	private int type = 0;
	private Object element;
	private float delay = 0.0f;
	private AnimationAccessor<Object> accessor;
	private AnimationEquation equation = AnimationEquations.easeLinear;
	private float[] targets;
	private float[] starts;
	private float[] values;
	private float[] lengths;
	private boolean ended = false;



	@SuppressWarnings("unchecked")
	public Animation(Object element, int type, float duration) {
		Preconditions.checkNotNull(element, "Element cannot be null!");
		this.element = element;
		this.duration = duration;
		this.type = type;
		accessor = (AnimationAccessor<Object>) findAccessor(element);
	}

	public static void addAccessor(Class<?> element, AnimationAccessor<?> accessor) {
		accessors.put(element, accessor);
	}

	@Override
	public Animation getAnimation() {
		return this;
	}

	public Object getObject() {
		return element;
	}

	/**
	 * Sets delay for animation (default 0.0f)
	 * @param delay Delay
	 * @return {@link Animation}
	 */
	public Animation delay(float delay) {
		this.delay = delay;
		return this;
	}

	/**
	 * Sets {@link AnimationEquation} for animation (default AnimationEquations.easeLinear)
	 * @param eq Animation
	 * @return {@link Animation}
	 */
	public Animation ease(AnimationEquation eq) {
		equation = eq;
		return this;
	}

	/**
	 * Finds {@link AnimationAccessor} for given element class
	 * @return {@link AnimationAccessor}
	 */
	private AnimationAccessor<?> findAccessor(Object element) {
		if (accessors.containsKey(element.getClass())) return accessors.get(element.getClass());

		Class<?> parentClass = element.getClass();
		do {
			parentClass = parentClass.getSuperclass();
		} while(parentClass != null && !accessors.containsKey(parentClass));

		return accessors.get(parentClass);
	}

	/**
	 * Returns AnimationAccessor for Animation element class
	 * @return {@link AnimationAccessor}
	 */
	public AnimationAccessor<Object> getAccessor() {
		return accessor;
	}

	/**
	 * Returns Callback for animation (default null)
	 * @return {@link AnimationCallback}
	 */
	@Override
	public AnimationCallback getCallback() {
		return callBack;
	}

	/**
	 * Returns Easing for animation (default AnimationEquation.easeLinear)
	 * @return {@link AnimationEquation}
	 */
	private AnimationEquation getEquation() {
		return equation;
	}

	/**
	 * Checks if animation is ended
	 * @return {@link Boolean}
	 */
	@Override
	public boolean hasEnded() {
		return ended;
	}

	/**
	 * Checks if animation is killed or ended
	 * @return {@link Boolean}
	 */
	@Override
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Checks if method <i>Animation.start(AnimationManager man)</i> is called
	 * @return {@link Boolean}
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * Kills animation but Callback (if it's set) won't called
	 */
	@Override
	public void kill() {
		finished = true;
	}

	/**
	 * Sets Callback for animation
	 * @param cl Callback
	 * @return {@link Animation}
	 */
	@Override
	public Animation setCallback(AnimationCallback cl) {
		callBack = cl;
		return this;
	}

	/**
	 * Creates arrays for type and adds animation to given AnimationManager
	 * @param man Manager
	 */
	@Override
	public void start(AnimationManager man) {

		int i = getAccessor().getValues(element, type, buffer);

		if (i < 0) {
			throw new RuntimeException("Given animationType doesn't exist!");
		}

		starts = new float[i];
		values = new float[i];
		lengths = new float[i];

		for (int j = 0; j < i; j++) {

			float buf = buffer[j];

			starts[j] = buf;
			values[j] = buf;
			lengths[j] = targets[j] - buf;
			//System.out.println(buf+" "+targets[j]+" "+lengths[j]);
		}


		if (man != null) {
			man.getAnimations().add(this);
		}
		started = true;
	}

	/**
	 * Sets target for type
	 * @param target Target
	 * @return {@link Animation}
	 */
	public Animation target(float... target) {
		targets = target;
		return this;
	}

	/**
	 * This method is called by <pre>AnimationManager.update(float delta)</pre>
	 * @param delta Delta
	 */
	@Override
	public void update(float delta) {

		if (pause) return;

		if (delay == 0) {

			updateElement(delta);

		} else {

			if ((delay -= delta) < 0) {
				updateElement(Math.abs(delay));
				delay = 0;
			}
		}

	}

	/**
	 * Computes the percentage value for Accessor<br>
	 * &nbspby percentage value of elapsed time
	 * @param delta
	 */
	private void updateElement(float delta) {
		elapsed += delta;

		if (elapsed > duration)
			elapsed = duration;

		//System.out.println(elapsed+":"+duration);
		float percent = elapsed / duration;

		float percentStep = getEquation().compute(percent);

		for (int j = 0; j < values.length; j++) {
			//System.out.println((lengths[j] * percentStep )+ " " + ( starts[j] + lengths[j] * percentStep));

			values[j] = starts[j] + lengths[j] * percentStep;
		}

		getAccessor().setValues(element, type, values);

		if (percent >= 1.0f) {
			finished = true;
			ended = true;
		}

	}

	@Override
	public void pause() {
		pause = true;
	}

	@Override
	public void resume() {
		pause = false;
	}

}

