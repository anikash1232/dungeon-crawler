package com.comp301.a09dungeon.model.board;

import com.comp301.a09dungeon.model.pieces.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardImpl implements Board {

  private final int width;
  private final int height;
  private final Piece[][] board;
  private final Random rand = new Random();
  private Posn heroPosn;

  public BoardImpl(int width, int height) {
    this.width = width;
    this.height = height;
    this.board = new Piece[height][width];
    this.heroPosn = null;
  }

  public BoardImpl(Piece[][] initialBoard) {
    this.height = initialBoard.length;
    this.width = initialBoard[0].length;
    this.board = initialBoard;
    this.heroPosn = null;
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        if (board[r][c] instanceof Hero) {
          heroPosn = new Posn(r, c);
        }
      }
    }
  }

  @Override
  public void init(int enemies, int treasures, int walls) {
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        board[r][c] = null;
      }
    }

    int needed = enemies + treasures + walls + 2;
    if (needed > width * height) {
      throw new IllegalArgumentException();
    }

    randomlyPlace(new Hero());
    randomlyPlace(new Exit());

    for (int i = 0; i < enemies; i++) {
      randomlyPlace(new Enemy());
    }
    for (int i = 0; i < treasures; i++) {
      randomlyPlace(new Treasure());
    }
    for (int i = 0; i < walls; i++) {
      randomlyPlace(new Wall());
    }
  }

  private void randomlyPlace(Piece p) {
    while (true) {
      int r = rand.nextInt(height);
      int c = rand.nextInt(width);
      if (board[r][c] == null) {
        Posn pos = new Posn(r, c);
        board[r][c] = p;
        p.setPosn(pos);
        if (p instanceof Hero) {
          heroPosn = pos;
        }
        return;
      }
    }
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public Piece get(Posn posn) {
    int r = posn.getRow();
    int c = posn.getCol();
    if (r < 0 || r >= height || c < 0 || c >= width) {
      return null;
    }
    return board[r][c];
  }

  @Override
  public void set(Piece p, Posn newPos) {
    int r = newPos.getRow();
    int c = newPos.getCol();
    board[r][c] = p;
    if (p != null) {
      p.setPosn(newPos);
      if (p instanceof Hero) {
        heroPosn = newPos;
      }
    }
  }

  @Override
  public CollisionResult moveHero(int drow, int dcol) {
    int r = heroPosn.getRow();
    int c = heroPosn.getCol();
    int nr = r + drow;
    int nc = c + dcol;

    // bounds or wall = no move
    if (!inBounds(nr, nc) || board[nr][nc] instanceof Wall) {
      return new CollisionResult(0, CollisionResult.Result.CONTINUE);
    }

    Piece target = board[nr][nc];

    Hero hero = (Hero) board[r][c];
    CollisionResult heroCR = hero.collide(target);

    // only remove AFTER collision logic
    if (target instanceof Treasure || target instanceof Enemy) {
      board[nr][nc] = null;
    }

    if (heroCR.getResults() == CollisionResult.Result.GAME_OVER) {
      return heroCR;
    }

    // move hero to new location
    board[r][c] = null;
    board[nr][nc] = hero;
    hero.setPosn(new Posn(nr, nc));
    heroPosn = new Posn(nr, nc);

    if (heroCR.getResults() == CollisionResult.Result.NEXT_LEVEL) {
      return heroCR;
    }

    // move enemies AFTER hero moves
    List<Enemy> enemies = getEnemies();
    for (Enemy e : enemies) {
      CollisionResult er = moveEnemy(e);
      if (er.getResults() == CollisionResult.Result.GAME_OVER) {
        return er;
      }
    }

    return heroCR;
  }

  private List<Enemy> getEnemies() {
    List<Enemy> list = new ArrayList<>();
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        if (board[r][c] instanceof Enemy) {
          list.add((Enemy) board[r][c]);
        }
      }
    }
    return list;
  }

  private CollisionResult moveEnemy(Enemy e) {
    int r = e.getPosn().getRow();
    int c = e.getPosn().getCol();
    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
    List<int[]> options = new ArrayList<>();

    for (int[] d : dirs) {
      int nr = r + d[0];
      int nc = c + d[1];
      if (!inBounds(nr, nc)) continue;
      Piece t = board[nr][nc];
      if (t instanceof Wall) continue;
      if (t instanceof Exit) continue;
      if (t instanceof Enemy) continue;
      options.add(new int[]{nr, nc});
    }

    if (options.isEmpty()) {
      return new CollisionResult(0, CollisionResult.Result.CONTINUE);
    }

    int[] move = options.get(rand.nextInt(options.size()));
    int nr = move[0];
    int nc = move[1];
    Piece target = board[nr][nc];

    if (target instanceof Hero) {
      return new CollisionResult(0, CollisionResult.Result.GAME_OVER);
    }

    if (target instanceof Treasure) {
      board[nr][nc] = null;
    }

    board[r][c] = null;
    board[nr][nc] = e;
    e.setPosn(new Posn(nr, nc));

    return new CollisionResult(0, CollisionResult.Result.CONTINUE);
  }

  private boolean inBounds(int r, int c) {
    return r >= 0 && r < height && c >= 0 && c < width;
  }
}