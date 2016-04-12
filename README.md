8-ball
======

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

### iOS?

Testing
-------

For now tests are common for all platforms and located in the `core/` project folder.

Type `sbt core/test` in the project folder to run the tests.
