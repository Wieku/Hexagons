package me.wieku.hexagons.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import me.wieku.hexagons.api.HColor;
import me.wieku.hexagons.engine.menu.MapSelect;
import me.wieku.hexagons.Main;
import me.wieku.hexagons.api.CurrentMap;
import me.wieku.hexagons.engine.camera.SkewCamera;
import me.wieku.hexagons.engine.render.Background;
import me.wieku.hexagons.engine.render.Center;
import me.wieku.hexagons.engine.render.Renderer;
import me.wieku.hexagons.engine.render.WallRenderer;
import me.wieku.hexagons.engine.ui.HProgressBar;
import me.wieku.hexagons.map.Map;
import me.wieku.hexagons.resources.ArchiveFileHandle;
import me.wieku.hexagons.audio.AudioPlayer;
import me.wieku.hexagons.utils.GUIHelper;

import java.text.DecimalFormat;
import java.util.LinkedList;

/**
 * @author Sebastian Krajewski on 28.03.15.
 */
public class Game implements Screen {

	Map map;
	AudioPlayer audioPlayer;

	public float exitPosition;

	ShapeRenderer renderer;
	SkewCamera camera = new SkewCamera();
	Stage stage;

	Background background = new Background();
	Center center = new Center();
	Player player = new Player();
	WallRenderer wallRenderer = new WallRenderer();

	Label fps;
	Label points;
	Label time;
	Label message;
	ProgressBar next;
	Sound levelUp;
	Sound sides;
	Sound go;
	Sound death;
	Sound gameOver;

	LinkedList<Renderer> renderers = new LinkedList<>();

	int width, height;

	float score = 0;

	public static float scale = 1f;

	private int inc = 1;

	DecimalFormat timeFormat = new DecimalFormat("0.000");
	DecimalFormat delayFormat = new DecimalFormat("0.00");
	public void playSound(Sound sound){
		long id = sound.play();
		sound.setVolume(id, (float) Settings.instance.masterVolume * (float) Settings.instance.effectVolume / 10000f);
	}

	public Game (Map map){
		this.map = map;

		renderer = new ShapeRenderer();

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
		stage.addActor(points);
		
		next = new HProgressBar(0f, 1f, 0.0001f, false);
		next.setSize(200, 14);
		next.layout();

		stage.addActor(next);
		
		audioPlayer = new AudioPlayer(new ArchiveFileHandle(map.file,map.info.audioFileName));
		audioPlayer.setLooping(true);


		levelUp = Gdx.audio.newSound(Gdx.files.internal("assets/sound/levelUp.ogg"));
		sides = Gdx.audio.newSound(Gdx.files.internal("assets/sound/beep.ogg"));
		go = Gdx.audio.newSound(Gdx.files.internal("assets/sound/go.ogg"));
		death = Gdx.audio.newSound(Gdx.files.internal("assets/sound/death.ogg"));
		gameOver = Gdx.audio.newSound(Gdx.files.internal("assets/sound/gameOver.ogg"));

		addRenderer(center);
		addRenderer(wallRenderer);
		addRenderer(player);

		start(map.info.startTimes[0]);
	}

	public void addRenderer(Renderer renderer){
		renderers.add(renderer);
		renderers.sort((o1, o2) -> Integer.compare(o1.getIndex(), o2.getIndex()));
	}


	@Override
	public void show() {

		Main.config.foregroundFPS = 0;
	}

