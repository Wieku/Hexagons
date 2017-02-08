package xyz.hexagons.client.api;

import xyz.hexagons.client.map.Hue;
import xyz.hexagons.client.utils.Utils;

/**
 * @author Sebastian Krajewski on 21.03.15.
 */
public class HColor {

	float fr, fg, fb, fa;
	float pr, pg, pb, pa;
	float hueShift;

	Hue hue;

	float offset = 1f, darkness = 1f;
	boolean pingPong, dynamic, dynamicDarknes, main = false;
	boolean pulse = false;
	public float r, g, b, a;

	int inc = 1;

	public HColor (float r, float g, float b, float a) {
		this.r = fr = r;
		this.g = fg = g;
		this.b = fb = b;
		this.a = fa = a;
	}

	public HColor addPulse(float r, float g, float b, float a){
		this.pr = r;
		this.pg = g;
		this.pb = b;
		this.pa = a;
		pulse = true;
		return this;
	}

	public HColor addHue(Hue hue) {
		dynamic = true;
		this.hue = hue;
		return this;
	}

	public HColor setHueInc(float inc){
		hue.hueInc = inc;
		return this;
	}

	public HColor setHueMin(float min){
		hue.hueMin = min;
		return this;
	}

	public HColor setHueMax(float max){
		hue.hueMax = max;
		return this;
	}

	public float getHueInc(){
		return hue.hueInc;
	}

	public HColor addHueOffset(float offset){
		this.offset = offset;
		return this;
	}

	public HColor setMain(boolean main){
		this.main = main;
		return this;
	}

	public HColor addHueShift(float shift){
		hueShift = shift;
		return this;
	}

	public HColor addDynamicDarkness(float darkness) {
		this.darkness = darkness;
		dynamicDarknes = true;
		return this;
	}

	float delta0;
	float percent;
	public void update (float delta) {

		float gr = fr, gg = fg, gb = fb;

		if(pulse){

			delta0 += delta * 60 * CurrentMap.gameProperties.colorPulseInc;

			if(delta0 < CurrentMap.gameProperties.colorPulseMin){
				delta0 = CurrentMap.gameProperties.colorPulseMin;
				CurrentMap.gameProperties.colorPulseInc *= -1f;
			}
			if(delta0 > CurrentMap.gameProperties.colorPulseMax){
				delta0 = CurrentMap.gameProperties.colorPulseMax;
				CurrentMap.gameProperties.colorPulseInc *= -1f;
			}

			percent = delta0 / CurrentMap.gameProperties.colorPulseMax;
		}

		if(dynamic) {

			if(!hue.shared) hue.update(delta);

			float[] rgb = Utils.getFromHSV((hue.hue + hueShift) / 360f, 1f, 1f);

			if(dynamicDarknes || main) {
				gr = rgb[0] / darkness;
				gg = rgb[1] / darkness;
				gb = rgb[2] / darkness;
			} else {
				gr += rgb[0] / offset;
				gg += rgb[1] / offset;
				gb += rgb[2] / offset;
			}
		}

		r = clamp(gr + percent * pr, 0f, 1f);
		g = clamp(gg + percent * pg, 0f, 1f);
		b = clamp(gb + percent * pb, 0f, 1f);
		a = clamp(fa + percent * pa, 0, 1f);

	}

	public float update (float delta, int increment, float pulseMax) {

		float gr = fr, gg = fg, gb = fb;

		if(pulse){

			delta0 += delta * increment;

			if(delta0 < 0){
				delta0 = 0;
				increment *= -1f;
			}
			if(delta0 > pulseMax){
				delta0 = pulseMax;
				increment *= -1f;
			}

			percent = delta0 / pulseMax;
		}

		if(dynamic) {

			if(!hue.shared) hue.update(delta);

			float[] rgb = Utils.getFromHSV((hue.hue + hueShift) / 360f, 1f, 1f);

			if(dynamicDarknes || main) {
				gr = rgb[0] / darkness;
				gg = rgb[1] / darkness;
				gb = rgb[2] / darkness;
			} else {
				gr += rgb[0] / offset;
				gg += rgb[1] / offset;
				gb += rgb[2] / offset;
			}
		}

		r = clamp(gr + percent * pr, 0f, 1f);
		g = clamp(gg + percent * pg, 0f, 1f);
		b = clamp(gb + percent * pb, 0f, 1f);
		a = clamp(fa + percent * pa, 0, 1f);

		return increment;
	}

	static float clamp(float value, float min, float max){
		return value < min ? min : value > max ? max : value;
	}



}
