package com.comp301.a09dungeon.view;

import com.comp301.a09dungeon.model.Observer;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class View implements FXComponent, Observer {

  public View() {}

  public Parent render() {
    Pane s = new StackPane();
    s.getChildren().add(new Label("Hello, World"));
    return s;
  }

  @Override
  public void update() {}
}