	@Override
	public void render(float delta) {
		updateGame(delta);

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		renderer.setProjectionMatrix(camera.combined);
		renderer.identity();
		renderer.translate(0, 0, 0);
		renderer.rotate(1, 0, 0, 90);
		renderer.begin(ShapeRenderer.ShapeType.Filled);
		background.render(renderer, delta, true);
		renderer.end();

		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

		for(int j = 1; j <= CurrentMap.layers; ++j){
			renderer.identity();
			renderer.translate(0, -j * CurrentMap.depth * 1.4f * Math.abs(CurrentMap.skew / CurrentMap.maxSkew), 0);

			renderer.rotate(1, 0, 0, 90);
			renderer.begin(ShapeRenderer.ShapeType.Filled);
			for(Renderer render : renderers){
				render.render(renderer, delta, true);
			}
			renderer.end();
		}

		renderer.identity();
		renderer.translate(0, 0, 0);
		renderer.rotate(1, 0, 0, 90);

		renderer.begin(ShapeRenderer.ShapeType.Filled);
		for(Renderer render : renderers){
			render.render(renderer, delta, false);
		}
		renderer.end();

		message.setPosition((stage.getWidth() - message.getWidth()) / 2, (stage.getHeight() - message.getHeight()) * 2.5f / 3);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
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

		CurrentMap.currentTime = 0f;
		CurrentMap.reset();
		score = 0;
		player.reset();
		camera.reset();
		audioPlayer.setVolume((float) Settings.instance.masterVolume * (float) Settings.instance.musicVolume / 10000f);
		audioPlayer.play(startTime);

		map.script.onInit();
		map.script.initColors();
		map.script.initEvents();
		playSound(go);
	}

	public void restart(){
		start(map.info.startTimes[MathUtils.random(0, map.info.startTimes.length - 1)]);
	}

