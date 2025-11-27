package com.comp301.a09dungeon.model;

import com.comp301.a09dungeon.model.board.Board;
import com.comp301.a09dungeon.model.board.BoardImpl;
import com.comp301.a09dungeon.model.board.Posn;
import com.comp301.a09dungeon.model.pieces.CollisionResult;
import com.comp301.a09dungeon.model.pieces.Piece;
import java.util.ArrayList;
import java.util.List;

public class ModelImpl implements Model {

    private Board board;
    private int curScore;
    private int highScore;
    private int level;
    private STATUS status;
    private final List<Observer> observers;

    public ModelImpl(int width, int height) {
        this.board = new BoardImpl(width, height);
        this.curScore = 0;
        this.highScore = 0;
        this.level = 0;
        this.status = STATUS.END_GAME;
        this.observers = new ArrayList<>();
    }

    public ModelImpl(Board b) {
        this.board = b;
        this.curScore = 0;
        this.highScore = 0;
        this.level = 0;
        this.status = STATUS.END_GAME;
        this.observers = new ArrayList<>();
    }

    @Override
    public int getWidth() {
        return board.getWidth();
    }

    @Override
    public int getHeight() {
        return board.getHeight();
    }

    @Override
    public Piece get(Posn p) {
        return board.get(p);
    }

    @Override
    public int getCurScore() {
        return curScore;
    }

    @Override
    public int getHighScore() {
        return highScore;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public STATUS getStatus() {
        return status;
    }

    @Override
    public void startGame() {
        curScore = 0;
        level = 1;
        status = STATUS.IN_PROGRESS;
        board = new BoardImpl(board.getWidth(), board.getHeight());
        board.init(level + 1, 2, 2);
        notifyObservers();
    }

    @Override
    public void endGame() {
        status = STATUS.END_GAME;
        if (curScore > highScore) {
            highScore = curScore;
        }
        notifyObservers();
    }

    private void handleResult(CollisionResult cr) {
        curScore += cr.getPoints();
        if (cr.getResults() == CollisionResult.Result.GAME_OVER) {
            endGame();
        }
        if (cr.getResults() == CollisionResult.Result.NEXT_LEVEL) {
            level++;
            board = new BoardImpl(board.getWidth(), board.getHeight());
            board.init(level + 1, 2, 2);
        }
    }

    @Override
    public void moveUp() {
        if (status == STATUS.END_GAME) return;
        CollisionResult cr = board.moveHero(-1, 0);
        handleResult(cr);
        notifyObservers();
    }

    @Override
    public void moveDown() {
        if (status == STATUS.END_GAME) return;
        CollisionResult cr = board.moveHero(1, 0);
        handleResult(cr);
        notifyObservers();
    }

    @Override
    public void moveLeft() {
        if (status == STATUS.END_GAME) return;
        CollisionResult cr = board.moveHero(0, -1);
        handleResult(cr);
        notifyObservers();
    }

    @Override
    public void moveRight() {
        if (status == STATUS.END_GAME) return;
        CollisionResult cr = board.moveHero(0, 1);
        handleResult(cr);
        notifyObservers();
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }


    public void notifyObservers() {
        for (Observer o : observers) {
            o.update();
        }
    }
}
