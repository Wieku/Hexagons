package xyz.hexagons.client.utils;

public class Glider {
	
	private float beginval;
	float targetVal;
	float time=0;
	float delta0=5;
	
	float value;
	
	public Glider(){
		
	}
	
	public Glider (float value) {
		this.value = value;
		this.targetVal = value;
	}
	
	public void glide(float target, float time) {
		beginval = value;
		targetVal = target;
		this.time = time;
		delta0 = 0;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	public float getValue() {
		return value;
	}
	
	public void update(float delta) {
		if (delta0 < time) {
			delta0+=delta;
			value = beginval + ((targetVal - beginval) * delta0) / time;
		} else {
			value = targetVal;
		}
	}
	
}
