package xyz.hexagons.launcher.util;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPUtil {
	public static String getText(String _url) throws Exception {
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
}
