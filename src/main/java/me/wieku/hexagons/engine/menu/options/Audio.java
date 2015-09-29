package me.wieku.hexagons.engine.menu.options;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import me.wieku.hexagons.audio.MenuPlaylist;
import me.wieku.hexagons.engine.Settings;
import me.wieku.hexagons.engine.menu.buttons.Action;
import me.wieku.hexagons.engine.menu.buttons.Slider;
import me.wieku.hexagons.utils.GUIHelper;

/**
 * @author Sebastian Krajewski on 07.04.15.
 */
public class Audio extends Section{

	public static Audio instance = new Audio();

	public Audio(){
		super();
		top().left();

		Slider main = new Slider("Master", 0, 100, 5, 100) {
			@Override
			public void writeValue(Integer value) {
				Settings.instance.masterVolume = value;
				//if(Menu.getInstance().audioPlayer != null)
					/*Menu.getInstance().audioPlayer*/
					MenuPlaylist.setVolume(((float) Settings.instance.masterVolume * (float) Settings.instance.menuMusicVolume / 10000f));
			}

			@Override
			public Integer loadValue() {
				return Settings.instance.masterVolume;
			}
		};

		main.select(true);

		Slider effect = new Slider("Effect", 0, 100, 5, 100) {
			@Override
			public void writeValue(Integer value) {
				Settings.instance.effectVolume = value;
			}

			@Override
			public Integer loadValue() {
				return Settings.instance.effectVolume;
			}
		};

		Slider music = new Slider("Music", 0, 100, 5, 100) {
			@Override
			public void writeValue(Integer value) {
				Settings.instance.musicVolume = value;
			}

			@Override
			public Integer loadValue() {
				return Settings.instance.musicVolume;
			}
		};

		Slider menuMusic = new Slider("Menu Music", 0, 100, 5, 100) {
			@Override
			public void writeValue(Integer value) {
				Settings.instance.menuMusicVolume = value;
				//if(Menu.getInstance().audioPlayer != null)
					/*Menu.getInstance().audioPlayer*/MenuPlaylist.setVolume(((float) Settings.instance.masterVolume * (float) Settings.instance.menuMusicVolume / 10000f));
			}

			@Override
			public Integer loadValue() {
				return Settings.instance.menuMusicVolume;
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
