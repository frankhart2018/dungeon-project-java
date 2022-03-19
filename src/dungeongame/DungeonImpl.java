package dungeongame;

import node.GenericNode;
import node.Node;
import utils.ValueSanity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class is an implementation of a dungeon and provides users with functionality for
 * generating random maze graphs or providing their own mazes.
 */
public class DungeonImpl implements Dungeon {
  private static final int MAX_PATIENCE_START_END_GEN = 1_000;
  private static final int MIN_NUM_ROWS_COLS = 4;

  private final int numRows;
  private final int numCols;
  private final int interconnectivity;
  private final boolean isWrapping;
  private final float percentCavesWithTreasureArrows;
  private final Node[][] graph;
  private final Random random;
  private final boolean forceInterconnectivityRange;
  private Node startNode;
  private Node endNode;
  private final int numOtyughs;
  private Player player;
  private Node playerPosition;
  private final List<Otyugh> otyughs;
  private Yugoloth yugoloth;
  private Node yugolothPosition;
  private Node[][] initialMaze;
  private Node thiefPosition;
  private Node pitPosition;

  /**
   * Constructs a random maze in a dungeon.
   *
   * @param numRows                     the number of rows in the dungeon
   * @param numCols                     the number of columns in the dungeon
   * @param interconnectivity           the interconnectivity of the dungeon
   * @param isWrapping                  whether the dungeon is wrapping or not
   * @param percentCavesWithTreasure    the percentage of caves that should contain treasure(s)
   * @param forceInterconnectivityRange whether to allow interconnectivity value higher than the
   *                                    safe range of [1, min(numRows, numCols)]
   * @param numOtyughs                  the  number of otyughs to place in the dungeon
   * @param random                      the random object to use for generating the dungeon
   * @throws IllegalArgumentException if number of rows is negative, or number of columns is
   *                                  negative, or interconnectivity is negative, or
   *                                  percentage of caves with treasure is negative, or
   *                                  random object is null,
   *                                  or number of rows is less than minimum allowed, or
   *                                  number of columns is less than minimum allowed, or
   *                                  percentage of caves with treasure is equal to zero, or
   *                                  percentage of caves with treasure is greater than one, or
   *                                  the number of otyughs is negative, or
   *                                  the number of otyughs is greater than the number of nodes, or
   *                                  it is not possible to find a path from start node to end
   *                                  node with a minimum distance of 5
   */
  public DungeonImpl(int numRows, int numCols, int interconnectivity, boolean isWrapping,
                     float percentCavesWithTreasure, boolean forceInterconnectivityRange,
                     int numOtyughs, Random random) {
    performChecksRandom(numRows, numCols, interconnectivity, isWrapping,
            percentCavesWithTreasure, forceInterconnectivityRange, numOtyughs, random);

    this.numRows = numRows;
    this.numCols = numCols;
    this.interconnectivity = interconnectivity;
    this.isWrapping = isWrapping;
    this.percentCavesWithTreasureArrows = percentCavesWithTreasure;
    this.random = random;
    this.forceInterconnectivityRange = forceInterconnectivityRange;
    this.numOtyughs = numOtyughs;
    this.otyughs = new ArrayList<>();
    this.yugoloth = new Yugoloth();
    hookRandomMoveStrategyToYugoloth(yugoloth);

    this.graph = generateGraph();
    generateRandomDungeon();

    this.setInitialMaze();
  }

  /**
   * Constructs a maze in dungeon from a given graph.
   *
   * @param graph            the graph to use as the maze in dungeon
   * @param startNode        the start node of the maze
   * @param endNode          the end node of the maze
   * @param isWrapping       whether the dungeon is wrapping or not
   * @param numOtyughs       the number of otyughs to place in the dungeon
   * @param yugolothPosition the position of the yugoloth
   * @param yugoloth         the yugoloth to use
   * @param thiefPosition    the position of the thief
   * @param pitPosition      the position of the pit
   * @param random           the random object to use for generating the dungeon
   * @throws IllegalArgumentException if graph is null, or start node is null, or end node is
   *                                  null, or start node is not in graph, or end node is not
   *                                  in graph, or number of rows is less than minimum allowed,
   *                                  or number of columns is less than minimum allowed, or
   *                                  the shortest path between start node and end node is
   *                                  not at least 5 nodes long, or there are zero caves
   *                                  with any treasure in them, or
   *                                  the number of otyughs is negative, or
   *                                  the number of otyughs is greater than the number of nodes, or
   *                                  tunnels or caves do not have appropriate number of entries
   */
  public DungeonImpl(Node[][] graph, String startNode, String endNode,
                     boolean isWrapping, int numOtyughs, Node yugolothPosition,
                     Yugoloth yugoloth, Node thiefPosition, Node pitPosition, Random random) {
    performChecksNonRandom(graph, startNode, endNode,
            isWrapping, numOtyughs, random);

    this.numRows = graph.length;
    this.numCols = graph[0].length;
    this.isWrapping = isWrapping;
    this.graph = graph;
    this.random = random;
    this.numOtyughs = numOtyughs;
    this.otyughs = new ArrayList<>();
    this.yugolothPosition = yugolothPosition;
    this.yugoloth = yugoloth;
    this.thiefPosition = thiefPosition;
    this.pitPosition = pitPosition;

    this.startNode = findNodeByNodeName(startNode);
    this.endNode = findNodeByNodeName(endNode);

    performChecksNonRandomAfterConstruct();
    setInitialMaze();

    // Pseudo values
    this.interconnectivity = 0;
    this.percentCavesWithTreasureArrows = 0;
    this.forceInterconnectivityRange = false;
  }

