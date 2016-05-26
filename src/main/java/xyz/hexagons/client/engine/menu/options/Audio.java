/*
package xyz.hexagons.client.engine.menu.options;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import xyz.hexagons.client.audio.MenuPlaylist;
import xyz.hexagons.client.engine.Settings;
import xyz.hexagons.client.engine.menu.buttons.Action;
import xyz.hexagons.client.engine.menu.buttons.Slider;
import xyz.hexagons.client.utils.GUIHelper;

*/
/**
 * @author Sebastian Krajewski on 07.04.15.
 *//*

public class Audio extends Section{

	public static Audio instance = new Audio();

	public Audio(){
		super();
		top().left();

		Slider main = new Slider("Master", 0, 100, 5, 100) {
			@Override
			public void writeValue(Integer value) {
				Settings.instance.audio.masterVolume = value;
				//if(Menu.getInstance().audioPlayer != null)
					*/
/*Menu.getInstance().audioPlayer*//*

					MenuPlaylist.setVolume(((float) Settings.instance.audio.masterVolume * (float) Settings.instance.audio.menuMusicVolume / 10000f));
			}

			@Override
			public Integer loadValue() {
				return Settings.instance.audio.masterVolume;
			}
		};

		main.select(true);

		Slider effect = new Slider("Effect", 0, 100, 5, 100) {
			@Override
			public void writeValue(Integer value) {
				Settings.instance.audio.effectVolume = value;
			}

			@Override
			public Integer loadValue() {
				return Settings.instance.audio.effectVolume;
			}
		};

		Slider music = new Slider("Music", 0, 100, 5, 100) {
			@Override
			public void writeValue(Integer value) {
				Settings.instance.audio.musicVolume = value;
			}

			@Override
			public Integer loadValue() {
				return Settings.instance.audio.musicVolume;
			}
		};

		Slider menuMusic = new Slider("Menu Music", 0, 100, 5, 100) {
			@Override
			public void writeValue(Integer value) {
				Settings.instance.audio.menuMusicVolume = value;
				//if(Menu.getInstance().audioPlayer != null)
					*/
/*Menu.getInstance().audioPlayer*//*
MenuPlaylist.setVolume(((float) Settings.instance.audio.masterVolume * (float) Settings.instance.audio.menuMusicVolume / 10000f));
			}

			@Override
			public Integer loadValue() {
				return Settings.instance.audio.menuMusicVolume;
			}
		};

		Action exit = new Action("Back") {
			@Override
			public void action() {
				Options.getInstance().setMenu(OptionMenu.instance);
			}
		};

		addButton(main);
		addButton(effect);
		addButton(music);
		addButton(menuMusic);
		addButton(exit);

		add(new Label("Audio", GUIHelper.getLabelStyle(Color.WHITE, 22))).colspan(2).padLeft(20).padBottom(20).left().fillX().row();
		add(main).fillX().row();
		add(effect).fillX().row();
		add(music).fillX().row();
		add(menuMusic).fillX().row();
		add(exit).fillX().colspan(2).padTop(22).row();

	}

}
*/
