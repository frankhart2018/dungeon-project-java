package controller;

import dungeongame.Dungeon;
import dungeongame.Player;
import utils.ValueSanity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/**
 * Implementation of a dungeon controller which interacts with the user via the
 * console.
 */
public class DungeonConsoleController implements DungeonController {
  private final Scanner scanner;
  private final Appendable out;

  /**
   * Constructs an instance of the dungeon console controller.
   *
   * @param in     the input stream to read from
   * @param out    the output stream to write to
   * @param random the random number generator to use
   * @throws IllegalArgumentException if in is null, or out is null, or random
   *                                  is null
   */
  public DungeonConsoleController(Readable in, Appendable out, Random random) {
    performChecks(in, out, random);

    this.scanner = new Scanner(in);
    this.out = out;
  }

  private void performChecks(Readable in, Appendable out, Random random) {
    ValueSanity.checkNull("Readable instance", in);
    ValueSanity.checkNull("Appendable instance", out);
    ValueSanity.checkNull("Random instance", random);
  }

  private void appendWithException(String... s) {
    try {
      for (String string : s) {
        this.out.append(string);
      }
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to append!");
    }
  }

  /**
   * Starts the dungeon game with a dungeon instance and a player which actually plays
   * the game.
   *
   * @param dungeon the dungeon instance
   * @param player  the player instance
   */
  @Override
  public void playGame(Dungeon dungeon, Player player) {
    appendWithException("\n"
            +
            "█████████████████████████████████████████████████████████████████████████████████████"
            + "█████████████████████████████████████\n"
            + "█░░░░░░░░░░░░███░░░░░░██░░░░░░█░░░░░░██████████░░░░░░█░░░░░░░░░░░░░░█"
            + "░░░░░░░░░░░░░░█░░░░░░░░░░░░░░█░░░░░░██████████░░░░░░█\n"
            + "█░░▄▀▄▀▄▀▄▀░░░░█░░▄▀░░██░░▄▀░░█░░▄▀░░░░░░░░░░██░░▄▀░░█░░▄▀▄▀▄▀▄▀▄▀░░█░░▄▀▄▀▄▀▄▀▄▀░"
            + "░█░░▄▀▄▀▄▀▄▀▄▀░░█░░▄▀░░░░░░░░░░██░░▄▀░░█\n"
            + "█░░▄▀░░░░▄▀▄▀░░█░░▄▀░░██░░▄▀░░█░░▄▀▄▀▄▀▄▀▄▀░░██░░▄▀░░█░░▄▀░░░░░░░░░░█░"
            + "░▄▀░░░░░░░░░░█░░▄▀░░░░░░▄▀░░█░░▄▀▄▀▄▀▄▀▄▀░░██░░▄▀░░█\n"
            + "█░░▄▀░░██░░▄▀░░█░░▄▀░░██░░▄▀░░█░░▄▀░░░░░░▄▀░░██░░▄▀░░█░░▄▀░░████████"
            + "█░░▄▀░░█████████░░▄▀░░██░░▄▀░░█░░▄▀░░░░░░▄▀░░██░░▄▀░░█\n"
            + "█░░▄▀░░██░░▄▀░░█░░▄▀░░██░░▄▀░░█░░▄▀░░██░░▄▀░░██░░▄▀░░█░░▄▀░░█████████"
            + "░░▄▀░░░░░░░░░░█░░▄▀░░██░░▄▀░░█░░▄▀░░██░░▄▀░░██░░▄▀░░█\n"
            + "█░░▄▀░░██░░▄▀░░█░░▄▀░░██░░▄▀░░█░░▄▀░░██░░▄▀░░██░░▄▀░░█░░▄▀░░██░░░░░░█░"
            + "░▄▀▄▀▄▀▄▀▄▀░░█░░▄▀░░██░░▄▀░░█░░▄▀░░██░░▄▀░░██░░▄▀░░█\n"
            + "█░░▄▀░░██░░▄▀░░█░░▄▀░░██░░▄▀░░█░░▄▀░░██░░▄▀░░██░░▄▀░░█░░▄▀░░██░░▄▀░"
            + "░█░░▄▀░░░░░░░░░░█░░▄▀░░██░░▄▀░░█░░▄▀░░██░░▄▀░░██░░▄▀░░█\n"
            + "█░░▄▀░░██░░▄▀░░█░░▄▀░░██░░▄▀░░█░░▄▀░░██░░▄▀░░░░░░▄▀░░█░░▄▀░░██░░▄▀░░"
            + "█░░▄▀░░█████████░░▄▀░░██░░▄▀░░█░░▄▀░░██░░▄▀░░░░░░▄▀░░█\n"
            + "█░░▄▀░░░░▄▀▄▀░░█░░▄▀░░░░░░▄▀░░█░░▄▀░░██░░▄▀▄▀▄▀▄▀▄▀░░█░░▄▀░░░░░░▄▀░░█░"
            + "░▄▀░░░░░░░░░░█░░▄▀░░░░░░▄▀░░█░░▄▀░░██░░▄▀▄▀▄▀▄▀▄▀░░█\n"
            + "█░░▄▀▄▀▄▀▄▀░░░░█░░▄▀▄▀▄▀▄▀▄▀░░█░░▄▀░░██░░░░░░░░░░▄▀░░█░░▄▀▄▀▄▀"
            + "▄▀▄▀░░█░░▄▀▄▀▄▀▄▀▄▀░░█░░▄▀▄▀▄▀▄▀▄▀░░█░░▄▀░░██░░░░░░░░░░▄▀░░█\n"
            + "█░░░░░░░░░░░░███░░░░░░░░░░░░░░█░░░░░░██████████░░░░░░█░░░░░░░░░░░░░░█░░"
            + "░░░░░░░░░░░░█░░░░░░░░░░░░░░█░░░░░░██████████░░░░░░█\n"
            + "██████████████████████████████████████████████████████████████████████"
            + "████████████████████████████████████████████████████\n");


    List<Object> dungeonConfig = dungeon.getDungeonConfig();
    appendWithException("Dungeon has the following configuration: ", "\n");
    appendWithException("Number of rows: " + dungeonConfig.get(0), "\n");
    appendWithException("Number of columns: " + dungeonConfig.get(1), "\n");
    appendWithException("Interconnectivity: " + dungeonConfig.get(2), "\n");
    appendWithException("Is dungeon wrapping? " + dungeonConfig.get(3), "\n");
    appendWithException("Percentage of caves with treasures: "
            + ((float) dungeonConfig.get(4)) * 100, "\n");
    appendWithException("Force the interconnectivity range? " + dungeonConfig.get(5), "\n");

    player.enterPlayerToDungeon(dungeon);

    appendWithException("---------------------------------------------------", "\n");
    appendWithException("LET THE GAME START!", "\n");

    String move = "";
    while (!dungeon.hasReachedEndNode() && !player.isPlayerDead()) {
      appendWithException("\n");
      appendWithException(dungeon.getCurrentPositionPlayer().getRoomDescription());
      appendWithException("\n");

      appendWithException("\nMove, Pickup, or Shoot (M-P-S)? ");
      try {
        move = scanner.next();
      } catch (NoSuchElementException e) {
        appendWithException("No more inputs to parse!");
      }

      switch (move) {
        case "M": {
          appendWithException("Where to? ");
          String direction = "";
          try {
            direction = scanner.next();
          } catch (NoSuchElementException e) {
            appendWithException("No more inputs to parse!");
          }

          switch (direction) {
            case "N": {
              try {
                player.moveUp(dungeon);
              } catch (IllegalArgumentException e) {
                appendWithException("\u001B[31mError: \u001B[0m" + e.getMessage(), "\n");
              }
              break;
            }
            case "S": {
              try {
                player.moveDown(dungeon);
              } catch (IllegalArgumentException e) {
                appendWithException("\u001B[31mError: \u001B[0m" + e.getMessage(), "\n");
              }
              break;
            }
            case "E": {
              try {
                player.moveRight(dungeon);
              } catch (IllegalArgumentException e) {
                appendWithException("\u001B[31mError: \u001B[0m" + e.getMessage(), "\n");
              }
              break;
            }
            case "W": {
              try {
                player.moveLeft(dungeon);
              } catch (IllegalArgumentException e) {
                appendWithException("\u001B[31mError: \u001B[0m" + e.getMessage(), "\n");
              }
              break;
            }
            default: {
              appendWithException("\u001B[31mError: \u001B[0mInvalid direction", "\n");
            }
          }
          break;
        }

        case "P": {
          appendWithException("What? ");
          String item = "";
          try {
            item = scanner.next().toUpperCase();
          } catch (NoSuchElementException e) {
            appendWithException("No more inputs to parse!");
          }

          switch (item) {
            case "RUBY":
            case "SAPPHIRE":
            case "DIAMOND": {
              try {
                player.pickUpTreasure(dungeon, item);
                appendWithException("You pick up a ", item, "\n");
              } catch (IllegalArgumentException e) {
                appendWithException("\u001B[31mError: \u001B[0m" + e.getMessage(), "\n");
              }
              break;
            }
            case "ARROW": {
              try {
                player.pickUpArrow(dungeon);
                appendWithException("You pick up an arrow", "\n");
              } catch (IllegalArgumentException e) {
                appendWithException("\u001B[31mError: \u001B[0m" + e.getMessage(), "\n");
              }
              break;
            }
            default: {
              appendWithException("\u001B[31mError: \u001B[0mInvalid item ", item, "\n");
            }
          }
          break;
        }
        case "S": {
          appendWithException("No. of caves? ");
          int distance = 0;
          try {
            distance = scanner.nextInt();
          } catch (NoSuchElementException e) {
            appendWithException("No more inputs to parse!");
          }
          appendWithException("Where to? ");
          String direction = "";
          try {
            direction = scanner.next();
          } catch (NoSuchElementException e) {
            appendWithException("No more inputs to parse!");
          }

          Map<String, String> directionToHeading = new HashMap<>();
          directionToHeading.put("N", "U");
          directionToHeading.put("S", "D");
          directionToHeading.put("E", "R");
          directionToHeading.put("W", "L");

          if (directionToHeading.containsKey(direction)) {
            int arrowShootResult;
            try {
              arrowShootResult = player.shootArrow(dungeon, directionToHeading.get(direction),
                      distance).get(2);

              if (arrowShootResult == 1) {
                appendWithException("You killed the Otyugh", "\n");
              } else if (arrowShootResult == 0) {
                appendWithException("You shot the Otyugh, shoot it again to kill", "\n");
              } else {
                appendWithException("You shot an arrow into darkness", "\n");
              }
            } catch (IllegalArgumentException e) {
              appendWithException("\u001B[31mError: \u001B[0m" + e.getMessage(), "\n");
            }
          } else {
            appendWithException("\u001B[31mError: \u001B[0mInvalid direction", "\n");
          }
          break;
        }
        default: {
          appendWithException("Invalid option!", "\n");
          break;
        }
      }
    }

    if (player.isPlayerDead()) {
      appendWithException("\nChomp, chomp, chomp, you are eaten by an Otyugh!", "\n");
      appendWithException("Better luck next time", "\n");
    } else {
      appendWithException("\nCongratulations you have reached the end of the dungeon", "\n");
      appendWithException("Here are your treasure collections: ", "\n");
      appendWithException(player.getPlayerTreasures(), "\n");
      appendWithException("You are left with: \n", player.getPlayerArrows(), "\n");
    }
  }

  /**
   * Starts the dungeon game with an interactive GUI and a player which actually plays
   * the game.
   */
  @Override
  public void playGuiGame() {
    throw new UnsupportedOperationException("GUI not supported in console!");
  }
}
