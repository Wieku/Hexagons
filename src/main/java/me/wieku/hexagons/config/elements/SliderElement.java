package me.wieku.hexagons.config.elements;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import me.wieku.hexagons.config.ConfigElement;
import me.wieku.hexagons.utils.GUIHelper;

public class SliderElement extends ConfigElement<Float,Slider> {

	private float min;
	private float max;
	
	public SliderElement(String nodeName, String name, Float min, Float max, Float def) {
		super(nodeName, name, def);
		this.min = min;
		this.max = max;
		Slider sl;
		setActor(sl = new Slider(min, max, 1, false, GUIHelper.getSliderStyle()));
		
		sl.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				event.stop();
				return false;
			}
		});
		sl.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent arg0, Actor arg1) {
				if(!sl.isDragging())
					updateValue();
			}
		});
	}

	@Override
	public void updateActorValue(Float value, Slider actor) {
		
		float val = value.floatValue();
		
		if(val > max){
			val = max;
		} else if (val < min){
			val = min;
		}
		
		actor.setValue(val);
		
		updateValue();
	}

	@Override
	public Float getActorValue(Slider actor) {
		return actor.getValue();
	}
	
}
