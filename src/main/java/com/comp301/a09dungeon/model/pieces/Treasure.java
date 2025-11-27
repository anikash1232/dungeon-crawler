package com.comp301.a09dungeon.model.pieces;

public class Treasure extends APiece {

  private final int value;

  public Treasure() {
    super("Treasure", "treasure.png");
    this.value = 50;
  }

  public int getValue() {
    return value;
  }
}
