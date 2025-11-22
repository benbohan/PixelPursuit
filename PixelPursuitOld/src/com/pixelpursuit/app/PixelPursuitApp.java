package com.pixelpursuit.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PixelPursuitApp extends Application {
    @Override
    public void start(Stage stage) {
        var title = new Label("PixelPursuit");
        title.getStyleClass().add("title");

        var play = new Button("Play");
        play.setOnAction(e -> System.out.println("Play clicked")); // hook up later

        var root = new VBox(12, title, play);
        root.setPadding(new Insets(16));

        var scene = new Scene(root, 420, 280);

        // optional CSS (src/ui/app.css) — safe to skip if not created yet
        var css = getClass().getResource("/ui/app.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setTitle("PixelPursuit");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
