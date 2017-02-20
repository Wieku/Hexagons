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

	@Section(name="ranking", enName="Account", order=0)
	public class Ranking {

		@Section.Account(name="login", enName="Login", order=0)
		public transient String acc;

		public String server = "https://rankserv.hexagons.xyz";
		public String authToken = null;
	}

	@Section(name="graphics", enName="Graphics", order=1)
	public class Graphics {
		@Section.Switch(name="vSync", enName="VSync", def = false, order=0)
		public boolean vSync = false;

		@Section.Switch(name="fullscreen", enName="Full Screen", def = false, order=1)
		public boolean fullscreen = false;

		@Section.Switch(name="fixedratio", enName="Fixed aspect ratio (4:3)", def = false, order=2)
		public boolean fixedratio = false;

		@Section.Combo(name="msaa", enName="MSAA", model={"OFF", "2x", "4x", "8x", "16x"}, def = "4x", order=3)
		public String msaa = "4x";
	}

	@Section(name="gameplay", enName="Gameplay", order=2)
	public class GamePlay {
		@Section.Switch(name="invincibility", enName="Invincibility", def = false, order=0)
		public boolean invincibility = false;

		@Section.Switch(name="hideUi", enName="Hide UI when in game", def = false, order=1)
		public boolean hideUi = false;
	}

	//@Slider(0, 100, 1, 100, "Sound", "General", "Volume", e -> doSomething())
	@Section(name="audio", enName="Audio", order=3)
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
