package node;

import dungeongame.Otyugh;
import dungeongame.Yugoloth;

import java.util.List;
import java.util.Random;

/**
 * This interface represents a node in a graph that represents the maze inside the dungeon,
 * a node can be a generic node, a cave, or a tunnel.
 */
public interface Node {
  /**
   * Converts a generic node to a cave node.
   *
   * @return a cave node
   * @throws IllegalArgumentException if the node is already a cave node, or
   *                                  if the node is a tunnel node
   */
  Node castToCaveNode();

  /**
   * Converts a generic node to a tunnel node.
   *
   * @return a tunnel node
   * @throws IllegalArgumentException if the node is already a tunnel node, or
   *                                  if the node is a cave node
   */
  Node castToTunnelNode();

  /**
   * Returns the description of a room, which consists of the directions in which
   * there is a path, and the treasures (if it is a cave) in the room.
   *
   * @return a string with the description of the room
   */
  String getRoomDescription();

  /**
   * Returns the node which is above the current node, if there is a path between the two.
   *
   * @return the node above the current node
   */
  Node getTopNode();

  /**
   * Returns the node which is below the current node, if there is a path between the two.
   *
   * @return the node below the current node
   */
  Node getBottomNode();

  /**
   * Returns the node which is right of the current node, if there is a path between the two.
   *
   * @return the node right of the current node
   */
  Node getRightNode();

  /**
   * Returns the node which is left of the current node, if there is a path between the two.
   *
   * @return the node left of the current node
   */
  Node getLeftNode();

  /**
   * Returns the name of the current node.
   *
   * @return a string with the name of the current node
   */
  String getNodeName();

  /**
   * Sets the top node of the current node, establishing a path between the two.
   *
   * @param node the node to be set as the top node
   * @throws IllegalArgumentException if the node is null
   */
  void setTopNode(Node node);

  /**
   * Returns the node which is below the current node, if there is a path between the two.
   *
   * @param node the node to be set as the bottom node
   * @throws IllegalArgumentException if the node is null
   */
  void setBottomNode(Node node);

  /**
   * Returns the node which is on right of the current node, if there is a path between the two.
   *
   * @param node the node to be set as the right node
   * @throws IllegalArgumentException if the node is null
   */
  void setRightNode(Node node);

  /**
   * Returns the node which is on left of the current node, if there is a path between the two.
   *
   * @param node the node to be set as the left node
   * @throws IllegalArgumentException if the node is null
   */
  void setLeftNode(Node node);

  /**
   * Places a random number and random types of treasure in the node (if it is a cave).
   *
   * @param random a random number generator
   * @throws IllegalArgumentException      if the random object is null
   * @throws UnsupportedOperationException if the node is not a cave
   */
  void placeTreasure(Random random);

  /**
   * Places a single treasure in the node (if it is a cave).
   *
   * @param treasureName name of the treasure to be placed
   * @param count        number of treasures of type treasureName to be placed
   * @throws IllegalArgumentException      if the random object is null
   * @throws UnsupportedOperationException if the node is not a cave
   */
  void placeTreasure(String treasureName, int count);

  /**
   * Removes a treasure (if it is present) from the node.
   *
   * @param treasureName the name of the treasure to be removed
   * @throws IllegalArgumentException      if the treasure name is null
   * @throws UnsupportedOperationException if the node is not a cave
   */
  void removeTreasure(String treasureName);

  /**
   * Returns the list of treasures (if any) in the node.
   *
   * @return a list of treasures in the node
   * @throws UnsupportedOperationException if the node is not a cave
   */
  List<String> getTreasures();

  /**
   * Returns the type of node (generic, cave or tunnel).
   *
   * @return a string with the type of node
   */
  String getType();

  /**
   * Clones a node and returns a new instance of it.
   *
   * @return a new instance of the node
   */
  Node cloneNode();

  /**
   * Updates the type of variable in all neighbours after casting the current node.
   */
  void updateCastInNeighbours();

  /**
   * Place an Otyugh in a node (only if the node is a cave).
   *
   * @param otyugh the Otyugh to be placed
   * @throws IllegalArgumentException      if the otyugh is null
   * @throws UnsupportedOperationException if the node is not a cave
   */
  void placeOtyugh(Otyugh otyugh);

  /**
   * Remove an Otyugh from a node (only if the node is a cave).
   *
   * @throws UnsupportedOperationException if the node is not a cave
   */
  void removeOtyugh();

  /**
   * Returns the Otyugh in the node (if any).
   *
   * @return the Otyugh in the node
   * @throws UnsupportedOperationException if the node is not a cave
   */
  Otyugh getOtyugh();

  /**
   * Updates the health of the Otyugh in the node (if any).
   *
   * @param newHealth the new health of the Otyugh
   * @throws UnsupportedOperationException if the node is not a cave
   */
  void setOtyughHealth(int newHealth);

  /**
   * Adds a random number of arrows to a node (if it is a cave or tunnel).
   *
   * @param random a random number generator
   * @throws IllegalArgumentException      if the random object is null
   * @throws UnsupportedOperationException if the node is not a cave or tunnel
   */
  void addArrows(Random random);

  /**
   * Adds a single arrow to a node (if it is a cave or tunnel).
   *
   * @throws UnsupportedOperationException if the node is not a cave or tunnel
   */
  void addSingleArrow();

  /**
   * Removes a single arrow from a node (if it is a cave or tunnel).
   *
   * @throws UnsupportedOperationException if the node is not a cave or tunnel
   */
  void removeArrow();

  /**
   * Returns the number of arrows in the node (if any).
   *
   * @return the number of arrows in the node
   * @throws UnsupportedOperationException if the node is not a cave or tunnel
   */
  int getNumArrows();

  /**
   * Place a yugoloth in the current node if it is a cave.
   *
   * @param yugoloth the yugoloth to be placed
   */
  void placeYugoloth(Yugoloth yugoloth);

  /**
   * Remove a yugoloth from the current node if it is a cave.
   */
  void removeYugoloth();

  /**
   * Place a thief in the current node if it is a cave.
   */
  void placeThief();

  /**
   * Returns the yugoloth in the current node if it is a cave.
   *
   * @return the yugoloth in the current node
   */
  Yugoloth getYugoloth();

  /**
   * Adds a pit to the current node if it is a cave.
   */
  void addPit();
}
