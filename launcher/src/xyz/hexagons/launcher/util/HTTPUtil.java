package xyz.hexagons.launcher.util;

import com.google.gson.Gson;
import xyz.hexagons.launcher.LauncherUi;
import xyz.hexagons.launcher.core.Settings;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

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

	public static void getAsset(String hash, String sha, File dataDir, LauncherUi ui) throws Exception {
		File file = new File(dataDir, hash);
		if(file.exists()) {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			FileInputStream fis = new FileInputStream(file);

			byte[] buf = new byte[1024 * 64];
			int read;
			while((read = fis.read(buf)) != -1) {
				sha256.update(buf, 0, read);
			}
			byte[] hashBytes = sha256.digest();
			StringBuffer hashBuf = new StringBuffer();
			for(byte hashByte : hashBytes) {
				hashBuf.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
			}

			if(sha.equals(hashBuf.toString())) {
				ui.reportStatus("Checksum match");
				return;
			}

			ui.reportStatus("Checksum mismatch:");
			ui.reportStatus("Filesystem: " + hashBuf.toString());
			ui.reportStatus("Remote: " + sha);
		}
		ui.startGetAsset(Settings.objectCdnBase + "/" + hash);
		HttpURLConnection conn = (HttpURLConnection) new URL(Settings.objectCdnBase + "/" + hash).openConnection();
		InputStream in = conn.getInputStream();
		FileOutputStream fos = new FileOutputStream(file);
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
