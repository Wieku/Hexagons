package xyz.hexagons.client.menu.settings.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import xyz.hexagons.client.audio.SoundManager;
import xyz.hexagons.client.utils.GUIHelper;

/**
 * @author Sebastian Krajewski on 07.04.15.
 */
public class State extends Element<Boolean> {

	String name;

	String select = "";
	CheckBox box;


	public State(String section, String sectionI18n, String name, String nameI18n, boolean def, int order){
		super(section, sectionI18n, name, nameI18n, order);

		value = loadValue();

		clear();

		box = new CheckBox("", GUIHelper.getCheckBoxStyle(Color.WHITE, Color.WHITE, 10));

		box.setChecked(value);
		add(box).left().padRight(5);
		add(nameLabel).left().width(512 - 5 - box.getWidth());
		nameLabel.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				box.setChecked(!value);
			}
		});

		box.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				value = box.isChecked();
				SoundManager.playSound("change");
				writeValue(value);
			}
		});
	}

	public void onEvent(InputEvent e){

	}

}
