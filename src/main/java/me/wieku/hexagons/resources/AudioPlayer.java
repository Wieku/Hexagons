package me.wieku.hexagons.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

public class AudioPlayer {

	Music music;
	boolean ended;

	public AudioPlayer(FileHandle file) {
		if(!file.exists()) throw new IllegalStateException("Cannot find audio");
		
		music = Gdx.audio.newMusic(file);
		music.setLooping(true);
		music.setOnCompletionListener(m -> ended = true);
	}

	public void setVolume(float volume){
		music.setVolume(volume);
	}
	
	public float getPosition(){
		return music.getPosition();
	}
	
	public void setPosition(float milis){
		music.setPosition(milis);
	}
	
	public void play(){
		music.play();
		ended = false;
	}

	public void pause(){
		music.pause();
	}
	
	public boolean hasEnded(){
		return ended;
	}
	
	public void stop(){
		music.stop();
		ended = true;
	}

	public boolean isPaused() {
		return !music.isPlaying() && !ended;
	}
}