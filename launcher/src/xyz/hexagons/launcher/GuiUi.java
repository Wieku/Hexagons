package xyz.hexagons.launcher;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class GuiUi implements LauncherUi {
	private Stage stage;
	private Label status;

	public GuiUi(Stage stage, Label status) {
		this.stage = stage;
		this.status = status;
	}

	@Override
	public void tryRun(UnsafeRunnable runnable) {

		new Thread(() -> {
			try {
				runnable.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public void reportStatus(String msg) {
		System.out.println(msg);
	}

	@Override
	public void startGetAsset(String url) {
		System.out.println("Get " + url);
	}

	@Override
	public void shortText(String msg) {
		Platform.runLater(() -> {
			status.setText(msg);
		});
	}

	@Override
	public void hideWindows() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Platform.exit();
	}
}
