package xyz.hexagons.client.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.common.io.CharStreams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.Version;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.audio.MenuPlaylist;
import xyz.hexagons.client.audio.SoundManager;
import xyz.hexagons.client.engine.camera.SkewCamera;
import xyz.hexagons.client.menu.screens.MapSelect;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.engine.render.MapRenderer;
import xyz.hexagons.client.engine.render.ObjRender;
//import xyz.hexagons.client.engine.render.Renderer;
import xyz.hexagons.client.menu.widgets.HProgressBar;
import xyz.hexagons.client.map.Map;
import xyz.hexagons.client.rankserv.RankApi;
import xyz.hexagons.client.utils.GUIHelper;
import xyz.hexagons.client.utils.Utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Sebastian Krajewski on 28.03.15.
 */
public class Game implements Screen {


	private Map map;

	private ObjRender renderer;
	private MapRenderer mapRenderer = new MapRenderer();
	private SkewCamera camera = new SkewCamera();
	private Stage stage;

	private Player player = new Player(this);

	private Label points;
	private Label time;
	private Label message;
	private ProgressBar next;

	private int retries = 0;

	private int width, height;

	private float score = 0;
	private boolean scoreSent = false;

	private int inc = 1;

	private DecimalFormat timeFormat = new DecimalFormat("0.000");

