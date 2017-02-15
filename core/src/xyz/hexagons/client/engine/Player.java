package xyz.hexagons.client.engine;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.Wall;
import xyz.hexagons.client.menu.settings.Settings;

/**
 * @author Sebastian Krajewski on 22.03.15.
 */
public class Player {

	private float rot;
	public Vector2 tmp = new Vector2();
	public Vector2 tmp2 = new Vector2();
	public Vector2 tmp3 = new Vector2();

	private Vector2 fCh = new Vector2();
	private Vector2 lCh = new Vector2();
	private Vector2 rCh = new Vector2();
	public boolean dead = false;
	private boolean captureMouse = false;
	int dir = 0;
	private Color shadow = new Color();
	private RotatationState rotatationState = RotatationState.STILL;

	float delta;
	Game game;

	public Player(Game game) {
		this.game = game;
	}

	public void update(float delta){
		this.delta += delta;

		float oldRot = rot;

		if(!dead) {
			if (isLeftPressed()) {
				switch(rotatationState) {
					case RIGHT:
						rotatationState = RotatationState.LEFT_ON_RIGHT;
						break;
					case RIGHT_ON_LEFT:
					case LEFT_ON_RIGHT:
						break;
					default:
						rotatationState = RotatationState.LEFT;
						break;
				}
			} else {
				switch(rotatationState) {
					case LEFT:
						rotatationState = RotatationState.STILL;
						break;
					case RIGHT_ON_LEFT:
					case LEFT_ON_RIGHT:
						rotatationState = RotatationState.RIGHT;
						break;
					default:
						break;
				}
			}

			if (isRightPressed()) {
				switch(rotatationState) {
					case LEFT:
						rotatationState = RotatationState.RIGHT_ON_LEFT;
						break;
					case LEFT_ON_RIGHT:
					case RIGHT_ON_LEFT:
						break;
					default:
						rotatationState = RotatationState.RIGHT;
						break;
				}
			} else {
				switch(rotatationState) {
					case RIGHT:
						rotatationState = RotatationState.STILL;
						break;
					case RIGHT_ON_LEFT:
					case LEFT_ON_RIGHT:
						rotatationState = RotatationState.LEFT;
						break;
					default:
						break;
				}
			}

			if(rotatationState == RotatationState.LEFT || rotatationState == RotatationState.LEFT_ON_RIGHT) {
				rot -= (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) ? 4.725f : 9.45f) * 60f * delta;
				dir = -1;
			} else if(rotatationState == RotatationState.RIGHT || rotatationState == RotatationState.RIGHT_ON_LEFT) {
				rot += (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) ? 4.725f : 9.45f) * 60f * delta;
				dir = 1;
			}

			if(captureMouse) {
				int deltaX = Gdx.input.getDeltaX();
				if (deltaX != 0) {
					rot += /*(deltaX > 0 ? 1 : -1) * (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) ? 4.725f : 9.45f) * 60f * delta;*/ deltaX;
					dir = deltaX > 0 ? 1 : -1;
					Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
				}

			}
			if (Gdx.input.isKeyJustPressed(Keys.CONTROL_LEFT)) {
				captureMouse = !captureMouse;
				if(captureMouse){
					Gdx.input.setCursorCatched(true);
				} else {
					Gdx.input.setCursorCatched(false);
				}
			}
		} else {
			Gdx.input.setCursorCatched(false);
			captureMouse = false;
		}

		rot = (rot < 0 ? rot + 360f : (rot > 360f ? rot - 360f : rot));

		fCh.set(0, 0.061f * Instance.diagonal * CurrentMap.gameProperties.beatPulse).rotate(rot);
		lCh.set(0, 0.01f).rotate(rot-90).add(fCh);
		rCh.set(0, 0.01f).rotate(rot+90).add(fCh);

		for(Wall wall : CurrentMap.gameProperties.wallTimeline.getObjects()){

			if(Settings.instance.gameplay.invincibility) continue;

			if((dir == -1 && (Intersector.intersectSegmentPolygon(fCh, lCh, wall.getPolygon()) || Intersector.isPointInPolygon(wall.getPolygon().getVertices(), 0, 8, lCh.x, lCh.y)))
					|| (dir == 1 && (Intersector.intersectSegmentPolygon(fCh, rCh, wall.getPolygon()) || Intersector.isPointInPolygon(wall.getPolygon().getVertices(), 0, 8, rCh.x, rCh.y)))) {
				rot = oldRot;
			}

			if(Intersector.isPointInPolygon(wall.getPolygon().getVertices(), 0, 8, tmp.x, tmp.y)){
				dead = true;
				game.onDie();
			}
		}

		tmp.set(0, 0.061f * Instance.diagonal * CurrentMap.gameProperties.beatPulse).rotate(rot);
		tmp2.set(0, 0.055f * Instance.diagonal * CurrentMap.gameProperties.beatPulse).rotate(rot - 6);
		tmp3.set(0, 0.055f * Instance.diagonal * CurrentMap.gameProperties.beatPulse).rotate(rot + 6);

	}

	private boolean isLeftTouched() {
		final int half = Gdx.graphics.getWidth()/2;
		for (int i = 0; i < 4; i++) {
			if(Gdx.input.isTouched(i) && Gdx.input.getX(i) <= half)
				return true;
		}
		return false;
	}

	private boolean isRightTouched() {
		final int half = Gdx.graphics.getWidth()/2;
		for (int i = 0; i < 4; i++) {
			if(Gdx.input.isTouched(i) && Gdx.input.getX(i) >= half)
				return true;
		}
		return false;
	}

	private boolean isLeftPressed() {
		return Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT) || (Gdx.app.getType() == ApplicationType.Desktop && Gdx.input.isButtonPressed(Buttons.LEFT))
				|| (Gdx.app.getType() == ApplicationType.Android && isLeftTouched());
	}

	private boolean isRightPressed() {
		return Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT) || (Gdx.app.getType() == ApplicationType.Desktop && Gdx.input.isButtonPressed(Buttons.RIGHT))
				|| (Gdx.app.getType() == ApplicationType.Android && isRightTouched());
	}

	public int getIndex(){
		return 3;
	}

	public void reset(){
		dead = false;
	}


	private enum RotatationState {
		STILL,
		LEFT,
		RIGHT,
		LEFT_ON_RIGHT,
		RIGHT_ON_LEFT
	}
}
