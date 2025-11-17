package com.comp301.a09dungeon.model.board;

import com.comp301.a09dungeon.model.pieces.CollisionResult;
import com.comp301.a09dungeon.model.pieces.Piece;

public class BoardImpl implements Board {
  @Override
  public void init(int enemies, int treasures, int walls) {}

  @Override
  public int getWidth() {
    return 0;
  }

  @Override
  public int getHeight() {
    return 0;
  }

  @Override
  public Piece get(Posn posn) {
    return null;
  }

  @Override
  public void set(Piece p, Posn newPos) {}

  @Override
  public CollisionResult moveHero(int drow, int dcol) {
    return null;
  }
}
