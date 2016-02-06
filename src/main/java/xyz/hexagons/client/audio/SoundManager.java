package xyz.hexagons.client.audio;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import xyz.hexagons.client.engine.Settings;

import java.util.HashMap;

public class SoundManager {

	static HashMap<String, Sound> sounds = new HashMap<>();

	public static void registerSound(String name, String path, boolean isInternal){
		sounds.put(name, Gdx.audio.newSound(Gdx.files.getFileHandle(path, isInternal ? FileType.Internal : FileType.Absolute)));
	}

	public static void removeSound(String name){
		sounds.remove(name);
	}

	public static long playSound(String name){
		Sound sound = sounds.get(name);
		long id = sound.play((float) Settings.instance.masterVolume * (float) Settings.instance.effectVolume / 10000f);
		return id;
	}

	public static void stopSound(String name, long id){
		sounds.get(name).stop(id);
	}

	public static void setVolume(String name, long id, float volume){
		sounds.get(name).setVolume(id, volume);
	}

}
