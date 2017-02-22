package xyz.hexagons.client.menu.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import me.wieku.animation.animations.Animation;
import me.wieku.animation.timeline.Timeline;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.menu.ActorAccessor;
import xyz.hexagons.client.utils.GUIHelper;
import xyz.hexagons.client.menu.settings.elements.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class SettingsTab extends Table{

	private static SettingsTab instance = new SettingsTab();
	private ScrollPane scr;
	private Table chld;
	private Timeline showingTween;
	private Timeline hidingTween;
	public String name = "";
	private boolean showed = false;
	private boolean hidden = true;
	private boolean showd = false;
	TextField field;
	TextButton button;
	Timeline error;

	public static SettingsTab getInstance(){
		return instance;
	}

	private SettingsTab(){
		super();
		setBackground(GUIHelper.getTxRegion(new Color(0,0,0,0.5f)));
		top().left();
		field = new TextField("", GUIHelper.getTextFieldStyle(Color.BLACK, Color.WHITE));
		field.setColor(1, 1, 1, 0);
		field.setMessageText("Search...");
		field.setVisible(false);

		field.setTextFieldFilter(new TextFieldFilter(){

			@Override
			public boolean acceptChar(TextField arg0, char arg1) {

				String phrase = field.getText() + Character.toString(arg1);

				if(ConfigEngine.matches(phrase) > 0){
					return true;
				}

				if(error != null && !error.isFinished()) error.kill();

				error = new Timeline().beginSequence().push(new Animation(field,ActorAccessor.TEXTCOLOR,0.2f*field.getColor().r).target(0.5f,0.5f,0.5f,1)).pushPause(0.2f)
						.push(new Animation(field,ActorAccessor.TEXTCOLOR,0.2f).target(1f,1f,1f,1)).end();
				error.start(Instance.getAnimationManager());

				return false;

			}

		});

		field.addListener(new InputListener(){
			@Override
			public boolean keyTyped(InputEvent event, char character) {

				if(!field.isDisabled())
					if(ConfigEngine.matches(field.getText()) != 0){
						build(field.getText());
					}

				return true;
			}
		});

		add(field).center().fillX().height(30).padTop(30).padBottom(5).expandX().row();

		chld = new Table();
		chld.top().left();

		scr = new ScrollPane(chld, GUIHelper.getScrollPaneStyle(Color.BLACK, Color.WHITE));
		scr.setScrollingDisabled(true, false);

		add(scr).top().fillY().fillX().row();

		add(button = new TextButton("Back", GUIHelper.getTextButtonStyle(Color.WHITE, 20))).right().padRight(5).bottom().height(40).padTop(10).expandY().expandX();
		button.addListener(new ClickListener(Buttons.LEFT){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				hide();
			}
		});
		button.setColor(1,1,1,0);
		button.setVisible(false);

		build("");

	}

	public boolean isShowed(){
		return showed;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		setHeight(getStage().getHeight());
		setPosition(0, 0);
		layout();
		if(showd){
			setWidth(512);
			layout();
		}

		super.draw(batch, parentAlpha);
	}

	public void show(){
		button.setVisible(true);
		button.setDisabled(false);
		field.setVisible(true);
		field.setDisabled(false);
		getStage().setScrollFocus(scr);
		if(hidingTween != null && !hidingTween.isFinished()){
			hidingTween.kill();
		}

		showed = true;
		hidden = false;
		showingTween = new Timeline().beginParallel().push(ActorAccessor.createSineTween(this, ActorAccessor.SIZEX, 1.0f-(getWidth()/512f), 512, 0))
				.push(ActorAccessor.createFadeTween(this, 1.0f, 0, 1.0f))
				.push(ActorAccessor.createFadeTween(scr, 1.0f, 0, 1.0f))
				.push(ActorAccessor.createFadeTween(field, 1.0f, 0, 1.0f))
				.push(ActorAccessor.createFadeTween(button, 1.0f, 0.4f, 1.0f))
				.end().setCallback((b)->{showd=true;});
		showingTween.start(Instance.getAnimationManager());
	}

	private boolean isHidden() {
		return hidden ;
	}

	public void hide(){
		if(hidden || button.isDisabled()) return;
		getStage().setScrollFocus(null);
		field.setDisabled(true);
		button.setDisabled(true);
		if(showingTween != null && !showingTween.isFinished()){
			showingTween.kill();
		}
		showd=false;
		showed = false;
		hidingTween = new Timeline().beginParallel().push(ActorAccessor.createQuadTween(this, ActorAccessor.SIZEX, (getWidth()/512f) * 1.0f, 0, 0f))
				.push(ActorAccessor.createFadeTween(this, 1.0f, 0f, 0f))
				.push(ActorAccessor.createFadeTween(scr, 1.0f, 0f, 0f))
				.push(ActorAccessor.createFadeTween(field, 1.0f, 0f, 0f))
				.push(ActorAccessor.createFadeTween(button, 0.4f, 0, 0f))
				.end()
				.setCallback((s)->hidden = true);

		hidingTween.start(Instance.getAnimationManager());
	}

	public void build(String phrase){
		chld.clear();

		Table mainTable = new Table();

		for(Entry<String, HashMap<String, ArrayList<Element<?>>>> sec : ConfigEngine.searchMap(phrase).entrySet()){

			Table tab1 = new Table(){
				public void draw(Batch arg0, float arg1) {
					setWidth(scr.getWidth());
					setX(0);
					layout();
					super.draw(arg0, arg1);
				}
			};

			Image image = new Image(GUIHelper.getTxHRegion(Color.WHITE,1));
			image.setScaling(Scaling.stretchX);

			tab1.add(image).fillX().row();
			tab1.add(new Label(sec.getKey().toUpperCase(), GUIHelper.getLabelStyle(Color.WHITE, 24))).right().padRight(5).expandX().row();

			Image image2 = new Image(GUIHelper.getTxHRegion(Color.WHITE,1));
			image2.setScaling(Scaling.stretchX);

			tab1.add(image2).fillX();

			mainTable.add(tab1).fillX().row();

			for(Entry<String, ArrayList<Element<?>>> subsec : sec.getValue().entrySet()){

				Table table = new Table();
				table.left();
				table.add(new Image(GUIHelper.getTxWRegion(Color.WHITE, 3), Scaling.stretchY)).fillY();

				Table subTable = new Table();

				subTable.left();
				subTable.add(new Label(subsec.getKey().toUpperCase(), GUIHelper.getLabelStyle(Color.WHITE, 14))).padBottom(10).padLeft(5).left().expandX().row();

				for(Element<?> el : subsec.getValue()){

					Table tab = new Table(){
						public void draw(Batch arg0, float arg1) {
							setWidth(scr.getWidth()-getX());
							layout();
							super.draw(arg0, arg1);
						}
					};
					tab.left();

					tab.add(el).fillX();

					subTable.add(tab).padLeft(5).fillX().row();
				}

				table.add(subTable).fillX().row();

				mainTable.add(table).pad(5).fillX().row();
			}
		}

		chld.add(mainTable);
	}


}
