package xyz.hexagons.client.engine;

import java.io.Serializable;

/**
 * @author Sebastian Krajewski on 07.04.15.
 */

public class Settings implements Serializable{

	public static transient Settings instance;

	public Graphics graphics = new Graphics();
	public Audio audio = new Audio();

	@Section(name="graphics", enName="Graphics", order=0)
	public class Graphics {
		@Section.Switch(name="vSync", enName="VSync", def = false, order=0)
		public boolean vSync = false;

		@Section.Switch(name="fullscreen", enName="Full Screen", def = false, order=1)
		public boolean fullscreen = false;

		@Section.Slider(name="msaa", enName="MSAA", model={0, 4, 1, 4}, order=2)
		public int msaa = 4;
	}


	//@Slider(0, 100, 1, 100, "Sound", "General", "Volume", e -> doSomething())
	@Section(name="audio", enName="Audio", order=1)
	public class Audio {
		@Section.Slider(name="masterVolume", enName="Master Volume", model={0, 100, 5, 100}, order=0)
		public int masterVolume = 100;

		@Section.Slider(name="effectVolume", enName="Effect Volume", model={0, 100, 5, 100}, order=1)
		public int effectVolume = 100;

		@Section.Slider(name="musicVolume", enName="Game Music Volume", model={0, 100, 5, 100}, order=2)
		public int musicVolume = 100;

		@Section.Slider(name="menuMusicVolume", enName="Menu Music Volume", model={0, 100, 5, 100}, order=3)
		public int menuMusicVolume = 100;
	}

}
