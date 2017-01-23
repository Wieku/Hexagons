package xyz.hexagons.client;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Environment;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dalvik.system.DexClassLoader;
import xyz.hexagons.client.Hexagons;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.utils.function.Consumer;
import xyz.hexagons.client.utils.function.Function;

import java.io.*;

public class AndroidLauncher extends AndroidApplication {
	int fps0;

	public AndroidLauncher() {
		super();
	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)) {
			File baseDirFile = getExternalFilesDir(null);
			if(baseDirFile == null) {
				Instance.storageRoot = getFilesDir();
				System.err.println("getExternalFilesDir returns null, falling back to internal storage!");
			} else {
				Instance.storageRoot = baseDirFile;
			}
		} else {
			Instance.storageRoot = getFilesDir();
			System.err.println("Ext storage not mounted, falling back to internal storage!");
		}

		if(!Instance.storageRoot.mkdirs()) {
			System.err.println("Error creating hexagons directory!");
		}

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

		Instance.cacheFile = new Function<String, File>() {
			@Override
			public File apply(String asset) {
				try {
					final File cacheFile = new File(AndroidLauncher.this.getContext().getCacheDir(), asset);
					cacheFile.getParentFile().mkdirs();
					OutputStream out = new FileOutputStream(cacheFile);
					InputStream in = AndroidLauncher.this.getContext().getAssets().open(asset);
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
			}
		};

		Instance.accountManager = new AndroidAccountManager();
		Instance.classLoaderSupplier = file ->
			new DexClassLoader(file.getPath(), getFilesDir().getPath(), null, this.getClassLoader());

		Instance.audioPlayerFactory = new AndroidAudioPlayerFactory();


		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		Instance.setForegroundFps = new Consumer<Integer>() {
			@Override
			public void accept(Integer fps) {
			}
		};

		config.numSamples = Settings.instance.graphics.msaa;
		config.hideStatusBar = true;
		config.useWakelock = true;
		initialize(new Hexagons(), config);
	}
}