	public Game (Map map){
		this.map = map;

		renderer = new ObjRender();

		stage = new Stage(new ScreenViewport());
		stage.getViewport().update(width, height, true);

		time = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 10));
		time.layout();
		time.setX(2);
		stage.addActor(time);

		message = new Label("", GUIHelper.getLabelStyle(new Color(0.9f, 0.9f, 0.9f, 1), 35));
		message.layout();
		stage.addActor(message);

		points = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 30));
		points.layout();
		points.getStyle().font.setFixedWidthGlyphs("01234567890");
		stage.addActor(points);
		
		next = new HProgressBar(0f, 1f, 0.0001f, false);
		next.setSize(200, 14);
		next.layout();

		stage.addActor(next);

		MenuPlaylist.setLooping(true);
		start(map.info.startTimes[0]);
	}

	@Override
	public void show() {
		Instance.setForegroundFps.accept(0);
		Gdx.graphics.setTitle("Hexagons! " + Version.version + " — " + map.info.songAuthor + " - " + map.info.songName);
	}

	@Override
	public void render(float delta) {
		updateGame(delta);

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		mapRenderer.renderObjects(renderer, delta, camera, player, CurrentMap.gameProperties.wallTimeline.getObjects());

		message.setPosition((stage.getWidth() - message.getWidth()) / 2, (stage.getHeight() - message.getHeight()) * 2.5f / 3);
		stage.getCamera().position.set(camera.rumbleX + stage.getWidth() / 2, camera.rumbleZ + stage.getHeight() / 2, 0);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {
		if(renderer != null) renderer.dispose();
	}

	public void start(float startTime) {

		delta0 = delta1 = delta5 = delta4 = delta3 = 0;
		scoreSent = false;

		CurrentMap.gameProperties.currentTime = 0f;
		CurrentMap.reset();

		score = 0;
		player.reset();
		camera.reset();
		MenuPlaylist.setVolume((float) Settings.instance.audio.masterVolume * (float) Settings.instance.audio.musicVolume / 10000f);

		MenuPlaylist.play();
		MenuPlaylist.setPosition(startTime);

		map.script.onInit();
		map.script.initColors();
		map.script.initEvents();

		if(Settings.instance.gameplay.hideUi) {
			points.setVisible(false);
			time.setVisible(false);
			next.setVisible(false);
			Instance.game.fps.setVisible(false);
		}

		SoundManager.playSound("start");
		RankApi.instance.setupRankedMap(map);
	}

	private void restart(){
		++retries;
		start(map.info.startTimes[MathUtils.random(0, map.info.startTimes.length - 1)]);
	}

	void onDie() {
		if(Settings.instance.gameplay.hideUi) {
			points.setVisible(true);
			time.setVisible(true);
			next.setVisible(true);
			Instance.game.fps.setVisible(true);
		}
	}

	private float fastRotate = 0f;
	private float delta0;
	private boolean escClick = false;
	private Color tmpColor = new Color();
	private boolean touch = true;

	private void updateGame(float delta) {

		if(CurrentMap.gameProperties.gameCompleted) player.dead = true;
		
		if(!player.dead && CurrentMap.gameProperties.wallTimeline.isFirstRemoved())
			score += delta * (CurrentMap.gameProperties.difficulty * CurrentMap.gameProperties.speed * (((int)CurrentMap.gameProperties.currentTime) * 5 + 300));


		updateTimeline(delta);

		if(player.dead){

			if (!MenuPlaylist.isPaused()) {
				SoundManager.playSound("death");
				SoundManager.playSound("gameover");

				camera.rumble(20f, 1f);

				MenuPlaylist.pause();
				touch = true;
			}

			if(!Gdx.input.isTouched()) touch = false;

			if(Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.UP) || (!touch && Gdx.input.isTouched())){
				restart();
			}

			if((Gdx.input.isKeyPressed(Keys.ESCAPE) || Gdx.input.isKeyPressed(Keys.BACK)) && !escClick){
				Instance.game.setScreen(MapSelect.getInstance());
			}

			if(!scoreSent && score > 0 && !Settings.instance.gameplay.invincibility) {
				scoreSent = true;
				RankApi.instance.sendScore(map, score);
			}

		} else {
			if(Gdx.input.isKeyPressed(Keys.ESCAPE) || Gdx.input.isKeyPressed(Keys.BACK)) {
				player.dead = true;
				escClick = true;
				onDie();
			}
		}

		if (!(Gdx.input.isKeyPressed(Keys.ESCAPE) || Gdx.input.isKeyPressed(Keys.BACK))){
			escClick = false;
		}
		updateRotation(delta);
		updatePulse(delta);
		camera.update(delta);
		player.update(delta);
		this.delta0+=delta;
		while (this.delta0 >= (1f / 60)) {

			updateText(1f / 60);
			updateSkew(1f / 60);

			if (!player.dead) {
				CurrentMap.gameProperties.walls.update(1f/60);
				tmpColor.set(CurrentMap.gameProperties.walls.r, CurrentMap.gameProperties.walls.g, CurrentMap.gameProperties.walls.b, CurrentMap.gameProperties.walls.a);

				time.getStyle().fontColor = tmpColor;
				message.getStyle().fontColor = tmpColor;
				points.getStyle().fontColor = tmpColor;
			}

			delta0 -= 0.016666668f;
		}

		if(!player.dead)
			map.script.update(delta);
	}

	private float delta1;
	private void updateText(float delta) {
		if(CurrentMap.currentText != null){

			if(!CurrentMap.currentText.visible){
				message.setText(CurrentMap.currentText.text.toUpperCase());
				CurrentMap.currentText.visible = true;
			}

			if((delta1 += delta) >= CurrentMap.currentText.duration){
				CurrentMap.currentText = null;
				message.setText("");
				delta1 = 0;
			}
		}


		time.setText("Time: " + timeFormat.format(CurrentMap.gameProperties.currentTime) + (Settings.instance.gameplay.invincibility ? "\nInvincibility mode" : "") + (retries > 1 ? "\nRetried " + retries + " times" : "") + (player.dead ? "\nYou died! Press \"Space\" to restart!" : ""));
		points.setText(String.format(Locale.US, "%08d", (int) score));
		points.pack();
		points.setPosition(stage.getWidth() - points.getWidth() - 5, stage.getHeight() - points.getHeight() + 5);
		next.setPosition(stage.getWidth() - next.getWidth() - 5 , stage.getHeight() - next.getHeight() - points.getHeight() + 5);

		time.pack();
		message.pack();

		time.setY(stage.getHeight() - time.getHeight());
	}

	private float delta2;
	private void updateSkew(float delta) {
		inc = (delta2 == 0 ? 1 : (delta2 == CurrentMap.gameProperties.skewTime ? -1 : inc));
		delta2 += delta * inc;
		delta2 = MathUtils.clamp(delta2, 0, CurrentMap.gameProperties.skewTime);

		float percent = delta2 / CurrentMap.gameProperties.skewTime;
		CurrentMap.gameProperties.skew = CurrentMap.gameProperties.minSkew + (CurrentMap.gameProperties.maxSkew - CurrentMap.gameProperties.minSkew) * percent;
	}

	private float delta3;
	private void updateTimeline(float delta) {

		if(!player.dead){
			CurrentMap.gameProperties.wallTimeline.update(delta);
			CurrentMap.gameProperties.eventTimeline.update(delta);
			CurrentMap.gameProperties.currentTime += delta;

			if((delta3 +=delta) >= CurrentMap.gameProperties.levelIncrement){

				fastRotate = CurrentMap.gameProperties.rapidSpinSpeed;

				SoundManager.playSound("levelup");

				CurrentMap.gameProperties.rapidSpin = true;
				CurrentMap.gameProperties.mustChangeSides = true;

				CurrentMap.gameProperties.rotationSpeed += Utils.signum(CurrentMap.gameProperties.rotationSpeed) * CurrentMap.gameProperties.rotationIncrement;//(CurrentMap.gameProperties.rotationSpeed > 0 ? CurrentMap.gameProperties.rotationIncrement: -CurrentMap.gameProperties.rotationIncrement );
				CurrentMap.gameProperties.rotationSpeed = MathUtils.clamp(-CurrentMap.gameProperties.rotationSpeed, -CurrentMap.gameProperties.rotationSpeedMax, CurrentMap.gameProperties.rotationSpeedMax);

				CurrentMap.gameProperties.speed += CurrentMap.gameProperties.speedInc;
				CurrentMap.gameProperties.delayMult += CurrentMap.gameProperties.delayMultInc;
				delta3 = 0;
			}

		}

		next.setValue(delta3 / CurrentMap.gameProperties.levelIncrement);

		if (CurrentMap.gameProperties.wallTimeline.isEmpty() && CurrentMap.gameProperties.mustChangeSides) {
			CurrentMap.gameProperties.sides = MathUtils.random(CurrentMap.gameProperties.minSides, CurrentMap.gameProperties.maxSides);
			SoundManager.playSound("beep");
			CurrentMap.gameProperties.mustChangeSides = false;
		}

		if (CurrentMap.gameProperties.wallTimeline.isAllSpawned() && !CurrentMap.gameProperties.mustChangeSides) {
			map.script.nextPattern();
		}

	}

	private void updateRotation(float delta) {
		if(player.dead){
			if(CurrentMap.gameProperties.rotationSpeed < 0)
				CurrentMap.gameProperties.rotationSpeed = Math.min(-0.02f, CurrentMap.gameProperties.rotationSpeed + 0.002f * 60 * delta);
			else if(CurrentMap.gameProperties.rotationSpeed > 0)
				CurrentMap.gameProperties.rotationSpeed = Math.max(0.02f, CurrentMap.gameProperties.rotationSpeed - 0.002f * 60 * delta);
		}

		camera.rotate(CurrentMap.gameProperties.rotationSpeed * (CurrentMap.gameProperties.useRadians ? MathUtils.radiansToDegrees * 10 : 360) * delta + Utils.signum(CurrentMap.gameProperties.rotationSpeed) * (Utils.getSmootherStep(0, CurrentMap.gameProperties.rapidSpinSpeed, fastRotate) / 3.5f) * 17.f * 60 * delta);
		fastRotate = Math.max(0, fastRotate - 60f * delta);
		if(fastRotate == 0) CurrentMap.gameProperties.rapidSpin = false;
	}

	private float delta4;
	private float delta5;
	private float delta6;
	private void updatePulse(float delta){
		if(player.dead) return;

		if((delta4 -= delta) <= 0){
			CurrentMap.gameProperties.beatPulse = CurrentMap.gameProperties.beatPulseMax;
			delta4 = CurrentMap.gameProperties.beatPulseDelay;
		}

		CurrentMap.gameProperties.beatPulse = Math.max(CurrentMap.gameProperties.beatPulse - 1.2f * delta, CurrentMap.gameProperties.beatPulseMin);

		if(delta5 <= 0 && delta6 <= 0){

			if((CurrentMap.gameProperties.pulseDir < 0 && CurrentMap.gameProperties.pulse <= CurrentMap.gameProperties.pulseMin) || (CurrentMap.gameProperties.pulseDir > 0 && CurrentMap.gameProperties.pulse >= CurrentMap.gameProperties.pulseMax)){
				CurrentMap.gameProperties.pulse = CurrentMap.gameProperties.pulseDir > 0 ? CurrentMap.gameProperties.pulseMax : CurrentMap.gameProperties.pulseMin;
				CurrentMap.gameProperties.pulseDir *= -1;
				delta6 = CurrentMap.gameProperties.pulseDelayHalfMax;
				if(CurrentMap.gameProperties.pulseDir < 0) delta5 = CurrentMap.gameProperties.pulseDelayMax;
			}

			CurrentMap.gameProperties.pulse += (CurrentMap.gameProperties.pulseDir > 0 ? CurrentMap.gameProperties.pulseSpeed : -CurrentMap.gameProperties.pulseSpeedR) * 60f * delta;

		}

		delta5 -= delta * 60;
		delta6 -= delta * 60;

	}

}
