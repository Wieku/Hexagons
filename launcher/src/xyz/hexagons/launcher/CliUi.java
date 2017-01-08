package xyz.hexagons.launcher;

public class CliUi implements LauncherUi {
	@Override
	public void tryRun(UnsafeRunnable runnable) {
		try {
			runnable.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
