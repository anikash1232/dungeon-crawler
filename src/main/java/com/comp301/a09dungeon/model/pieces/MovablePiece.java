package com.comp301.a09dungeon.model.pieces;

public interface MovablePiece extends Piece {
  CollisionResult collide(Piece other);
}
