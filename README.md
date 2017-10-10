8-ball
======

The goal of this project was to create a billiard game and implement the physics for it on my own.

To make a realistic simulation of a game of billiard, the angular velocity of the balls has to be taken into account. For instance, hitting the top of the cue ball will give it a forward spin causing it to continue forward after hitting other balls (instead of stopping). To allow for this kind of shots, the physics engine takes both the velocity and angular velocity into consideration as the balls roll over the board and collide with each other.

The game can be played by two players taking turns on the same device. The full set of rules specified by the World Pool-Billiard Association have not been implemented. Only the most essential rules, such as allowing the same player to continue shooting if one of the player’s balls is pocketed,are implemented.

This game uses the Java game framework [libgdx](https://libgdx.badlogicgames.com) for rendering. However, none if its physics and mathematics libraries were used in this project. These have been implemented on my own. The main reason why libgdx was chosen was its cross-platform support.

Games can be saved and returned to at a later point in time.

Structure
---------

The project is separated into three sub-projects: `core`, `desktop`, and `android`.

All game-specific code that is shared across all platforms is stored in core `core` project. The `desktop` and `android` projects contain platform-specific launchers for starting the game on the different platforms.

All three projects are Eclipse projects and can be imported directly. However, compilation must be done from the terminal using `sbt` (see the compilation section below).

Compiling
---------

### Desktop

1. Run `sbt` in the project folder to set up the project locally
2. Run `> desktop/run` to start the game (or `sbt desktop/run` from the project folder)

### Android

For compiling the Android project the Android SDK Tools, Platform-tools, Build-tools, and API version 19 have to be installed on the computer. Also make sure to point `ANDROID_HOME` to the installation directory in the system PATH, if not set by default.

1. Run `sbt android:package-debug` to build an Android package for debugging purposes (.apk file). The resulting package will be stored as `android/bin/android-debug.apk`.
2. Install the .APK using `adb install <package-file>` (Android Debug Bridge) or transfer the .APK to the device and install it locally using file manager.

Testing
-------

For now tests are common for all platforms and located in the `core/` project folder.

Type `sbt core/test` in the project folder to run the tests.