  /**
   * Constructs a maze in dungeon from a given graph.
   *
   * @param graph      the graph to use as the maze in dungeon
   * @param startNode  the start node of the maze
   * @param endNode    the end node of the maze
   * @param isWrapping whether the dungeon is wrapping or not
   * @param yugolothPosition the position of the yugoloth
   * @param yugoloth         the yugoloth to use
   * @param thiefPosition    the position of the thief
   * @param pitPosition      the position of the pit
   * @param numOtyughs the number of otyughs to place in the dungeon
   * @throws IllegalArgumentException if graph is null, or start node is null, or end node is
   *                                  null, or start node is not in graph, or end node is not
   *                                  in graph, or number of rows is less than minimum allowed,
   *                                  or number of columns is less than minimum allowed, or
   *                                  the shortest path between start node and end node is
   *                                  not at least 5 nodes long, or there are zero caves
   *                                  with any treasure in them, or
   *                                  the number of otyughs is negative, or
   *                                  the number of otyughs is greater than the number of nodes, or
   *                                  tunnels or caves do not have appropriate number of entries
   */
  public DungeonImpl(Node[][] graph, String startNode, String endNode,
                     boolean isWrapping, int numOtyughs, Node yugolothPosition,
                     Yugoloth yugoloth, Node thiefPosition, Node pitPosition) {
    this(graph, startNode, endNode, isWrapping, numOtyughs, yugolothPosition, yugoloth,
            thiefPosition, pitPosition, new Random());
  }

  private void performChecksRandom(int numRows, int numCols, int interconnectivity,
                                   boolean isWrapping, float percentCavesWithTreasure,
                                   boolean forceInterconnectivityRange, int numOtyughs,
                                   Random random) {
    ValueSanity.checkNegative("Number of rows", numRows);
    ValueSanity.checkNegative("Number of columns", numCols);
    ValueSanity.checkNegative("Interconnectivity", interconnectivity);
    ValueSanity.checkNegative("Percentage of caves that have treasure", percentCavesWithTreasure);

    ValueSanity.checkNull("Random object", random);

    if (numRows < MIN_NUM_ROWS_COLS) {
      throw new IllegalArgumentException("Minimum number of rows allowed is "
              + MIN_NUM_ROWS_COLS + "!");
    }

    if (numCols < MIN_NUM_ROWS_COLS) {
      throw new IllegalArgumentException("Minimum number of cols allowed is "
              + MIN_NUM_ROWS_COLS + "!");
    }

    if (percentCavesWithTreasure == 0) {
      throw new IllegalArgumentException("Percentage of caves that have treasure is expected to "
              + "be non-zero.");
    }

    if (percentCavesWithTreasure > 1) {
      throw new IllegalArgumentException("Percentage of caves that have treasure is expected to "
              + "be less than or equal to 100.");
    }

    if (numOtyughs > numRows * numCols) {
      throw new IllegalArgumentException("Number of otyughs is expected to be less than or equal "
              + "to the number of nodes in the dungeon!");
    }

    if (numOtyughs < 0) {
      throw new IllegalArgumentException("Number of otyughs is expected to be non-negative!");
    }
  }

  private void performChecksNonRandom(Node[][] graph, String startNode, String endNode,
                                      boolean isWrapping, int numOtyughs, Random random) {
    ValueSanity.checkNull("Starting node", startNode);
    ValueSanity.checkNull("Ending node", endNode);
    ValueSanity.checkNull("Graph representing dungeon", graph);
    ValueSanity.checkNull("Random object", random);

    if (graph.length < MIN_NUM_ROWS_COLS) {
      throw new IllegalArgumentException("Minimum number of rows allowed is "
              + MIN_NUM_ROWS_COLS + "!");
    }

    if (graph[0].length < MIN_NUM_ROWS_COLS) {
      throw new IllegalArgumentException("Minimum number of cols allowed is "
              + MIN_NUM_ROWS_COLS + "!");
    }

    if (numOtyughs > (graph.length * graph[0].length)) {
      throw new IllegalArgumentException("Number of otyughs is expected to be less than or equal "
              + "to the number of nodes in the graph!");
    }

    if (numOtyughs < 0) {
      throw new IllegalArgumentException("Number of otyughs is expected to be non-negative!");
    }
  }

  private void performChecksNonRandomAfterConstruct() {
    if (findShortestPathDistance(this.startNode, this.endNode) < 5) {
      throw new IllegalArgumentException("Distance between start and end node is less than 5!");
    }

    int numCavesWithTreasures = 0;
    for (Node[] row : this.graph) {
      for (Node node : row) {
        if (node.getType().equals("C") && node.getTreasures().size() > 0) {
          numCavesWithTreasures++;
        }
      }
    }

    if (numCavesWithTreasures == 0) {
      throw new IllegalArgumentException("No caves have treasures!");
    }

    int numNeighbours = 0;
    for (Node[] row : this.graph) {
      for (Node node : row) {
        numNeighbours = 0;

        if (node.getTopNode() != null) {
          numNeighbours++;
        }
        if (node.getBottomNode() != null) {
          numNeighbours++;
        }
        if (node.getLeftNode() != null) {
          numNeighbours++;
        }
        if (node.getRightNode() != null) {
          numNeighbours++;
        }

        if (node.getType().equals("C") && (numNeighbours != 1 && numNeighbours != 3
                && numNeighbours != 4)) {
          throw new IllegalArgumentException("Cave can have either 1, 3, or 4 entries!");
        } else if (node.getType().equals("T") && numNeighbours != 2) {
          throw new IllegalArgumentException("Tunnel can have only 2 entries!");
        }
      }
    }
  }

  private Node[][] generateGraph() {
    Node[][] nodes = new Node[this.numRows][this.numCols];
    int counter = 1;
    for (int i = 0; i < nodes.length; i++) {
      for (int j = 0; j < nodes[i].length; j++) {
        nodes[i][j] = new GenericNode(String.valueOf(counter++));
      }
    }

    int idx;
    for (int i = 0; i < this.numRows; i++) {
      for (int j = 0; j < this.numCols; j++) {
        if (isWrapping || (i - 1) >= 0) {
          idx = (i - 1) < 0 ? (this.numRows - 1) : (i - 1);
          nodes[i][j].setTopNode(nodes[idx][j]);
        }

        if (isWrapping || (i + 1) < numRows) {
          idx = (i + 1) >= numRows ? 0 : (i + 1);
          nodes[i][j].setBottomNode(nodes[idx][j]);
        }

        if (isWrapping || (j - 1) >= 0) {
          idx = (j - 1) < 0 ? (this.numCols - 1) : (j - 1);
          nodes[i][j].setLeftNode(nodes[i][idx]);
        }

        if (isWrapping || (j + 1) < numCols) {
          idx = (j + 1) >= numCols ? 0 : (j + 1);
          nodes[i][j].setRightNode(nodes[i][idx]);
        }
      }
    }

    return nodes;
  }

