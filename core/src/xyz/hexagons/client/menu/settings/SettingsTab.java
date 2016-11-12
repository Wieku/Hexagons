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
import com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import me.wieku.animation.animations.Animation;
import me.wieku.animation.timeline.Timeline;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.Main;
import xyz.hexagons.client.menu.ActorAccessor;
import xyz.hexagons.client.utils.GUIHelper;

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

		add(field).center().fillX().padTop(30).padBottom(5).expandX().row();

		chld = new Table();
		chld.top().left();
		//Conf.init();

		scr = new ScrollPane(chld, GUIHelper.getScrollPaneStyle(Color.BLACK, Color.WHITE));

		add(scr).top().expandY().fillX().uniform().row();

		add(button = new TextButton("Back", GUIHelper.getTextButtonStyle(Color.WHITE, 20))).left().bottom().height(40).uniform().padTop(10).expandX();
		button.addListener(new ClickListener(Buttons.LEFT){@Override
		public void clicked(InputEvent event, float x, float y) {
			hide();
		}});

		button.setVisible(false);

		build("");

	}

	public boolean isShowed(){
		return showed;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		setHeight(Gdx.graphics.getHeight());
		setPosition(0, 0);

		if(showd){
			setWidth(Gdx.graphics.getWidth()*0.45f);
		}

		super.draw(batch, parentAlpha);
	}

	public void show(){
		button.setVisible(true);
		field.setVisible(true);
		field.setDisabled(false);
		if(hidingTween != null && !hidingTween.isFinished()){
			hidingTween.kill();
		}

		showed = true;
		hidden = false;
		showingTween = new Timeline().beginParallel().push(ActorAccessor.createSineTween(this, ActorAccessor.SIZEX, 1.0f, Gdx.graphics.getWidth()*0.45f, 0)).push(ActorAccessor.createFadeTween(this, 1.0f, 0, 1.0f))
				.push(ActorAccessor.createFadeTween(scr, 1.0f, 0, 1.0f))
				.push(ActorAccessor.createFadeTween(field, 1.0f, 0, 1.0f))
				.end().setCallback((b)->{showd=true;});
		showingTween.start(Instance.getAnimationManager());
	}

	private boolean isHidden() {
		return hidden ;
	}

	public void hide(){
		field.setDisabled(true);
		if(showingTween != null && !showingTween.isFinished()){
			showingTween.kill();
		}
		showd=false;
		showed = false;
		hidingTween = new Timeline().beginParallel().push(ActorAccessor.createQuadTween(this, ActorAccessor.SIZEX, 1.0f, 0, 0)).push(ActorAccessor.createFadeTween(this, 1.0f, 0, 0.0f))
				.push(ActorAccessor.createFadeTween(scr, 1.0f, 0, 0.0f))
				.push(ActorAccessor.createFadeTween(field, 1.0f, 0, 0.0f))
				.end()
				.setCallback((s)->{hidden = true;});

		hidingTween.start(Instance.getAnimationManager());
	}

	public void build(String phrase){
		chld.clear();
		TreeStyle style = new TreeStyle();
		style.background = GUIHelper.getTxRegion(new Color(0,0,0,0));
		style.minus = GUIHelper.getTxRegion(new Color(0,0,0,0));
		style.over = GUIHelper.getTxRegion(new Color(0,0,0,0));
		style.plus = GUIHelper.getTxRegion(new Color(0,0,0,0));
		style.selection = GUIHelper.getTxRegion(new Color(0,0,0,0));

		//Tree mainTree = new Tree(style);
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

			//Node node = new Node(tab1);

			mainTable.add(tab1).fillX().row();

			for(Entry<String, ArrayList<Element<?>>> subsec : sec.getValue().entrySet()){

				Table table = new Table();
				table.left();
				table.add(new Image(GUIHelper.getTxWRegion(Color.WHITE, 3), Scaling.stretchY)).fillY();

				Table subTable = new Table();

				subTable.left();
				subTable.add(new Label(subsec.getKey().toUpperCase(), GUIHelper.getLabelStyle(Color.WHITE, 14))).padBottom(10).padLeft(5).left().expandX().row();
				//

				for(Element<?> el : subsec.getValue()){

					Table tab = new Table(){
						public void draw(Batch arg0, float arg1) {
							setWidth(scr.getWidth()-getX());
							//getCells().get(1).width(getWidth()/2.5f);
							layout();
							super.draw(arg0, arg1);
						}
					};
					tab.left();
					//tab.add(new Label(el.getName(), GUIHelper.getLabelStyle(Color.WHITE, 16))).uniform().left();

					tab.add(el).fillX();//right().padRight(10).expandX().uniform();

					/*subNode.add(new Node(tab));*/
					subTable.add(tab).padLeft(5).fillX().row();
				}

				table.add(subTable).fillX().row();

				mainTable.add(table).pad(5).fillX().row();
				//subNode.expandAll();
				//node.add(subNode);

			}
			//node.expandAll();
			//mainTree.add(node);

		}

		//mainTree.expandAll();
		//chld.add(mainTree);
		chld.add(mainTable);
	}


}
