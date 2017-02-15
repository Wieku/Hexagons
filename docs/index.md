# Hexagons!

Hexagons! is a Java-based clone of OpenHexagon, which itself is heavily
inspired by the SuperHexagon game by Terry Cavanagh. Some ideas are taken
from OSU!

Main focus of the game is to bring the community of hexagon-based together
and help it grow.

## Early beta

The game is now in early beta stage. This means that you'll see many bugs
and missing features. All feedback is more than welcome.

## Running the game

To run the game, follow these steps:

* Download launcher from https://hexagons.xyz/download
* Get maps from https://hexagons.xyz/maps
* Create new directory for the game, move launcher jar there.
* Start the Launcher, let it download and start the game.
* In game, while in main menu, press L to login using Google account or K to login using Steam account
* Close the game, you should see it created `Maps` and `Data` directories where it was ran
* Extract maps to the `Maps` directory
* Launch the game & Enjoy!
 
## Known problems

 * `Maps` directory is not created:
   * If you are on Windows, those directories can get created in `C:\Users\YourUsername`
   * If you are on Linux, try running it using terminal in that directory.
     * This is caused by most DEs running apps in user home directory bu default
 * Game doesn't launch
   * Windows
     * Make sure you have correct java version installed
       * 64bit java for 64bit windows
       * 32bit java for 32bit windows
     * Try running it in CMD
       * Shift-RightClick in directory the launcher is in
       * Click `Open command prompt here`
       * Type `java -jar HexagonsLauncher.jar`
       * Try `javaw` if `java` didn't work
       * If the game is not launching look at logs.
       * It it says something about gdx.dll or lwjgl.dll try removing %TEMP%/libgdx* directory
       * Retry running the game.
       * If still broken, ensure that you have only 1 java version installed and that it's fairly recent Java 8
       * Uninstall all java versions, install 1 from official website.
   * Linux
     * Try running in terminal

If those steps didn't help contact us for help.
     
       