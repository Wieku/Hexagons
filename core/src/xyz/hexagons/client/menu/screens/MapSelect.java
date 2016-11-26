package xyz.hexagons.client.menu.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.Version;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.audio.MenuPlaylist;
import xyz.hexagons.client.audio.SoundManager;
import xyz.hexagons.client.engine.Game;
import xyz.hexagons.client.menu.widgets.CScrollPane;
import xyz.hexagons.client.menu.widgets.MenuMap;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.engine.camera.SkewCamera;
import xyz.hexagons.client.engine.render.MapRenderer;
import xyz.hexagons.client.engine.render.ObjRender;
import xyz.hexagons.client.map.Map;
import xyz.hexagons.client.rankserv.RankApi;
import xyz.hexagons.client.utils.GUIHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author Sebastian Krajewski on 04.04.15.
 */
public class MapSelect implements Screen {

	ArrayList<Map> maps;
	Stage stage;

	Table info;
	Label number, name, description, author, music;
	Game game;

	Table table;
	Label nickname;

	SkewCamera camera = new SkewCamera();
	ObjRender shapeRenderer;

	MapRenderer mapRenderer = new MapRenderer();

	public ArrayList<MenuMap> mapButtons = new ArrayList<>();
	public CScrollPane scrollPane;

	public static int mapIndex = 0;

	public static MapSelect instance;

	private Table scoreTable = new Table();
	public ScrollPane leaderboard;

	private Table myScoreTable = new Table();
	private Label myScoreLabel;
	private Table myScore;

	int tnumber=1;

