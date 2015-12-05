package me.wieku.hexagons.engine.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import me.wieku.hexagons.Main;
import me.wieku.hexagons.animation.animations.Animation;
import me.wieku.hexagons.animation.timeline.Timeline;
import me.wieku.hexagons.engine.ActorAccessor;
import me.wieku.hexagons.map.Map;
import me.wieku.hexagons.utils.GUIHelper;

public class MenuMap extends Button {

	public Map map;
	private Label name;
	private Label author;
	private Label description;
	private Label song;

	private Image borderTop;
	private Image borderBottom;
	private Image borderLeft;
	private Image borderRight;

	private boolean laststatus;
	private float origHeight;
	public MenuMap(Map map){
		super(GUIHelper.getTextButtonStyle(new Color(0, 0, 0, 0.5f),Color.BLACK, 1));
		this.map = map;




		borderTop = new Image(GUIHelper.getTxHRegion(Color.WHITE, 2), Scaling.stretchX);
		borderBottom = new Image(GUIHelper.getTxHRegion(Color.WHITE, 2), Scaling.stretchX);
		borderLeft = new Image(GUIHelper.getTxWRegion(Color.WHITE, 2), Scaling.stretchY);
		borderRight = new Image(GUIHelper.getTxWRegion(Color.WHITE, 2), Scaling.stretchY);

		borderTop.setVisible(false);
		borderBottom.setVisible(false);
		borderLeft.setVisible(false);
		borderRight.setVisible(false);

		add(borderTop).fill().colspan(4).row();


		Table info = new Table();
		info.add(name = new Label(map.info.name.substring(0, Math.min(24, map.info.name.length())), GUIHelper.getLabelStyle(Color.WHITE, 12))).top().left().expandX().fillX().row();
		info.add(author = new Label("Author: "+map.info.author, GUIHelper.getLabelStyle(Color.WHITE, 10))).top().left().expandX().fillX().row();
		info.add(description = new Label(map.info.description, GUIHelper.getLabelStyle(Color.WHITE, 10))).top().left().expandX().fillX().row();
		//info.add(song = new Label(map.info.songName + " by " + map.info.songAuthor, GUIHelper.getLabelStyle(Color.WHITE, 10))).top().left().expandX().fillX().row();


		add(borderLeft).fillY();
		add(info).expand().padLeft(5).padTop(5).padBottom(5).left();

		add(borderRight).fillY().row();
		add(borderBottom).colspan(4).fill();

		pack();
		origHeight = getHeight();
	}

	Timeline animation;

	public void update(){

		if(animation != null && !animation.isFinished()){
			animation.kill();
		}

		float g = 30*Math.abs(MapSelect.mapIndex-MapSelect.instance.mapButtons.indexOf(this));

		animation = new Timeline().beginParallel().push(ActorAccessor.createSineTween(this, ActorAccessor.SLIDEX, 0.5f, g, 0))
				.push(ActorAccessor.createSineTween(this, ActorAccessor.SIZEY, 0.2f, origHeight, 0)).end();

		animation.start(Main.getInstance().getAnimationManager());

	}

	@Override
	public void setX(float x){
		super.setX(x);
		layout();
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if(laststatus != isOver()){

			if(animation != null && !animation.isFinished()){
				animation.kill();
			}

			float g = 30*Math.abs(MapSelect.mapIndex-MapSelect.instance.mapButtons.indexOf(this));

			animation = new Timeline().beginParallel().push(ActorAccessor.createElasticEndTween(this, ActorAccessor.SLIDEX, 0.5f, Math.max(0, g+(laststatus?10:-10)), 0))
			.push(ActorAccessor.createElasticEndTween(this, ActorAccessor.SIZEY, 0.5f, (laststatus?origHeight:origHeight+20), 0)).end();

			animation.start(Main.getInstance().getAnimationManager());

			laststatus = isOver();
		}

	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		setWidth(MapSelect.instance.scrollPane.getWidth());
		layout();
		super.draw(batch, parentAlpha);
	}

	public void check(boolean visible) {
		borderTop.setVisible(visible);
		borderBottom.setVisible(visible);
		borderLeft.setVisible(visible);
		borderRight.setVisible(visible);
	}

}
