package com.comp301.a09dungeon.model.pieces;

public class Treasure extends APiece {

  private final int value;

  public Treasure(String name, String resourcePath) {
    super(name, resourcePath);
    this.value = 50;
  }

  public int getValue() {
    return value;
  }
}
