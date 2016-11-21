package xyz.hexagons.client.desktop;

import com.badlogic.gdx.files.FileHandle;
import xyz.hexagons.client.audio.AudioPlayer;
import xyz.hexagons.client.audio.AudioPlayer.IAudioPlayerFactory;

public class DesktopAudioPlayerFactory implements IAudioPlayerFactory {
	@Override
	public AudioPlayer instance(FileHandle file) {
		return new DesktopAudioPlayer(file);
	}
}
