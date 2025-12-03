package com.comp301.a09dungeon.view;

import com.comp301.a09dungeon.model.Model;
import com.comp301.a09dungeon.model.ModelImpl;
import com.comp301.a09dungeon.controller.Controller;
import com.comp301.a09dungeon.controller.ControllerImpl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class AppLauncher extends Application {

  private Scene scene;
  private Model model;
  private Controller controller;
  private TitleScreenView titleView;
  private GameView gameView;
  private MediaPlayer music;

  @Override
  public void start(Stage stage) {

    model = new ModelImpl(10, 10);
    controller = new ControllerImpl(model);

    // create views once and reuse them
    titleView = new TitleScreenView(model, controller, this);
    gameView = new GameView(model, controller, this);

    // register observers
    model.addObserver(titleView);
    model.addObserver(gameView);

    // start on title
    scene = new Scene(titleView.render(), 1000, 700);
    scene.getStylesheets().add("dungeon.css");

    stage.setTitle("Dungeon Crawler");
    stage.setScene(scene);
    stage.show();

    scene.getRoot().requestFocus();
    scene.setOnMouseClicked(e -> scene.getRoot().requestFocus());

    // background music
    playMusic("music.mp3");
  }

  private void playMusic(String file) {
    try {
      String path = "src/main/resources/" + file;
      Media media = new Media(new File(path).toURI().toString());
      music = new MediaPlayer(media);
      music.setCycleCount(MediaPlayer.INDEFINITE);
      music.setVolume(0.35);
      music.play();
    } catch (Exception e) {
      System.out.println("Music could not be loaded: " + e.getMessage());
    }
  }

  public Scene getScene() {
    return scene;
  }

  public void setView(View v) {
    scene.setRoot(v.render());
    scene.getRoot().requestFocus();
  }

  public TitleScreenView getTitleView() {
    return titleView;
  }

  public GameView getGameView() {
    return gameView;
  }
}
