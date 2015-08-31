package me.wieku.hexagons.engine.menu.options;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import me.wieku.hexagons.Main;
import me.wieku.hexagons.engine.menu.Menu;
import me.wieku.hexagons.engine.menu.buttons.Action;
import me.wieku.hexagons.utils.GUIHelper;

/**
 * @author Sebastian Krajewski on 08.04.15.
 */
public class OptionMenu extends Section{

	public static OptionMenu instance = new OptionMenu();

	public OptionMenu(){
		super();
		top().left();

		Action graphics = new Action("Graphics") {
			@Override
			public void action() {
				Options.getInstance().setMenu(Graphics.instance);
			}
		};

		graphics.select(true);

		Action audio = new Action("Audio") {
			@Override
			public void action() {
				Options.getInstance().setMenu(Audio.instance);
			}
		};

		Action online = new Action("Online") {
			@Override
			public void action() {

			}
		};

		Action exit = new Action("Exit") {
			@Override
			public void action() {
				Main.getInstance().setScreen(Menu.getInstance());
			}
		};

		addButton(graphics);
		addButton(audio);
		addButton(online);
		addButton(exit);

		add(new Label("Menu", GUIHelper.getLabelStyle(Color.WHITE, 22))).colspan(2).padLeft(20).padBottom(20).left().fillX().row();
		add(graphics).fillX().row();
		add(audio).fillX().row();
		add(online).fillX().row();
		add(exit).fillX().padTop(22).colspan(2).row();

	}

}
