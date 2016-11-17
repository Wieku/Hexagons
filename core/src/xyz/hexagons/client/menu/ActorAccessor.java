package xyz.hexagons.client.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import me.wieku.animation.AnimationEquations;
import me.wieku.animation.animations.Animation;
import me.wieku.animation.api.AnimationAccessor;
import me.wieku.animation.api.AnimationEquation;
import xyz.hexagons.client.Instance;

public class ActorAccessor implements AnimationAccessor<Actor> {

	public static final int SLIDEX = 0;
	public static final int SLIDEY = 1;
	public static final int FADE = 2;
	public static final int SIZEX = 3;
	public static final int FADETABLE = 4;
	public static final int TEXTCOLOR = 5;
	public static final int SIZEC = 6;
	public static final int SIZEY = 7;

	@Override
	public int getValues(Actor target, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case SLIDEX:
				returnValues[0] = target.getX();
				return 1;
			case SIZEX:
				returnValues[0] = target.getWidth();
				return 1;
			case SIZEY:
				returnValues[0] = target.getHeight();
				return 1;
			case FADE:
				returnValues[0] = target.getColor().a;
				return 1;
			case SLIDEY:
				returnValues[0] = target.getY();
				return 1;
			case SIZEC:
				returnValues[0] = target.getScaleX();
				return 1;
			case FADETABLE:
				returnValues[0] = target.getColor().a;
				return 1;
			case TEXTCOLOR:
				Color col = ((TextField) target).getStyle().fontColor;

				returnValues[0] = col.r;
				returnValues[1] = col.g;
				returnValues[2] = col.b;
				returnValues[3] = col.a;

				return 4;
			default:
				return -1;
		}
	}

	@Override
	public void setValues(Actor target, int tweenType, float[] newValues) {
		switch (tweenType) {
			case SLIDEX:
				target.setX(newValues[0]);
				return;
			case SIZEX:
				target.setWidth(newValues[0]);
				return;
			case SIZEY:

				float g = (newValues[0] - target.getHeight()) / 2;
				target.setY(target.getY()-g);
				target.setHeight(newValues[0]);
				return;
			case SLIDEY:
				target.setY(newValues[0]);
				return;
			case FADE:
				Color col = target.getColor();
				target.setColor(col.r, col.g, col.b, newValues[0]);
				return;
			case FADETABLE:
				Table table = (Table) target;
				Color colr = table.getColor();
				table.setColor(colr.r, colr.g, colr.b, newValues[0]);
				for (Actor child : table.getChildren()) {
					colr = child.getColor();
					child.setColor(colr.r, colr.g, colr.b, newValues[0]);
				}
				return;
			case SIZEC:
				float posX = target.getX(Align.center);
				float posY = target.getY(Align.center);
				float sizeX = target.getWidth()*target.getScaleX();
				float sizeY = target.getHeight()*target.getScaleY();
				target.setScale(newValues[0]);
				target.setOrigin(target.getWidth()/2, target.getHeight()/2);
				target.setPosition(target.getX(), target.getY());
				//target.setPosition(target.getX() + (target.getWidth() * target.getScaleX() - sizeX) / 2 , target.getY() + (target.getHeight()*target.getScaleY()-sizeY) / 2);
				return;
			case TEXTCOLOR:
				Color col1 = ((TextField) target).getStyle().fontColor;

				col1.r = newValues[0];
				col1.g = newValues[1];
				col1.b = newValues[2];
				col1.a = newValues[3];

				return;
			default:
				assert false;
				return;
		}

	}

	public static Animation startTween(Animation tween) {
		if (tween == null) {
			throw new NullPointerException("Tween is null");
		}

		/*if(tween.isStarted() && !tween.isFinished()){
			tween.kill();
		}*/

		tween.start(Instance.getAnimationManager());
		return tween;
	}

	public static Animation createFadeTableTween(Table ac, float duration, float delay, float fade) {
		return new Animation(ac, FADETABLE, duration).ease(AnimationEquations.easeOutSine).target(fade).delay(delay);
	}

	public static Animation createFadeTween(Actor ac, float duration, float delay, float fade) {
		return new Animation(ac, FADE, duration).ease(AnimationEquations.easeInOutSine).target(fade).delay(delay);
	}

	public static Animation createSineTween(Actor ac, int type, float duration, float target, float delay) {
		return createTween(ac, type, duration, target, delay, AnimationEquations.easeInOutSine);
	}

	public static Animation createCircleOutTween(Actor ac, int type, float duration, float target, float delay) {
		return createTween(ac, type, duration, target, delay, AnimationEquations.easeOutCirc);
	}

	public static Animation createQuadTween(Actor ac, int type, float duration, float target, float delay) {
		return createTween(ac, type, duration, target, delay, AnimationEquations.easeInOutQuad);
	}

	public static Animation createInBackTween(Actor ac, int type, float duration, float target, float delay) {
		return createTween(ac, type, duration, target, delay, AnimationEquations.easeInBack);
	}

	public static Animation createBackTween(Actor ac, int type, float duration, float target, float delay) {
		return createTween(ac, type, duration, target, delay, AnimationEquations.easeOutBack);
	}

	public static Animation createElasticEndTween(Actor ac, int type, float duration, float target, float delay) {
		return createTween(ac, type, duration, target, delay, AnimationEquations.easeOutElastic);
	}

	public static Animation createTween(Actor ac, int type, float duration, float target, float delay, AnimationEquation e) {
		return new Animation(ac, type, duration).ease(e).target(target).delay(delay);
	}
}