	float fastRotate = 0f;
	float delta0;
	boolean escClick = false;
	Color tmpColor = new Color();
	int framesps = 0;
	int[] fpsBuffer = new int[64];
	int fpsIndex = 0;
	public void updateGame(float delta){

		if(!player.dead && CurrentMap.wallTimeline.isFirstRemoved())
			score += delta * (CurrentMap.difficulty * CurrentMap.speed * (((int)CurrentMap.currentTime) * 5 + 300));

		updateTimeline(delta);

		if(player.dead){

			if (audioPlayer != null && !audioPlayer.hasEnded()) {
				playSound(death);
				playSound(gameOver);
				camera.rumble(20f, 1f);
				exitPosition = audioPlayer.getPosition();
				audioPlayer.stop();
			}

			if(Gdx.input.isKeyPressed(Keys.SPACE)){
				restart();
			}

			if(Gdx.input.isKeyPressed(Keys.ESCAPE) && !escClick){
				audioPlayer.dispose();
				Main.getInstance().setScreen(MapSelect.getInstance());
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
		camera.update(delta);
		this.delta0 += delta;
		renderers.forEach(o -> o.update(delta));
		while (this.delta0 >= (1f / 60)) {

			fpsBuffer[fpsIndex++] = (int)(1f / delta);

			if(fpsIndex >=fpsBuffer.length)
				fpsIndex = 0;

			framesps = 0;
			for(int i : fpsBuffer){
				framesps += i;
			}
			framesps /= fpsBuffer.length;

			updateText(1f / 60);
			updateSkew(1f / 60);
			updatePulse(1f/60);

			if (!player.dead) {
				CurrentMap.walls.update(1f/60);
				background.update(1f / 60);
				tmpColor.set(CurrentMap.walls.r, CurrentMap.walls.g, CurrentMap.walls.b, CurrentMap.walls.a);
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

		fps.setText(framesps + "FPS\n" + delayFormat.format(1000f/framesps)+"ms");
		time.setText("Time: " + timeFormat.format(CurrentMap.currentTime) + (player.dead?"\nYou died! Press \"Space\" to restart!":""));
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
		inc = (delta2 == 0 ? 1 : (delta2 == CurrentMap.skewTime ? -1 : inc));
		delta2 += delta * inc;
		delta2 = Math.min(CurrentMap.skewTime, Math.max(delta2, 0));
		float percent = delta2 / CurrentMap.skewTime;
		CurrentMap.skew = CurrentMap.minSkew + (CurrentMap.maxSkew - CurrentMap.minSkew) * percent;
	}

	float delta3;
	public void updateTimeline(float delta) {

		if(!player.dead){
			CurrentMap.wallTimeline.update(delta);
			CurrentMap.eventTimeline.update(delta);
			CurrentMap.currentTime += delta;
		}

		if(!player.dead && (delta3 +=delta) >= CurrentMap.levelIncrement){

			fastRotate = CurrentMap.fastRotate;

			playSound(levelUp);

			CurrentMap.isFastRotation = true;
			CurrentMap.rotationSpeed += (CurrentMap.rotationSpeed > 0 ? CurrentMap.rotationIncrement: -CurrentMap.rotationIncrement );
			CurrentMap.rotationSpeed *= -1;
			CurrentMap.rotationSpeed = Math.min(CurrentMap.rotationSpeedMax, Math.max(-CurrentMap.rotationSpeedMax, CurrentMap.rotationSpeed));

			CurrentMap.mustChangeSides = true;

			CurrentMap.speed += CurrentMap.speedInc;
			CurrentMap.delayMult += CurrentMap.delayMultInc;
			delta3 = 0;
		}

		next.setValue(delta3 / CurrentMap.levelIncrement);

		if (CurrentMap.wallTimeline.isEmpty() && CurrentMap.mustChangeSides) {
			CurrentMap.sides = MathUtils.random(CurrentMap.minSides, CurrentMap.maxSides);
			playSound(sides);
			CurrentMap.mustChangeSides = false;
		}

		if (CurrentMap.wallTimeline.isAllSpawned() && !CurrentMap.mustChangeSides) {
			map.script.nextPattern();
		}

	}

	public void updateRotation(float delta) {

		if(player.dead) {
			if(CurrentMap.rotationSpeed < 0) {
				CurrentMap.rotationSpeed = Math.min(-0.02f, CurrentMap.rotationSpeed + 0.002f * 60 * delta);
			} else if(CurrentMap.rotationSpeed > 0) {
				CurrentMap.rotationSpeed = Math.max(0.02f, CurrentMap.rotationSpeed - 0.002f * 60 * delta);
			}
		}
		camera.rotate(CurrentMap.rotationSpeed * 360f * delta + (CurrentMap.rotationSpeed > 0 ? 1 : -1) * (getSmootherStep(0, CurrentMap.fastRotate, fastRotate) / 3.5f) * 17.f * 60 * delta);
		fastRotate = Math.max(0, fastRotate - 60f * delta);
		if(fastRotate == 0) CurrentMap.isFastRotation = false;
	}

	float delta4;
	float delta5;
	public void updatePulse(float delta){

		if(player.dead) return;

		if(delta4 <= 0){
			CurrentMap.beatPulse = CurrentMap.beatPulseMax;
			delta4 = CurrentMap.beatPulseDelay;
		}

		delta4 -= delta;

		if(CurrentMap.beatPulse > CurrentMap.beatPulseMin) scale = CurrentMap.beatPulse -= 1.2f * delta;

		if(delta5 <= 0){

			if((CurrentMap.pulseDir < 0 && CurrentMap.pulse <= CurrentMap.pulseMin) || (CurrentMap.pulseDir > 0 && CurrentMap.pulse >= CurrentMap.pulseMax)){
				CurrentMap.pulse = CurrentMap.pulseDir > 0 ? CurrentMap.pulseMax : CurrentMap.pulseMin;
				CurrentMap.pulseDir *= -1;
				if(CurrentMap.pulseDir < 0) delta5 = CurrentMap.pulseDelayMax;
			}

			CurrentMap.pulse += (CurrentMap.pulseDir > 0 ? CurrentMap.pulseSpeed : -CurrentMap.pulseSpeedR) * 60f * delta;

		}

		delta5 -= delta * 60;

	}

	float getSaturated(float mValue) { return Math.max(0.f, Math.min(1.f, mValue)); }
	float getSmootherStep(float edge0, float edge1, float x)
	{
		x = getSaturated((x - edge0)/(edge1 - edge0));
		return x * x * x * (x * (x * 6 - 15) + 10);
	}


}
