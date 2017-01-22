package xyz.hexagons.client.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.utils.PathUtil;
import xyz.hexagons.client.utils.Utils;

import java.util.HashMap;

public class SoundManager {

	static HashMap<String, Sound> sounds = new HashMap<>();

	public static void registerSound(String name, String path, boolean isInternal) {
		Sound sound = Utils.tryOr(() -> Gdx.audio.newSound(PathUtil.getFileHandle(path)), null);
		if(sound != null) {
			sounds.put(name, sound);//, isInternal ? FileType.Internal : FileType.Absolute)));
		}
	}

	public static void removeSound(String name) {
		if(sounds.containsKey(name)) {
			sounds.remove(name);
		}
	}

	public static long playSound(String name) {
		if(sounds.containsKey(name)) {
			Sound sound = sounds.get(name);
			return sound.play((float) Settings.instance.audio.masterVolume * (float) Settings.instance.audio.effectVolume / 10000f);
		}
		return -1;
	}

	public static void stopSound(String name, long id) {
		if(sounds.containsKey(name)) {
			sounds.get(name).stop(id);
		}
	}

	public static void setVolume(String name, long id, float volume) {
		if(sounds.containsKey(name)) {
			sounds.get(name).setVolume(id, volume);
		}
	}

}
