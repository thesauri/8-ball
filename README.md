8-ball
======

Nice description here

Structure
---------

The project is separated into three sub-projects: `core`, `desktop`, and `android`.

All game-specific code that is shared across all platforms is stored in core `core` project.
The `desktop` and `android` projects contain platform-specific launchers for starting the game on the different platforms.

All three projects are Eclipse projects and can be imported directly. However, compilation must be done from the terminal using `sbt` (see the compilation section below).

Compiling
---------

### Desktop

1. Run `sbt` in the project folder to set up the project locally
2. Run `> desktop/run` to start the game (or `sbt desktop/run` from the project folder)

### Android

### iOS?