	public MapSelect(ArrayList<Map> maps){
		this.maps = maps;
		maps.sort((e1, e2)->e1.info.name.compareTo(e2.info.name));
		instance = this;
		shapeRenderer = new ObjRender();

		stage = new Stage(new ExtendViewport(1024, 768));

		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		stage.addListener(new InputListener(){

			@Override
			public boolean keyDown(InputEvent event, int keycode) {

				if(!maps.isEmpty()){

					if(keycode == Keys.UP){

						mapButtons.get(mapIndex).check(false);

						if(--mapIndex < 0) mapIndex = maps.size()-1;
						selectIndex(mapIndex);
						mapButtons.get(mapIndex).check(true);

						MenuMap ms = mapButtons.get(mapIndex);
						scrollPane.scrollTo(0, ms.getY()+(scrollPane.getHeight()/2-ms.getHeight()/2)*(mapIndex==maps.size()-1?-1:1), ms.getWidth(), ms.getHeight());

						mapButtons.forEach(MenuMap::update);
					}

					if(keycode == Keys.DOWN){

						mapButtons.get(mapIndex).check(false);
						if(++mapIndex > maps.size()-1) mapIndex = 0;
						selectIndex(mapIndex);
						mapButtons.get(mapIndex).check(true);

						MenuMap ms = mapButtons.get(mapIndex);
						scrollPane.scrollTo(0, ms.getY()+(scrollPane.getHeight()/2-ms.getHeight()/2)*(mapIndex==0?1:-1), ms.getWidth(), ms.getHeight());

						mapButtons.forEach(MenuMap::update);
					}

					//System.out.println(mapButtons.get(mapIndex).getY());

					if(keycode == Keys.DOWN || keycode == Keys.UP) {
						try {
							Method method = scrollPane.getClass().getDeclaredMethod("resetFade");
							method.setAccessible(true);
							method.invoke(scrollPane);
						} catch (Exception e) {}
					}

					if(keycode == Keys.ENTER){
						SoundManager.playSound("beep");
						Gdx.input.setInputProcessor(null);
						//audioPlayer.pause();
						MenuPlaylist.pause();

						Instance.game.setScreen(game = new Game(maps.get(mapIndex)));
					}

				}



				if(keycode == Keys.ESCAPE){
					SoundManager.playSound("beep");
					Instance.game.setScreen(MainMenu.instance);
				}

				return false;
			}
		});

		stage.addListener(new ActorGestureListener(){
			@Override
			public void tap(InputEvent event, float x, float y, int count, int button) {
				Vector2 r = scrollPane.screenToLocalCoordinates(new Vector2(x, Gdx.graphics.getHeight()-y));
				for(MenuMap m : mapButtons) {
					if(m.isIn((int)r.x, (int)r.y)) {

						System.out.println(m.map.info.name);
						if(mapButtons.indexOf(m) != mapIndex) {
							if(mapButtons.indexOf(m) > mapIndex){

								mapButtons.get(mapIndex).check(false);

								mapIndex = mapButtons.indexOf(m);
								selectIndex(mapIndex);
								mapButtons.get(mapIndex).check(true);

								MenuMap ms = mapButtons.get(mapIndex);
								scrollPane.scrollTo(0, ms.getY()+(scrollPane.getHeight()/2-ms.getHeight()/2)*(mapIndex==maps.size()-1?-1:1), ms.getWidth(), ms.getHeight());

								mapButtons.forEach(MenuMap::update);
							}

							if(mapButtons.indexOf(m) < mapIndex){

								mapButtons.get(mapIndex).check(false);
								mapIndex = mapButtons.indexOf(m);
								selectIndex(mapIndex);
								mapButtons.get(mapIndex).check(true);

								MenuMap ms = mapButtons.get(mapIndex);
								scrollPane.scrollTo(0, ms.getY()+(scrollPane.getHeight()/2-ms.getHeight()/2)*(mapIndex==0?1:-1), ms.getWidth(), ms.getHeight());

								mapButtons.forEach(MenuMap::update);
							}
						} else {
							SoundManager.playSound("beep");
							Gdx.input.setInputProcessor(null);
							//audioPlayer.pause();
							MenuPlaylist.pause();
							Instance.game.setScreen(game = new Game(maps.get(mapIndex)));
						}

						if(count == 2) {
							SoundManager.playSound("beep");
							Gdx.input.setInputProcessor(null);
							//audioPlayer.pause();
							MenuPlaylist.pause();
							Instance.game.setScreen(game = new Game(maps.get(mapIndex)));
						}

						return;
					}

				}
			}
		});
		info = new Table();
		info.add(number = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 8))).left().row();
		info.add(name = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 8))).left().row();
		info.add(description = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 8))).left().row();
		info.add(author = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 8))).left().row();
		info.add(music = new Label("No maps available!", GUIHelper.getLabelStyle(Color.WHITE, 8))).left().row();
		number.getStyle().font.setFixedWidthGlyphs("01234567890");
		info.pack();

		info.setWidth(Gdx.graphics.getWidth()/3);
		info.setPosition(5, stage.getHeight()-5-info.getHeight());

		stage.addActor(info);
		
		table = new Table();

		for(Map map : maps){
			MenuMap mp = new MenuMap(map);
			mapButtons.add(mp);
			table.add(mp).left().center().fillX().pad(1).row();
		}

		table.pack();
		table.setHeight(Gdx.graphics.getHeight()-200);




		scrollPane = new CScrollPane(table, GUIHelper.getScrollPaneStyle(Color.WHITE));
		scrollPane.setupFadeScrollBars(1f, 1f);
		scrollPane.setSmoothScrolling(true);
		scrollPane.setVelocityY(0.1f);
		scrollPane.setScrollingDisabled(true, false);

		((Table) scrollPane.getChildren().get(0)).center().left();
		scrollPane.setCancelTouchFocus(true);

		stage.addActor(scrollPane);

		leaderboard = new ScrollPane(scoreTable, GUIHelper.getScrollPaneStyle(Color.WHITE));
		leaderboard.setupFadeScrollBars(1f, 1f);
		leaderboard.setSmoothScrolling(true);
		leaderboard.setVelocityY(0.1f);
		leaderboard.setScrollBarPositions(true, false);
		leaderboard.setScrollingDisabled(true, false);

		((Table) leaderboard.getChildren().get(0)).top().left();
		leaderboard.setCancelTouchFocus(true);

		stage.addActor(leaderboard);

		myScoreTable.add(myScoreLabel = GUIHelper.text("Personal score:", Color.WHITE, 12)).left().pad(5).width(300).row();
		myScore = GUIHelper.getTable(new Color(0,0,0,0.5f));
		myScoreTable.add(myScore).width(300).left();
		addMyScore(0, 0, 0);
		myScoreTable.pack();
		stage.addActor(myScoreTable);

		nickname = GUIHelper.text("Nickname: " + Settings.instance.ranking.nickname, Color.WHITE, 12);
		nickname.pack();
		nickname.setPosition(5, 70);
		stage.addActor(nickname);
		//if(Settings.instance.graphics.fullscreen)
		//	Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode());
	}

	float delta0 = 0;

	Color tmpC = new Color();

	boolean showed = false;
	@Override
	public void render(float delta) {

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		updateSkew(delta);
		camera.rotate(CurrentMap.data.rotationSpeed * 360f * delta);
		camera.update(delta);

		if((delta0 += delta)>=1f/60){
			CurrentMap.data.walls.update(delta0);

			MenuPlaylist.update(delta0);

			CurrentMap.setMinSkew(0.9999f);
			CurrentMap.setMaxSkew(1);
			CurrentMap.setSkewTime(1);

			tmpC.set(CurrentMap.data.walls.r, CurrentMap.data.walls.g, CurrentMap.data.walls.b, CurrentMap.data.walls.a);

			number.getStyle().fontColor = tmpC;
			name.getStyle().fontColor = tmpC;
			description.getStyle().fontColor = tmpC;
			author.getStyle().fontColor = tmpC;
			music.getStyle().fontColor = tmpC;
			nickname.getStyle().fontColor = tmpC;
			myScoreLabel.getStyle().fontColor = tmpC;

			delta0 = 0;
		}

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		mapRenderer.renderBackground(shapeRenderer, delta, true, 0);
		shapeRenderer.end();

		scrollPane.setBounds(stage.getWidth()-Math.max(468, stage.getWidth()/3), 100, Math.max(468, stage.getWidth()/3), stage.getHeight()-200);
		scrollPane.layout();

		myScoreTable.setBounds(0, 100, 300, myScoreTable.getHeight());
		leaderboard.setBounds(0, 100+myScoreTable.getHeight(), 300, stage.getHeight()-info.getHeight()-120-myScoreTable.getHeight());
		leaderboard.layout();

		if(mapButtons.size() > 0)
			if(!showed) {
				MenuMap ms = mapButtons.get(mapIndex);

				if(ms.getY()+ms.getHeight()/2<scrollPane.getHeight()/2) {
					scrollPane.scrollTo(0, ms.getY()+(scrollPane.getHeight()/2-ms.getHeight()/2)*(mapIndex==0?1:-1), ms.getWidth(), ms.getHeight());
				} else if((table.getHeight()-ms.getY()-ms.getHeight()/2) < scrollPane.getHeight()/2) {
					scrollPane.scrollTo(0, ms.getY()+(scrollPane.getHeight()/2-ms.getHeight()/2)*(mapIndex==maps.size()-1?-1:1), ms.getWidth(), ms.getHeight());
				}
				showed = true;
			}

		stage.act(delta);
		stage.draw();
	}

	int inc;
	float delta2;
	public void updateSkew(float delta) {
		inc = (delta2 == 0 ? 1 : (delta2 == CurrentMap.data.skewTime ? -1 : inc));
		delta2 += delta * inc;
		delta2 = Math.min(CurrentMap.data.skewTime, Math.max(delta2, 0));
		float percent = delta2 / CurrentMap.data.skewTime;
		CurrentMap.data.skew = CurrentMap.data.minSkew + (CurrentMap.data.maxSkew - CurrentMap.data.minSkew) * percent;
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		info.setWidth(Gdx.graphics.getWidth()/3);
		scrollPane.setBounds(stage.getWidth()-Math.max(468, stage.getWidth()/3)-15, 0, Math.max(468, stage.getWidth()/3)+15, stage.getHeight());
		scrollPane.layout();

		myScoreTable.setBounds(0, 100, 300, myScoreTable.getHeight());
		leaderboard.setBounds(0, 100+myScoreTable.getHeight(), 300, stage.getHeight()-info.getHeight()-120-myScoreTable.getHeight());
		leaderboard.layout();

		mapButtons.forEach(e->{e.setX(0);e.update();});

		info.pack();
		info.setPosition(5, stage.getHeight()-5-info.getHeight());
	}

	@Override
	public void show() {

		Instance.setForegroundFps.accept(0);
		Gdx.graphics.setTitle("Hexagons! " + Version.version);
		selectIndex(mapIndex= Instance.maps.indexOf(MenuPlaylist.getCurrent()));

		if(game != null){
			MenuPlaylist.play();
			//MenuPlaylist.setPosition(game.exitPosition);
			game = null;
		}
		MenuPlaylist.setLooping(true);
		if(!mapButtons.isEmpty()){
			for(int i=0; i<mapButtons.size();i++)mapButtons.get(i).check(i==mapIndex);
		}
		mapButtons.forEach(MenuMap::update);

		scrollPane.setBounds(Gdx.graphics.getWidth()-Math.max(468, Gdx.graphics.getWidth()/3), 100, Math.max(468, Gdx.graphics.getWidth()/3), Gdx.graphics.getHeight()-200);
		scrollPane.layout();

		table.setHeight(Gdx.graphics.getHeight()-200);
		table.layout();
		showed = false;
		Gdx.input.setInputProcessor(stage);

		//System.out.println("Map selection screen showed up");
	}


	public void selectIndex(int index){
		if(maps.isEmpty()){
			number.setText("");
			name.setText("");
			description.setText("");
			author.setText("");
			music.setText("No maps available!");
			info.pack();
			info.setPosition(5, stage.getHeight()-5-info.getHeight());
		} else {

			Map map = maps.get(index);
			CurrentMap.reset();
			maps.get(mapIndex).script.initColors();
			maps.get(mapIndex).script.onInit();
			camera.reset();

			if(MenuPlaylist.getCurrent() == null || !MenuPlaylist.getCurrent().equals(map)){
				MenuPlaylist.replaceCurrent(map);
				MenuPlaylist.skipToPreview();
				MenuPlaylist.setVolume(((float) Settings.instance.audio.masterVolume * (float) Settings.instance.audio.menuMusicVolume) / 10000f);
			}

			SoundManager.playSound("click");

			number.setText("[" + (index + 1) + "/" + maps.size() + "] Pack: " + map.info.pack);
			name.setText(map.info.name);
			description.setText(map.info.description);
			author.setText("Author: " + map.info.author);
			music.setText("Music: " + map.info.songName + " by " + map.info.songAuthor);
			info.pack();
			info.setPosition(5, stage.getHeight()-5-info.getHeight());

			scoreTable.clear();

			Table tb = GUIHelper.getTable(new Color(0,0,0,0.5f));
			tb.center().setFillParent(true);
			tb.add(GUIHelper.text("Retrieving scores...", Color.WHITE, 14));
			scoreTable.add(tb).height(leaderboard.getHeight());

			Instance.executor.execute(() -> {
				RankApi.LeaderBoard lb = RankApi.instance.getScoreForMap(map, 50);
				Instance.scheduleOnMain.accept(() -> {
					scoreTable.clear();
					tnumber = 1;
					if(lb == null) {
						Table tb1 = GUIHelper.getTable(new Color(0,0,0,0.5f));
						tb1.center().setFillParent(true);
						tb1.add(GUIHelper.text("No scores available", Color.WHITE, 14));
						scoreTable.add(tb1).height(leaderboard.getHeight());
					} else {
						lb.list.forEach(e -> addScore(tnumber++, e.nick, e.score));
						addMyScore(lb.position, lb.ownBest, lb.mapPlayers);
					}
				});

			});
		}
	}


	public void addScore(int position, String name, int score) {
			float ty = (Settings.instance.ranking.nickname.equals(name)?0.2f:0.0f);
			Table table = GUIHelper.getTable(new Color(ty,ty,ty,0.5f));
			table.left();

			Table subTable = new Table();

			subTable.left();
			subTable.add(new Label(name, GUIHelper.getLabelStyle(Color.WHITE, 13))).padBottom(2).padLeft(5).left().expandX().row();
			subTable.add(new Label("Score: " + score, GUIHelper.getLabelStyle(Color.WHITE, 9))).padBottom(2).padLeft(5).left().expandX().row();

			subTable.layout();
			subTable.pack();

			Table minTable = new Table();
			minTable.center();
			minTable.add(GUIHelper.text(Integer.toString(position), Color.WHITE, 11)).fillX();

			table.add(minTable).width(subTable.getHeight()).fillY();
			table.add(new Image(GUIHelper.getTxWRegion(Color.WHITE, 2), Scaling.stretchY)).padTop(1).padBottom(1).fillY();

			table.add(subTable).width(298-subTable.getHeight()).row();

			scoreTable.add(table).padBottom(2).padTop(2).width(300).row();
			//subNode.expandAll();
			//node.add(subNode);
			leaderboard.setScrollPercentY(0);
	}

	public void addMyScore(int position, int score, int players) {
		myScore.clear();
		if(score > 0) {

			Table table = new Table();
			table.left();

			Table subTable = new Table();

			subTable.left();
			subTable.add(new Label("#" + position + " of " + players, GUIHelper.getLabelStyle(Color.WHITE, 11))).padBottom(2).padLeft(5).left().expandX().row();
			subTable.add(new Label("Score: " + score, GUIHelper.getLabelStyle(Color.WHITE, 10))).padBottom(2).padLeft(5).left().expandX().row();

			subTable.layout();
			subTable.pack();

			Table minTable = new Table();
			minTable.center();
			minTable.add(GUIHelper.text(Integer.toString(position), Color.WHITE, 11)).fillX();

			table.add(minTable).width(subTable.getHeight()).fillY();
			table.add(new Image(GUIHelper.getTxWRegion(Color.WHITE, 2), Scaling.stretchY)).padTop(1).padBottom(1).fillY();

			table.add(subTable).width(298-subTable.getHeight()).row();

			myScore.add(table).width(300);
		} else {
			myScore.add(GUIHelper.text("Never played", Color.WHITE, 14)).center().pad(9);
		}
		myScoreTable.pack();
	}


	public static MapSelect getInstance() {
		return instance;
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {}

}
