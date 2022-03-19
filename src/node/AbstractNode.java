package node;

import dungeongame.Otyugh;
import dungeongame.Yugoloth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

abstract class AbstractNode implements Node {
  protected static final int MAX_NUM_ARROWS = 4;
  private static final int MAX_SMELL_DISCOVERY_DISTANCE = 2;

  protected Node topNode;
  protected Node bottomNode;
  protected Node rightNode;
  protected Node leftNode;
  private final String nodeName;
  private static int counter = 1;
  protected Otyugh otyugh;
  protected int numArrows;
  protected Yugoloth yugoloth;
  protected boolean hasThief;
  protected boolean hasPit;

  public AbstractNode(Node topNode, Node bottomNode, Node rightNode,
                      Node leftNode, String nodeName) {
    this.topNode = topNode;
    this.bottomNode = bottomNode;
    this.rightNode = rightNode;
    this.leftNode = leftNode;
    this.nodeName = nodeName != null ? nodeName : "" + counter++;
    this.otyugh = null;
    this.numArrows = 0;
  }

  public Node getTopNode() {
    return topNode;
  }

  public Node getBottomNode() {
    return bottomNode;
  }

  public Node getRightNode() {
    return rightNode;
  }

  public Node getLeftNode() {
    return leftNode;
  }

  @Override
  public String getNodeName() {
    return nodeName;
  }

  @Override
  public void setTopNode(Node node) {
    if (node == this) {
      throw new IllegalArgumentException("Cannot set top node as this node!");
    }

    this.topNode = node;
  }

  @Override
  public void setBottomNode(Node node) {
    if (node == this) {
      throw new IllegalArgumentException("Cannot set bottom node as this node!");
    }

    this.bottomNode = node;
  }

  @Override
  public void setRightNode(Node node) {
    if (node == this) {
      throw new IllegalArgumentException("Cannot set right node as this node!");
    }

    this.rightNode = node;
  }

  @Override
  public void setLeftNode(Node node) {
    if (node == this) {
      throw new IllegalArgumentException("Cannot set left node as this node!");
    }

    this.leftNode = node;
  }

  @Override
  public Node castToCaveNode() {
    if (this.getType().equals("G")) {
      return new CaveNode(this.topNode, this.bottomNode, this.rightNode, this.leftNode,
              this.nodeName);
    }

    throw new IllegalArgumentException("Only a GenericNode can be cast to CaveNode!");
  }

  @Override
  public Node castToTunnelNode() {
    if (this.getType().equals("G")) {
      return new TunnelNode(this.topNode, this.bottomNode, this.rightNode, this.leftNode,
              this.nodeName);
    }

    throw new IllegalArgumentException("Only a GenericNode can be cast to TunnelNode!");
  }

  private int numOtyughsInNeighbouringNodes(Node startNode, int maxDistance) {
    List<String> visitedNodes = new ArrayList<>();
    Map<String, Integer> distances = new TreeMap<>();

    List<Node> queue = new ArrayList<>();

    visitedNodes.add(startNode.getNodeName());
    queue.add(startNode);
    distances.put(startNode.getNodeName(), 0);

    Node topNode;
    List<Node> neighbours;
    int distance;
    int numOtyughs = 0;
    boolean stopScan = false;
    while (queue.size() != 0) {
      topNode = queue.get(0);
      queue.remove(0);
      neighbours = new ArrayList<>();
      if (topNode.getLeftNode() != null) {
        neighbours.add(topNode.getLeftNode());
      }
      if (topNode.getRightNode() != null) {
        neighbours.add(topNode.getRightNode());
      }
      if (topNode.getBottomNode() != null) {
        neighbours.add(topNode.getBottomNode());
      }
      if (topNode.getTopNode() != null) {
        neighbours.add(topNode.getTopNode());
      }

      for (Node neighbour : neighbours) {
        if (!visitedNodes.contains(neighbour.getNodeName())) {
          distance = distances.get(topNode.getNodeName()) + 1;
          if (neighbour.getType().equals("C") && neighbour.getOtyugh() != null
                  && distance == maxDistance) {
            numOtyughs++;
          }

          visitedNodes.add(neighbour.getNodeName());
          if (distance > maxDistance) {
            stopScan = true;
            break;
          }
          distances.put(neighbour.getNodeName(), distance);
          queue.add(neighbour);
        }
      }

      if (stopScan) {
        break;
      }
    }

    return numOtyughs;
  }

