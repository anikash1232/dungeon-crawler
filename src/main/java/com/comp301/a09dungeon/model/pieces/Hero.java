package com.comp301.a09dungeon.model.pieces;

public class Hero extends APiece implements MovablePiece {

  // number of shields the hero currently has
  private int shields;

  public Hero() {
    super("Hero", "hero.png");
    this.shields = 0;
  }

  // --- Shield API ---

  public int getShields() {
    return shields;
  }

  public void addShield() {
    shields++;
  }

  @Override
  public CollisionResult collide(Piece other) {
    if (other == null) {
      return new CollisionResult(0, CollisionResult.Result.CONTINUE);
    }

    // pick up treasure – same as before
    if (other instanceof Treasure t) {
      return new CollisionResult(t.getValue(), CollisionResult.Result.CONTINUE);
    }

    // go to next level – same as before
    if (other instanceof Exit) {
      return new CollisionResult(0, CollisionResult.Result.NEXT_LEVEL);
    }

    // NEW: pick up a shield
    if (other instanceof Shield) {
      addShield();
      return new CollisionResult(0, CollisionResult.Result.CONTINUE);
    }

    // NEW: collide with enemy – consume shield if we have one
    if (other instanceof Enemy) {
      if (shields > 0) {
        shields--; // shield breaks, but you live
        return new CollisionResult(0, CollisionResult.Result.CONTINUE);
      } else {
        return new CollisionResult(0, CollisionResult.Result.GAME_OVER);
      }
    }

    if (other instanceof Wall) {
      throw new IllegalArgumentException();
    }
    throw new IllegalArgumentException();
  }
  public void consumeShield() {
    if (shields > 0) {
      shields--;
    }
  }

}