  private Node findNodeByNodeName(String nodeName) {
    for (Node[] nodes : graph) {
      for (Node value : nodes) {
        if (value.getNodeName().equals(nodeName)) {
          return value;
        }
      }
    }

    throw new IllegalArgumentException("Node with name " + nodeName + ", not found in the graph!");
  }

  private List<List<Node>> getNodePairs() {
    Set<List<Node>> nodePairs = new HashSet<>();
    List<Node> nodePair;

    for (Node[] nodes : graph) {
      for (Node node : nodes) {
        if (node.getLeftNode() != null) {
          nodePair = new ArrayList<>();
          nodePair.add(node);
          nodePair.add(node.getLeftNode());
          nodePair.sort(Comparator.comparing(Node::getNodeName));
          nodePairs.add(nodePair);
        }
        if (node.getRightNode() != null) {
          nodePair = new ArrayList<>();
          nodePair.add(node);
          nodePair.add(node.getRightNode());
          nodePair.sort(Comparator.comparing(Node::getNodeName));
          nodePairs.add(nodePair);
        }
        if (node.getTopNode() != null) {
          nodePair = new ArrayList<>();
          nodePair.add(node);
          nodePair.add(node.getTopNode());
          nodePair.sort(Comparator.comparing(Node::getNodeName));
          nodePairs.add(nodePair);
        }
        if (node.getBottomNode() != null) {
          nodePair = new ArrayList<>();
          nodePair.add(node);
          nodePair.add(node.getBottomNode());
          nodePair.sort(Comparator.comparing(Node::getNodeName));
          nodePairs.add(nodePair);
        }
      }
    }

    return new ArrayList<>(nodePairs);
  }

  private List<List<Node>> getInitialClouds() {
    List<List<Node>> clouds = new ArrayList<>();
    List<Node> cloud;

    for (Node[] nodes : graph) {
      for (Node node : nodes) {
        cloud = new ArrayList<>();
        cloud.add(node);
        clouds.add(cloud);
      }
    }

    return clouds;
  }

  private void deleteCloud(List<Node> removeCloud, List<List<Node>> clouds) {
    for (int i = 0; i < clouds.size(); i++) {
      if (clouds.get(i).equals(removeCloud)) {
        clouds.remove(removeCloud);
      }
    }
  }

  private List<Node> findCloud(Node removeCloud, List<List<Node>> clouds) {
    for (List<Node> cloud : clouds) {
      if (cloud.contains(removeCloud)) {
        return cloud;
      }
    }

    return null;
  }

  private void removeNeighbour(Node sourceNode, Node targetNode) {
    if (sourceNode.getLeftNode() != null && sourceNode.getLeftNode() == targetNode) {
      sourceNode.setLeftNode(null);
      targetNode.setRightNode(null);
    } else if (sourceNode.getRightNode() != null && sourceNode.getRightNode() == targetNode) {
      sourceNode.setRightNode(null);
      targetNode.setLeftNode(null);
    } else if (sourceNode.getTopNode() != null && sourceNode.getTopNode() == targetNode) {
      sourceNode.setTopNode(null);
      targetNode.setBottomNode(null);
    } else if (sourceNode.getBottomNode() != null && sourceNode.getBottomNode() == targetNode) {
      sourceNode.setBottomNode(null);
      targetNode.setTopNode(null);
    }
  }

  private String getNodeDirection(Node node1, Node node2) {
    if (node1.getLeftNode() != null
            && node1.getLeftNode().getNodeName().equals(node2.getNodeName())) {
      return "L";
    } else if (node1.getRightNode() != null
            && node1.getRightNode().getNodeName().equals(node2.getNodeName())) {
      return "R";
    } else if (node1.getTopNode() != null
            && node1.getTopNode().getNodeName().equals(node2.getNodeName())) {
      return "U";
    } else if (node1.getBottomNode() != null
            && node1.getBottomNode().getNodeName().equals(node2.getNodeName())) {
      return "D";
    }

    return "";
  }

  private void mergeClouds(List<Node> nodePair, List<List<Node>> clouds,
                           List<List<Node>> leftOver, List<String> leftOverDirection) {
    for (int i = 0; i < clouds.size(); i++) {
      if (clouds.get(i).contains(nodePair.get(0))) {
        List<Node> toRemove = findCloud(nodePair.get(1), clouds);
        if (toRemove != null) {
          if (toRemove.contains(nodePair.get(0))) {
            leftOver.add(nodePair);
            leftOverDirection.add(getNodeDirection(nodePair.get(0), nodePair.get(1)));
            removeNeighbour(nodePair.get(0), nodePair.get(1));
            removeNeighbour(nodePair.get(1), nodePair.get(0));
          } else {
            clouds.get(i).addAll(toRemove);
            deleteCloud(toRemove, clouds);
          }
        }
      }
    }
  }

  private void addConnection(Node node1, Node node2, String direction) {
    switch (direction) {
      case "L": {
        node1.setLeftNode(node2);
        node2.setRightNode(node1);
        break;
      }
      case "R": {
        node1.setRightNode(node2);
        node2.setLeftNode(node1);
        break;
      }
      case "U": {
        node1.setTopNode(node2);
        node2.setBottomNode(node1);
        break;
      }
      case "D": {
        node1.setBottomNode(node2);
        node2.setTopNode(node1);
        break;
      }
      default: {
        break;
      }
    }
  }

  private void generateMinSpanTree(List<List<Node>> leftOver, List<String> directionLeftOver) {
    List<List<Node>> clouds = getInitialClouds();
    List<List<Node>> nodePairs = getNodePairs();

    int randIdx;
    List<Node> nodePair;

    // Randomly choose node pairs and connect them,
    // merge clouds if possible or put them in leftover.
    do {
      randIdx = random.nextInt(nodePairs.size());
      nodePair = nodePairs.get(randIdx);
      mergeClouds(nodePair, clouds, leftOver, directionLeftOver);
      nodePairs.remove(randIdx);
    }
    while (nodePairs.size() != 0);
  }

