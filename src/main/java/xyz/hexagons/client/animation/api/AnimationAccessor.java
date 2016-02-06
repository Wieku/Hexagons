package xyz.hexagons.client.animation.api;

public interface AnimationAccessor<T> {

	int getValues(T element, int tweenType, float[] values);
	void setValues(T element, int tweenType, float[] newValues);
	
}
