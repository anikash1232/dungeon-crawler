package com.comp301.a09dungeon.model.board;

import static com.comp301.a09dungeon.model.pieces.CollisionResult.Result.CONTINUE;

import com.comp301.a09dungeon.model.pieces.*;
import com.comp301.a09dungeon.model.pieces.CollisionResult.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardImpl implements Board {
  private final Random rand = new Random();
  private final int width;
  private final int height;
  private final Piece[][] board;

  public BoardImpl(int width, int height) {
    this.width = width;
    this.height = height;
    this.board = new Piece[height][width];
  }

  public BoardImpl(Piece[][] pieces) {
    this.height = pieces.length;
    this.width = pieces[0].length;
    this.board = pieces;
  }

  @Override
  public void init(int enemies, int treasures, int walls) {
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        board[r][c] = null;
      }
    }

    int total = enemies + treasures + walls + 2; // hero + exit
    if (total > width * height) {
      throw new IllegalArgumentException("Too many pieces");
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
        board[r][c] = p;
        p.setPosn(new Posn(r, c));
        return;
      }
    }
  }

  private Hero findHero() {
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        if (board[r][c] instanceof Hero) {
          return (Hero) board[r][c];
        }
      }
    }
    throw new IllegalStateException("Hero not found");
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
  public Piece get(Posn pos) {
    return board[pos.getRow()][pos.getCol()];
  }

  @Override
  public void set(Piece p, Posn newPos) {
    board[newPos.getRow()][newPos.getCol()] = p;
  }

  @Override
  public CollisionResult moveHero(int drow, int dcol) {
    Hero hero = findHero();
    Posn pos = hero.getPosn();

    int nr = pos.getRow() + drow;
    int nc = pos.getCol() + dcol;

    if (!isBounds(nr, nc) || board[nr][nc] instanceof Wall) {
      return new CollisionResult(0, CONTINUE);
    }

    int points = 0;
    Result heroResult = CONTINUE;

    Piece target = board[nr][nc];

    if (target != null) {
      CollisionResult cr = hero.collide(target);
      points += cr.getPoints();
      heroResult = cr.getResults();

      if (target instanceof Treasure || target instanceof Enemy || target instanceof Exit) {
        board[nr][nc] = null;
      }
    }

    movePiece(hero, nr, nc);

    if (heroResult == Result.NEXT_LEVEL) {
      return new CollisionResult(points, Result.NEXT_LEVEL);
    }

    if (heroResult == Result.GAME_OVER) {
      return new CollisionResult(points, Result.GAME_OVER);
    }

    if (moveAllEnemies() == Result.GAME_OVER) {
      return new CollisionResult(points, Result.GAME_OVER);
    }

    return new CollisionResult(points, CONTINUE);
  }

  private Result moveAllEnemies() {
    for (Enemy e : allEnemies()) {
      Result r = moveEnemyOnce(e);
      if (r == Result.GAME_OVER) {
        return Result.GAME_OVER;
      }
    }
    return CONTINUE;
  }

  private Result moveEnemyOnce(Enemy e) {
    int r = e.getPosn().getRow();
    int c = e.getPosn().getCol();

    int[] dr = {1, -1, 0, 0};
    int[] dc = {0, 0, 1, -1};
    List<int[]> options = new ArrayList<>();

    for (int i = 0; i < 4; i++) {
      int nr = r + dr[i];
      int nc = c + dc[i];

      if (!isBounds(nr, nc)) continue;
      Piece p = board[nr][nc];

      if (p instanceof Wall) continue;
      if (p instanceof Exit) continue;
      if (p instanceof Enemy) continue;

      options.add(new int[] {nr, nc});
    }

    if (options.isEmpty()) return CONTINUE;

    int[] move = options.get(rand.nextInt(options.size()));
    int nr = move[0];
    int nc = move[1];

    Piece target = board[nr][nc];

    if (target instanceof Hero) {
      return Result.GAME_OVER;
    }
    if (target instanceof Treasure) {
      board[nr][nc] = null;
    }

    movePiece(e, nr, nc);
    return CONTINUE;
  }

  private List<Enemy> allEnemies() {
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

  private void movePiece(Piece p, int nr, int nc) {
    Posn old = p.getPosn();
    board[old.getRow()][old.getCol()] = null;
    board[nr][nc] = p;
    p.setPosn(new Posn(nr, nc));
  }

  private boolean isBounds(int r, int c) {
    return r >= 0 && r < height && c >= 0 && c < width;
  }
}