  private void increaseInterconnectivity(List<List<Node>> leftOver,
                                         List<String> directionLeftOver) {
    int randIdx;
    if (interconnectivity > 0) {
      if (interconnectivity > Math.min(numRows, numCols) && !forceInterconnectivityRange) {
        throw new IllegalArgumentException("Max interconnectivity allowed = "
                + Math.min(numRows, numCols) + ", greater interconnectivities do not guarantee "
                + "viable start and end positions in dungeon, if you still want to increase "
                + "interconnectivity then set forceInterconnectivityRange to true!");
      } else if (forceInterconnectivityRange && interconnectivity > leftOver.size()) {
        throw new IllegalArgumentException("Max interconnectivity cannot exceed "
                + leftOver.size() + "!");
      }

      List<Node> nodePair;
      int numInterconnected = 0;
      String nodeDirection;
      do {
        randIdx = random.nextInt(leftOver.size());
        nodePair = leftOver.get(randIdx);
        nodeDirection = directionLeftOver.get(randIdx);
        addConnection(nodePair.get(0), nodePair.get(1), nodeDirection);
        leftOver.remove(randIdx);
        directionLeftOver.remove(randIdx);
        numInterconnected++;
      }
      while (numInterconnected != interconnectivity);
    }
  }

  private List<Node> castNodesToCaveTunnelNode() {
    int numEntries;
    List<Node> caves = new ArrayList<>();
    for (int i = 0; i < graph.length; i++) {
      for (int j = 0; j < graph[i].length; j++) {
        numEntries = 0;
        if (graph[i][j].getLeftNode() != null) {
          numEntries++;
        }
        if (graph[i][j].getRightNode() != null) {
          numEntries++;
        }
        if (graph[i][j].getTopNode() != null) {
          numEntries++;
        }
        if (graph[i][j].getBottomNode() != null) {
          numEntries++;
        }

        if (numEntries == 1 || numEntries == 3 || numEntries == 4) {
          graph[i][j] = graph[i][j].castToCaveNode();
          caves.add(graph[i][j]);
        } else if (numEntries == 2) {
          graph[i][j] = graph[i][j].castToTunnelNode();
        }

        graph[i][j].updateCastInNeighbours();
      }
    }

    return caves;
  }

  private void addTreasureToCaves(List<Node> caves) {
    int randIdx;
    float actualPercent = (float) (percentCavesWithTreasureArrows
            + Math.random() * (1 - percentCavesWithTreasureArrows));
    int numCavesWithTreasures = (int) Math.ceil(actualPercent * caves.size());
    int numCavesWithTreasureAssigned = 0;
    Node cave;
    do {
      randIdx = random.nextInt(caves.size());
      cave = caves.get(randIdx);
      cave.placeTreasure(random);
      numCavesWithTreasureAssigned++;
      caves.remove(randIdx);
    }
    while (numCavesWithTreasureAssigned != numCavesWithTreasures);
  }

  private void hookRandomMoveStrategyToYugoloth(Yugoloth yugoloth) {
    String[] strategies = {"RandomMoveStrategy"};

    int randomIdx = (int) (Math.random() * strategies.length);

    if (strategies[randomIdx].equals("RandomMoveStrategy")) {
      yugoloth.hookMoveStrategy(new RandomMove());
    }
  }

  private void addOtyughsToCave(List<Node> caves) {
    // One Otyugh has to be added to end node, so saving it for later.
    int numOtyughsAdded = 1;

    int randIdx;
    Node cave;
    do {
      randIdx = random.nextInt(caves.size());
      cave = caves.get(randIdx);
      Otyugh otyugh = new Otyugh();
      this.otyughs.add(otyugh);
      cave.placeOtyugh(otyugh);
      numOtyughsAdded++;
      caves.remove(randIdx);
    }
    while (numOtyughsAdded != numOtyughs);
  }

  private void addArrowsToCavesAndTunnels(List<Node> locations) {
    int numArrowsAdded = 0;
    float actualPercent = (float) (percentCavesWithTreasureArrows
            + Math.random() * (1 - percentCavesWithTreasureArrows));
    int numLocationsWithArrows = (int) Math.ceil(actualPercent * locations.size());

    int randIdx;
    do {
      randIdx = random.nextInt(locations.size());
      locations.get(randIdx).addArrows(random);
      numArrowsAdded++;
      locations.remove(randIdx);
    }
    while (numArrowsAdded != numLocationsWithArrows);
  }

  /*
    Kruskal's algorithm taken partly from:
    https://www.geeksforgeeks.org/kruskals-minimum-spanning-tree-algorithm-greedy-algo-2/
   */
  private void generateRandomDungeon() {
    // Generate MST, interconnectivity = 0
    List<List<Node>> leftOver = new ArrayList<>();
    List<String> directionLeftOver = new ArrayList<>();
    generateMinSpanTree(leftOver, directionLeftOver);


    // Process the leftovers, connect the required number of
    // leftovers to increase interconnectivity.
    increaseInterconnectivity(leftOver, directionLeftOver);

    // Cast GenericNode to CaveNode or TunnelNode based on number of openings
    List<Node> caves = castNodesToCaveTunnelNode();
    List<Node> cavesCopy = new ArrayList<>(caves);

    // Add otyughs to caves
    if (numOtyughs > caves.size()) {
      throw new IllegalArgumentException("Number of otyughs cannot exceed number of caves!");
    }

    addOtyughsToCave(cavesCopy);

    // Add treasures to caves
    addTreasureToCaves(caves);

    // Add arrows to caves and tunnels
    List<Node> allLocations = new ArrayList<>();
    for (Node[] row : graph) {
      allLocations.addAll(Arrays.asList(row));
    }

    addArrowsToCavesAndTunnels(allLocations);

    // Generate random start and end nodes
    generateRandomStartEndNode();

    // Add an otyugh at the end node
    addOtyughToEndCave();

    // Add yugoloth to the first available cave or tunnel
    addYugolothToDungeon();

    // Add thief to the first available cave or tunnel
    addThiefToDungeon();

    // Add pit to the first available cave
    addPitToDungeon();
  }

