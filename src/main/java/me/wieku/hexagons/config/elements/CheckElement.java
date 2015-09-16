package me.wieku.hexagons.config.elements;/*package pl.tinlink.josu.config.elements;

import pl.tinlink.josu.config.ConfigElement;
import pl.tinlink.josu.utils.GUIHelper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CheckElement extends ConfigElement<Boolean, CheckBox>{

	private String falseName;
	private String trueName;
	
	public CheckElement(String nodeName, String name, Boolean defValue) {
		super(nodeName, name, defValue);
		this.falseName = falseName;
		this.trueName = trueName;
		CheckBox b = new CheckBox("",GUIHelper.getCheckBoxStyle(Color.BLACK, Color.WHITE, 16));
		setActor(b);
		b.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				if(getValue()){
					b.setText(falseName);
				} else {
					b.setText(trueName);
				}
			
				updateValue();
			}
		});
		
	}

	@Override
	public void updateActorValue(Boolean value, TextButton actor) {
		
		if(value){
			actor.setText(trueName);
		} else {
			actor.setText(falseName);
		}
		
		
	}

	@Override
	public Boolean getActorValue(TextButton actor) {
		if(trueName.equals(actor.getText().toString())){
			return true;
		} else if (falseName.equals(actor.getText().toString())){
			return false;
		}
		
		return false;
	}

}
*/