package me.wieku.hexagons.config.elements;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import me.wieku.hexagons.config.ConfigElement;
import me.wieku.hexagons.utils.GUIHelper;

public class ComboBoxElement extends ConfigElement<String, SelectBox<String>> {

	ArrayList<String> er = new ArrayList<String>();

	public ComboBoxElement(String nodeName, String name, String defValue, String... otherValues) {
		super(nodeName, name, defValue);
		SelectBox<String> sl;
		setActor(sl = new SelectBox<String>(GUIHelper.getSelectBoxStyle(Color.BLACK, Color.GRAY, Color.GRAY, 16)));
		
		Array<String> ar = new Array<String>();
		ar.add(defValue);
		for(String str : otherValues){
			ar.add(str);
			er.add(str);
		}
		
		sl.setItems(ar);
		sl.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent arg0, Actor arg1) {
				if(!sl.getSelected().equals(getValue())){
					updateValue();
				}
			}
		});
	}

	@Override
	public void updateActorValue(String value, SelectBox<String> actor) {
		
		if(er.contains(value)){
			actor.setSelected(value);
		} else {
			actor.setSelected(getDefaultValue());
			updateValue();
		}
		
	}

	@Override
	public String getActorValue(SelectBox<String> actor) {
		return actor.getSelected();
	}
	
}