  private void addPitToDungeon() {
    // Pit is added to the first available cave,
    // that does not have a yugoloth, and is not the start cave,
    // and does not have a theif in it.
    boolean isPitAdded = false;
    for (Node[] row : graph) {
      for (Node node : row) {
        if (node.getType().equals("C") && node.getOtyugh() == null
                && !node.getNodeName().equals(yugolothPosition.getNodeName())
                && !node.getNodeName().equals(startNode.getNodeName())
                && !node.getNodeName().equals(thiefPosition.getNodeName())) {
          node.addPit();
          this.pitPosition = node;
          isPitAdded = true;
          break;
        }
      }

      if (isPitAdded) {
        break;
      }
    }
  }

  private void addThiefToDungeon() {
    // Thief is added to the first available cave,
    // that does not have a yugoloth, and is not the start cave.
    boolean isThiefPlaced = false;
    for (Node[] row : this.graph) {
      for (Node node : row) {
        if (node.getType().equals("C") && node.getOtyugh() == null
                && !node.getNodeName().equals(yugolothPosition.getNodeName())
                && !node.getNodeName().equals(startNode.getNodeName())) {
          node.placeThief();
          this.thiefPosition = node;
          isThiefPlaced = true;
          break;
        }
      }

      if (isThiefPlaced) {
        break;
      }
    }
  }

  private void addYugolothToDungeon() {
    // Yugoloth is added to the first available cave,
    // that is not the start node.
    boolean isYugolothPlaced = false;
    for (Node[] row : this.graph) {
      for (Node node : row) {
        if (node.getType().equals("C") && node.getOtyugh() == null
                && !node.getNodeName().equals(startNode.getNodeName())) {
          node.placeYugoloth(this.yugoloth);
          this.yugolothPosition = node;
          isYugolothPlaced = true;
          break;
        }
      }

      if (isYugolothPlaced) {
        break;
      }
    }
  }

  private void addOtyughToEndCave() {
    if (endNode.getOtyugh() == null) {
      Otyugh otyugh = new Otyugh();
      endNode.placeOtyugh(otyugh);
      endNode.updateCastInNeighbours();
      this.otyughs.add(otyugh);
    } else {
      boolean foundIdx = false;
      for (Node[] row : graph) {
        for (Node node : row) {
          if (node.getType().equals("C") && node.getOtyugh() == null
                  && !node.getNodeName().equals(startNode.getNodeName())) {
            Otyugh otyugh = new Otyugh();
            node.placeOtyugh(otyugh);
            node.updateCastInNeighbours();
            this.otyughs.add(otyugh);
            foundIdx = true;
            break;
          }
        }

        if (foundIdx) {
          break;
        }
      }
    }
  }

  private void generateRandomStartEndNode() {
    int i = 0;
    String startNodeStr;
    String endNodeStr;

    boolean foundStartEndNodes = false;
    while (i < MAX_PATIENCE_START_END_GEN) {
      startNodeStr = String.valueOf(random.nextInt((numRows * numCols) - 1) + 1);
      endNodeStr = String.valueOf(random.nextInt((numRows * numCols) - 1) + 1);

      if (startNodeStr.equals(endNodeStr)) {
        i++;
        continue;
      }

      startNode = findNodeByNodeName(startNodeStr);
      endNode = findNodeByNodeName(endNodeStr);

      if (startNode.getType().equals("C") && startNode.getOtyugh() != null) {
        i++;
        continue;
      }

      int distance = findShortestPathDistance(startNode, endNode);
      if (distance >= 5 && endNode.getType().equals("C")) {
        foundStartEndNodes = true;
        break;
      }

      i++;
    }

    if (!foundStartEndNodes) {
      throw new IllegalArgumentException("Cannot find any start and end nodes with shortest path "
              + "length of 5");
    }

    for (Node[] nodes : graph) {
      for (Node node : nodes) {
        if (node.getNodeName().equals(startNode.getNodeName())) {
          startNode = node;
          startNode.updateCastInNeighbours();
        }
      }
    }
  }

  boolean placePlayerInDungeon(Player player) {
    if (this.player != null && this.player == player) {
      throw new IllegalArgumentException("Cannot place same player in dungeon more than once!");
    } else if (this.player != null) {
      throw new IllegalArgumentException("Cannot place more than one player in dungeon!");
    }

    ValueSanity.checkNull("Player to be entered to dungeon", player);

    this.player = player;
    this.playerPosition = startNode;
    return false;
  }

  private Node getPlayerCurrentPosition() {
    if (playerPosition == null) {
      throw new IllegalArgumentException("Player not placed in dungeon!");
    }

    return playerPosition;
  }

  private boolean killPlayer(Node node) {
    if (node.getType().equals("C") && node.getOtyugh() != null) {
      if (node.getType().equals("C") && node.getOtyugh().getHealth() == 100) {
        return true;
      } else if (node.getOtyugh().getHealth() == 50) {
        return (this.random.nextInt(101 - 1) + 1) <= 50;
      }
    }

    return false;
  }

  boolean movePlayer(String direction, Player movingPlayer) {
    ValueSanity.checkNull("Direction in which to move", direction);

    if (player == null) {
      throw new IllegalArgumentException("No player placed in dungeon!");
    } else if (player != movingPlayer) {
      throw new IllegalArgumentException("This player is not placed in the dungeon!");
    }

    switch (direction) {
      case "L": {
        Node leftNode = getPlayerCurrentPosition().getLeftNode();
        if (leftNode == null) {
          throw new IllegalArgumentException("Cannot move west!");
        }

        this.playerPosition = leftNode;
        return killPlayer(leftNode);
      }
      case "R": {
        Node rightNode = getPlayerCurrentPosition().getRightNode();
        if (rightNode == null) {
          throw new IllegalArgumentException("Cannot move east!");
        }

        this.playerPosition = rightNode;
        return killPlayer(rightNode);
      }
      case "U": {
        Node topNode = getPlayerCurrentPosition().getTopNode();
        if (topNode == null) {
          throw new IllegalArgumentException("Cannot move north!");
        }

        this.playerPosition = topNode;
        return killPlayer(topNode);
      }
      case "D": {
        Node bottomNode = getPlayerCurrentPosition().getBottomNode();
        if (bottomNode == null) {
          throw new IllegalArgumentException("Cannot move south!");
        }

        this.playerPosition = bottomNode;
        return killPlayer(bottomNode);
      }
      default: {
        throw new IllegalArgumentException("Invalid direction!");
      }
    }
  }

