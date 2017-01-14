package xyz.hexagons.launcher;

public interface LauncherUi {
	void tryRun(UnsafeRunnable runnable);
	void reportStatus(String msg);
	void startGetAsset(String url);
	void shortText(String msg);
	void hideWindows();

	interface UnsafeRunnable {
		void run() throws Exception;
	}
}
