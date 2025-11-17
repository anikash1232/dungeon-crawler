package com.comp301.a09dungeon.model.pieces;

import com.comp301.a09dungeon.model.board.Posn;

public interface Piece {
  String getName();

  Posn getPosn();

  void setPosn(Posn posn);

  String getResourcePath();
}