  void pickUpTreasure(String treasureName, Player pickingPlayer) {
    ValueSanity.checkNull("Treasure to pick up", treasureName);
    ValueSanity.checkNull("Player picking up treasure", pickingPlayer);

    if (this.player == null) {
      throw new IllegalArgumentException("No player placed in dungeon!");
    } else if (this.player != pickingPlayer) {
      throw new IllegalArgumentException("This player is not placed in the dungeon!");
    }

    this.playerPosition.removeTreasure(treasureName);
  }

  void pickUpArrow(Player pickingPlayer) {
    ValueSanity.checkNull("Player to pick up arrow", pickingPlayer);

    if (this.player == null) {
      throw new IllegalArgumentException("No player placed in dungeon!");
    } else if (this.player != pickingPlayer) {
      throw new IllegalArgumentException("This player is not placed in the dungeon!");
    }

    this.playerPosition.removeArrow();
  }

  private Node getNodeInDirection(Node currentNode, String direction) {
    switch (direction) {
      case "L": {
        return currentNode.getLeftNode();
      }
      case "R": {
        return currentNode.getRightNode();
      }
      case "U": {
        return currentNode.getTopNode();
      }
      case "D": {
        return currentNode.getBottomNode();
      }
      default: {
        throw new IllegalArgumentException("Invalid direction!");
      }
    }
  }

  List<Integer> shootArrow(String direction, int distance, Player shootingPlayer) {
    ValueSanity.checkNull("Direction in which to shoot", direction);
    ValueSanity.checkNull("Player shooting arrow", shootingPlayer);

    if (this.player == null) {
      throw new IllegalArgumentException("No player placed in dungeon!");
    } else if (this.player != shootingPlayer) {
      throw new IllegalArgumentException("This player is not placed in the dungeon!");
    }

    int arrowDistanceTravelled = 0;

    Node tempCurrentNode;
    Node currentArrowPosition = getPlayerCurrentPosition();
    boolean isFirstNode = true;
    String currentHeading = direction;
    do {
      if (currentArrowPosition.getType().equals("C") && !isFirstNode) {
        arrowDistanceTravelled++;
      }
      isFirstNode = false;

      if (arrowDistanceTravelled == distance) {
        break;
      }

      tempCurrentNode = getNodeInDirection(currentArrowPosition, currentHeading);
      if (tempCurrentNode == null) {
        break;
      }
      currentArrowPosition = tempCurrentNode;

      if (currentArrowPosition.getType().equals("T")) {
        if (currentArrowPosition.getLeftNode() != null && !currentHeading.equals("R")) {
          currentHeading = "L";
        } else if (currentArrowPosition.getRightNode() != null && !currentHeading.equals("L")) {
          currentHeading = "R";
        } else if (currentArrowPosition.getTopNode() != null && !currentHeading.equals("D")) {
          currentHeading = "U";
        } else if (currentArrowPosition.getBottomNode() != null && !currentHeading.equals("U")) {
          currentHeading = "D";
        }
      }

    }
    while (arrowDistanceTravelled != distance);

    List<Integer> result = new ArrayList<>();
    int currentArrowPositionI = -1;
    int currentArrowPositionJ = -1;
    for (int i = 0; i < this.graph.length; i++) {
      for (int j = 0; j < this.graph[i].length; j++) {
        if (this.graph[i][j].getNodeName().equals(currentArrowPosition.getNodeName())) {
          currentArrowPositionI = i;
          currentArrowPositionJ = j;
        }
      }
    }
    result.add(currentArrowPositionI);
    result.add(currentArrowPositionJ);

    if (arrowDistanceTravelled == distance) {
      if (currentArrowPosition.getType().equals("C")) {
        if (currentArrowPosition.getOtyugh() != null) {
          int newHealth = currentArrowPosition.getOtyugh().gotHitByArrow();
          currentArrowPosition.setOtyughHealth(newHealth);
          if (currentArrowPosition.getOtyugh().getHealth() <= 0) {
            Otyugh otyughToBeRemoved = null;
            for (Otyugh otyugh : this.otyughs) {
              if (otyugh.getName().equals(currentArrowPosition.getOtyugh().getName())) {
                otyughToBeRemoved = otyugh;
              }
            }

            this.otyughs.remove(otyughToBeRemoved);

            currentArrowPosition.removeOtyugh();

            result.add(1);
          }
          result.add(0);
        }
      } else if (currentArrowPosition.getType().equals("T")) {
        currentArrowPosition.addSingleArrow();
      }
    } else {
      currentArrowPosition.addSingleArrow();
    }

    result.add(-1);

    return result;
  }

  /**
   * Checks if the entered player has reached the end node or not.
   *
   * @return true if the player has reached the end node, false otherwise
   */
  @Override
  public boolean hasReachedEndNode() {
    ValueSanity.checkNull("Player in dungeon", player);

    return getPlayerCurrentPosition().getNodeName().equals(endNode.getNodeName());
  }

  /**
   * Returns the graph of the maze in the dungeon.
   *
   * @return the graph of the maze
   */
  @Override
  public Node[][] getMaze() {
    Node[][] graphCopy = new Node[graph.length][graph[0].length];
    for (int i = 0; i < graph.length; i++) {
      for (int j = 0; j < graph[i].length; j++) {
        graphCopy[i][j] = graph[i][j].cloneNode();
      }
    }

    return graphCopy;
  }

  /**
   * Returns the current position of the player in the dungeon.
   *
   * @return the current position of the player in the dungeon
   * @throws IllegalArgumentException if the player is not placed in the dungeon
   */
  @Override
  public Node getCurrentPositionPlayer() {
    return getPlayerCurrentPosition().cloneNode();
  }

  /**
   * Returns the configurations used to construct the dungeon.
   *
   * @return the configurations used to construct the dungeon
   */
  @Override
  public List<Object> getDungeonConfig() {
    List<Object> dungeonConfig = new ArrayList<>();
    dungeonConfig.add(numRows);
    dungeonConfig.add(numCols);
    dungeonConfig.add(interconnectivity);
    dungeonConfig.add(isWrapping);
    dungeonConfig.add(percentCavesWithTreasureArrows);
    dungeonConfig.add(forceInterconnectivityRange);
    dungeonConfig.add(numOtyughs);

    return dungeonConfig;
  }

