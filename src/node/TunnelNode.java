package node;

import dungeongame.Otyugh;
import dungeongame.Yugoloth;
import utils.ValueSanity;

import java.util.List;
import java.util.Random;

/*
  Since the user does not need to create a tunnel node directly, only the GenericNode
  implementation of a Node is exposed to the user.
 */
class TunnelNode extends AbstractNode {
  public TunnelNode(Node northNode, Node southNode, Node eastNode, Node westNode, String nodeName) {
    super(northNode, southNode, eastNode, westNode, nodeName);
  }

  public TunnelNode(Node northNode, Node southNode, Node eastNode, Node westNode, String nodeName,
                    int numArrows, Yugoloth yugoloth, boolean hasThief) {
    super(northNode, southNode, eastNode, westNode, nodeName);

    this.numArrows = numArrows;
    this.yugoloth = yugoloth;
    this.hasThief = hasThief;
  }

  @Override
  public void placeTreasure(Random random) {
    throw new UnsupportedOperationException("Cannot place treasure in a tunnel node!");
  }

  @Override
  public void placeTreasure(String treasureName, int count) {
    throw new UnsupportedOperationException("Cannot place treasure in a tunnel node!");
  }

  @Override
  public void removeTreasure(String treasureName) {
    throw new UnsupportedOperationException("Tunnel does not have any treasure to remove!");
  }

  @Override
  public List<String> getTreasures() {
    throw new UnsupportedOperationException("Tunnel does not have any treasure!");
  }

  @Override
  public String getType() {
    return "T";
  }

  @Override
  public Node cloneNode() {
    return new TunnelNode(this.topNode, this.bottomNode, this.rightNode, this.leftNode,
            this.getNodeName(), this.numArrows, this.yugoloth, this.hasThief);
  }

  @Override
  public void placeOtyugh(Otyugh otyugh) {
    throw new UnsupportedOperationException("Tunnel cannot have Otyugh!");
  }

  @Override
  public void removeOtyugh() {
    throw new UnsupportedOperationException("Tunnel cannot have Otyugh!");
  }

  @Override
  public Otyugh getOtyugh() {
    throw new UnsupportedOperationException("Tunnel cannot have Otyugh!");
  }

  @Override
  public void setOtyughHealth(int newHealth) {
    throw new UnsupportedOperationException("Tunnel cannot have Otyugh!");
  }

  @Override
  public void addArrows(Random random) {
    this.numArrows = random.nextInt((MAX_NUM_ARROWS - 1) + 1) + 1;
  }

  @Override
  public void addSingleArrow() {
    this.numArrows++;
  }

  @Override
  public void removeArrow() {
    if (this.numArrows == 0) {
      throw new IllegalArgumentException("No arrows to remove!");
    }

    this.numArrows--;
  }

  @Override
  public int getNumArrows() {
    return this.numArrows;
  }

  @Override
  public void placeYugoloth(Yugoloth yugoloth) {
    ValueSanity.checkNull("Yugoloth", yugoloth);
    this.yugoloth = yugoloth;
  }

  @Override
  public void removeYugoloth() {
    if (this.yugoloth == null) {
      throw new IllegalArgumentException("No Yugoloth to remove from location!");
    }

    this.yugoloth = null;
  }

  @Override
  public void placeThief() {
    this.hasThief = true;
  }

  @Override
  public void addPit() {
    throw new UnsupportedOperationException("Tunnel cannot have pits!");
  }
}
