package xyz.hexagons.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidMusic;
import com.badlogic.gdx.files.FileHandle;
import xyz.hexagons.client.audio.AudioPlayer;
import xyz.hexagons.client.utils.Glider;

public class AndroidAudioPlayer implements AudioPlayer {

	private boolean isPaused = false;
	AndroidMusic music;
	private FileHandle file;
	private Glider glider = new Glider();
	private boolean ended = false;
	private float beginVolume, targetVolume, deltaTime = -1, time = -2;


	public AndroidAudioPlayer(FileHandle fileHandle) {
		file = fileHandle;
		music = (AndroidMusic) Gdx.audio.newMusic(fileHandle);
		music.setOnCompletionListener(m -> ended = true);
	}

	@Override
	public FileHandle getFile() {
		return file;
	}

	@Override
	public float getVolume() {
		return music.getVolume();
	}

	@Override
	public void setVolume(float volume) {
		music.setVolume(volume);
	}

	@Override
	public float getPosition() {
		return music.getPosition();
	}

	@Override
	public float getDuration() {
		return music.getDuration();
	}

	@Override
	public void setPosition(float milis) {
		music.setPosition(milis);
	}

	@Override
	public void play() {
		music.play();
		isPaused = false;
		ended = false;
	}

	@Override
	public void play(float time) {
		music.setPosition(time);
		play();
	}

	@Override
	public void update(float delta) {
		if (deltaTime < time) {
			deltaTime += delta;
			setVolume(beginVolume + ((targetVolume - beginVolume) * deltaTime) / time);
		}
	}

	@Override
	public void glideVolume(float volume, float time) {
		beginVolume = getVolume();
		targetVolume = volume;
		this.time = time;
		deltaTime = 0;
	}

	float[] data = new float[40];
	@Override
	public float[] getFFT() {
		return data;
	}

	@Override
	public boolean isOnset() {
		return false;
	}

	@Override
	public void pause() {
		music.pause();
		isPaused = true;
	}

	@Override
	public boolean hasEnded() {
		return false;
	}

	@Override
	public void stop() {
		music.stop();
		ended = true;
	}

	@Override
	public void dispose() {
		music.dispose();
	}

	@Override
	public boolean isPaused() {
		return !music.isPlaying() && !ended;
	}

	@Override
	public void setLooping(boolean looping) {
		music.setLooping(looping);
	}
}