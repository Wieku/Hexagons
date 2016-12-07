package xyz.hexagons.client.menu.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import xyz.hexagons.client.utils.GUIHelper;

import java.text.NumberFormat;
import java.util.Locale;

public class PlayerRank extends Stack {
	
	Table uTable;
	Image image;
	Table lTable;
	Label nickLabel;
	Label rScoreLabel;
	Table rTable;
	Label rankLabel;
	
	String nickname;
	int rankedScore;
	int overallScore;
	int rank;
	
	public PlayerRank() {
		super();
		nickname=null;
		rankedScore=0;
		overallScore=0;
		rank = 0;
		
		uTable = new Table();
		lTable = new Table();
		rTable = GUIHelper.getTable(new Color(0,0,0,0.4f));
		
		Texture tex = new Texture(Gdx.files.internal("assets/hexlogo.png"), true);
		tex.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		image = new Image(tex);
		//image.scaleBy(48f/image.getWidth());
		
		uTable.add(image).pad(8).width(48).height(48);
		
		nickLabel = GUIHelper.text("Wieku", Color.WHITE, 18);
		rScoreLabel = GUIHelper.text("Score: 0", Color.WHITE, 8);
		
		lTable.left();
		lTable.add(nickLabel).padLeft(1).expandX().left().row();
		lTable.add(rScoreLabel).padLeft(1).fillY().expandX().left().row();
		
		uTable.add(lTable).height(64).width(300-64);
		
		rankLabel = GUIHelper.text("#1", new Color(1,1,1,0.2f), 30);
		rankLabel.setAlignment(Align.right);
		rTable.add(rankLabel).width(295).height(64).padRight(5).right();
		rTable.setFillParent(true);
		add(rTable);
		add(uTable);
		setHeight(64);
		setWidth(300);
		reset();
	}
	
	public void reset() {
		
		rankLabel.setText("Unknown");
		image.setVisible(false);
		nickLabel.setText("");
		rScoreLabel.setText("");
	}
	
	public void update(String nickname, int rank, int score) {
		rankLabel.setText("#"+rank);
		image.setVisible(true);
		nickLabel.setText(nickname);
		rScoreLabel.setText("Score: " + String.format(Locale.US, "%,d", score));
	}
	
}
