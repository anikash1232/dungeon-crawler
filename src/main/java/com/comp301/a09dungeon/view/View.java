package com.comp301.a09dungeon.view;

import com.comp301.a09dungeon.model.Model;
import com.comp301.a09dungeon.model.Observer;
import com.comp301.a09dungeon.controller.Controller;
import javafx.scene.Parent;

public abstract class View implements FXComponent, Observer {

  protected final Model model;
  protected final Controller controller;
  protected final AppLauncher launcher;

  public View(Model model, Controller controller, AppLauncher launcher) {
    this.model = model;
    this.controller = controller;
    this.launcher = launcher;
  }

  @Override
  public abstract Parent render();

  @Override
  public abstract void update();
}
