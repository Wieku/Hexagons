package xyz.hexagons.client;

import com.badlogic.gdx.files.FileHandle;
import xyz.hexagons.client.audio.AudioPlayer;

public class AndroidAudioPlayer implements AudioPlayer {

    private boolean isPaused = false;

    public AndroidAudioPlayer(FileHandle fileHandle) {

    }

    @Override
    public FileHandle getFile() {
        return null;
    }

    @Override
    public float getVolume() {
        return 0;
    }

    @Override
    public void setVolume(float volume) {

    }

    @Override
    public float getPosition() {
        return 0;
    }

    @Override
    public float getDuration() {
        return 0;
    }

    @Override
    public void setPosition(float milis) {

    }

    @Override
    public void play() {

    }

    @Override
    public void play(float time) {
        isPaused = false;
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void glideVolume(float volume, float time) {

    }

    @Override
    public float[] getFFT() {
        return new float[40];
    }

    @Override
    public boolean isOnset() {
        return false;
    }

    @Override
    public void pause() {
        isPaused = true;
    }

    @Override
    public boolean hasEnded() {
        return false;
    }

    @Override
    public void stop() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public void setLooping(boolean looping) {

    }
}
