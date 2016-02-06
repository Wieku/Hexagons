package me.wieku.hexagons.map;

import me.wieku.hexagons.api.HColor;
import me.wieku.hexagons.utils.Hocon;

import java.util.ArrayList;
import java.util.List;

public class Style {

	String name;
	HColor mainColor;
	ArrayList<HColor> colors = new ArrayList<>();
	Hue sharedHue;


	public int layers = 6;
	public float depth = 1.5f,
	skew = 0f,
	minSkew = 0f,
	maxSkew = 1f,
	skewTime = 0f,
	wallSkewLeft = 0f,
	wallSkewRight = 0f,
	darkenMultiplier = 1.5f,
	alphaMultiplier = 1f,
	alphaFalloff = 0f;


	Style(String name, Hocon styleConfig) {
		this.name = name;

		List<Float> arr = styleConfig.getFloatArray("hue");


		sharedHue = new Hue(arr.get(0),arr.get(1),arr.get(2),arr.get(3)==1,true);

		layers = styleConfig.getInteger("layers");
		depth = styleConfig.getFloat("depth");
		darkenMultiplier = styleConfig.getFloat("darkenMultiplier");
		alphaMultiplier = styleConfig.getFloat("alphaMultiplier");
		alphaFalloff = styleConfig.getFloat("alphaFalloff");

		if(styleConfig.contains("skew")) {
			List<Float> arr1 = styleConfig.getFloatArray("skew");
			minSkew = arr1.get(0);
			maxSkew = arr1.get(1);
			skewTime = arr1.get(2);
		}
		if(styleConfig.contains("wallSkew")){
			List<Float> arr2 = styleConfig.getFloatArray("wallSkew");
			wallSkewLeft = arr2.get(0);
			wallSkewRight = arr2.get(1);
		}

	}

	int inc;
	float delta2;
	public void update(float delta) {

		mainColor.update(delta);
		colors.forEach(e->e.update(delta));

		inc = (delta2 == 0 ? 1 : (delta2 == skewTime ? -1 : inc));
		delta2 = Math.min(skewTime, Math.max(delta2 += delta * inc, 0));
		skew = minSkew + (maxSkew - minSkew) * (delta2 / skewTime);


	}

}