  /**
   * Returns the starting node of the dungeon.
   *
   * @return the starting node of the dungeon
   */
  @Override
  public Node getStartNode() {
    return this.startNode.cloneNode();
  }

  /**
   * Returns the row number of the start node of the dungeon.
   *
   * @return the row number of the start node of the dungeon
   */
  @Override
  public int getStartNodeI() {
    for (int i = 0; i < graph.length; i++) {
      for (int j = 0; j < graph[i].length; j++) {
        if (graph[i][j].getNodeName().equals(startNode.getNodeName())) {
          return i;
        }
      }
    }

    return -1;
  }

  /**
   * Returns the column number of the start node of the dungeon.
   *
   * @return the column number of the start node of the dungeon
   */
  @Override
  public int getStartNodeJ() {
    for (int i = 0; i < graph.length; i++) {
      for (int j = 0; j < graph[i].length; j++) {
        if (graph[i][j].getNodeName().equals(startNode.getNodeName())) {
          return j;
        }
      }
    }

    return -1;
  }

  /**
   * Returns the end node of the dungeon.
   *
   * @return the end node of the dungeon
   */
  @Override
  public Node getEndNode() {
    return this.endNode.cloneNode();
  }

  /**
   * Returns the row number of the current position of all otyughs.
   *
   * @return the row number of the current position of all otyughs
   */
  @Override
  public List<Integer> getOtyughNodeIs() {
    List<Integer> otyughNodeIs = new ArrayList<>();
    for (int i = 0; i < graph.length; i++) {
      for (int j = 0; j < graph[i].length; j++) {
        if (graph[i][j].getType().equals("C") && graph[i][j].getOtyugh() != null) {
          otyughNodeIs.add(i);
        }
      }
    }

    return otyughNodeIs;
  }

  /**
   * Returns the column number of the current position of all otyughs.
   *
   * @return the column number of the current position of all otyughs
   */
  @Override
  public List<Integer> getOtyughNodeJs() {
    List<Integer> otyughNodeJs = new ArrayList<>();
    for (int i = 0; i < graph.length; i++) {
      for (int j = 0; j < graph[i].length; j++) {
        if (graph[i][j].getType().equals("C") && graph[i][j].getOtyugh() != null) {
          otyughNodeJs.add(j);
        }
      }
    }

    return otyughNodeJs;
  }

  /**
   * Gets a map of all the treasures along with their locations.
   *
   * @return a map of all the treasures along with their locations
   */
  @Override
  public Map<List<Integer>, List<String>> getTreasureAndLocations() {
    Map<List<Integer>, List<String>> treasureNameAndLocations = new HashMap<>();

    List<String> treasureNames;
    List<Integer> location;
    for (int i = 0; i < graph.length; i++) {
      for (int j = 0; j < graph[i].length; j++) {
        if (graph[i][j].getType().equals("C")) {
          treasureNames = graph[i][j].getTreasures();
          if (treasureNames != null) {
            location = new ArrayList<>();
            location.add(i);
            location.add(j);

            treasureNameAndLocations.put(location, treasureNames);
          }
        }
      }
    }

    return treasureNameAndLocations;
  }

  /**
   * Checks if the dungeon is wrapping or not.
   *
   * @return true if the dungeon is wrapping, false otherwise
   */
  @Override
  public boolean isWrapping() {
    return isWrapping;
  }

  /**
   * Moves a yugoloth in the dungeon.
   */
  @Override
  public void moveYugoloth() {
    this.yugolothPosition = this.yugoloth.move(this.yugolothPosition);
  }

  /**
   * Returns the number of otyughs in the dungeon.
   *
   * @return the number of otyughs in the dungeon
   */
  @Override
  public int getNumOtyughs() {
    return numOtyughs;
  }

  /*
    Shortest distance taken from:
   */
  private int findShortestPathDistance(Node startNode, Node endNode) {
    List<String> visitedNodes = new ArrayList<>();
    Map<String, Integer> distances = new TreeMap<>();

    List<Node> queue = new ArrayList<>();

    visitedNodes.add(startNode.getNodeName());
    queue.add(startNode);
    distances.put(startNode.getNodeName(), 0);

    Node topNode;
    List<Node> neighbours;
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
          visitedNodes.add(neighbour.getNodeName());
          distances.put(neighbour.getNodeName(), distances.get(topNode.getNodeName()) + 1);
          queue.add(neighbour);
        }

