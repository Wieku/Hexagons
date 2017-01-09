package xyz.hexagons.launcher;

public interface LauncherUi {
	void tryRun(UnsafeRunnable runnable);
	void reportStatus(String msg);

	interface UnsafeRunnable {
		void run() throws Exception;
	}
}
