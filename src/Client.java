import java.io.InputStreamReader;
import java.util.Random;
import java.util.Scanner;

import controller.DungeonConsoleController;
import controller.DungeonController;
import controller.DungeonControllerWView;
import controller.DungeonSwingController;
import dungeongame.Dungeon;
import dungeongame.DungeonImpl;
import dungeongame.Player;
import dungeongame.PlayerImpl;
import view.DungeonView;
import view.DungeonViewImpl;

/**
 * A basic menu-driven controller that creates a random dungeon and
 * a player and allows the user to player a game by passing inputs.
 */
public class Client {
  private static void printUsage() {
    System.out.println("Invalid arguments passed!");
    System.out.println("Usage: java -jar pdp-project-3.jar <numRows> <numCols> <interconnectivity>"
            + "<Wrapping(W)/Non-wrapping(N)> <percentCavesWithTreasure> <force interconnectivity "
            + "(Y/N)> <numOtyughs>");
  }

  private static int parseIntArg(String argName, String[] args, int idx) {
    int val;
    try {
      val = Integer.parseInt(args[idx]);
    } catch (NumberFormatException e) {
      System.out.println("Expected integer input for " + argName + "!");
      printUsage();
      return -1;
    } catch (ArrayIndexOutOfBoundsException e) {
      printUsage();
      return -1;
    }

    return val;
  }

  private static int parseBooleanArg(String argName, String[] args, int idx,
                                     String trueVal, String falseVal) {
    String parsedVal;
    try {
      parsedVal = args[idx];
    } catch (ArrayIndexOutOfBoundsException e) {
      printUsage();
      return -1;
    }

    if (parsedVal.equalsIgnoreCase(trueVal)) {
      return 1;
    } else if (parsedVal.equalsIgnoreCase(falseVal)) {
      return 0;
    } else {
      System.out.println("Invalid option: " + parsedVal + ", for " + argName + "!");
      printUsage();
      return -1;
    }
  }

  /**
   * The main method that creates models and passes the control to the controller,
   * if there are command line args then it starts the CLI game, otherwise
   * it starts the GUI game.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    Random random = new Random();

    if (args.length > 0) {
      int numRows = parseIntArg("number of rows", args, 0);
      if (numRows == -1) {
        return;
      }

      int numCols = parseIntArg("number of columns", args, 1);
      if (numCols == -1) {
        return;
      }

      int interconnectivity = parseIntArg("interconnectivity", args, 2);
      if (interconnectivity == -1) {
        return;
      }

      boolean isWrapping = false;
      int isWrappingInt = parseBooleanArg("wrapping/non-wrapping",
              args, 3, "W", "N");
      if (isWrappingInt == 1) {
        isWrapping = true;
      }

      float percentCavesWithTreasureArrows;
      try {
        percentCavesWithTreasureArrows = Float.parseFloat(args[4]) / 100;
      } catch (NumberFormatException e) {
        System.out.println("Floating point number expected for percentage "
                + "of caves with treasures!");
        printUsage();
        return;
      } catch (ArrayIndexOutOfBoundsException e) {
        printUsage();
        return;
      }

      int forceInterconnectivityRangeInt = parseBooleanArg("force interconnectivity",
              args, 5, "Y", "N");
      boolean forceInterconnectivityRange = false;
      if (forceInterconnectivityRangeInt == 1) {
        forceInterconnectivityRange = true;
      }

      int numOtyughs = parseIntArg("number of Otyughs", args, 6);
      if (numOtyughs == -1) {
        return;
      }

      Dungeon dungeon;
      try {
        dungeon = new DungeonImpl(numRows, numCols, interconnectivity,
                isWrapping, percentCavesWithTreasureArrows, forceInterconnectivityRange,
                numOtyughs, random);
      } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
        return;
      }

      Scanner scanner = new Scanner(System.in);

      System.out.println("Enter player name: ");
      String playerName = scanner.next();
      Player player = new PlayerImpl(playerName);

      Readable r = new InputStreamReader(System.in);
      Appendable a = System.out;
      DungeonController controller = new DungeonConsoleController(r, a, random);
      controller.playGame(dungeon, player);
    } else {
      String playerName = "Player";
      Player player = new PlayerImpl(playerName);

      Dungeon dungeon = new DungeonImpl(4, 4, 2,
              false, 0.25f,
              false, 2, random);
      player.enterPlayerToDungeon(dungeon);

      DungeonView view = new DungeonViewImpl();

      DungeonControllerWView controller = new DungeonSwingController(dungeon, player, view, random);
      view.addListener(controller);
      controller.playGuiGame();
    }
  }
}
