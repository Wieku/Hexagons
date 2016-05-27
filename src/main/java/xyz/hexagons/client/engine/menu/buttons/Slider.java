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
public class Slider extends Element<Integer> {


	Label valueLabel;
	String select = "";
	int value, min, max, jump;

	public Slider(String section, String sectionI18n, String name, String nameI18n, int min, int max, int jump, int def, int order){
		super(section, sectionI18n, name, nameI18n, order);

		this.jump = jump;
		this.min = min;
		this.max = max;

		value = loadValue();
		if(value < min || value > max) value = def;

		System.out.println(value);

		valueLabel = new Label(Integer.toString(value), GUIHelper.getLabelStyle(Color.WHITE, 10));
		add(valueLabel).padRight(2);
	}

	public void action(){}

	public void onEvent(InputEvent e){
		if(e.getType() == Type.keyTyped){

			if(e.getKeyCode() == Keys.LEFT){
				value = Math.max(min, value - jump);
				valueLabel.setText(Integer.toString(value));
				writeValue(value);
				SoundManager.playSound("beep");
			}

			if(e.getKeyCode() == Keys.RIGHT){
				value = Math.min(max, value + jump);
				valueLabel.setText(Integer.toString(value));
				writeValue(value);
				SoundManager.playSound("beep");
			}
		}
	}

	public void select(boolean state){
		select = (state?">":"");
		nameLabel.setText(select+nameI18n);
	}
}
