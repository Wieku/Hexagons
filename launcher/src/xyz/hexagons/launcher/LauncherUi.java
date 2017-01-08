package xyz.hexagons.launcher;

public interface LauncherUi {
	public void tryRun(UnsafeRunnable runnable);

	public interface UnsafeRunnable {
		public void run() throws Exception;
	}
}
