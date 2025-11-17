package com.comp301.a09dungeon.model;

import com.comp301.a09dungeon.model.board.Posn;
import com.comp301.a09dungeon.model.pieces.Piece;

public interface Model extends Subject {
  int getWidth();

  int getHeight();

  Piece get(Posn p);

  int getCurScore();

  int getHighScore();

  int getLevel();

  // Change status from IN_PROGRESS and END_GAME
  STATUS getStatus();

  void startGame();

  void endGame();

  void moveUp();

  void moveDown();

  void moveLeft();

  void moveRight();

  enum STATUS {
    END_GAME,
    IN_PROGRESS
  }
}
