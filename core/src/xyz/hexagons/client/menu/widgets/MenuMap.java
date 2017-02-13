package xyz.hexagons.client.menu.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import me.wieku.animation.timeline.Timeline;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.api.HColor;
import xyz.hexagons.client.audio.SoundManager;
import xyz.hexagons.client.menu.ActorAccessor;
import xyz.hexagons.client.menu.screens.MapSelect;
import xyz.hexagons.client.map.Map;
import xyz.hexagons.client.utils.GUIHelper;

import java.util.Arrays;
import java.util.stream.Collectors;

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

	static ShapeRenderer renderer;

	private int clrIncrement = -1;
	private boolean fade = false;

	HColor color = new HColor(0, 0, 0, 0.5f).addPulse(100f/255f, 100f/255f, 100f/255f, 0f);

	public MenuMap(Map map){
		super(GUIHelper.getTextButtonStyle(new Color(0, 0, 0, 0.0f), Color.BLACK, 1));
		this.map = map;

		if(renderer == null) renderer = new ShapeRenderer();


		borderTop = new Image(GUIHelper.getTxHRegion(Color.WHITE, 1), Scaling.stretchX);
		borderBottom = new Image(GUIHelper.getTxHRegion(Color.WHITE, 1), Scaling.stretchX);
		borderLeft = new Image(GUIHelper.getTxWRegion(Color.WHITE, 1), Scaling.stretchY);
		borderRight = new Image(GUIHelper.getTxWRegion(Color.WHITE, 1), Scaling.stretchY);

		add(borderTop).fill().colspan(4).row();

		Table info = new Table();
		info.add(name = new Label(map.info.name.substring(0, Math.min(24, map.info.name.length())), GUIHelper.getLabelStyle(Color.WHITE, 12))).top().left().expandX().fillX().row();
		info.add(author = new Label("Author: "+map.info.author, GUIHelper.getLabelStyle(Color.WHITE, 10))).top().left().expandX().fillX().row();

		StringBuilder descriptionBuilder = new StringBuilder();
		for(String lines : map.info.description.split("\n")) {
			if(descriptionBuilder.length() > 0)
				descriptionBuilder.append("\n");
			descriptionBuilder.append(lines.substring(0, Math.min(39, lines.length())));
		}
		info.add(description = new Label(descriptionBuilder.toString(), GUIHelper.getLabelStyle(Color.WHITE, 10))).top().left().expandX().fillX().row();


		add(borderLeft).fillY();
		add(info).expand().padLeft(5).padTop(5).padBottom(5).left();

		add(borderRight).fillY().row();
		add(borderBottom).colspan(4).fill();

		pack();
		origHeight = getHeight();
	}

	Timeline animation;

	boolean selected=false;
	public void update(){

		if(animation != null && !animation.isFinished()){
			animation.kill();
		}

		float g = 15+15*Math.abs(MapSelect.mapIndex-MapSelect.instance.mapButtons.indexOf(this));

		animation = new Timeline().beginParallel().push(ActorAccessor.createCircleOutTween(this, ActorAccessor.SLIDEX, 1f, g, 0))
				.push(ActorAccessor.createSineTween(this, ActorAccessor.SIZEY, 0.2f, origHeight, 0)).end();

		animation.start(Instance.getAnimationManager());

		if(Math.abs(MapSelect.mapIndex-MapSelect.instance.mapButtons.indexOf(this))==0d) {
			clrIncrement = 1;
			fade = true;
			selected = true;
		} else if(selected) {
			clrIncrement = -1;
			fade = true;
			selected=false;
		}
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
			
			SoundManager.playSound("click");

			if(animation != null && !animation.isFinished()){
				animation.kill();
			}

			float g = 15+15*Math.abs(MapSelect.mapIndex-MapSelect.instance.mapButtons.indexOf(this));

			/*animation = new Timeline().beginParallel().push(ActorAccessor.createElasticEndTween(this, ActorAccessor.SLIDEX, 0.5f, Math.max(0, g+(laststatus?5:-5)), 0))
			.push(ActorAccessor.createElasticEndTween(this, ActorAccessor.SIZEY, 0.5f, (laststatus?origHeight:origHeight+20), 0)).end();*/

			animation = new Timeline().beginParallel().push(ActorAccessor.createCircleOutTween(this, ActorAccessor.SLIDEX, 0.5f, Math.max(0, g+(laststatus?0:-10)), 0))
					.end();

			animation.start(Instance.getAnimationManager());

			laststatus = isOver();
		}

		if(fade)
			if(color.update(delta, clrIncrement, 0.5f)!=clrIncrement) fade = false;

	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		setWidth(MapSelect.instance.scrollPane.getWidth()+30);
		layout();

		batch.end();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		renderer.setTransformMatrix(batch.getTransformMatrix());
		renderer.begin(ShapeType.Filled);
		renderer.setColor(color.r, color.g, color.b, color.a);
		renderer.rect(getX(), getY(), getWidth(),getHeight());
		renderer.end();

		Gdx.gl.glDisable(GL20.GL_BLEND);

		batch.begin();
		super.draw(batch, parentAlpha);
	}

	public void check(boolean visible) {
		//borderTop.setVisible(visible);
		//borderBottom.setVisible(visible);
		//borderLeft.setVisible(visible);
		//borderRight.setVisible(visible);
	}

	public boolean isIn(int x, int y) {
		if(x >= getX() && x <= getX()+getWidth() && y >= getY() && y <= getY()+getHeight()) return true;
		return false;
	}

}
