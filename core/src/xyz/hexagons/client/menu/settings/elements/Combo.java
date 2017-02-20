package xyz.hexagons.client.menu.settings.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import xyz.hexagons.client.utils.GUIHelper;

import java.util.Arrays;

/**
 * @author Sebastian Krajewski on 07.04.15.
 */
public class Combo extends Element<String> {

	String[] model;
	String def;
	SelectBox<String> combo;

	public Combo(String section, String sectionI18n, String name, String nameI18n, String[] model, String def, int order){
		super(section, sectionI18n, name, nameI18n, order);

		this.model = model;
		this.def = def;

		value = loadValue();
		if(!Arrays.asList(model).contains(value)) {
			value = def;
			writeValue(value);
		}

		combo = new SelectBox<>(GUIHelper.getSelectBoxStyle(new Color(0,0,0, 0.8f), Color.WHITE, new Color(0.2f,0.2f,0.2f, 0.8f), 12));
		combo.setItems(model);
		combo.setSelected(value);

		add(combo).width(512 - 13 - 10 - 2 - nameLabel.getWidth());

		combo.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent arg0, Actor arg1) {
				value = combo.getSelected();
				writeValue(value);
			}
		});

	}

	public void onEvent(InputEvent e){
	}

}
