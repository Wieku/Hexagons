package me.wieku.hexagons.config.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import me.wieku.hexagons.config.ConfigElement;
import me.wieku.hexagons.utils.GUIHelper;

public class SwitchElement extends ConfigElement<Boolean, TextButton> {

	private String falseName;
	private String trueName;
	
	public SwitchElement(String nodeName, String name, String falseName, String trueName, Boolean defValue) {
		super(nodeName, name, defValue);
		this.falseName = falseName;
		this.trueName = trueName;
		TextButton b = new TextButton(falseName, GUIHelper.getTextButtonStyle(Color.BLACK, Color.WHITE, 16));
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
