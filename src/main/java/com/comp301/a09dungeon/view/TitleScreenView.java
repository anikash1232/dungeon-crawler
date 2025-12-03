package com.comp301.a09dungeon.view;

import com.comp301.a09dungeon.model.Model;
import com.comp301.a09dungeon.controller.Controller;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

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

        // only start the game; view switching happens in update()
        startBtn.setOnAction(e -> controller.startGame());

        Label byName = new Label("By Anirudh Kashyap");
        byName.getStyleClass().add("label");

        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(title, highScore, lastScore, startBtn, byName);

        return root;
    }

    @Override
    public void update() {
        if (model.getStatus() == Model.STATUS.END_GAME) {
            launcher.setView(this);
        } else if (model.getStatus() == Model.STATUS.IN_PROGRESS) {
            launcher.setView(launcher.getGameView());
        }
    }
}
