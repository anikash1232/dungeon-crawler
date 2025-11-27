package com.comp301.a09dungeon.model.pieces;

public class Hero extends APiece implements MovablePiece {

  public Hero() {
    super("Hero", "hero.png");
  }

  @Override
  public CollisionResult collide(Piece other) {
    if (other == null) {
      return new CollisionResult(0, CollisionResult.Result.CONTINUE);
    }
    if (other instanceof Treasure t) {
        return new CollisionResult(t.getValue(), CollisionResult.Result.CONTINUE);
    }
    if (other instanceof Exit) {
      return new CollisionResult(0, CollisionResult.Result.NEXT_LEVEL);
    }
    if (other instanceof Enemy) {
      return new CollisionResult(0, CollisionResult.Result.GAME_OVER);
    }
    if (other instanceof Wall) {
      throw new IllegalArgumentException();
    }
    throw new IllegalArgumentException();
  }
}
