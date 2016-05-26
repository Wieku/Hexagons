package xyz.hexagons.client.engine.menu.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import xyz.hexagons.client.audio.SoundManager;
import xyz.hexagons.client.utils.GUIHelper;

/**
 * @author Sebastian Krajewski on 07.04.15.
 */
public class State extends Element<Boolean> {

	Label valueLabel;
	String name;
	boolean value;
	String select = "";

	public State(String section, String sectionI18n, String name, String nameI18n, boolean def){
		super(section, sectionI18n, name, nameI18n);

		value = loadValue();

		valueLabel = new Label((value?"ON":"OFF"), GUIHelper.getLabelStyle(Color.WHITE, 10));

		add(valueLabel).right().padRight(2);
	}

	public void action(){}

	public void onEvent(InputEvent e){
		if(e.getType() == Type.keyDown){
			if(e.getKeyCode() == Keys.ENTER){
				value = !value;
				valueLabel.setText(value?"ON":"OFF");
				SoundManager.playSound("beep");
				writeValue(value);
			}
		}
	}

	public void select(boolean state){
		select =  (state?">":"");
		nameLabel.setText(select+name);
	}
}
