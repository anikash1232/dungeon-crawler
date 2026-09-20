# Dungeon Crawler

A turn-based dungeon crawler in Java with a JavaFX interface, built on a
model-view-controller architecture with observer-driven rendering.

## The game

You descend through procedurally generated dungeon levels, collecting treasure and
avoiding enemies that hunt you each turn. Reach the exit to advance; the dungeon regenerates
with a fresh layout every level. Score carries across levels and a high score persists for
the session.

Shields are a defensive pickup: carrying one lets you survive a single enemy collision,
consuming the shield instead of ending the run. Without one, contact with an enemy ends the
game.

- Procedurally generated levels — enemies, treasure, walls and the exit are placed randomly
  on each new board
- Enemies move once per player turn, pursuing the hero
- Collectible treasure with per-item scoring, and shields as consumable protection
- Wall collision detection, bounds checking, and level progression through the exit tile
- Title screen and in-game views, with score and level displayed live

## Architecture

```
model/
  Model, ModelImpl          game state, scoring, level progression
  Subject, Observer         observer pattern - views subscribe to model changes
  board/
    Board, BoardImpl        2D grid, procedural generation, movement resolution
    Posn                    immutable grid coordinate
  pieces/
    Piece, APiece           piece interface and shared abstract base
    MovablePiece            movement contract
    Hero, Enemy             actors, each with its own collision behaviour
    Treasure, Shield        collectibles
    Wall, Exit              terrain
    CollisionResult         score delta plus an outcome enum
view/
  View, GameView            JavaFX rendering, observes the model
  TitleScreenView           menu and game start
  FXComponent, AppLauncher  component contract and entry point
controller/
  Controller, ControllerImpl  input handling, drives the model
```

Collision resolution is the core of the design. Rather than branching on piece types from
the outside, every piece answers `collide(Piece other)` for itself and returns a
`CollisionResult` carrying a score delta and an outcome — `CONTINUE`, `NEXT_LEVEL`, or
`GAME_OVER`. The board applies the result without knowing what kind of piece produced it,
so adding a new pickup means adding one class, not editing a switch.

The model knows nothing about JavaFX. Views register as observers and re-render when
notified, which keeps game logic testable independently of the interface.

## Running it

Requires Java 17+ and Maven.

```bash
mvn clean javafx:run
```

## Notes

Built for COMP 301 at UNC-Chapel Hill. The project skeleton — interfaces, piece stubs and
the JavaFX harness — was course-provided scaffolding; the implementation is mine, about
1,100 lines across the view layer, board logic, model and piece behaviours. The shield
mechanic is an extension beyond the base requirements.
