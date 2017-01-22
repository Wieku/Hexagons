package xyz.hexagons.client;

import android.os.Bundle;

import android.os.Environment;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import xyz.hexagons.client.Hexagons;
import xyz.hexagons.client.menu.settings.Settings;

import java.io.*;

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

		Instance.cacheFile = asset -> {
			try {
				final File cacheFile = new File(getContext().getCacheDir(), asset);
				cacheFile.getParentFile().mkdirs();
				OutputStream out = new FileOutputStream(cacheFile);
				InputStream in = getContext().getAssets().open(asset);
				try {
					byte[] buf = new byte[4 << 10];
					int read;
					while ((read = in.read(buf)) > 0) {
						out.write(buf, 0, read);
					}
					out.flush();
				} finally {
					out.close();
					in.close();
				}
				return cacheFile;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		};

		Instance.accountManager = new AndroidAccountManager();

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		Instance.setForegroundFps = fps -> {};

		config.numSamples = Settings.instance.graphics.msaa;
		config.hideStatusBar = true;
		config.useWakelock = true;
		initialize(new Hexagons(), config);
	}
}