        if (neighbour.getNodeName().equals(endNode.getNodeName())) {
          return distances.get(topNode.getNodeName()) + 1;
        }
      }
    }

    return -1;
  }

  /**
   * String representation of a dungeon containing information about the maze,
   * the starting and ending nodes and the list of caves and the treasures in them.
   *
   * @return the string representation of the dungeon
   */
  @Override
  public String toString() {
    String whiteSpaces;
    StringBuilder singleRow;
    StringBuilder bottomArrows;
    StringBuilder dungeonSB = new StringBuilder();

    for (Node[] nodes : graph) {
      singleRow = new StringBuilder();
      bottomArrows = new StringBuilder();
      for (Node node : nodes) {
        whiteSpaces = ("" + node.getNodeName()).length() == 1 ? " " : "";
        singleRow.append(whiteSpaces).append(node.getNodeName());

        if (node.getNodeName().equals(startNode.getNodeName())) {
          singleRow.append("*");
        } else if (node.getNodeName().equals(endNode.getNodeName())) {
          singleRow.append("+");
        } else {
          singleRow.append(" ");
        }

        singleRow.append(": ").append(node.getType());

        if (node.getRightNode() != null) {
          singleRow.append(" - ");
        } else {
          singleRow.append("   ");
        }

        if (node.getBottomNode() != null) {
          bottomArrows.append("     |   ");
        } else {
          bottomArrows.append("         ");
        }
      }
      dungeonSB.append(singleRow).append("\n");
      dungeonSB.append(bottomArrows).append("\n");
    }

    dungeonSB.append("Treasures in caves: \n");
    for (Node[] nodes : graph) {
      for (Node node : nodes) {
        if (node.getType().equals("C")) {
          dungeonSB
                  .append(node.getNodeName())
                  .append(": ")
                  .append(node.getTreasures())
                  .append("\n");
        }
      }
    }

    dungeonSB.append("Otyughs in caves: \n");
    Otyugh otyugh;
    for (Node[] nodes : graph) {
      for (Node node : nodes) {
        if (node.getType().equals("C")) {
          otyugh = node.getOtyugh();

          if (otyugh != null) {
            dungeonSB
                    .append(node.getNodeName())
                    .append(": ")
                    .append(node.getOtyugh())
                    .append("\n");
          } else {
            dungeonSB
                    .append(node.getNodeName())
                    .append(": ")
                    .append("No otyugh")
                    .append("\n");
          }
        }
      }
    }

    dungeonSB.append("Arrows in locations: \n");
    for (Node[] nodes : graph) {
      for (Node node : nodes) {
        dungeonSB
                .append(node.getNodeName())
                .append(": ")
                .append(node.getNumArrows())
                .append("\n");
      }
    }

    dungeonSB.append("\n* - Start Node (").append(startNode.getNodeName()).append(")").append("\n");
    dungeonSB.append("+ - End Node (").append(endNode.getNodeName()).append(")").append("\n");

    return dungeonSB.toString();
  }

  private void setInitialMaze() {
    this.initialMaze = new Node[this.graph.length][this.graph[0].length];

    for (int i = 0; i < this.graph.length; i++) {
      for (int j = 0; j < this.graph[i].length; j++) {
        Node nodeClone = this.graph[i][j].cloneNode();
        this.initialMaze[i][j] = nodeClone;
      }
    }
  }

  /**
   * Returns the initial maze of the dungeon before the game starts.
   *
   * @return the initial maze
   */
  @Override
  public Node[][] getInitialMaze() {
    return this.initialMaze;
  }

  /**
   * Returns the row number of the current position of yugoloth.
   *
   * @return the row number of the current position of yugoloth
   */
  @Override
  public int getYugolothNodeI() {
    for (int i = 0; i < this.graph.length; i++) {
      for (int j = 0; j < this.graph[i].length; j++) {
        if (this.graph[i][j].getNodeName().equals(this.yugolothPosition.getNodeName())) {
          return i;
        }
      }
    }

    return -1;
  }

  /**
   * Returns the column number of the current position of yugoloth.
   *
   * @return the column number of the current position of yugoloth
   */
  @Override
  public int getYugolothNodeJ() {
    for (int i = 0; i < this.graph.length; i++) {
      for (int j = 0; j < this.graph[i].length; j++) {
        if (this.graph[i][j].getNodeName().equals(this.yugolothPosition.getNodeName())) {
          return j;
        }
      }
    }

    return -1;
  }

  /**
   * Performs a hand to hand battle between the player and the Yugoloth.
   *
   * @return true if the player wins the hand to hand battle, false otherwise
   */
  @Override
  public boolean handToHandBattle() {
    // The player dies with 50% probability
    return (this.random.nextInt(101 - 1) + 1) <= 50;
  }

  /**
   * Kills the yugoloth in the dungeon.
   */
  @Override
  public void killYugoloth() {
    this.yugoloth = null;
    this.yugolothPosition = null;
  }

  /**
   * Returns the current position of yugoloth in the dungeon.
   *
   * @return the current position of yugoloth in the dungeon
   */
  @Override
  public Node getYugolothPosition() {
    return this.yugolothPosition;
  }

  /**
   * Returns the initial starting position of the yugoloth.
   *
   * @return the initial starting position of the yugoloth
   */
  @Override
  public Node getInitialYugolothPosition() {
    for (Node[] nodes : this.initialMaze) {
      for (Node node : nodes) {
        if (node.getYugoloth() != null) {
          return node;
        }
      }
    }

    return null;
  }

  /**
   * Returns the row number of the current position of the thief.
   *
   * @return the row number of the current position of the thief
   */
  @Override
  public int getThiefNodeI() {
    for (int i = 0; i < this.graph.length; i++) {
      for (int j = 0; j < this.graph[i].length; j++) {
        if (this.graph[i][j].getNodeName().equals(this.thiefPosition.getNodeName())) {
          return i;
        }
      }
    }

    return -1;
  }

  /**
   * Returns the column number of the current position of the thief.
   *
   * @return the column number of the current position of the thief
   */
  @Override
  public int getThiefNodeJ() {
    for (int i = 0; i < this.graph.length; i++) {
      for (int j = 0; j < this.graph[i].length; j++) {
        if (this.graph[i][j].getNodeName().equals(this.thiefPosition.getNodeName())) {
          return j;
        }
      }
    }

    return -1;
  }

  /**
   * Returns the yugoloth in the dungeon.
   *
   * @return the yugoloth in the dungeon
   */
  @Override
  public Yugoloth getYugoloth() {
    return this.yugoloth;
  }

  /**
   * Returns the thief in the dungeon.
   *
   * @return the thief in the dungeon
   */
  @Override
  public Node getThiefPosition() {
    return this.thiefPosition.cloneNode();
  }

  /**
   * Returns the position of pit in the dungeon.
   *
   * @return the position of pit in the dungeon
   */
  @Override
  public Node getPitPosition() {
    return this.pitPosition.cloneNode();
  }

  /**
   * Returns the row number of the pit in the dungeon.
   *
   * @return the row number of the pit in the dungeon
   */
  @Override
  public int getPitNodeI() {
    for (int i = 0; i < this.graph.length; i++) {
      for (int j = 0; j < this.graph[i].length; j++) {
        if (this.graph[i][j].getNodeName().equals(this.pitPosition.getNodeName())) {
          return i;
        }
      }
    }

    return -1;
  }

  /**
   * Returns the column number of the pit in the dungeon.
   *
   * @return the column number of the pit in the dungeon
   */
  @Override
  public int getPitNodeJ() {
    for (int i = 0; i < this.graph.length; i++) {
      for (int j = 0; j < this.graph[i].length; j++) {
        if (this.graph[i][j].getNodeName().equals(this.pitPosition.getNodeName())) {
          return j;
        }
      }
    }

    return -1;
  }
}

