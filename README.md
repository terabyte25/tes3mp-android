# tes3mp for Android


## Notes from fork maintainer
This is forked from xyzz/openmw-android, and implements support for tes3mp. All credits go to the tes3mp team and OpenMW team for making this possible, as well as Schnibbsel for helping me test it. If there are any questions please feel free to ask in the issues tab. Also feel free to report any abnormalities in the Issues tab as well.

**THIS IS NOT THE WORK OF ANY OFFICIAL OPENMW OR TES3MP MEMBERS**

### Setup guide
You must first install one of the packages (obviously). Then you will have to somehow get the data files for which you want to run, presumably vanilla Morrowind, in which case you will transfer the files over to your device. After transferring these files, you must open the application which was just installed, and in the 'Path to game data files' field, you will have to input the exact path of the data files which you just transfered to your device. You can get the exact path from mostly any file browser app. After this, you must enable the `Multiplayer` option which is in the menu. After toggling this, you can join any server from 2 different methods. 
The first, and most useful, is the browser menu, which you can open in the menu navigation bar, in the top left. In order to refresh the browser, you must pull down the tab. To sort through the server entries, you can press on the settings tool bar on the top right, which presents 2 options, which are to sort by number of players, and sort alphabetically.
The second method is used to connect to servers which are not displayed on the browser, or in the event that the master server is down. You must first enter to the settings menu, and press on the `Custom command line arguments` field. Then, you must add the following field(s): `--connect` followed by a space and `[ip address of server]:[port number of server]`, where the fields are replaced by their respecitive values. In the case that there is a required password to said server, use the `--password` field, followed by a spacebar and the password. 

If you want to go back to Singleplayer, simply untoggle the Multiplayer setting in the settings menu, and you can just start the game into Singleplayer by pressing the play icon.

## Building

There are two steps for building tes3mp for Android. The first step is building C/C++ libraries. The second step is building the Java launcher.

### Prerequisites

You will need some standard tools installed that you probably already have (bash, gcc, g++, sha256sum, unzip).

CMake 3.6.0 or newer is **required**, you can download the latest version [here](https://cmake.org/download/) (and place in your `PATH`) if your distro ships with an outdated version.

Additionally, to build the launcher you will need Android SDK installed, it is suggested that you use Android Studio which can set it up for you (see step 2).

### Step 1: Build the libraries

Go into the `buildscripts` directory and run `./build.sh`. The script will automatically download the Android native toolchain and all dependencies, and will compile and install them.

### Step 2: Build the Java launcher

To get an APK file you can install, open the `android-port` directory in Android Studio and run the project.

Alternatively, if you do not have Android Studio installed or would rather not use it, run `./gradlew assembleDebug` from the root directory of this repository. The resulting APK, located at `./app/build/outputs/apk/debug/app-debug.apk`, can be transferred to the device and installed.

## Notes for developers

### Debugging native code

You can debug native code with `ndk-gdb`. To use it, once you've built both libraries and the apk and installed the apk, run the application and let it stay on the main menu. Then `cd` to `app/src/main` and run `./gdb.sh [arch]`. The `arch` variable has to match the library your device will be using (one of `arm`, `arm64`, `x86_64`, `x86`; `arm` is the default).

This also automatically enables gdb to use unstripped libraries, so you get proper symbols, source code references, etc.

### Running Address Sanitizer

To compile everything with ASAN:

```
# Clean previous build
./clean.sh
# Build with ASAN enabled & debug symbols
./build.sh --ccache --asan --debug
# Or: ./build.sh --ccache --asan --debug --arch arm64
```

Then open Android Studio and compile and install the project.

To get symbolized output:

```
adb logcat | ./tool/asan_symbolize.py --demangle -s ./build/arm/symbols/
# Or: adb logcat | ./tool/asan_symbolize.py --demangle -s ./build/arm64/symbols/
```

## Credits

Original Java code written by sandstranger. Build scripts originally written by sandstranger and bwhaines.
