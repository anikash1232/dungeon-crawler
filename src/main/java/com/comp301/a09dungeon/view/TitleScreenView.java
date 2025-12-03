package com.comp301.a09dungeon.view;

import com.comp301.a09dungeon.model.Model;
import com.comp301.a09dungeon.controller.Controller;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class TitleScreenView extends View {

    public TitleScreenView(Model model, Controller controller, AppLauncher launcher) {
        super(model, controller, launcher);
    }

    @Override
    public Parent render() {

        Label title = new Label("Dungeon Crawler");
        title.getStyleClass().add("title-label");

        Label highScore = new Label("High Score: " + model.getHighScore());
        highScore.getStyleClass().add("label");

        Label lastScore = new Label("Last Score: " + model.getCurScore());
        lastScore.getStyleClass().add("label");

        Button startBtn = new Button("Start Game");
        startBtn.getStyleClass().add("button");

        startBtn.setOnAction(e -> {
            controller.startGame();
            launcher.setView(new GameView(model, controller, launcher));
        });

        Label byName = new Label("By Anirudh Kashyap");
        byName.getStyleClass().add("label");

        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(title, highScore, lastScore, startBtn, byName);

        return root;
    }

    @Override
    public void update() {
        // Called whenever the model changes.
        // Only react when the game has ended.
        if (model.getStatus() == Model.STATUS.END_GAME) {

            // Current root is the GameView when the collision happens
            Parent currentRoot = launcher.getScene().getRoot();

            if (currentRoot != null) {
                // Shake the whole board
                TranslateTransition shake = new TranslateTransition(Duration.millis(80), currentRoot);
                shake.setFromX(-10);
                shake.setToX(10);
                shake.setAutoReverse(true);
                shake.setCycleCount(4);
                shake.play();
            }

            // After a short delay, switch to the title screen
            PauseTransition delay = new PauseTransition(Duration.millis(250));
            delay.setOnFinished(e -> launcher.setView(this));
            delay.play();
        }
    }
}