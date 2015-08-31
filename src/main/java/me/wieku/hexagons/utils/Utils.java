package me.wieku.hexagons.utils;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Utils {

	public static void sleep(long ms){
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void restartGame() throws URISyntaxException, IOException, InterruptedException {
		System.out.println("Trying to restart game...");
		System.out.println("Launch command: java -jar \"" + getGameFile() + "\"");
		Process proc = new ProcessBuilder("java","-jar", getGameFile()).start();
		System.out.println("Waiting for process to start...");
		if(proc.isAlive()){
			System.out.println("Process started!");
		}
		System.exit(0);
	}

	public static void downloadFile(String url, String destFile) throws IOException {
		downloadFileWithProgressBar(url, destFile, null);
	}

	public static void downloadFileWithProgressBar(String url, String destFile, ProgressBar bar) throws IOException {

		HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();

		if(connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {

			if(bar != null)
				bar.setRange(0, connection.getContentLength());

			BufferedInputStream in = null;
			FileOutputStream fout = null;

			try {
				in = new BufferedInputStream(connection.getInputStream());

				fout = new FileOutputStream(destFile);
				final byte data[] = new byte[1024];
				int count;
				while ((count = in.read(data, 0, 1024)) != -1) {
					fout.write(data, 0, count);
					if(bar != null)
						bar.setValue(bar.getValue() + count);
				}
			} finally {
				if (in != null) {
					in.close();
				}
				if (fout != null) {
					fout.close();
				}
			}
			connection.disconnect();
		} else {
			connection.disconnect();
			throw new IOException("Wrong response code!");
		}

	}

	public static String getGameFile() {
		try {
			return new File(Utils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getGameDir() {
		return new File(getGameFile()).getParent() + File.separator;
	}

	public static String getBuildNumber(){
		try {
			Enumeration resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
			while (resEnum.hasMoreElements()) {
				try {
					URL url = (URL)resEnum.nextElement();
					InputStream is = url.openStream();
					if (is != null) {
						Manifest manifest = new Manifest(is);
						Attributes mainAttribs = manifest.getMainAttributes();
						return mainAttribs.getValue("Build-Number");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return null;
	}
}
