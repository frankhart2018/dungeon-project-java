package view;

import java.util.List;

import controller.DungeonControllerWView;

/**
 * GUI view for the dungeon.
 */
public interface DungeonView {
  /**
   * Moves the player to the given direction.
   *
   * @param direction the direction to move the player
   */
  void moveInDirection(String direction);

  /**
   * Adds the nodes to render the maze.
   *
   * @param nodeTypes the node types to render
   */
  void addNodeTypes(List<List<NodeType>> nodeTypes);

  /**
   * Make the frame visible.
   */
  void makeVisible();

  /**
   * Make the frame non-resizable.
   */
  void makeNonResizable();

  /**
   * Adds player to node with given coordinates.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   */
  void addPlayerToNode(int i, int j);

  /**
   * Removes player from the current node.
   */
  void removePlayerFromNode();

  /**
   * Adds a controller to listen to events.
   *
   * @param listener the controller to add
   */
  void addListener(DungeonControllerWView listener);

  /**
   * Moves the yugoloth asynchronously in the background.
   */
  void asyncMoveYugoloth();

  /**
   * Adds an otyguh to the node with the given coordinates.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   */
  void addOtyughToNode(int i, int j);

  /**
   * Removes the otyugh from the current node.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   */
  void removeOtyughFromNode(int i, int j);

  /**
   * Displays a dialog box with the given message and title.
   *
   * @param header the title of the dialog box
   * @param value the message to display
   */
  void displayDialog(String header, String value);

  /**
   * Adds treasure to a node with the given coordinates.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   * @param treasureName the name of the treasure
   */
  void addTreasureToNode(int i, int j, List<String> treasureName);

  /**
   * Refreshes the frame.
   */
  void refresh();

  /**
   * Adds arrows to the node with the given coordinates.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   * @param numArrows the number of arrows to add
   */
  void addArrowsToNode(int i, int j, int numArrows);

  /**
   * Adds smell to the node with the given coordinates.
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   * @param smellLevel the smell level to add
   */
  void addSmellToNode(int i, int j, String smellLevel);

  /**
   * Removes smell from the node with the given coordinates.
   *
   * @param playerI the x coordinate of the node
   * @param playerJ the y coordinate of the node
   */
  void removeSmellFromNode(int playerI, int playerJ);

  /**
   * Uncovers the node with given coordinates.
   *
   * @param startNodeI the x coordinate of the node
   * @param startNodeJ the y coordinate of the node
   */
  void uncoverNode(int startNodeI, int startNodeJ);

  /**
   * Stops the asynchronous movement of the yugoloth.
   */
  void stopYugolothMovement();

  /**
   * Removes yugoloth from the node with the given coordinates.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   */
  void removeYugolothFromNode(int i, int j);

  /**
   * Adds yugoloth to the node with the given coordinates.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   */
  void addYugolothToNode(int i, int j);

  /**
   * Disables actions on the frame.
   */
  void disableActions();

  /**
   * Adds thief to the node with the given coordinates.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   */
  void addThiefToNode(int i, int j);

  /**
   * Adds pit to the node with the given coordinates.
   *
   * @param pitI the x coordinate of the node
   * @param pitJ the y coordinate of the node
   */
  void addPitToNode(int pitI, int pitJ);

  /**
   * Marks the node as end node.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   */
  void markEndNode(int i, int j);
}
