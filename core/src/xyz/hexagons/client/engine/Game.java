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
import xyz.hexagons.client.audio.AudioPlayer;
import xyz.hexagons.client.audio.SoundManager;
import xyz.hexagons.client.engine.camera.SkewCamera;
import xyz.hexagons.client.menu.screens.MapSelect;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.engine.render.MapRenderer;
import xyz.hexagons.client.engine.render.ObjRender;
//import xyz.hexagons.client.engine.render.Renderer;
import xyz.hexagons.client.menu.widgets.HProgressBar;
import xyz.hexagons.client.map.Map;
import xyz.hexagons.client.resources.ArchiveFileHandle;
import xyz.hexagons.client.utils.FpsCounter;
import xyz.hexagons.client.utils.GUIHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Sebastian Krajewski on 28.03.15.
 */
public class Game implements Screen {
	private Executor executor = Executors.newSingleThreadExecutor();

	Map map;
	AudioPlayer audioPlayer;

	public float exitPosition;

	ObjRender renderer;
	MapRenderer mapRenderer = new MapRenderer();
	SkewCamera camera = new SkewCamera();
	Stage stage;

	Player player = new Player();

	Label fps;
	Label points;
	Label time;
	Label message;
	ProgressBar next;

	//LinkedList<Renderer> renderers = new LinkedList<>();

	int width, height;

	float score = 0;
	boolean scoreSent = false;

	public static float scale = 1f;

	private int inc = 1;

	DecimalFormat timeFormat = new DecimalFormat("0.000");
	DecimalFormat delayFormat = new DecimalFormat("0.00");

	FpsCounter fpsCounter = new FpsCounter(60);

