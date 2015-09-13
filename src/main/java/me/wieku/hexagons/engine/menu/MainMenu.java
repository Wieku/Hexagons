package me.wieku.hexagons.engine.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import me.wieku.hexagons.Main;
import me.wieku.hexagons.engine.ActorAccessor;
import me.wieku.hexagons.engine.Settings;
import me.wieku.hexagons.engine.menu.buttons.MenuButton;
import me.wieku.hexagons.map.Map;
import me.wieku.hexagons.utils.GUIHelper;

import java.util.ArrayList;

public class MainMenu implements Screen {

	Stage stage;
	Table mainTable;
	MenuButton button, button2, button3;
	ArrayList<MenuButton> list = new ArrayList<>();
	Label version, copyright;
	int currentIndex = -1;
	Image icon;
	Sound beep;

	public MainMenu(){
		stage = new Stage(new ScreenViewport());
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		stage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.UP) {
					int index = (currentIndex == 0 ? list.size() - 1 : currentIndex - 1);
					selectIndex(index);
				}
				if (keycode == Keys.DOWN) {
					int index = (currentIndex == list.size() - 1 ? 0 : currentIndex + 1);
					selectIndex(index);
				}
				if(keycode == Keys.ENTER){
					if(currentIndex == 0){
						Main.getInstance().setScreen(new Menu(Main.getInstance().maps));
					}

					if(currentIndex == 2){
						Gdx.app.exit();
					}
				}
				return false;
			}
		});

		mainTable = new Table();
		mainTable.setBackground(GUIHelper.getTxRegion(new Color(0x0f0f0fff)));
		mainTable.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage.addActor(mainTable);

		version = new Label("Version: "+Main.version, GUIHelper.getLabelStyle(new Color(0xa0a0a0ff), 10));
		version.pack();
		version.setPosition(5, stage.getHeight() - version.getHeight() - 5);
		stage.addActor(version);

		copyright = new Label("Hexagons! 2015 Created by: Magik6k and Wieku",GUIHelper.getLabelStyle(new Color(0xa0a0a0ff), 10));
		copyright.pack();
		copyright.setPosition(stage.getWidth() - copyright.getWidth() - 5, 5);
		stage.addActor(copyright);

		icon = new Image(new Texture(Gdx.files.internal("assets/hexlogobig.png"), true));
		icon.setScaling(Scaling.fit);
		icon.setOrigin((3 * stage.getWidth()) / 5, (3 * stage.getHeight()) / 5);
		icon.setSize((3 * stage.getWidth()) / 5, (4 * stage.getHeight()) / 5);
		stage.addActor(icon);


		list.add(button = new MenuButton("Start"));
		list.add(button2 = new MenuButton("Options"));
		list.add(button3 = new MenuButton("Exit"));

		button.setBounds(/*(715f/1024)**/stage.getWidth() - 309, 252, 512, 100);
		button2.setBounds(/*(645f/1024)**/stage.getWidth()-379,142,512,100);
		button3.setBounds(/*(575f/1024)**/stage.getWidth()-449,32,512,100);
		stage.addActor(button);
		stage.addActor(button2);
		stage.addActor(button3);

		beep = Gdx.audio.newSound(Gdx.files.internal("assets/sound/menuclick.ogg"));

		selectIndex(0);
	}

		@Override
	public void show() {
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
			Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		mainTable.setBounds(0, 0, width, height);
		version.setPosition(5, stage.getHeight() - version.getHeight() - 5);
		copyright.setPosition(stage.getWidth() - copyright.getWidth() - 5, 5);
		icon.setSize((508f / 1024) * stage.getWidth(), (508f / 768) * stage.getHeight());
		icon.setPosition((401f / 1024) * stage.getWidth() - icon.getWidth() / 2, ((768f - 301f) / 768) * stage.getHeight() - icon.getHeight() / 2);
		button.setBounds(/*(715f/1024)**/stage.getWidth() - 309 - (list.indexOf(button) == currentIndex ? 20 : 0), 252, 512, 100);
		button2.setBounds(/*(645f/1024)**/stage.getWidth() - 379 - (list.indexOf(button2) == currentIndex ? 20 : 0), 142, 512, 100);
		button3.setBounds(/*(575f/1024)**/stage.getWidth() - 449 - (list.indexOf(button3) == currentIndex?20:0), 32, 512, 100);
	}

	private void selectIndex(int index){
		if(currentIndex != -1){
			list.get(currentIndex).select(false);
			ActorAccessor.startTween(ActorAccessor.createSineTween(list.get(currentIndex), ActorAccessor.SLIDEX, 0.05f, list.get(currentIndex).getX()+20, 0f));
		}
		currentIndex = index;
		playBeep();
		list.get(currentIndex).select(true);
		ActorAccessor.startTween(ActorAccessor.createSineTween(list.get(currentIndex), ActorAccessor.SLIDEX, 0.05f, list.get(currentIndex).getX()-20, 0f));
	}

	void playBeep(){
		long id = beep.play();
		beep.setVolume(id, (float) Settings.instance.masterVolume * (float) Settings.instance.effectVolume / 10000f);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}
}
