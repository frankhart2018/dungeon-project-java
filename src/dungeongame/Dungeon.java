package dungeongame;

import node.Node;

import java.util.List;
import java.util.Map;

/**
 * This interface represents a dungeon which contains a maze of caves and tunnels
 * and with treasures and arrows which a player can pick up through its way from start
 * to end point in the maze, a dungeon also consists of smelly monsters called Otyughs
 * which the player has to slay or be eaten by.
 */
public interface Dungeon {
  /**
   * Checks if the entered player has reached the end node or not.
   *
   * @return true if the player has reached the end node, false otherwise
   */
  boolean hasReachedEndNode();

  /**
   * Returns the graph of the maze in the dungeon.
   *
   * @return the graph of the maze
   */
  Node[][] getMaze();

  /**
   * Returns the current node of the player.
   *
   * @return the current node of the player
   */
  Node getCurrentPositionPlayer();

  /**
   * Returns the configurations used to construct the dungeon.
   *
   * @return the configurations used to construct the dungeon
   */
  List<Object> getDungeonConfig();

  /**
   * Returns the starting node of the dungeon.
   *
   * @return the starting node of the dungeon
   */
  Node getStartNode();

  /**
   * Returns the row number of the start node of the dungeon.
   *
   * @return the row number of the start node of the dungeon
   */
  int getStartNodeI();

  /**
   * Returns the column number of the start node of the dungeon.
   *
   * @return the column number of the start node of the dungeon
   */
  int getStartNodeJ();

  /**
   * Returns the end node of the dungeon.
   *
   * @return the end node of the dungeon
   */
  Node getEndNode();

  /**
   * Returns the row number of the current position of all otyughs.
   *
   * @return the row number of the current position of all otyughs
   */
  List<Integer> getOtyughNodeIs();

  /**
   * Returns the column number of the current position of all otyughs.
   *
   * @return the column number of the current position of all otyughs
   */
  List<Integer> getOtyughNodeJs();

  /**
   * Gets a map of all the treasures along with their locations.
   *
   * @return a map of all the treasures along with their locations
   */
  Map<List<Integer>, List<String>> getTreasureAndLocations();

  /**
   * Checks if the dungeon is wrapping or not.
   *
   * @return true if the dungeon is wrapping, false otherwise
   */
  boolean isWrapping();

  /**
   * Moves a yugoloth in the dungeon.
   */
  void moveYugoloth();

  /**
   * Returns the number of otyughs in the dungeon.
   *
   * @return the number of otyughs in the dungeon
   */
  int getNumOtyughs();

  /**
   * Returns the initial maze of the dungeon before the game starts.
   *
   * @return the initial maze
   */
  Node[][] getInitialMaze();

  /**
   * Returns the row number of the current position of yugoloth.
   *
   * @return the row number of the current position of yugoloth
   */
  int getYugolothNodeI();

  /**
   * Returns the column number of the current position of yugoloth.
   *
   * @return the column number of the current position of yugoloth
   */
  int getYugolothNodeJ();

  /**
   * Performs a hand to hand battle between the player and the Yugoloth.
   *
   * @return true if the player wins the hand to hand battle, false otherwise
   */
  boolean handToHandBattle();

  /**
   * Kills the yugoloth in the dungeon.
   */
  void killYugoloth();

  /**
   * Returns the current position of yugoloth in the dungeon.
   *
   * @return the current position of yugoloth in the dungeon
   */
  Node getYugolothPosition();

  /**
   * Returns the initial starting position of the yugoloth.
   *
   * @return the initial starting position of the yugoloth
   */
  Node getInitialYugolothPosition();

  /**
   * Returns the row number of the current position of the thief.
   *
   * @return the row number of the current position of the thief
   */
  int getThiefNodeI();

  /**
   * Returns the column number of the current position of the thief.
   *
   * @return the column number of the current position of the thief
   */
  int getThiefNodeJ();

  /**
   * Returns the yugoloth in the dungeon.
   *
   * @return the yugoloth in the dungeon
   */
  Yugoloth getYugoloth();

  /**
   * Returns the thief in the dungeon.
   *
   * @return the thief in the dungeon
   */
  Node getThiefPosition();

  /**
   * Returns the position of pit in the dungeon.
   *
   * @return the position of pit in the dungeon
   */
  Node getPitPosition();

  /**
   * Returns the row number of the pit in the dungeon.
   *
   * @return the row number of the pit in the dungeon
   */
  int getPitNodeI();

  /**
   * Returns the column number of the pit in the dungeon.
   *
   * @return the column number of the pit in the dungeon
   */
  int getPitNodeJ();
}
