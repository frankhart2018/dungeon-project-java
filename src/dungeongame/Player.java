package dungeongame;

import java.util.List;

/**
 * This interface represents a player that is placed in a dungeon
 * and has to find a way from the start node to end node while collecting
 * as many treasures as possible.
 */
public interface Player {
  /**
   * Places the player at the starting node of the dungeon.
   *
   * @param dungeon the dungeon in which the player is placed
   * @throws IllegalArgumentException if the dungeon object is null, or
   *                                  if the player is already placed in dungeon, or
   *                                  if another player is already in the dungeon
   */
  void enterPlayerToDungeon(Dungeon dungeon);

  /**
   * Moves the player to the left from its current position in the dungeon.
   *
   * @param dungeon the dungeon in which the player is placed
   * @throws IllegalArgumentException if the dungeon object is null, or
   *                                  there is no path on the left in current position,
   *                                  if the player is not placed in the dungeon, or
   *                                  if another player is already in the dungeon, or
   *                                  if the player is dead
   */
  void moveLeft(Dungeon dungeon);

  /**
   * Moves the player to the right from its current position in the dungeon.
   *
   * @param dungeon the dungeon in which the player is placed
   * @throws IllegalArgumentException if the dungeon object is null, or
   *                                  there is no path on the right in current position,
   *                                  if the player is not placed in the dungeon, or
   *                                  if another player is already in the dungeon, or
   *                                  if the player is dead
   */
  void moveRight(Dungeon dungeon);

  /**
   * Moves the player to the up from its current position in the dungeon.
   *
   * @param dungeon the dungeon in which the player is placed
   * @throws IllegalArgumentException if the dungeon object is null, or
   *                                  there is no path towards the top in current position,
   *                                  if the player is not placed in the dungeon, or
   *                                  if another player is already in the dungeon, or
   *                                  if the player is dead
   */
  void moveUp(Dungeon dungeon);

  /**
   * Moves the player to the down from its current position in the dungeon.
   *
   * @param dungeon the dungeon in which the player is placed
   * @throws IllegalArgumentException if the dungeon object is null, or
   *                                  there is no path towards the bottom in current
   *                                  position,
   *                                  if the player is not placed in the dungeon, or
   *                                  if another player is already in the dungeon, or
   *                                  if the player is dead
   */
  void moveDown(Dungeon dungeon);

  /**
   * Picks up treasure from the current location of the player if any.
   *
   * @param dungeon      the dungeon in which the player is placed
   * @param treasureName the name of the treasure to be picked up
   * @throws IllegalArgumentException if the dungeon object is null, or
   *                                  if the treasure name is null, or
   *                                  if the current position is a tunnel, or
   *                                  if the name of treasure is illegal, or
   *                                  if the cave has no treasure, or
   *                                  if the cave does not have that treasure, or
   *                                  if the player is not placed in the dungeon, or
   *                                  if another player is already in the dungeon, or
   *                                  if the player is dead
   */
  void pickUpTreasure(Dungeon dungeon, String treasureName);

  /**
   * Returns the string representing bag of treasures of the player.
   *
   * @return the string representing bag of treasures of the player
   */
  String getPlayerTreasures();

  /**
   * Picks up an arrow from the current location if any.
   *
   * @param dungeon the dungeon in which the player is placed
   * @throws IllegalArgumentException if the dungeon object is null, or
   *                                  if there are no arrows in the current position, or
   *                                  if the player is not placed in the dungeon, or
   *                                  if another player is already in the dungeon, or
   *                                  if the player is dead
   */
  void pickUpArrow(Dungeon dungeon);

  /**
   * Returns the representation of number or arrows the player has.
   *
   * @return the representation of number or arrows the player has
   */
  String getPlayerArrows();

  /**
   * Shoots an arrow in given direction and at given distance.
   *
   * @param dungeon   the dungeon in which the player is placed
   * @param direction the direction of the arrow
   * @param distance  the distance of the arrow
   * @return list containing the current arrow position followed by an integer,
   *         1 if the Otyugh dies, 0 if the Otyugh is injured, -1 if the arrow does not
   *         hit the Otyugh
   * @throws IllegalArgumentException if the dungeon object is null, or
   *                                  if the direction is null, or
   *                                  if the distance is negative or zero, or
   *                                  if the player is not placed in the dungeon, or
   *                                  if another player is already in the dungeon, or
   *                                  if the player is dead, or
   *                                  if the player has no arrows
   */
  List<Integer> shootArrow(Dungeon dungeon, String direction, int distance);

  /**
   * Checks if the player is alive.
   *
   * @return true if the player is alive, false otherwise
   */
  boolean isPlayerDead();

  /**
   * Returns the name of the player.
   *
   * @return the name of the player
   */
  String getName();

  /**
   * Returns the number of arrows the player has.
   *
   * @return the number of arrows the player has
   */
  int getNumArrows();

  /**
   * Returns the list of treasures the player has.
   *
   * @return the list of treasures the player has
   */
  List<String> getPlayerTreasuresList();

  /**
   * Updates the name of the player to the given name.
   *
   * @param playerName the new name of the player
   */
  void updateName(String playerName);

  /**
   * Kills the player.
   */
  void killPlayer();

  /**
   * Steals all the treasures from the player.
   */
  void treasureStolen();
}
