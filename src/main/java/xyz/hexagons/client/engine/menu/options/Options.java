/*
package xyz.hexagons.client.engine.menu.options;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import xyz.hexagons.client.audio.SoundManager;
import xyz.hexagons.client.engine.menu.MainMenu;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.utils.GUIHelper;

*/
/**
 * @author Sebastian Krajewski on 07.04.15.
 *//*

public class Options implements Screen {

	Stage stage;
	Label name;
	Label message;
	Image image;
	ShapeRenderer rend;
	static Table currentSection;

	static Options instance;

	public Options(){
		stage = new Stage(new ScreenViewport());
		name = new Label("SETTINGS", GUIHelper.getLabelStyle(Color.WHITE, 30));
		name.pack();
		instance = this;

		stage.addListener(new InputListener() {

			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.ESCAPE && currentSection.equals(OptionMenu.instance)) {
					SoundManager.playSound("beep");
					//Main.getInstance().setScreen(MainMenu.instance);
					MainMenu.instance.optionsShowed = false;
				}
				return super.keyDown(event, keycode);
			}

		});

		message = new Label("", GUIHelper.getLabelStyle(new Color(0.9f, 0.9f, 0.9f, 1), 14));
		message.layout();
		stage.addActor(message);

		stage.addActor(name);

		image = new Image(GUIHelper.getTxHRegion(new Color(0x02EAFAFF),2));
		image.setScaling(Scaling.stretchX);
		image.setHeight(2);

		stage.addActor(image);

		rend = new ShapeRenderer();

		setMenu(currentSection != null ? currentSection : OptionMenu.instance);

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		//name.setBounds((Gdx.graphics.getWidth()-name.getWidth())/2, (float) Gdx.graphics.getHeight() * 2.25f / 3, Gdx.graphics.getWidth() - 5, (float) Gdx.graphics.getHeight() * 0.75f / 3);
		name.setHeight((float) Gdx.graphics.getHeight() * 0.75f / 3);
		name.setPosition((Math.min(Gdx.graphics.getWidth(), 512) - name.getWidth()) / 2, (float) Gdx.graphics.getHeight() * 2.25f / 3);

		image.setBounds(0, ((float) Gdx.graphics.getHeight() * 2.25f / 3) - image.getHeight(), Math.min(Gdx.graphics.getWidth(), 512), 2);
	}

	float delta1;
	@Override
	public void render(float delta) {

		if(CurrentMap.currentText != null){

			if(!CurrentMap.currentText.visible){
				message.setText(CurrentMap.currentText.text);
				message.pack();
				CurrentMap.currentText.visible = true;
			}

			if((delta1 += delta) >= CurrentMap.currentText.duration){
				CurrentMap.currentText = null;
				message.setText("");
				delta1 = 0;
			}
		}

		currentSection.setBounds(5, 0, Math.min(Gdx.graphics.getWidth(), 512), (float) Gdx.graphics.getHeight() * 2.25f / 3 - 5);
		currentSection.layout();

		message.setPosition(Gdx.graphics.getWidth() - 5 - message.getWidth(), 5);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		rend.begin(ShapeType.Filled);
		rend.setColor(0, 0, 0, 0.7f);
		rend.rect(0, 0, Math.min(Gdx.graphics.getWidth(), 512), Gdx.graphics.getHeight());
		rend.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		name.setHeight((float) Gdx.graphics.getHeight() * 0.75f / 3);
		name.setPosition((Gdx.graphics.getWidth() - name.getWidth()) / 2, (float) Gdx.graphics.getHeight() * 2.25f / 3);
		image.setBounds(0, ((float) Gdx.graphics.getHeight() * 2.25f / 3)-image.getHeight(), width, 2);
	}

	public void setMenu(Table section) {

		if (currentSection != null)
			currentSection.setVisible(false);

		currentSection = section;
		if (!stage.getActors().contains(section, true)) stage.addActor(section);
		currentSection.setVisible(true);
		stage.setKeyboardFocus(currentSection);
	}

	public static Options getInstance(){
		return instance;
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {}
}
*/