	public Game (Map map){
		this.map = map;

		renderer = new ObjRender();

		stage = new Stage(new ScreenViewport());
		stage.getViewport().update(width, height, true);

		fps = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 10));
		fps.layout();
		fps.setX(2);
		stage.addActor(fps);

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
		
		audioPlayer = new AudioPlayer(new ArchiveFileHandle(map.file,map.info.audioFileName));
		audioPlayer.setLooping(true);


		//addRenderer(player);

		start(map.info.startTimes[0]);
	}

	//public void addRenderer(Renderer renderer){
		//renderers.add(renderer);
		//renderers.sort((o1, o2) -> Integer.compare(o1.getIndex(), o2.getIndex()));
	//}


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

		mapRenderer.renderObjects(renderer, delta, camera, player, CurrentMap.data.wallTimeline.getObjects());

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

	public void start(float startTime){

		delta0 = delta1 = delta5 = delta4 = delta3 = 0;
		scoreSent = false;

		CurrentMap.data.currentTime = 0f;
		CurrentMap.reset();
		score = 0;
		player.reset();
		camera.reset();
		audioPlayer.setVolume((float) Settings.instance.audio.masterVolume * (float) Settings.instance.audio.musicVolume / 10000f);
		audioPlayer.play(startTime);

		map.script.onInit();
		map.script.initColors();
		map.script.initEvents();

		//CurrentMap.data.eventTimeline.update(startTime);
		SoundManager.playSound("start");
	}

	public void restart(){
		start(map.info.startTimes[MathUtils.random(0, map.info.startTimes.length - 1)]);
	}

	float fastRotate = 0f;
	float delta0;
	boolean escClick = false;
	Color tmpColor = new Color();

	public void updateGame(float delta){

		if(!player.dead && CurrentMap.data.wallTimeline.isFirstRemoved())
			score += delta * (CurrentMap.data.difficulty * CurrentMap.data.speed * (((int)CurrentMap.data.currentTime) * 5 + 300));


		updateTimeline(delta);

		if(player.dead){

			if (audioPlayer != null && !audioPlayer.hasEnded()) {
				SoundManager.playSound("death");
				SoundManager.playSound("gameover");
				camera.rumble(20f, 1f);
				exitPosition = audioPlayer.getPosition();
				audioPlayer.stop();
			}

			if(Gdx.input.isKeyPressed(Keys.SPACE)){
				restart();
			}

			if(Gdx.input.isKeyPressed(Keys.ESCAPE) && !escClick){
				audioPlayer.dispose();
				Instance.game.setScreen(MapSelect.getInstance());
			}

			if(!scoreSent && score > 0 && !Settings.instance.gameplay.invincibility) {
				scoreSent = true;
				executor.execute(() -> {
					try {
						System.out.println("Sending score");
						URL url = new URL(Settings.instance.ranking.server);

						HttpClient httpclient = HttpClients.createDefault();
						HttpPost httppost = new HttpPost(Settings.instance.ranking.server + "/game");

						List<NameValuePair> params = new ArrayList<>(3);
						params.add(new BasicNameValuePair("nick", Settings.instance.ranking.nickname));
						params.add(new BasicNameValuePair("score", String.valueOf((long) score)));
						params.add(new BasicNameValuePair("mapid", map.info.uuid));
						httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

						HttpResponse response = httpclient.execute(httppost);
						HttpEntity entity = response.getEntity();

						if (entity != null) {
							InputStream in = entity.getContent();
							System.out.println(CharStreams.toString(new InputStreamReader(in, StandardCharsets.UTF_8)));
							in.close();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				});
			}

		} else {
			if(Gdx.input.isKeyPressed(Keys.ESCAPE)){
				player.dead = true;
				escClick = true;
			}
		}

		if (!Gdx.input.isKeyPressed(Keys.ESCAPE)){
			escClick = false;
		}
		updateRotation(delta);
		updatePulse(delta);
		camera.update(delta);
		player.update(delta);
		this.delta0+=delta;
		while (this.delta0 >= (1f / 60)) {

			fpsCounter.update(delta);
			updateText(1f / 60);
			updateSkew(1f / 60);

			if (!player.dead) {
				CurrentMap.data.walls.update(1f/60);
				tmpColor.set(CurrentMap.data.walls.r, CurrentMap.data.walls.g, CurrentMap.data.walls.b, CurrentMap.data.walls.a);

				fps.getStyle().fontColor = tmpColor;
				time.getStyle().fontColor = tmpColor;
				message.getStyle().fontColor = tmpColor;
				points.getStyle().fontColor = tmpColor;
			}

			delta0 -= 0.016666668f;
		}

		if(!player.dead)
			map.script.update(delta);
	}

	float delta1;
	public void updateText(float delta) {
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

		fps.setText((int)(fpsCounter.getFPS()) + "FPS\n" + delayFormat.format(1000f/fpsCounter.getFPS())+"ms");
		time.setText("Time: " + timeFormat.format(CurrentMap.data.currentTime) + (Settings.instance.gameplay.invincibility?"\nInvincibility mode":"") + (player.dead?"\nYou died! Press \"Space\" to restart!":""));
		points.setText(String.format("%08d", (int) score));
		points.pack();
		points.setPosition(stage.getWidth() - points.getWidth() - 5, stage.getHeight() - points.getHeight() + 5);
		next.setPosition(stage.getWidth() - next.getWidth() - 5 , stage.getHeight() - next.getHeight() - points.getHeight() + 5);
		
		fps.pack();
		time.pack();
		message.pack();

		time.setY(stage.getHeight()-time.getHeight());
	}

	float delta2;
	public void updateSkew(float delta) {
		inc = (delta2 == 0 ? 1 : (delta2 == CurrentMap.data.skewTime ? -1 : inc));
		delta2 += delta * inc;
		delta2 = Math.min(CurrentMap.data.skewTime, Math.max(delta2, 0));
		float percent = delta2 / CurrentMap.data.skewTime;
		CurrentMap.data.skew = CurrentMap.data.minSkew + (CurrentMap.data.maxSkew - CurrentMap.data.minSkew) * percent;
	}

	float delta3;
	public void updateTimeline(float delta) {

		if(!player.dead){
			CurrentMap.data.wallTimeline.update(delta);
			CurrentMap.data.eventTimeline.update(delta);
			CurrentMap.data.currentTime += delta;
		}

		if(!player.dead && (delta3 +=delta) >= CurrentMap.data.levelIncrement){

			fastRotate = CurrentMap.data.fastRotate;

			SoundManager.playSound("levelup");

			CurrentMap.data.isFastRotation = true;
			CurrentMap.data.rotationSpeed += (CurrentMap.data.rotationSpeed > 0 ? CurrentMap.data.rotationIncrement: -CurrentMap.data.rotationIncrement );
			CurrentMap.data.rotationSpeed *= -1;
			CurrentMap.data.rotationSpeed = Math.min(CurrentMap.data.rotationSpeedMax, Math.max(-CurrentMap.data.rotationSpeedMax, CurrentMap.data.rotationSpeed));

			CurrentMap.data.mustChangeSides = true;

			CurrentMap.data.speed += CurrentMap.data.speedInc;
			CurrentMap.data.delayMult += CurrentMap.data.delayMultInc;
			delta3 = 0;
		}

		next.setValue(delta3 / CurrentMap.data.levelIncrement);

		if (CurrentMap.data.wallTimeline.isEmpty() && CurrentMap.data.mustChangeSides) {
			CurrentMap.data.sides = MathUtils.random(CurrentMap.data.minSides, CurrentMap.data.maxSides);
			SoundManager.playSound("beep");
			CurrentMap.data.mustChangeSides = false;
		}

		if (CurrentMap.data.wallTimeline.isAllSpawned() && !CurrentMap.data.mustChangeSides) {
			map.script.nextPattern();
		}

	}

	public void updateRotation(float delta) {

		if(player.dead) {
			if(CurrentMap.data.rotationSpeed < 0) {
				CurrentMap.data.rotationSpeed = Math.min(-0.02f, CurrentMap.data.rotationSpeed + 0.002f * 60 * delta);
			} else if(CurrentMap.data.rotationSpeed > 0) {
				CurrentMap.data.rotationSpeed = Math.max(0.02f, CurrentMap.data.rotationSpeed - 0.002f * 60 * delta);
			}
		}
		camera.rotate(CurrentMap.data.rotationSpeed * 360f * delta + (CurrentMap.data.rotationSpeed > 0 ? 1 : -1) * (getSmootherStep(0, CurrentMap.data.fastRotate, fastRotate) / 3.5f) * 17.f * 60 * delta);
		fastRotate = Math.max(0, fastRotate - 60f * delta);
		if(fastRotate == 0) CurrentMap.data.isFastRotation = false;
	}

	float delta4;
	float delta5;
	float delta6;
	public void updatePulse(float delta){
		if(player.dead) return;

		if(delta4 <= 0){
			CurrentMap.data.beatPulse = CurrentMap.data.beatPulseMax;
			delta4 = CurrentMap.data.beatPulseDelay;
		}

		delta4 -= delta;

		if(CurrentMap.data.beatPulse > CurrentMap.data.beatPulseMin) scale = CurrentMap.data.beatPulse -= 1.2f * delta;

		if(delta5 <= 0 && delta6 <= 0){

			if((CurrentMap.data.pulseDir < 0 && CurrentMap.data.pulse <= CurrentMap.data.pulseMin) || (CurrentMap.data.pulseDir > 0 && CurrentMap.data.pulse >= CurrentMap.data.pulseMax)){
				CurrentMap.data.pulse = CurrentMap.data.pulseDir > 0 ? CurrentMap.data.pulseMax : CurrentMap.data.pulseMin;
				CurrentMap.data.pulseDir *= -1;
				delta6 = CurrentMap.data.pulseDelayHalfMax;
				if(CurrentMap.data.pulseDir < 0) delta5 = CurrentMap.data.pulseDelayMax;
			}

			CurrentMap.data.pulse += (CurrentMap.data.pulseDir > 0 ? CurrentMap.data.pulseSpeed : -CurrentMap.data.pulseSpeedR) * 60f * delta;

		}

		delta5 -= delta * 60;
		delta6 -= delta * 60;

	}

	float getSaturated(float mValue) { return Math.max(0.f, Math.min(1.f, mValue)); }
	float getSmootherStep(float edge0, float edge1, float x)
	{
		x = getSaturated((x - edge0)/(edge1 - edge0));
		return x * x * x * (x * (x * 6 - 15) + 10);
	}


}
