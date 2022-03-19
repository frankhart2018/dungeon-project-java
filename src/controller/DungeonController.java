package controller;

import dungeongame.Dungeon;
import dungeongame.Player;

/**
 * This interface represents the controller used that the user interacts with
 * for playing a dungeon game.
 */
public interface DungeonController {
  /**
   * Starts the dungeon game with a dungeon instance and a player which actually plays
   * the game.
   *
   * @param dungeon the dungeon instance
   * @param player  the player instance
   */
  void playGame(Dungeon dungeon, Player player);

  /**
   * Starts the dungeon game with an interactive GUI and a player which actually plays
   * the game.
   */
  void playGuiGame();
}
