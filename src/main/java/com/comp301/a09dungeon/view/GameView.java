package com.comp301.a09dungeon.view;

import com.comp301.a09dungeon.model.Model;
import com.comp301.a09dungeon.controller.Controller;
import com.comp301.a09dungeon.model.board.Posn;
import com.comp301.a09dungeon.model.pieces.*;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class GameView extends View {

    private Timeline timer;
    private int timeElapsed = 0;

    private Label[][] tileGrid;
    private BorderPane rootPane; // used for whole-board shake

    public GameView(Model model, Controller controller, AppLauncher launcher) {
        super(model, controller, launcher);
    }

    @Override
    public Parent render() {

        BorderPane root = new BorderPane();
        rootPane = root; // save reference so we can shake the whole board
        root.setPadding(new Insets(20));
        root.setFocusTraversable(true);
        root.requestFocus();

        // SCORE
        Label score = new Label("Score: " + model.getCurScore());
        score.getStyleClass().add("label");

        // TIMER
        Label timerLabel = new Label("Time: 00:00");
        timerLabel.getStyleClass().add("label");

        HBox topBar = new HBox(40, score, timerLabel);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(10, 0, 20, 0));
        root.setTop(topBar);

        // BOARD GRID
        GridPane grid = new GridPane();
        grid.getStyleClass().add("board-grid");

        int h = model.getHeight();
        int w = model.getWidth();
        tileGrid = new Label[h][w];

        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                Label tile = new Label();
                tile.getStyleClass().add("tile");
                tile.setFocusTraversable(false);
                tile.setMouseTransparent(true);

                Piece p = model.get(new Posn(r, c));
                tile.setText(getEmoji(p));

                tileGrid[r][c] = tile;
                grid.add(tile, c, r);
            }
        }

        HBox gridHolder = new HBox(grid);
        gridHolder.setAlignment(Pos.CENTER);
        root.setCenter(gridHolder);

        // MOVEMENT BUTTONS
        Button up = new Button("↑");
        Button down = new Button("↓");
        Button left = new Button("←");
        Button right = new Button("→");

        up.getStyleClass().add("button");
        down.getStyleClass().add("button");
        left.getStyleClass().add("button");
        right.getStyleClass().add("button");

        up.setOnAction(e -> handleMove(-1, 0, () -> controller.moveUp()));
        down.setOnAction(e -> handleMove(1, 0, () -> controller.moveDown()));
        left.setOnAction(e -> handleMove(0, -1, () -> controller.moveLeft()));
        right.setOnAction(e -> handleMove(0, 1, () -> controller.moveRight()));

        VBox vertical = new VBox(15, up, down);
        vertical.setAlignment(Pos.CENTER);

        HBox horizontal = new HBox(15, left, right);
        horizontal.setAlignment(Pos.CENTER);

        VBox controls = new VBox(20, vertical, horizontal);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(0, 20, 0, 20));
        root.setRight(controls);

        // KEYBOARD CONTROLS
        root.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            switch (code) {
                case W:
                case UP:
                    handleMove(-1, 0, () -> controller.moveUp());
                    break;
                case S:
                case DOWN:
                    handleMove(1, 0, () -> controller.moveDown());
                    break;
                case A:
                case LEFT:
                    handleMove(0, -1, () -> controller.moveLeft());
                    break;
                case D:
                case RIGHT:
                    handleMove(0, 1, () -> controller.moveRight());
                    break;
            }
        });

        // START TIMER
        startTimer(timerLabel);

        return root;
    }

    // Map model piece -> emoji
    private String getEmoji(Piece p) {
        if (p instanceof Hero) return "🧙";
        if (p instanceof Enemy) return "👾";
        if (p instanceof Treasure) return "💎";
        if (p instanceof Exit) return "🚪";
        if (p instanceof Wall) return "🧱";
        return "";
    }

    /**
     * Core move handler:
     *  - looks at target tile BEFORE move
     *  - moves immediately
     *  - fade on hero move
     *  - pop + flash on treasure
     *
     *  Enemy shake is now handled in update() when END_GAME happens,
     *  so we don't special-case enemy here.
     */
    private void handleMove(int dRow, int dCol, Runnable moveAction) {
        Posn before = findHeroPos();
        if (before == null) return;

        int targetRow = before.getRow() + dRow;
        int targetCol = before.getCol() + dCol;

        Piece targetPiece = null;
        boolean inBounds =
                targetRow >= 0
                        && targetRow < model.getHeight()
                        && targetCol >= 0
                        && targetCol < model.getWidth();

        if (inBounds) {
            targetPiece = model.get(new Posn(targetRow, targetCol));
        }

        // move in the model (hero move + enemy moves + collision logic)
        moveAction.run();

        Posn after = findHeroPos();
        if (after == null) return;

        // keep board text in sync with model
        refreshBoardEmojis();

        // QUICK FADE when hero successfully moves (keep as-is)
        if (!before.equals(after)) {
            Label heroTile = tileGrid[after.getRow()][after.getCol()];
            playFade(heroTile);
        }

        // TREASURE EFFECT (keep as-is)
        if (inBounds && targetPiece instanceof Treasure) {
            Label treasureTile = tileGrid[targetRow][targetCol];

            // pop
            playPop(treasureTile);

            // flash gold slightly after pop starts
            PauseTransition delay = new PauseTransition(Duration.millis(120));
            delay.setOnFinished(ev -> playGoldenFlash(treasureTile));
            delay.play();

            // clear emoji after animation
            PauseTransition removeDelay = new PauseTransition(Duration.millis(350));
            removeDelay.setOnFinished(ev -> treasureTile.setText(""));
            removeDelay.play();
        }
    }

    // Re-sync emojis with the model after each move
    private void refreshBoardEmojis() {
        int h = model.getHeight();
        int w = model.getWidth();
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                Piece p = model.get(new Posn(r, c));
                tileGrid[r][c].setText(getEmoji(p));
            }
        }
    }

    // Find hero coordinates in the model
    private Posn findHeroPos() {
        int h = model.getHeight();
        int w = model.getWidth();
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                if (model.get(new Posn(r, c)) instanceof Hero) {
                    return new Posn(r, c);
                }
            }
        }
        return null;
    }

    // ANIMATION: fade on hero move (unchanged)
    private void playFade(Label tile) {
        FadeTransition ft = new FadeTransition(Duration.millis(200), tile);
        ft.setFromValue(0.3);
        ft.setToValue(1.0);
        ft.play();
    }

    // ANIMATION: treasure pop (unchanged)
    private void playPop(Label tile) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), tile);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.4);
        st.setToY(1.4);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    // ANIMATION: golden flash (unchanged)
    private void playGoldenFlash(Label tile) {
        tile.setStyle("-fx-background-color: gold; -fx-background-radius: 6;");

        FadeTransition ft = new FadeTransition(Duration.millis(300), tile);
        ft.setFromValue(1.0);
        ft.setToValue(0.2);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);

        ft.setOnFinished(e -> tile.setStyle(""));
        ft.play();
    }

    // ANIMATION: shake WHOLE BOARD (used on END_GAME)
    private void playBoardShake() {
        if (rootPane == null) return;

        TranslateTransition tt = new TranslateTransition(Duration.millis(80), rootPane);
        tt.setFromX(-10);
        tt.setToX(10);
        tt.setAutoReverse(true);
        tt.setCycleCount(4);
        tt.play();
    }

    // TIMER
    private void startTimer(Label timerLabel) {
        if (timer != null) timer.stop();
        timeElapsed = 0;

        timer = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    timeElapsed++;
                    int m = timeElapsed / 60;
                    int s = timeElapsed % 60;
                    timerLabel.setText(String.format("Time: %02d:%02d", m, s));
                })
        );
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    @Override
    public void update() {
        if (model.getStatus() == Model.STATUS.IN_PROGRESS) {
            // normal board repaint when things change
            launcher.setView(this);
        } else if (model.getStatus() == Model.STATUS.END_GAME) {
            // enemy collision (or any game over) → shake + then go to title
            if (timer != null) timer.stop();

            playBoardShake();

            PauseTransition delay = new PauseTransition(Duration.millis(250));
            delay.setOnFinished(e -> launcher.setView(launcher.getTitleView()));
            delay.play();
        }
    }
}