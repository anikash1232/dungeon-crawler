package com.comp301.a09dungeon.view;

import com.comp301.a09dungeon.model.Model;
import com.comp301.a09dungeon.controller.Controller;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class TitleScreenView extends View {

    public TitleScreenView(Model model, Controller controller, AppLauncher launcher) {
        super(model, controller, launcher);
    }

    @Override
    public Parent render() {

        // ──────────────────────────────
        //  Create ALL text elements
        // ──────────────────────────────

        Label title = new Label("Dungeon Crawler");
        title.setStyle(
                "-fx-font-size: 72px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #ccff99;" +
                        "-fx-effect: dropshadow(gaussian, black, 12, 0.9, 0, 0);"
        );

        Label highScore = new Label("High Score: " + model.getHighScore());
        highScore.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-text-fill: white;" +
                        "-fx-effect: dropshadow(gaussian, black, 10, 0.8, 0, 0);"
        );

        Label lastScore = new Label("Last Score: " + model.getCurScore());
        lastScore.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-text-fill: white;" +
                        "-fx-effect: dropshadow(gaussian, black, 10, 0.8, 0, 0);"
        );

        Button startBtn = new Button("Start Game");
        startBtn.getStyleClass().add("button");
        startBtn.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-padding: 12px 30px;" +
                        "-fx-background-color: rgba(40,80,40,0.85);" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, black, 12, 0.6, 0, 0);"
        );

        startBtn.setOnAction(e -> {
            controller.startGame();
            launcher.setView(new GameView(model, controller, launcher));
        });

        Label byName = new Label("By Anirudh Kashyap");
        byName.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-text-fill: white;" +
                        "-fx-effect: dropshadow(gaussian, black, 10, 0.8, 0, 0);"
        );

        // ──────────────────────────────
        // DARK TRANSLUCENT OVERLAY PANEL
        // ──────────────────────────────

        VBox textBox = new VBox(25, title, highScore, lastScore, startBtn, byName);
        textBox.setAlignment(Pos.CENTER);
        textBox.setPadding(new Insets(30));
        textBox.setStyle(
                "-fx-background-color: rgba(0,0,0,0.40);" +  // dark transparent panel
                        "-fx-background-radius: 15;"
        );

        // ──────────────────────────────
        // BACKGROUND IMAGE WRAPPER
        // ──────────────────────────────

        StackPane root = new StackPane();
        root.setStyle(
                "-fx-background-image: url('background.png');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center;" +
                        "-fx-background-repeat: no-repeat;"
        );

        root.getChildren().add(textBox);

        return root;
    }

    @Override
    public void update() {
        if (model.getStatus() == Model.STATUS.END_GAME) {

            Parent currentRoot = launcher.getScene().getRoot();

            if (currentRoot != null) {
                TranslateTransition shake = new TranslateTransition(Duration.millis(80), currentRoot);
                shake.setFromX(-10);
                shake.setToX(10);
                shake.setAutoReverse(true);
                shake.setCycleCount(4);
                shake.play();
            }

            PauseTransition delay = new PauseTransition(Duration.millis(250));
            delay.setOnFinished(e -> launcher.setView(this));
            delay.play();
        }
    }
}
