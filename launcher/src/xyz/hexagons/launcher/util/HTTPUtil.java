package xyz.hexagons.launcher.util;

import com.google.gson.Gson;
import xyz.hexagons.launcher.LauncherUi;
import xyz.hexagons.launcher.core.Settings;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPUtil {
	public static String getText(String url) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestMethod("GET");
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		StringBuilder res = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			res.append(line);
		}
		reader.close();
		return res.toString();
	}

	public static <T> T getJson(String url, Class<T> clazz) throws Exception {
		Gson gson = new Gson();
		return gson.fromJson(getText(url), clazz);
	}

	public static void getAsset(String hash, File dataDir, LauncherUi ui) throws Exception {
		ui.startGetAsset(Settings.objectCdnBase + "/" + hash);
		HttpURLConnection conn = (HttpURLConnection) new URL(Settings.objectCdnBase + "/" + hash).openConnection();
		InputStream in = conn.getInputStream();
		FileOutputStream fos = new FileOutputStream(new File(dataDir, hash));
		byte[] buf = new byte[8192];
		while (true) {
			int len = in.read(buf);
			if (len == -1) {
				break;
			}
			fos.write(buf, 0, len);
		}
		in.close();
		fos.flush();
		fos.close();
	}
}
