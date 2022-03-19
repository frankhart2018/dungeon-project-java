package controller;

import java.util.List;

/**
 * Representation of a dungeon controller with a GUI view.
 */
public interface DungeonControllerWView extends DungeonController {
  /**
   * Moves the player and returns if the move was successful or not.
   *
   * @param direction the direction to move in
   * @return true if the move was successful, false otherwise
   */
  boolean move(String direction);

  /**
   * Checks if there is an item (treasure or arrow) in the given position.
   *
   * @param i the row
   * @param j the column
   * @return true if there is an item in the given position, false otherwise
   */
  boolean hasItemAtLocation(int i, int j);

  /**
   * Picks up item with given name from the current position of player.
   *
   * @param itemName the name of the item to pick up
   */
  void pickUpItem(String itemName);

  /**
   * Returns the current dungeon's configuration.
   * @return the current dungeon's configuration
   */
  List<Object> getDungeonConfig();

  /**
   * Creates a new random dungeon from the given configuration.
   *
   * @param numRows the number of rows in the dungeon
   * @param numCols the number of columns in the dungeon
   * @param interconnectivity the interconnectivity of the dungeon
   * @param isWrapping whether the dungeon wraps around
   * @param percentCavesWithTreasures the percentage of caves that have treasures
   * @param forceInterconnectivityRange whether to force interconnectivity or not
   * @param numOtyughs the number of otyughs in the dungeon
   */
  void getNewDungeon(int numRows, int numCols, int interconnectivity,
                     boolean isWrapping, float percentCavesWithTreasures,
                     boolean forceInterconnectivityRange, int numOtyughs);

  /**
   * Shoots an arrow in a given direction and to a specified distance.
   *
   * @param direction the direction to shoot the arrow
   * @param distance the distance to shoot the arrow
   * @return the result of the shooting
   */
  List<Integer> shoot(String direction, int distance);

  /**
   * Checks if the dungeon is wrapping or not.
   *
   * @return true if the dungeon is wrapping, false otherwise
   */
  boolean isDungeonWrapping();

  /**
   * Returns the height of the dungeon.
   *
   * @return the height of the dungeon
   */
  int getDungeonHeight();

  /**
   * Returns the width of the dungeon.
   *
   * @return the width of the dungeon
   */
  int getDungeonWidth();

  /**
   * Returns the name of the player playing the game.
   *
   * @return the name of the player playing the game
   */
  String getPlayerName();

  /**
   * Returns the number of arrows left in the player's inventory.
   *
   * @return the number of arrows left in the player's inventory
   */
  int getArrowsLeft();

  /**
   * Returns the treasures collected by the player.
   *
   * @return the treasures collected by the player
   */
  List<String> getTreasuresCollected();

  /**
   * Returns the current position type (cave/tunnel) of the player.
   *
   * @return the current position type (cave/tunnel) of the player
   */
  String getCurrentPositionType();

  /**
   * Returns the list of treasures in the current location of player.
   *
   * @return the list of treasures in the current location of player
   */
  List<String> getTreasuresInNode();

  /**
   * Returns the number of arrows in the current location of player.
   *
   * @return the number of arrows in the current location of player
   */
  int getNumArrows();

  /**
   * Checks if the current location of player has any smell or not.
   *
   * @return YES if the current location of player has any smell, NO otherwise
   */
  String hasSmell();

  /**
   * Sets the player's name to the given name.
   *
   * @param playerName the name to set the player's name to
   */
  void setPlayerName(String playerName);

  /**
   * Move the Yugoloths around the dungeon.
   */
  void moveMonsters();

  /**
   * Click and move the player in the dungeon.
   *
   * @param r the row to move to
   * @param c the column to move to
   */
  void clickAndMove(int r, int c);

  /**
   * Check if the otyugh at position is hit with a single arrow or not.
   *
   * @param r the row of the otyugh
   * @param c the column of the otyugh
   * @return true if the otyugh is hit, false otherwise
   */
  boolean isOtyughHit(int r, int c);

  /**
   * Restarts the game with the current dungeon.
   */
  void restartGame();
}
