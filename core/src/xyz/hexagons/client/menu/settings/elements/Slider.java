package xyz.hexagons.client.menu.settings.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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


	private Label valueLabel;
	private int value, min, max, jump;
	private com.badlogic.gdx.scenes.scene2d.ui.Slider slider;


	public Slider(String section, String sectionI18n, String name, String nameI18n, int min, int max, int jump, int def, int order){
		super(section, sectionI18n, name, nameI18n, order);

		this.jump = jump;
		this.min = min;
		this.max = max;

		value = loadValue();
		if(value < min || value > max) value = def;

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

		valueLabel = new Label(Integer.toString(value), GUIHelper.getLabelStyle(Color.WHITE, 10));
		valueLabel.setAlignment(Align.center);
		add(slider).width(512 - 13 - 10 - 2 - 2 - 5 - nameLabel.getWidth() - 45);
		add(valueLabel).width(45).padLeft(5).padRight(2).right();

		slider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent arg0, Actor arg1) {
				value = (int) slider.getValue();
				SoundManager.playSound("change");
				writeValue(value);
				valueLabel.setText(Integer.toString(value));
			}
		});
	}

	public void onEvent(InputEvent e){

	}

}
