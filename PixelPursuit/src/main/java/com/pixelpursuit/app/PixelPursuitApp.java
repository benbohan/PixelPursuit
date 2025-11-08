package main.java.com.pixelpursuit.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PixelPursuitApp extends Application {
    @Override
    public void start(Stage stage) {
        VBox root = new VBox(12, new Label("PixelPursuit"));
        Scene scene = new Scene(root, 360, 240);
        stage.setTitle("PixelPursuit");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) { launch(args); }
}
