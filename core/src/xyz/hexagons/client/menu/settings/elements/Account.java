package xyz.hexagons.client.menu.settings.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.google.common.eventbus.Subscribe;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.rankserv.EventLogin;
import xyz.hexagons.client.utils.GUIHelper;

public class Account extends Element<String> {

	Label nameLabel2;
	Button buttonGoogle;
	Button buttonSteam;

	public Account(String section, String sectionI18n, String name, String nameI18n, int order){
		super(section, sectionI18n, name, nameI18n, order);


		nameLabel2 = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 10));
		nameLabel2.pack();

		nameLabel.setText("Not logged in, log in using:");
		nameLabel.pack();
		clear();
		add(nameLabel).left().padLeft(2).padBottom(5).width(512 - 5).row();

		buttonGoogle = new TextButton("Google", GUIHelper.getTextButtonStyle(new Color(0,0,0, 0.8f), Color.WHITE, 14));
		buttonGoogle.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Instance.currentAccount = Instance.accountManager.loginGoogle();
				super.clicked(event, x, y);
			}
		});
		buttonSteam = new TextButton("Steam", GUIHelper.getTextButtonStyle(new Color(0,0,0, 0.8f), Color.WHITE, 14));
		buttonSteam.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Instance.currentAccount = Instance.accountManager.loginSteam();
				super.clicked(event, x, y);
			}
		});

		add(buttonGoogle).left().padLeft(2).padBottom(5).width(nameLabel.getWidth()).row();
		add(buttonSteam).left().padLeft(2).width(nameLabel.getWidth()).row();

		Instance.eventBus.register(this);
	}

	public void onEvent(InputEvent e){
	}

	@Subscribe
	public void onLogin(EventLogin event) {
		nameLabel.setText("Logged in as "+event.getAccount().nick()+".");
		nameLabel2.setText("Logged with Google/Steam.");
		clear();
		add(nameLabel).left().padLeft(2).width(512 - 5).row();
		add(nameLabel2).left().padLeft(2).width(512 - 5);
	}

}
