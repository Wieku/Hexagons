package xyz.hexagons.client;

import android.os.Bundle;

import android.os.Environment;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import xyz.hexagons.client.Hexagons;
import xyz.hexagons.client.menu.settings.Settings;

import java.io.File;
import java.io.FileReader;

public class AndroidLauncher extends AndroidApplication {
	int fps0;

	public AndroidLauncher() {
		super();
		Instance.storageRoot = new File(Environment.getExternalStorageDirectory(), "hexagons");
		Instance.storageRoot.mkdirs();
		System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP:"+Instance.storageRoot.toString());
	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			Gson gson = new GsonBuilder().create();
			File file = new File(Instance.storageRoot, "settings.json");
			if(!file.exists())
				Settings.instance = new Settings();
			else
				Settings.instance = gson.fromJson(new FileReader(file), Settings.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Instance.setForegroundFps = fps -> fps0 = fps;

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.numSamples = Settings.instance.graphics.msaa;
		config.hideStatusBar = true;
		config.useWakelock = true;
		initialize(new Hexagons(), config);
	}
}
