package xyz.hexagons.client.desktop;

import com.badlogic.gdx.backends.lwjgl.audio.OpenALPlayer;
import com.badlogic.gdx.files.FileHandle;
import me.wieku.audio.analysis.BeatDetect;
import me.wieku.audio.analysis.FFT;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.audio.AudioPlayer;
import xyz.hexagons.client.utils.Utils;

public class DesktopAudioPlayer implements AudioPlayer {
	
	public static OpenALPlayer musicPlayer;
	
	private boolean ended;
	private FileHandle handle;
	private boolean firstStart = false;
	private FFT fft;
	private BeatDetect detect;
	private float[] outBuf = new float[60];
	
	private static final int SIZE = 4096;
	private float[] bufferFloat = new float[SIZE * 2];
	private float[] bufferFloat2 = new float[SIZE];
	
	private float beginVolume, targetVolume, deltaTime = -1, time = -2;
	
	
	public DesktopAudioPlayer(FileHandle fileHandle) {
		if (!fileHandle.exists()) throw new IllegalStateException("Cannot find audio");
		handle = fileHandle;
		
		if (musicPlayer == null) musicPlayer = new OpenALPlayer();
		
		musicPlayer.setFile(fileHandle);
		musicPlayer.setOnCompletionListener(m -> ended = true);
		
		fft = new FFT(SIZE, musicPlayer.getRate());
		fft.linAverages(60);
		
		detect = new BeatDetect(SIZE * 2, musicPlayer.getRate());
		detect.setSensitivity(10);
	}
	
	@Override
	public FileHandle getFile() {
		return handle;
	}
	
	@Override
	public float getVolume() {
		return musicPlayer.getVolume();
	}
	
	@Override
	public void setVolume(float volume) {
		musicPlayer.setVolume(volume);
	}
	
	@Override
	public float getPosition() {
		return musicPlayer.getPosition();
	}
	
	@Override
	public void setPosition(float milis) {
		Instance.executor.execute(() -> musicPlayer.setPosition(milis));
	}
	
	@Override
	public void play() {
		musicPlayer.play();
		firstStart = true;
		ended = false;
	}
	
	@Override
	public void play(float time) {
		musicPlayer.play();
		if (firstStart || time > 0) {
			setPosition(time);
		}
		firstStart = true;
		ended = false;
		
	}
	
	@Override
	public void update(float delta) {
		
		if (deltaTime < time) {
			deltaTime += delta;
			setVolume(beginVolume + ((targetVolume - beginVolume) * deltaTime) / time);
		}
		
		Utils.byteToFloat(musicPlayer.tempBytes, 0, bufferFloat, 0, bufferFloat.length);
		
		for (int i = 0, j = 0; i < bufferFloat2.length; i++, j += 2)
			bufferFloat2[i] = (bufferFloat[j] + bufferFloat[j + 1]) / 2;
		
		fft.forward(bufferFloat2);
		
		for (int i = 0; i < fft.avgSize(); i++)
			outBuf[i] = fft.getAvg(i);
		
		detect.detect(bufferFloat);
	}
	
	@Override
	public void glideVolume(float volume, float time) {
		beginVolume = getVolume();
		targetVolume = volume;
		this.time = time;
		deltaTime = 0;
	}
	
	@Override
	public float[] getFFT() {
		return outBuf;
	}
	
	@Override
	public boolean isOnset() {
		return detect.isRange(1, 3, 2);
	}
	
	@Override
	public void pause() {
		musicPlayer.pause();
	}
	
	@Override
	public boolean hasEnded() {
		return ended;
	}
	
	@Override
	public void stop() {
		musicPlayer.stop();
		ended = true;
	}
	
	@Override
	public void dispose() {
		musicPlayer.dispose();
	}
	
	@Override
	public boolean isPaused() {
		return !musicPlayer.isPlaying() && !ended;
	}
	
	@Override
	public void setLooping(boolean looping) {
		musicPlayer.setLooping(looping);
	}
	
}