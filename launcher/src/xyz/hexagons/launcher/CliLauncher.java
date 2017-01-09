package xyz.hexagons.launcher;

import xyz.hexagons.launcher.core.LauncherLogic;

public class CliLauncher {
	private LauncherLogic launcherLogic;
	private CliUi ui;

	public void main(String[] args) {
		ui = new CliUi();

		ui.tryRun(this::init);
	}

	private void init() throws Exception {
		System.out.println("Launch using CLI");
		launcherLogic = new LauncherLogic(ui);
		launcherLogic.launch();
	}
}
