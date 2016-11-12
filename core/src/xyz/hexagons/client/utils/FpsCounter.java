package xyz.hexagons.client.utils;

public class FpsCounter {

	private int sampleCount;
	private int sampleIndex;
	private float[] samples;

	private float fps = 0f;

	public FpsCounter(int sampleCount) {
		this.sampleCount = sampleCount;
		samples = new float[sampleCount];
	}

	public void update(float delta) {

		samples[sampleIndex++] = (int)(1f / delta);

		if(sampleIndex >= sampleCount)
			sampleIndex = 0;

		fps = 0;

		for(float i : samples)
			fps += i;

		fps /= sampleCount;
	}

	public float getFPS() {
		return fps;
	}

}
