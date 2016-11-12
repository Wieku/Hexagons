package xyz.hexagons.client.menu.settings;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
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
	CheckBox box;


	public State(String section, String sectionI18n, String name, String nameI18n, boolean def, int order){
		super(section, sectionI18n, name, nameI18n, order);

		value = loadValue();

		valueLabel = new Label((value?"ON":"OFF"), GUIHelper.getLabelStyle(Color.WHITE, 10));
		valueLabel.setAlignment(Align.center);

		clear();
		//TextButton new TextButton()
		box = new CheckBox("", GUIHelper.getCheckBoxStyle(Color.WHITE, Color.WHITE, 10));

		//add()
		box.setChecked(value);
		add(box).left().padRight(5);
		add(nameLabel).left();
		nameLabel.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("lelelel");
			}
		});
	}

	public void action(){
		//System.out.println(lele);
	}

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
