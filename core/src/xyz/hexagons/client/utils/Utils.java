package xyz.hexagons.client.utils;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

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

	public static String getFileHash(File file){
		try {
			return Files.hash(file, Hashing.sha1()).toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	static public void byteToFloat(byte[] src, int offsetSrc, float[] dst, int offsetDst, int numBytes) {
		if (numBytes % 2 != 0) throw new GdxRuntimeException("bytes must be even (2 bytes 16-bit PCM expected)");
		float scale = 1.0f / Short.MAX_VALUE;
		for (int i = offsetSrc, ii = offsetDst; i < numBytes;) {
			int b1 = src[i++] & 0xff;
			int b2 = src[i++] & 0xff;
			dst[ii++] = (short)(b1 | (b2 << 8)) * scale;
		}
	}

	static public void shortToFloat (short[] src, int offsetSrc, float[] dst, int offsetDst, int numBytes) {
		float scale = 1.0f / Short.MAX_VALUE;
		for (int i = offsetSrc, ii = offsetDst; i < numBytes; i++, ii++)
			dst[ii] = src[i] * scale;
	}

	public static float[] getFromHSV(float hue, float saturation, float brightness) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int)(brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float)Math.floor(hue)) * 6.0f;
			float f = h - (float)Math.floor(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int)h) {
				case 0:
					r = (int)(brightness * 255.0f + 0.5f);
					g = (int)(t * 255.0f + 0.5f);
					b = (int)(p * 255.0f + 0.5f);
					break;
				case 1:
					r = (int)(q * 255.0f + 0.5f);
					g = (int)(brightness * 255.0f + 0.5f);
					b = (int)(p * 255.0f + 0.5f);
					break;
				case 2:
					r = (int)(p * 255.0f + 0.5f);
					g = (int)(brightness * 255.0f + 0.5f);
					b = (int)(t * 255.0f + 0.5f);
					break;
				case 3:
					r = (int)(p * 255.0f + 0.5f);
					g = (int)(q * 255.0f + 0.5f);
					b = (int)(brightness * 255.0f + 0.5f);
					break;
				case 4:
					r = (int)(t * 255.0f + 0.5f);
					g = (int)(p * 255.0f + 0.5f);
					b = (int)(brightness * 255.0f + 0.5f);
					break;
				case 5:
					r = (int)(brightness * 255.0f + 0.5f);
					g = (int)(p * 255.0f + 0.5f);
					b = (int)(q * 255.0f + 0.5f);
					break;
			}
		}
		return new float[]{r/255f, g/255f, b/255f};
	}

	public interface Supplier<T> {
		T get() throws Exception;
	}
	
	public static<T> T tryOr(Supplier<T> f, T alt) {
		try {
			return f.get();
		} catch (Exception e) {
			e.printStackTrace();
			return alt;
		}
	}

	public static Throwable getRootCause(Throwable th) {
		Throwable cause = th.getCause();
		while(cause != null) {
			th = cause;
			cause = th.getCause();
		}
		return th;
	}
}
