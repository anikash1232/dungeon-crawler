package com.comp301.a09dungeon.view;

import com.comp301.a09dungeon.model.Model;
import com.comp301.a09dungeon.controller.Controller;
import com.comp301.a09dungeon.model.board.Posn;
import com.comp301.a09dungeon.model.pieces.*;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class GameView extends View {

    private Timeline timer;
    private int timeElapsed = 0;

    private Label[][] tileGrid;
    private BorderPane rootPane;

    private Label scoreLabel;
    private Label timerLabel;
    private Label shieldLabel;

    private boolean firstRender = true;

    public GameView(Model model, Controller controller, AppLauncher launcher) {
        super(model, controller, launcher);
    }

    @Override
    public Parent render() {

        BorderPane root = new BorderPane();
        rootPane = root;
        root.setPadding(new Insets(20));
        root.setFocusTraversable(true);
        root.requestFocus();

        // ─────────────────────────────────────────────
        // TOP BAR: Score | Time | Shields
        // ─────────────────────────────────────────────

        scoreLabel = new Label();
        scoreLabel.getStyleClass().add("label");

        timerLabel = new Label("Time: 00:00");
        timerLabel.getStyleClass().add("label");

        shieldLabel = new Label();
        shieldLabel.getStyleClass().add("label");

        scoreLabel.setStyle("-fx-font-size: 22px;");
        timerLabel.setStyle("-fx-font-size: 22px;");
        shieldLabel.setStyle("-fx-font-size: 22px;");


        HBox topBar = new HBox(40, scoreLabel, timerLabel, shieldLabel);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(10, 0, 20, 0));
        root.setTop(topBar);

        updateTopBarLabels();  // first fill of labels

        // ─────────────────────────────────────────────
        // BOARD GRID
        // ─────────────────────────────────────────────

        GridPane grid = new GridPane();
        tileGrid = new Label[model.getHeight()][model.getWidth()];
        grid.getStyleClass().add("board-grid");

        for (int r = 0; r < model.getHeight(); r++) {
            for (int c = 0; c < model.getWidth(); c++) {

                Label tile = new Label();
                tile.getStyleClass().add("tile");
                tile.setMouseTransparent(true);

                // ⭐ MAKE BOARD BIGGER ⭐
                tile.setPrefSize(60, 60);
                tile.setMinSize(60, 60);
                tile.setMaxSize(60, 60);

                tile.setAlignment(Pos.CENTER);

                tile.setStyle("-fx-font-size: 32px;");

                tile.setText(getEmoji(model.get(new Posn(r , c))));

                tileGrid[r][c] = tile;
                grid.add(tile, c, r);
            }
        }

        HBox gridHolder = new HBox(grid);
        gridHolder.setAlignment(Pos.CENTER);
        root.setCenter(gridHolder);

        // ─────────────────────────────────────────────
        // MOVEMENT BUTTONS
        // ─────────────────────────────────────────────

        Button up = new Button("↑");
        Button down = new Button("↓");
        Button left = new Button("←");
        Button right = new Button("→");

        up.setOnAction(e -> handleMove(-1, 0, () -> controller.moveUp()));
        down.setOnAction(e -> handleMove(1, 0, () -> controller.moveDown()));
        left.setOnAction(e -> handleMove(0, -1, () -> controller.moveLeft()));
        right.setOnAction(e -> handleMove(0, 1, () -> controller.moveRight()));

        // NEW keyboard-style layout
        HBox topRow = new HBox(up);
        topRow.setAlignment(Pos.CENTER);

        HBox bottomRow = new HBox(15, left, down, right);
        bottomRow.setAlignment(Pos.CENTER);

        VBox controls = new VBox(15, topRow, bottomRow);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(0, 20, 0, 20));

        root.setRight(controls);


        // ─────────────────────────────────────────────
        // KEYBOARD INPUT
        // ─────────────────────────────────────────────

        root.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W: case UP:    handleMove(-1, 0, () -> controller.moveUp()); break;
                case S: case DOWN:  handleMove(1, 0, () -> controller.moveDown()); break;
                case A: case LEFT:  handleMove(0, -1, () -> controller.moveLeft()); break;
                case D: case RIGHT: handleMove(0, 1, () -> controller.moveRight()); break;
            }
        });

        // ─────────────────────────────────────────────
        // BOTTOM MECHANIC LABEL
        // ─────────────────────────────────────────────

        Label mechanicLabel = new Label("🛡 Shield: Picking one up lets you survive one enemy hit! NOTE: Shields don't carry over to the next level!");
        mechanicLabel.getStyleClass().add("label");

        VBox bottomInfo = new VBox(10, mechanicLabel);
        bottomInfo.setAlignment(Pos.CENTER);
        root.setBottom(bottomInfo);

        // ─────────────────────────────────────────────
        // START TIMER ONLY ONCE
        // ─────────────────────────────────────────────

        if (timer == null) {
            startTimer();
        }

        return root;
    }


    // EMOJI MAP
    private String getEmoji(Piece p) {
        if (p instanceof Hero) return "🧙";
        if (p instanceof Enemy) return "👾";
        if (p instanceof Treasure) return "💎";
        if (p instanceof Exit) return "🚪";
        if (p instanceof Wall) return "🧱";
        if (p instanceof Shield) return "🛡";
        return "";
    }

    // HANDLE MOVE
    private void handleMove(int dRow, int dCol, Runnable moveAction) {
        Hero heroBefore = findHero();
        if (heroBefore == null) return;
        Posn before = heroBefore.getPosn();

        int targetRow = before.getRow() + dRow;
        int targetCol = before.getCol() + dCol;

        boolean inBounds = targetRow >= 0 &&
                targetRow < model.getHeight() &&
                targetCol >= 0 &&
                targetCol < model.getWidth();

        Piece targetPiece = null;
        if (inBounds) {
            targetPiece = model.get(new Posn(targetRow, targetCol));
        }

        moveAction.run();

        refreshBoardEmojis();
        updateTopBarLabels();

        Hero heroAfter = findHero();
        if (heroAfter == null) return;
        Posn after = heroAfter.getPosn();

        if (!before.equals(after)) {
            playFade(tileGrid[after.getRow()][after.getCol()]);
        }

        if (targetPiece instanceof Treasure && inBounds) {
            Label treasureTile = tileGrid[targetRow][targetCol];

            playPop(treasureTile);

            PauseTransition delay = new PauseTransition(Duration.millis(120));
            delay.setOnFinished(ev -> playGoldenFlash(treasureTile));
            delay.play();

            PauseTransition remove = new PauseTransition(Duration.millis(350));
            remove.setOnFinished(ev -> treasureTile.setText(""));
            remove.play();
        }
    }

    private void updateTopBarLabels() {
        scoreLabel.setText("Score: " + model.getCurScore());

        Hero hero = findHero();
        if (hero != null) {
            shieldLabel.setText("Shields: " + hero.getShields());
        }
    }

    private void refreshBoardEmojis() {
        for (int r = 0; r < model.getHeight(); r++) {
            for (int c = 0; c < model.getWidth(); c++) {
                tileGrid[r][c].setText(
                        getEmoji(model.get(new Posn(r, c)))
                );
            }
        }
    }

    private Hero findHero() {
        for (int r = 0; r < model.getHeight(); r++) {
            for (int c = 0; c < model.getWidth(); c++) {
                Piece p = model.get(new Posn(r, c));
                if (p instanceof Hero) return (Hero) p;
            }
        }
        return null;
    }

    private void playFade(Label tile) {
        FadeTransition ft = new FadeTransition(Duration.millis(200), tile);
        ft.setFromValue(0.3);
        ft.setToValue(1.0);
        ft.play();
    }

    private void playPop(Label tile) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), tile);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.4);
        st.setToY(1.4);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }

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

    private void playBoardShake() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(80), rootPane);
        tt.setFromX(-10);
        tt.setToX(10);
        tt.setAutoReverse(true);
        tt.setCycleCount(4);
        tt.play();
    }

    private void startTimer() {
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

            if (firstRender) {
                launcher.setView(this);
                firstRender = false;
            } else {
                refreshBoardEmojis();
                updateTopBarLabels();
            }

        } else if (model.getStatus() == Model.STATUS.END_GAME) {

            if (timer != null) timer.stop();
            playBoardShake();

            firstRender = true;
            timer = null;

            PauseTransition delay = new PauseTransition(Duration.millis(250));
            delay.setOnFinished(e -> launcher.setView(launcher.getTitleView()));
            delay.play();
        }
    }
}
