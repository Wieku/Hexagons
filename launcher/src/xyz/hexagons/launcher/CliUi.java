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

	@Override
	public void reportStatus(String msg) {
		System.out.println("STATUS > " + msg);
	}

	@Override
	public void startGetAsset(String url) {
		System.out.println("GET:" + url);
	}

	@Override
	public void shortText(String msg) {

	}

	@Override
	public void hideWindows() {

	}
}
