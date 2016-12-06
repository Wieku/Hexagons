package xyz.hexagons.client.menu.settings;

import java.io.Serializable;

/**
 * @author Sebastian Krajewski on 07.04.15.
 */

public class Settings implements Serializable {

	public static transient Settings instance;

	public Graphics graphics = new Graphics();
	public Audio audio = new Audio();
	public GamePlay gameplay = new GamePlay();
	public Ranking ranking = new Ranking();

	@Section(name="graphics", enName="Graphics", order=0)
	public class Graphics {
		@Section.Switch(name="vSync", enName="VSync", def = false, order=0)
		public boolean vSync = false;

		@Section.Switch(name="fullscreen", enName="Full Screen", def = false, order=1)
		public boolean fullscreen = false;

		@Section.Switch(name="fixedratio", enName="Fixed aspect ratio (4:3)", def = false, order=2)
		public boolean fixedratio = false;

		@Section.Slider(name="msaa", enName="MSAA", model={0, 4, 1, 4}, order=3)
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

	@Section(name="gameplay", enName="Gameplay", order=3)
	public class GamePlay {
		@Section.Switch(name="invincibility", enName="Invincibility", def = false, order=0)
		public boolean invincibility = false;
	}

	@Section(name="ranking", enName="Ranking", order=4)
	public class Ranking {
		public String server = "https://rankserv.hexagons.xyz";

		public String nickname = "Anonymous";

		public String authToken = null;
	}
}
