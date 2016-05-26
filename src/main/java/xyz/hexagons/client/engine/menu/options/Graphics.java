/*
package xyz.hexagons.client.engine.menu.options;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import xyz.hexagons.client.Main;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.engine.Settings;
import xyz.hexagons.client.engine.menu.buttons.Action;
import xyz.hexagons.client.engine.menu.buttons.Slider;
import xyz.hexagons.client.engine.menu.buttons.State;
import xyz.hexagons.client.utils.GUIHelper;

*/
/**
 * @author Sebastian Krajewski on 07.04.15.
 *//*

public class Graphics extends Section{

	public static Graphics instance = new Graphics();

	public Graphics(){
		super();
		top().left();

		State vsync = new State("VSync", false) {
			@Override
			public void writeValue(Boolean value) {
				Settings.instance.graphics.vSync = value;
				Main.config.vSyncEnabled = value;
				Gdx.graphics.setVSync(value);
			}

			@Override
			public Boolean loadValue() {
				return Settings.instance.graphics.vSync;
			}
		};

		vsync.select(true);

		State fullScreen = new State("Full Screen", false) {
			@Override
			public void writeValue(Boolean value) {
				Settings.instance.graphics.fullscreen = value;
				Main.config.fullscreen = value;
				if(value){
					Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode());
				} else {
					Gdx.graphics.setDisplayMode(1024, 768, false);
				}
			}

			@Override
			public Boolean loadValue() {
				return Settings.instance.graphics.fullscreen;
			}
		};

		Slider msaa = new Slider("MSAA", 0, 4, 1, 4) {
			@Override
			public void writeValue(Integer value) {
				Settings.instance.graphics.msaa = (value == 0 ? 0 : (int) Math.pow(2, value));
				Main.config.samples = (value == 0 ? 0 : (int) Math.pow(2, value));
				CurrentMap.pushText("Restart game to MSAA changes take effect!", 6f);
			}

			@Override
			public Integer loadValue() {
				return (Settings.instance.graphics.msaa==0 ? 0 : (int) MathUtils.log2(Settings.instance.graphics.msaa));
			}
		};

		Action exit = new Action("Back") {
			@Override
			public void action() {
				Options.getInstance().setMenu(OptionMenu.instance);
			}
		};

		addButton(vsync);
		addButton(fullScreen);
		addButton(msaa);
		addButton(exit);

		add(new Label("Graphics", GUIHelper.getLabelStyle(Color.WHITE, 22))).colspan(2).padLeft(20).padBottom(20).left().fillX().row();
		add(vsync).fillX().row();
		add(fullScreen).fillX().row();
		add(msaa).fillX().row();
		add(exit).fillX().colspan(2).padTop(22).row();
	}

}

*/
