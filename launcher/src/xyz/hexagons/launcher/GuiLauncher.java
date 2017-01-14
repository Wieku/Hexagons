package xyz.hexagons.launcher;

import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import xyz.hexagons.launcher.core.LauncherLogic;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.swing.*;

public class GuiLauncher extends Application {
    private static Font orbitron;
    private Stage primaryStage;
	private LauncherLogic launcherLogic;
	private GuiUi ui;
	private Label statusLabel;

	static {
        orbitron = Font.loadFont(GuiLauncher.class.getClassLoader().getResourceAsStream("Orbitron-Regular.ttf"), 20);
    }

	public void main(String[] args) {
		launch(args);
	}

	private void runLogic() throws Exception {
        ui = new GuiUi(primaryStage, statusLabel);
        ui.tryRun(() -> {
            System.out.println("Launch using GUI");

            launcherLogic = new LauncherLogic(ui);
            launcherLogic.launch();
        });
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Hexagons! Launcher");

		this.primaryStage = primaryStage;
		Image logo = new Image("icon.png", 256, 256, true, true);
		ImageView logoView = new ImageView();
		logoView.setImage(logo);
		BorderPane imagePane = new BorderPane();
		imagePane.setCenter(logoView);

		Label name = new Label("Hexagons!");
		name.setStyle("-fx-font-size: 3em; -fx-text-fill: #f0f0f0;-fx-font-family:Orbitron");
        BorderPane namePane = new BorderPane();
        namePane.setCenter(name);

        statusLabel = new Label("wait...");
        statusLabel.setStyle("-fx-font-size: 1.5em; -fx-text-fill: #f0f0f0;-fx-font-family:Orbitron");
        BorderPane statusPane = new BorderPane();
        statusPane.setCenter(statusLabel);

		VBox mainBox = new VBox();
		mainBox.getChildren().add(imagePane);
		mainBox.getChildren().add(namePane);
        mainBox.getChildren().add(statusPane);
		mainBox.setFillWidth(true);

		BorderPane pane = new BorderPane();
		pane.setCenter(mainBox);
		pane.setStyle("-fx-background-color: #272727;");

		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);

		primaryStage.setMinHeight(256 + 128);
		primaryStage.setMinWidth(256 + 128);

		primaryStage.show();
		runLogic();
	}
}
