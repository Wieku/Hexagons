package me.wieku.hexagons.api;

/**
 * @author Sebastian Krajewski on 21.03.15.
 */
public class HColor {

	float fr, fg, fb, fa;
	float pr, pg, pb, pa;
	float hue, hueMin, hueMax, hueInc, hueShift;
	float offset = 1f, darkness = 1f;
	boolean pingPong, dynamic, dynamicDarknes, main = false;
	boolean pulse = false;
	public float r, g, b, a;

	int inc = 1;

	public HColor (float r, float g, float b, float a){
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

	public HColor addHue(float hueMin, float hueMax, float hueInc, boolean pingPong) {
		this.hueMin = hueMin;
		this.hueMax = hueMax;
		this.hueInc = hueInc;
		dynamic = true;
		this.pingPong = pingPong;
		return this;
	}

	public HColor setHueInc(float inc){
		hueInc = inc;
		return this;
	}

	public float getHueInc(){
		return hueInc;
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

	public HColor addDynamicDarkness(float darkness){
		this.darkness = darkness;
		dynamicDarknes = true;
		return this;
	}

	float delta0;
	float percent;
	public void update (float delta){

		float gr = fr, gg = fg, gb = fb;

		if(pulse){

			delta0 += delta * 60 * CurrentMap.colorPulseInc;

			if(delta0 < 0){
				delta0 = 0;
				CurrentMap.colorPulseInc *= -1f;
			}
			if(delta0 > CurrentMap.colorPulse){
				delta0 = CurrentMap.colorPulse;
				CurrentMap.colorPulseInc *= -1f;
			}

			percent = delta0 / CurrentMap.colorPulse;
		}

		if(dynamic){

			hue += hueInc * delta * 60f * Math.pow(CurrentMap.difficulty, 0.8);

			if(hue < hueMin)
			{
				if(pingPong) { hue = hueMin; hueInc *= -1.f; }
				else hue = hueMax;
			}
			if(hue > hueMax)
			{
				if(pingPong) { hue = hueMax; hueInc *= -1.f; }
				else hue = hueMin;
			}

			float[] rgb = getFromHSV((hue + hueShift) / 360f, 1f, 1f);

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

	static float clamp(float value, float min, float max){
		return value < min ? min : value > max ? max : value;
	}

	public static float[] getFromHSV(float hue, float saturation, float brightness) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int)(brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float)Math.floor(hue)) * 6.0f;
			float f = h - (float)Math.floor(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int)h) {
				case 0:
					r = (int)(brightness * 255.0f + 0.5f);
					g = (int)(t * 255.0f + 0.5f);
					b = (int)(p * 255.0f + 0.5f);
					break;
				case 1:
					r = (int)(q * 255.0f + 0.5f);
					g = (int)(brightness * 255.0f + 0.5f);
					b = (int)(p * 255.0f + 0.5f);
					break;
				case 2:
					r = (int)(p * 255.0f + 0.5f);
					g = (int)(brightness * 255.0f + 0.5f);
					b = (int)(t * 255.0f + 0.5f);
					break;
				case 3:
					r = (int)(p * 255.0f + 0.5f);
					g = (int)(q * 255.0f + 0.5f);
					b = (int)(brightness * 255.0f + 0.5f);
					break;
				case 4:
					r = (int)(t * 255.0f + 0.5f);
					g = (int)(p * 255.0f + 0.5f);
					b = (int)(brightness * 255.0f + 0.5f);
					break;
				case 5:
					r = (int)(brightness * 255.0f + 0.5f);
					g = (int)(p * 255.0f + 0.5f);
					b = (int)(q * 255.0f + 0.5f);
					break;
			}
		}
		return new float[]{r/255f, g/255f, b/255f};
	}

}
