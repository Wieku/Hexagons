package me.wieku.hexagons.engine;

import java.io.Serializable;

/**
 * @author Sebastian Krajewski on 07.04.15.
 */
public class Settings implements Serializable{

	public static Settings instance;

	public boolean vSync = false;
	public boolean fullscreen = false;
	public int msaa = 4;

	//@Slider(0, 100, 1, 100, "Sound", "General", "Volume", e -> doSomething())
	public int masterVolume = 100;

	public int effectVolume = 100;
	public int musicVolume = 100;
	public int menuMusicVolume = 100;
}
