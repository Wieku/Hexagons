package xyz.hexagons.client;

import com.badlogic.gdx.files.FileHandle;
import xyz.hexagons.client.audio.AudioPlayer;

public class AndroidAudioPlayerFactory implements AudioPlayer.IAudioPlayerFactory {
    @Override
    public AudioPlayer instance(FileHandle file) {
        return new AndroidAudioPlayer(file);
    }
}