  private int getNeighbourCount() {
    int numNeighbours = 0;
    if (this.getTopNode() != null) {
      numNeighbours++;
    }
    if (this.getBottomNode() != null) {
      numNeighbours++;
    }
    if (this.getRightNode() != null) {
      numNeighbours++;
    }
    if (this.getLeftNode() != null) {
      numNeighbours++;
    }

    return numNeighbours;
  }

  @Override
  public String getRoomDescription() {
    if (!this.getType().equals("C") && !this.getType().equals("T")) {
      throw new IllegalArgumentException("Cannot get room description from "
              + this.getType()
              + " node!");
    }

    if (this.getNeighbourCount() == 0) {
      throw new IllegalArgumentException("Cannot get room description of a disconnected node"
              + ", there are no connections in any side!");
    }

    if (this.getType().equals("C")) {
      int neighbourCount = this.getNeighbourCount();
      if (neighbourCount != 1 && neighbourCount != 3 && neighbourCount != 4) {
        throw new IllegalArgumentException("Cave can have either 1, 3 or 4 neighbours!");
      }
    } else if (this.getType().equals("T")) {
      if (this.getNeighbourCount() != 2) {
        throw new IllegalArgumentException("Tunnel can have exactly 2 neighbours!");
      }
    }

    StringBuilder sb = new StringBuilder();

    String type = this.getType().equals("C") ? "cave" : "tunnel";
    sb.append("You are in a ").append(type);

    if (type.equals("cave")) {
      sb.append("\nDoors lead to the ");
    } else {
      sb.append("\nthat continues to the ");
    }

    if (this.topNode != null) {
      sb.append("N, ");
    }
    if (this.rightNode != null) {
      sb.append("E, ");
    }
    if (this.bottomNode != null) {
      sb.append("S, ");
    }
    if (this.leftNode != null) {
      sb.append("W");
    }

    if (sb.charAt(sb.length() - 1) == ' ' && sb.charAt(sb.length() - 2) == ',') {
      sb.deleteCharAt(sb.length() - 1);
      sb.deleteCharAt(sb.length() - 1);
    }

    if (this.getType().equals("C")) {
      List<String> treasureList = this.getTreasures();

      Set<String> distinct = new HashSet<>(treasureList);
      int countTreasureType;

      for (String s : distinct) {
        countTreasureType = Collections.frequency(treasureList, s);
        sb.append("\nYou find ").append(Collections.frequency(treasureList, s));
        sb.append(" ");
        switch (s) {
          case "RUBY":
            if (countTreasureType == 1) {
              sb.append("ruby");
            } else {
              sb.append("rubies");
            }
            break;
          case "SAPPHIRE":
            if (countTreasureType == 1) {
              sb.append("sapphire");
            } else {
              sb.append("sapphires");
            }
            break;
          case "DIAMOND":
            if (countTreasureType == 1) {
              sb.append("diamond");
            } else {
              sb.append("diamonds");
            }
            break;
          default:
            break;
        }
        sb.append(" here");
      }
    }

    if (this.getType().equals("C") || this.getType().equals("T")) {
      if (this.numArrows == 1) {
        sb.append("\nYou find 1 arrow here");
      } else if (this.numArrows > 1) {
        sb.append("\nYou find ").append(this.numArrows).append(" arrows here");
      }
    }

    int numOtyughsInDistanceOne = this.numOtyughsInNeighbouringNodes(this, 1);
    if (numOtyughsInDistanceOne >= 1) {
      sb.append("\nYou smell a strong pungent smell");
    } else {
      int numOtyughsInDistanceTwo = this.numOtyughsInNeighbouringNodes(this, 2);
      if (numOtyughsInDistanceTwo == 1) {
        sb.append("\nYou smell a pungent smell");
      } else if (numOtyughsInDistanceTwo > 1) {
        sb.append("\nYou smell a strong pungent smell");
      }
    }

    return sb.toString();
  }

  @Override
  public void updateCastInNeighbours() {
    if (this.leftNode != null) {
      this.leftNode.setRightNode(this);
    }
    if (this.rightNode != null) {
      this.rightNode.setLeftNode(this);
    }
    if (this.topNode != null) {
      this.topNode.setBottomNode(this);
    }
    if (this.bottomNode != null) {
      this.bottomNode.setTopNode(this);
    }
  }

  @Override
  public Yugoloth getYugoloth() {
    return this.yugoloth;
  }
}
