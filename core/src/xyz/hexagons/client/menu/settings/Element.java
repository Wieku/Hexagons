package xyz.hexagons.client.menu.settings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import xyz.hexagons.client.utils.GUIHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author Sebastian Krajewski on 07.04.15.
 */
public abstract class Element<T> extends Table {

	String section;
	String sectionI18n;

	String name;
	String nameI18n;

	int order;

	Label nameLabel;

	public T value;

	Object fieldParent;
	Field valReflection;

	public Element(String section, String sectionI18n, String name, String nameI18n, int order) {

		this.section = section;
		this.sectionI18n = sectionI18n;
		this.name = name;
		this.nameI18n = nameI18n;
		this.order = order;

		for(Field f : Settings.instance.getClass().getFields()) {
			if((f.getModifiers() & Modifier.STATIC)==0) {
				if(f.getName().equals(section)) {
					try {
						fieldParent = f.get(Settings.instance);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						throw new IllegalStateException(e);
					}
					for(Field f1 : fieldParent.getClass().getFields()) {
						if(f1.getName().equals(name)) {
							valReflection = f1;
						}
					}
				}
			}
		}

		left();
		nameLabel = new Label(nameI18n, GUIHelper.getLabelStyle(Color.WHITE, 10));
		nameLabel.pack();
		add(nameLabel).left().padLeft(2).padRight(10);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		setWidth(getParent().getWidth());
		layout();
		super.draw(batch, parentAlpha);
	}

	public abstract void onEvent(InputEvent e);
	public void writeValue(T value) {
		try {
			valReflection.set(fieldParent, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	public T loadValue() {
		try {
			return (T)valReflection.get(fieldParent);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	public String getSection() {
		return sectionI18n;
	}

	public String getName() {
		return nameI18n;
	}

	public Integer getOrder() {
		return order;
	}

	public boolean match(String phrase){
		phrase = phrase.toLowerCase();
		return sectionI18n.toLowerCase().contains(phrase) || nameI18n.toLowerCase().contains(phrase);
	}

}
