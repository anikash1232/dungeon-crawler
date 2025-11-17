package com.comp301.a09dungeon.model.board;

import com.comp301.a09dungeon.model.pieces.CollisionResult;
import com.comp301.a09dungeon.model.pieces.Piece;

/** Defines the public operations on the game board. */
public interface Board {

  void init(int enemies, int treasures, int walls);

  int getWidth();

  int getHeight();

  Piece get(Posn posn);

  void set(Piece p, Posn newPos);

  CollisionResult moveHero(int drow, int dcol);
}
