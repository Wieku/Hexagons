package xyz.hexagons.client.menu.settings;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import xyz.hexagons.client.audio.SoundManager;
import xyz.hexagons.client.utils.GUIHelper;

/**
 * @author Sebastian Krajewski on 07.04.15.
 */
public class Slider extends Element<Integer> {


	Label valueLabel;
	String select = "";
	int value, min, max, jump;
	com.badlogic.gdx.scenes.scene2d.ui.Slider slider;


	public Slider(String section, String sectionI18n, String name, String nameI18n, int min, int max, int jump, int def, int order){
		super(section, sectionI18n, name, nameI18n, order);

		this.jump = jump;
		this.min = min;
		this.max = max;

		value = loadValue();
		if(value < min || value > max) value = def;

		//System.out.println(value);

		slider = new com.badlogic.gdx.scenes.scene2d.ui.Slider(min, max, jump, false, GUIHelper.getSliderStyle());
		InputListener stopTouchDown = new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				event.stop();
				return false;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				super.touchDragged(event, x, y, pointer);
			}
		};
		slider.addListener(stopTouchDown);

		slider.setValue(value);
		add(slider).fillX().padRight(5);
		valueLabel = new Label(Integer.toString(value), GUIHelper.getLabelStyle(Color.WHITE, 10));
		valueLabel.setAlignment(Align.center);
		add(valueLabel).width(45).padRight(2);

		slider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent arg0, Actor arg1) {
				value = (int) slider.getValue();
				writeValue(value);
				valueLabel.setText(Integer.toString(value));
			}
		});
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
