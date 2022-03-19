package node;

import dungeongame.Otyugh;
import dungeongame.Yugoloth;

import java.util.List;
import java.util.Random;

/**
 * This is a generic node which can be used as a placeholder until a node
 * can be converted to either a cave or a tunnel node.
 */
public class GenericNode extends AbstractNode {
  /**
   * Constructs a generic node with given neighbours and name of the node.
   *
   * @param topNode    the node above this node
   * @param bottomNode the node below this node
   * @param rightNode  the node to the right of this node
   * @param leftNode   the node to the left of this node
   * @param nodeName   the name of the node
   */
  public GenericNode(Node topNode, Node bottomNode, Node leftNode, Node rightNode,
                     String nodeName) {
    super(topNode, bottomNode, leftNode, rightNode, nodeName);
  }

  /**
   * Creates a generic node with no neighbours and no name.
   */
  public GenericNode() {
    this(null, null, null, null, null);
  }

  /**
   * Creates a generic node with no neighbours but with a name.
   *
   * @param nodeName the name of the node
   */
  public GenericNode(String nodeName) {
    this(null, null, null, null, nodeName);
  }

  /**
   * This is not supported by GenericNode.
   *
   * @throws UnsupportedOperationException if the node is not a cave
   */
  @Override
  public void placeTreasure(Random random) {
    throw new UnsupportedOperationException("Cannot place treasure in a generic node!");
  }

  @Override
  public void placeTreasure(String treasureName, int count) {
    throw new UnsupportedOperationException("Cannot place treasure in a generic node!");
  }

  /**
   * This is not supported by GenericNode.
   *
   * @throws UnsupportedOperationException if the node is not a cave
   */
  @Override
  public void removeTreasure(String treasureName) {
    throw new UnsupportedOperationException("Generic node does not have any treasure to remove!");
  }

  /**
   * This is not supported by GenericNode.
   *
   * @throws UnsupportedOperationException if the node is not a cave
   */
  @Override
  public List<String> getTreasures() {
    throw new UnsupportedOperationException("Generic node does not have any treasure!");
  }

  /**
   * Returns the type of node (generic).
   *
   * @return a string with the type of node
   */
  @Override
  public String getType() {
    return "G";
  }

  /**
   * Clones a generic node and returns a new instance of it.
   *
   * @return a new instance of generic node
   */
  @Override
  public Node cloneNode() {
    return new GenericNode(this.topNode, this.bottomNode, this.rightNode,
            this.leftNode, this.getNodeName());
  }

  /**
   * Place an Otyugh in a node (only if the node is a cave).
   *
   * @param otyugh the Otyugh to be placed
   * @throws IllegalArgumentException      if the otyugh is null
   * @throws UnsupportedOperationException if the node is not a cave
   */
  @Override
  public void placeOtyugh(Otyugh otyugh) {
    throw new UnsupportedOperationException("Generic node cannot have an Otyugh!");
  }

  /**
   * Remove an Otyugh from a node (only if the node is a cave).
   *
   * @throws UnsupportedOperationException if the node is not a cave
   */
  @Override
  public void removeOtyugh() {
    throw new UnsupportedOperationException("Generic node does not have an Otyugh!");
  }

  /**
   * Returns the Otyugh in the node (if any).
   *
   * @return the Otyugh in the node
   * @throws UnsupportedOperationException if the node is not a cave
   */
  @Override
  public Otyugh getOtyugh() {
    throw new UnsupportedOperationException("Generic node does not have an Otyugh!");
  }

  /**
   * Updates the health of the Otyugh in the node (if any).
   *
   * @param newHealth the new health of the Otyugh
   * @throws UnsupportedOperationException if the node is not a cave
   */
  @Override
  public void setOtyughHealth(int newHealth) {
    throw new UnsupportedOperationException("Generic node does not have an Otyugh!");
  }

  /**
   * Adds a random number of arrows to a node (if it is a cave or tunnel).
   *
   * @param random a random number generator
   * @throws IllegalArgumentException      if the random object is null
   * @throws UnsupportedOperationException if the node is not a cave or tunnel
   */
  @Override
  public void addArrows(Random random) {
    throw new UnsupportedOperationException("Generic node cannot have any arrows!");
  }

  /**
   * Adds a single arrow to a node (if it is a cave or tunnel).
   *
   * @throws UnsupportedOperationException if the node is not a cave or tunnel
   */
  @Override
  public void addSingleArrow() {
    throw new UnsupportedOperationException("Generic node cannot have any arrows!");
  }

  /**
   * Removes a single arrow from a node (if it is a cave or tunnel).
   *
   * @throws UnsupportedOperationException if the node is not a cave or tunnel
   */
  @Override
  public void removeArrow() {
    throw new UnsupportedOperationException("Generic node cannot have any arrows!");
  }

  /**
   * Returns the number of arrows in the node (if any).
   *
   * @return the number of arrows in the node
   * @throws UnsupportedOperationException if the node is not a cave or tunnel
   */
  @Override
  public int getNumArrows() {
    throw new UnsupportedOperationException("Generic node cannot have any arrows!");
  }

  /**
   * Place a yugoloth in the current node if it is a cave.
   *
   * @param yugoloth the yugoloth to be placed
   */
  @Override
  public void placeYugoloth(Yugoloth yugoloth) {
    throw new UnsupportedOperationException("Generic node cannot have any Yugoloth!");
  }

  /**
   * Remove a yugoloth from the current node if it is a cave.
   */
  @Override
  public void removeYugoloth() {
    throw new UnsupportedOperationException("Generic node does not have any Yugoloth!");
  }

  /**
   * Place a thief in the current node if it is a cave.
   */
  @Override
  public void placeThief() {
    throw new UnsupportedOperationException("Generic node cannot have thief!");
  }

  /**
   * Adds a pit to the current node if it is a cave.
   */
  @Override
  public void addPit() {
    throw new UnsupportedOperationException("Generic node cannot have pits!");
  }
}
