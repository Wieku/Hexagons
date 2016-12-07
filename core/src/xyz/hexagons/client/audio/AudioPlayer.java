package xyz.hexagons.client.audio;

import com.badlogic.gdx.files.FileHandle;

public interface AudioPlayer {

	interface IAudioPlayerFactory {
		AudioPlayer instance(FileHandle file);
	}

	FileHandle getFile();

	float getVolume();

	void setVolume(float volume);

	float getPosition();

	float getDuration();
	
	void setPosition(float milis);

	void play();

	void play(float time);

	void update(float delta);

	void glideVolume(float volume, float time);

	float[] getFFT();

	boolean isOnset();

	void pause();

	boolean hasEnded();

	void stop();

	void dispose();

	boolean isPaused();

	void setLooping(boolean looping);
}
