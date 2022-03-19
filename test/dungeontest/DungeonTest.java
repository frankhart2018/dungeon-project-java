package dungeontest;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import dungeongame.Dungeon;
import dungeongame.DungeonImpl;
import dungeongame.Otyugh;
import dungeongame.Player;
import dungeongame.PlayerImpl;
import dungeongame.Yugoloth;
import node.GenericNode;
import node.Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for the Dungeon class.
 */
public class DungeonTest {
  private static final int SEED = 42;

  private Dungeon deterministicDungeon;
  private Player player;

  @Before
  public void setUp() {
    this.deterministicDungeon = getDeterministicDungeon("2", "13");
    this.player = new PlayerImpl("TestPlayer");
  }

  private void addConnection(Node[][] graph, int i, int j, String direction) {
    int idx;
    switch (direction) {
      case "U":
        idx = ((i - 1) > 0) ? i - 1 : graph.length - 1;
        graph[i][j].setTopNode(graph[idx][j]);
        break;
      case "D":
        idx = ((i + 1) < graph.length) ? i + 1 : 0;
        graph[i][j].setBottomNode(graph[idx][j]);
        break;
      case "L":
        idx = ((j - 1) > 0) ? j - 1 : graph[i].length - 1;
        graph[i][j].setLeftNode(graph[i][idx]);
        break;
      case "R":
        idx = ((j + 1) < graph[i].length) ? j + 1 : 0;
        graph[i][j].setRightNode(graph[i][idx]);
        break;
      default:
        break;
    }
  }

  private Dungeon getDeterministicDungeon(String... otyughPositions) {
    Node[][] graph = new Node[4][4];

    int k = 1;
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        graph[i][j] = new GenericNode(String.valueOf(k++));
      }
    }

    String[][][] connections = {{{"R", "D"}, {"L", "U", "R", "D"}, {"L", "R"}, {"L", "D"}},
      {{"U", "D"}, {"U", "D"}, {"R"}, {"L", "U", "D"}},
      {{"U", "R"}, {"L", "U", "R", "D"}, {"L", "R", "D"}, {"L", "U"}},
      {{"R"}, {"L", "U", "D"}, {"U", "R"}, {"L"}}};

    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        for (String direction : connections[i][j]) {
          addConnection(graph, i, j, direction);
        }
      }
    }

    String[][] nodeTypes = {{"T", "C", "T", "T"}, {"T", "T", "C", "C"}, {"T", "C", "C", "T"},
      {"C", "C", "T", "C"}};

    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        switch (nodeTypes[i][j]) {
          case "T":
            graph[i][j] = graph[i][j].castToTunnelNode();
            break;
          case "C":
            graph[i][j] = graph[i][j].castToCaveNode();
            break;
          default:
            break;
        }

        graph[i][j].updateCastInNeighbours();
      }
    }

    ////////////////////////////////////////////
    // Place treasures
    ////////////////////////////////////////////
    graph[0][1].placeTreasure("RUBY", 1); // 2

    graph[1][2].placeTreasure("RUBY", 1); // 7
    graph[1][2].placeTreasure("DIAMOND", 1); // 7

    graph[2][1].placeTreasure("DIAMOND", 2); // 10

    graph[2][2].placeTreasure("SAPPHIRE", 2); // 11

    ////////////////////////////////////////////
    // Place Otyughs
    ////////////////////////////////////////////
    int otyughName = 1;
    for (String otyughPosition : otyughPositions) {
      for (Node[] row : graph) {
        for (Node node : row) {
          if (node.getNodeName().equals(otyughPosition)) {
            node.placeOtyugh(new Otyugh(String.valueOf(otyughName++), 100));
          }
        }
      }
    }

    ////////////////////////////////////////////
    // Place arrows
    ////////////////////////////////////////////
    graph[0][1].addSingleArrow(); // 2
    graph[0][1].addSingleArrow();

    graph[0][3].addSingleArrow(); // 4
    graph[0][3].addSingleArrow();
    graph[0][3].addSingleArrow();
    graph[0][3].addSingleArrow();

    graph[1][0].addSingleArrow(); // 5

    graph[1][1].addSingleArrow(); // 6
    graph[1][1].addSingleArrow();

    graph[1][3].addSingleArrow(); // 8
    graph[1][3].addSingleArrow();
    graph[1][3].addSingleArrow();
    graph[1][3].addSingleArrow();

    graph[2][1].addSingleArrow(); // 10
    graph[2][1].addSingleArrow();

    graph[2][3].addSingleArrow(); // 12

    graph[3][3].addSingleArrow(); // 16
    graph[3][3].addSingleArrow();

    return new DungeonImpl(graph, "7", "13", true, 2,
            graph[0][1], new Yugoloth(), graph[0][1], graph[0][1], getRandom());
  }

  private boolean makeSeriesOfMoves(String... moves) {
    boolean isPlayerDead = false;
    for (String move : moves) {
      switch (move) {
        case "U":
          this.player.moveUp(this.deterministicDungeon);
          break;
        case "D":
          this.player.moveDown(this.deterministicDungeon);
          break;
        case "L":
          this.player.moveLeft(this.deterministicDungeon);
          break;
        case "R":
          this.player.moveRight(this.deterministicDungeon);
          break;
        default:
          break;
      }
    }

    return isPlayerDead;
  }

  private Random getRandom() {
    return new Random(SEED);
  }

  /**
   * Tests if player has reached end node, when no player has been placed.
   */
  @Test
  public void testReachedEndNodeNoPlayerInDungeon() {
    try {
      assertFalse(this.deterministicDungeon.hasReachedEndNode());
      fail("No player in dungeon");
    } catch (IllegalArgumentException e) {
      assertEquals("Player in dungeon is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests if player has reached end node, when player has not reached end node.
   */
  @Test
  public void testReachedEndNodeNotReached() {
    this.player.enterPlayerToDungeon(this.deterministicDungeon);
    assertFalse(this.deterministicDungeon.hasReachedEndNode());
  }

  /**
   * Tests if player has reached end node, when player has reached end node.
   */
  @Test
  public void testReachedEndNodeReached() {
    this.player.enterPlayerToDungeon(this.deterministicDungeon);

    makeSeriesOfMoves("R", "D", "L", "L", "D");
    this.player.shootArrow(this.deterministicDungeon, "L", 1);
    this.player.shootArrow(this.deterministicDungeon, "L", 1);
    makeSeriesOfMoves("L");
    assertTrue(this.deterministicDungeon.hasReachedEndNode());
  }

  /**
   * Tests fetching the maze in the dungeon.
   */
  @Test
  public void testGetMaze() {
    Node[][] maze = this.deterministicDungeon.getMaze();

    int k = 1;
    String[][][] directionsConnected = {{{"R", "D"}, {"L", "U", "R", "D"}, {"L", "R"}, {"L", "D"}},
      {{"U", "D"}, {"U", "D"}, {"R"}, {"L", "U", "D"}},
      {{"U", "R"}, {"L", "U", "R", "D"}, {"L", "R", "D"}, {"L", "U"}},
      {{"R"}, {"L", "U", "D"}, {"U", "R"}, {"L"}}};
    String[][] nodeTypes = {{"T", "C", "T", "T"}, {"T", "T", "C", "C"}, {"T", "C", "C", "T"},
      {"C", "C", "T", "C"}};
    int[][] numArrows = {{0, 2, 0, 4}, {1, 2, 0, 4}, {0, 2, 0, 1}, {0, 0, 0, 2}};

    for (int i = 0; i < maze.length; i++) {
      for (int j = 0; j < maze[i].length; j++) {
        assertEquals(String.valueOf(k++), maze[i][j].getNodeName());

        for (int l = 0; l < directionsConnected[i][j].length; l++) {
          switch (directionsConnected[i][j][l]) {
            case "U":
              assertNotNull(maze[i][j].getTopNode());
              break;
            case "D":
              assertNotNull(maze[i][j].getBottomNode());
              break;
            case "L":
              assertNotNull(maze[i][j].getLeftNode());
              break;
            case "R":
              assertNotNull(maze[i][j].getRightNode());
              break;
            default:
              break;
          }
        }

        assertEquals(nodeTypes[i][j], maze[i][j].getType());
        assertEquals(numArrows[i][j], maze[i][j].getNumArrows());
      }
    }

    assertEquals("[RUBY]", maze[0][1].getTreasures().toString());
    assertEquals("[RUBY, DIAMOND]", maze[1][2].getTreasures().toString());
    assertEquals("[DIAMOND, DIAMOND]", maze[2][1].getTreasures().toString());
    assertEquals("[SAPPHIRE, SAPPHIRE]", maze[2][2].getTreasures().toString());

    assertEquals("1", maze[0][1].getOtyugh().getName());
    assertEquals(100, maze[0][1].getOtyugh().getHealth());
    assertEquals("2", maze[3][0].getOtyugh().getName());
    assertEquals(100, maze[3][0].getOtyugh().getHealth());
  }

  /**
   * Tests fetching the current position of the player.
   */
  @Test
  public void testGetPlayerCurrentPosition() {
    this.player.enterPlayerToDungeon(this.deterministicDungeon);
    assertEquals("7", this.deterministicDungeon.getCurrentPositionPlayer().getNodeName());
    this.player.moveRight(this.deterministicDungeon);
    assertEquals("8", this.deterministicDungeon.getCurrentPositionPlayer().getNodeName());
  }

  /**
   * Tests fetching dungeon config.
   */
  @Test
  public void testGetDungeonConfig() {
    Dungeon randomDungeon = new DungeonImpl(4, 4, 2, false,
            0.25f, false, 2, getRandom());
    List<Object> actualDungeonConfig = randomDungeon.getDungeonConfig();
    List<Object> expectedDungeonConfig = new ArrayList<>();
    expectedDungeonConfig.add(4);
    expectedDungeonConfig.add(4);
    expectedDungeonConfig.add(2);
    expectedDungeonConfig.add(false);
    expectedDungeonConfig.add(0.25f);
    expectedDungeonConfig.add(false);
    expectedDungeonConfig.add(2);

    assertEquals(expectedDungeonConfig, actualDungeonConfig);
  }

  private void reachableNodes(Node node, int numRows, int numCols, List<String> visitedNodes) {
    if (!visitedNodes.contains(node.getNodeName())) {
      visitedNodes.add(node.getNodeName());
    }

    if (visitedNodes.size() == (numRows * numCols)) {
      return;
    }

    if (node.getLeftNode() != null && !visitedNodes.contains(node.getLeftNode().getNodeName())) {
      reachableNodes(node.getLeftNode(), numRows, numCols, visitedNodes);
    }
    if (node.getRightNode() != null && !visitedNodes.contains(node.getRightNode().getNodeName())) {
      reachableNodes(node.getRightNode(), numRows, numCols, visitedNodes);
    }
    if (node.getTopNode() != null && !visitedNodes.contains(node.getTopNode().getNodeName())) {
      reachableNodes(node.getTopNode(), numRows, numCols, visitedNodes);
    }
    if (node.getBottomNode() != null
            && !visitedNodes.contains(node.getBottomNode().getNodeName())) {
      reachableNodes(node.getBottomNode(), numRows, numCols, visitedNodes);
    }
  }

  private int getNumEdgesInGraph(Node[][] graph) {
    int numEdges = 0;

    for (Node[] nodes : graph) {
      for (Node node : nodes) {
        if (node.getRightNode() != null) {
          numEdges++;
        }

        if (node.getBottomNode() != null) {
          numEdges++;
        }
      }
    }

    return numEdges;
  }

  /**
   * Tests whether each node is connected to every other node or not.
   */
  @Test
  public void testNodeConnectivity() {
    Node[][] graph = this.deterministicDungeon.getMaze();
    Dungeon dungeon = new DungeonImpl(graph, "1", "16",
            false, 2, graph[0][1],
            new Yugoloth(), graph[0][1], graph[0][1]);

    Node[][] maze = dungeon.getMaze();
    int numRows = maze.length;
    int numCols = maze[0].length;
    List<String> visitedNodes;

    for (Node[] row : maze) {
      for (Node node : row) {
        visitedNodes = new ArrayList<>();
        reachableNodes(node, numRows, numCols, visitedNodes);
        assertEquals(numRows * numCols, visitedNodes.size());
      }
    }
  }

  /**
   * Tests if the dungeon with 0 interconnectivity is a Minimum Spanning Tree or not.
   */
  @Test
  public void testCheckMST() {
    Dungeon dungeon = new DungeonImpl(4, 4, 0,
            true, 0.25f,
            false, 2, getRandom());

    Node[][] graph = dungeon.getMaze();
    int numEdges = getNumEdgesInGraph(graph);
    int numRows = graph.length;
    int numCols = graph[0].length;

    assertEquals(((long) numRows * numCols) - 1, numEdges);
  }

  /**
   * Tests the interconnectivity of the dungeon.
   */
  @Test
  public void testInterconnectivity() {
    Dungeon dungeon = new DungeonImpl(4, 4, 2,
            true, 0.25f,
            false, 2, getRandom());

    Node[][] graph = dungeon.getMaze();
    int numEdges = getNumEdgesInGraph(graph);
    int numRows = graph.length;
    int numCols = graph[0].length;

    assertEquals(2 + ((long) numRows * numCols) - 1, numEdges);
  }

  /**
   * Tests constructing a random dungeon with negative number of rows.
   */
  @Test
  public void testConstructRandomDungeonNegativeNumRows() {
    try {
      new DungeonImpl(-4, 4,
              2, true,
              0.25f, false, 2, getRandom());
      fail("Number of rows should be positive!");
    } catch (IllegalArgumentException e) {
      assertEquals("Number of rows is expected to be positive!", e.getMessage());
    }
  }

  /**
   * Tests constructing a random dungeon with negative number of columns.
   */
  @Test
  public void testConstructRandomDungeonNegativeNumCols() {
    try {
      new DungeonImpl(4, -4,
              2, true,
              0.25f, false, 2, getRandom());
      fail("Number of columns should be positive!");
    } catch (IllegalArgumentException e) {
      assertEquals("Number of columns is expected to be positive!", e.getMessage());
    }
  }

  /**
   * Tests constructing a random dungeon with negative interconnectivity.
   */
  @Test
  public void testConstructRandomDungeonNegativeInterconnectivity() {
    try {
      new DungeonImpl(4, 4,
              -2, true,
              0.25f, false, 2, getRandom());
      fail("Interconnectivity should be positive!");
    } catch (IllegalArgumentException e) {
      assertEquals("Interconnectivity is expected to be positive!", e.getMessage());
    }
  }

  /**
   * Tests constructing a random dungeon with negative percentage of caves with treasures.
   */
  @Test
  public void testConstructRandomDungeonNegativePercentCavesWithTreasures() {
    try {
      new DungeonImpl(4, 4,
              2, true,
              -0.25f, false, 2, getRandom());
      fail("Percentage of caves with treasures should be positive!");
    } catch (IllegalArgumentException e) {
      assertEquals("Percentage of caves that have treasure is expected to be positive!",
              e.getMessage());
    }
  }

  /**
   * Tests constructing a random dungeon with percentage of caves with treasures equal to zero.
   */
  @Test
  public void testConstructRandomDungeonPercentCavesZero() {
    try {
      new DungeonImpl(4, 4,
              2, true,
              0.0f, false, 2, getRandom());
      fail("Percentage of caves with treasures should be positive!");
    } catch (IllegalArgumentException e) {
      assertEquals("Percentage of caves that have treasure is expected to be non-zero.",
              e.getMessage());
    }
  }

  /**
   * Tests constructing a random dungeon with null random object.
   */
  @Test
  public void testConstructRandomDungeonNullRandom() {
    try {
      new DungeonImpl(4, 4,
              2, true,
              0.25f, false, 2, null);
      fail("Random object should not be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Random object is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests constructing a random dungeon with number of rows less than minimum allowed rows.
   */
  @Test
  public void testConstructRandomDungeonNumRowsLessThanMinRows() {
    try {
      new DungeonImpl(3, 4,
              2, true,
              0.25f, false, 2, getRandom());
      fail("Number of rows should be at least 4!");
    } catch (IllegalArgumentException e) {
      assertEquals("Minimum number of rows allowed is 4!", e.getMessage());
    }
  }

  /**
   * Tests constructing a random dungeon with number of columns lesser than minimum allowed columns.
   */
  @Test
  public void testConstructRandomDungeonNumColsLessThanMinCols() {
    try {
      new DungeonImpl(4, 3,
              2, true,
              0.25f, false, 2, getRandom());
      fail("Number of columns should be at least 4!");
    } catch (IllegalArgumentException e) {
      assertEquals("Minimum number of cols allowed is 4!", e.getMessage());
    }
  }

  /**
   * Tests constructing a random dungeon where interconnectivity is greater than minimum of
   * rows and columns and the forced interconnectivity option is turned off.
   */
  @Test
  public void testConstructRandomDungeonInterconnectivityExceedsForceOff() {
    try {
      new DungeonImpl(4, 4,
              6, true,
              0.25f, false, 2, getRandom());
      fail("Interconnectivity should be less than 4!");
    } catch (IllegalArgumentException e) {
      assertEquals("Max interconnectivity allowed = 4, greater interconnectivities "
              + "do not guarantee viable start and end positions in dungeon, if you still want to"
              + " increase interconnectivity then set forceInterconnectivityRange to true!",
              e.getMessage());
    }
  }

  /**
   * Tests constructing a random dungeon where interconnectivity is greater than max allowed
   * interconnectivity and the forced interconnectivity option is turned on.
   */
  @Test
  public void testConstructRandomDungeonInterconnectivityExceedsForceOn() {
    try {
      new DungeonImpl(4, 4,
              100, true,
              0.25f, true, 2, getRandom());
      fail("Interconnectivity should be less than 100!");
    } catch (IllegalArgumentException e) {
      assertEquals("Max interconnectivity cannot exceed 17!", e.getMessage());
    }
  }

  /**
   * Create a dungeon with percentage of treasure greater than hundred (100%).
   */
  @Test
  public void testConstructRandomPercentageTreasureGreaterThanHundred() {
    try {
      Dungeon dungeon = new DungeonImpl(4, 4, 2,
              true, 1.25f,
              false, 2, getRandom());
      fail("Percentage of treasure should be less than 100!");
    } catch (IllegalArgumentException e) {
      assertEquals("Percentage of caves that have treasure is expected to be "
              + "less than or equal to 100.", e.getMessage());
    }
  }

  /**
   * Tests constructing a non-random dungeon with null starting node.
   */
  @Test
  public void testConstructorNonRandomNullStartingNode() {
    Node[][] graph = this.deterministicDungeon.getMaze();

    try {
      new DungeonImpl(graph, null, "8", false,
              2, graph[0][1],
              new Yugoloth(), graph[0][1], graph[0][1]);
      fail("Starting node should not be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Starting node is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests constructing a non-random dungeon with null ending node.
   */
  @Test
  public void testConstructNonRandomNullEndingNode() {
    Node[][] graph = this.deterministicDungeon.getMaze();

    try {
      new DungeonImpl(graph, "8", null, false,
              2, graph[0][1],
              new Yugoloth(), graph[0][1], graph[0][1]);
      fail("Ending node should not be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Ending node is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests constructing a non-random dungeon with null graph.
   */
  @Test
  public void testConstructNonRandomNullGraph() {
    try {
      new DungeonImpl(null, "8", "4",
              false, 2, new GenericNode(),
              new Yugoloth(), new GenericNode(), new GenericNode());
      fail("Graph should not be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Graph representing dungeon is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests constructing a non-random dungeon with number of rows less than minimum.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructNonRandomNumRowsLessThanMin() {
    Node[][] graph = {this.deterministicDungeon.getMaze()[0]};
    new DungeonImpl(graph, "1", "16", false,
            2, graph[0][1], new Yugoloth(), graph[0][1], graph[0][1]);
  }

  /**
   * Tests constructing a non-random dungeon with number of columns less than minimum.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructNonRandomNumColsLessThanMin() {
    Node[][] graph = this.deterministicDungeon.getMaze();
    graph[0] = new Node[]{graph[0][0]};
    graph[1] = new Node[]{graph[1][0]};
    graph[2] = new Node[]{graph[2][0]};
    graph[3] = new Node[]{graph[3][0]};
    new DungeonImpl(graph, "1", "16", false,
            2, graph[0][0], new Yugoloth(), graph[0][0], graph[0][0]);
  }

  /**
   * Tests constructing a non-random dungeon with invalid starting node.
   */
  @Test
  public void testConstructNonRandomInvalidStartNode() {
    Node[][] graph = this.deterministicDungeon.getMaze();

    try {
      new DungeonImpl(graph, "90", "4", false,
              2, graph[0][0], new Yugoloth(), graph[0][0], graph[0][0]);
      fail("Starting node should be valid!");
    } catch (IllegalArgumentException e) {
      assertEquals("Node with name 90, not found in the graph!", e.getMessage());
    }
  }

  /**
   * Tests constructing a non-random dungeon with invalid ending node.
   */
  @Test
  public void testConstructNonRandomInvalidEndNode() {
    Node[][] graph = this.deterministicDungeon.getMaze();

    try {
      new DungeonImpl(graph, "8", "90", false,
              2, graph[0][0], new Yugoloth(), graph[0][0], graph[0][0]);
      fail("Ending node should be valid!");
    } catch (IllegalArgumentException e) {
      assertEquals("Node with name 90, not found in the graph!", e.getMessage());
    }
  }

  /**
   * Tests constructing a non-random dungeon with zero (0) caves having treasures.
   */
  @Test
  public void testConstructNonRandomPercentTreasureZero() {
    Node[][] graph = new Node[4][4];
    int counter = 1;
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        graph[i][j] = new GenericNode(String.valueOf(counter++));
      }
    }

    graph[0][0].setBottomNode(graph[1][0]);
    graph[0][1].setRightNode(graph[0][2]);
    graph[0][1].setBottomNode(graph[1][1]);
    graph[0][2].setLeftNode(graph[0][1]);
    graph[0][2].setRightNode(graph[0][3]);
    graph[0][2].setBottomNode(graph[1][2]);
    graph[0][3].setLeftNode(graph[0][2]);
    graph[0][3].setBottomNode(graph[1][3]);

    graph[1][0].setTopNode(graph[0][0]);
    graph[1][0].setBottomNode(graph[2][0]);
    graph[1][1].setTopNode(graph[0][1]);
    graph[1][1].setRightNode(graph[1][2]);
    graph[1][1].setBottomNode(graph[2][1]);
    graph[1][2].setTopNode(graph[0][2]);
    graph[1][2].setBottomNode(graph[2][2]);
    graph[1][3].setTopNode(graph[0][3]);

    graph[2][0].setTopNode(graph[1][0]);
    graph[2][0].setRightNode(graph[2][1]);
    graph[2][1].setLeftNode(graph[2][0]);
    graph[2][1].setRightNode(graph[2][2]);
    graph[2][1].setTopNode(graph[1][1]);
    graph[2][2].setLeftNode(graph[2][1]);
    graph[2][2].setRightNode(graph[2][3]);
    graph[2][2].setTopNode(graph[1][2]);
    graph[2][2].setBottomNode(graph[3][2]);
    graph[2][3].setLeftNode(graph[2][2]);
    graph[2][3].setBottomNode(graph[3][3]);

    graph[3][0].setRightNode(graph[3][1]);
    graph[3][1].setLeftNode(graph[3][0]);
    graph[3][1].setRightNode(graph[3][2]);
    graph[3][2].setLeftNode(graph[3][1]);
    graph[3][2].setTopNode(graph[2][2]);
    graph[3][3].setTopNode(graph[2][3]);

    String[][] types = {{"C", "T", "C", "T"}, {"T", "C", "C", "C"}, {"T", "C", "C", "T"},
      {"C", "T", "T", "C"}};
    for (int i = 0; i < graph.length; i++) {
      for (int j = 0; j < graph[i].length; j++) {
        if (types[i][j].equals("C")) {
          graph[i][j] = graph[i][j].castToCaveNode();
        } else if (types[i][j].equals("T")) {
          graph[i][j] = graph[i][j].castToTunnelNode();
        }

        graph[i][j].updateCastInNeighbours();
      }
    }

    try {
      new DungeonImpl(graph, "1", "16", true,
              2, graph[0][0], new Yugoloth(), graph[0][0], graph[0][0]);
      fail("Dungeon should not be valid!");
    } catch (IllegalArgumentException e) {
      assertEquals("No caves have treasures!", e.getMessage());
    }
  }

  /**
   * Tests constructing a non-random dungeon with distance between start and end node
   * less than 5.
   */
  @Test
  public void testConstructNonRandomStartEndLessThan5() {
    Node[][] graph = this.deterministicDungeon.getMaze();

    try {
      new DungeonImpl(graph, "1", "3", true,
              2, graph[0][0], new Yugoloth(), graph[0][0], graph[0][0]);
      fail("Distance between start and end node is less than 5!");
    } catch (IllegalArgumentException e) {
      assertEquals("Distance between start and end node is less than 5!", e.getMessage());
    }
  }

  /**
   * Tests constructing a non-random dungeon with invalid tunnel.
   */
  @Test
  public void testConstructNonRandomInvalidTunnel() {
    Node[][] graph = this.deterministicDungeon.getMaze();
    graph[2][0].setBottomNode(graph[3][0]);
    graph[3][0].setTopNode(graph[2][0]);

    try {
      new DungeonImpl(graph, "1", "16", true,
              2, graph[0][0], new Yugoloth(), graph[0][0], graph[0][0]);
      fail("Tunnel should be valid!");
    } catch (IllegalArgumentException e) {
      assertEquals("Tunnel can have only 2 entries!", e.getMessage());
    }
  }

  /**
   * Tests constructing a non-random dungeon with invalid cave.
   */
  @Test
  public void testConstructNonRandomInvalidCave() {
    Node[][] graph = this.deterministicDungeon.getMaze();
    graph[2][1].setRightNode(null);
    graph[2][2].setLeftNode(null);

    try {
      new DungeonImpl(graph, "1", "16", true,
              2, graph[0][0], new Yugoloth(), graph[0][0], graph[0][0]);
      fail("Cave should be valid!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cave can have either 1, 3, or 4 entries!", e.getMessage());
    }
  }

  /**
   * Tests that tunnels have three two entries and caves have one, three, or four entries
   * in a non-random dungeon.
   */
  @Test
  public void testConstructNonRandomValidCaveAndTunnelEntry() {
    Node[][] graph = this.deterministicDungeon.getMaze();

    int numNeighbours;
    for (Node[] row : graph) {
      for (Node node : row) {
        numNeighbours = 0;

        if (node.getTopNode() != null) {
          numNeighbours++;
        }
        if (node.getRightNode() != null) {
          numNeighbours++;
        }
        if (node.getBottomNode() != null) {
          numNeighbours++;
        }
        if (node.getLeftNode() != null) {
          numNeighbours++;
        }

        if (node.getType().equals("T")) {
          assertEquals(2, numNeighbours);
        } else if (node.getType().equals("C")) {
          assertTrue(numNeighbours == 1 || numNeighbours == 3 || numNeighbours == 4);
        }
      }
    }
  }

  /**
   * Tests that tunnels have three two entries and caves have one, three, or four entries
   * in a random dungeon.
   */
  @Test
  public void testConstructRandomValidCaveAndTunnelEntry() {
    Node[][] graph = new DungeonImpl(4, 4,
            2, true,
            0.25f, false, 2, getRandom()).getMaze();

    int numNeighbours;
    for (Node[] row : graph) {
      for (Node node : row) {
        numNeighbours = 0;

        if (node.getTopNode() != null) {
          numNeighbours++;
        }
        if (node.getRightNode() != null) {
          numNeighbours++;
        }
        if (node.getBottomNode() != null) {
          numNeighbours++;
        }
        if (node.getLeftNode() != null) {
          numNeighbours++;
        }

        if (node.getType().equals("T")) {
          assertEquals(2, numNeighbours);
        } else if (node.getType().equals("C")) {
          assertTrue(numNeighbours == 1 || numNeighbours == 3 || numNeighbours == 4);
        }
      }
    }

    graph = new DungeonImpl(4, 4,
            2, false,
            0.25f, false, 2, getRandom()).getMaze();
    for (Node[] row : graph) {
      for (Node node : row) {
        numNeighbours = 0;

        if (node.getTopNode() != null) {
          numNeighbours++;
        }
        if (node.getRightNode() != null) {
          numNeighbours++;
        }
        if (node.getBottomNode() != null) {
          numNeighbours++;
        }
        if (node.getLeftNode() != null) {
          numNeighbours++;
        }

        if (node.getType().equals("T")) {
          assertEquals(2, numNeighbours);
        } else if (node.getType().equals("C")) {
          assertTrue(numNeighbours == 1 || numNeighbours == 3 || numNeighbours == 4);
        }
      }
    }
  }

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
   * Tests for whether the distance between starting and ending node in a random dungeon is
   * at least 5.
   */
  @Test
  public void testDistanceBetweenStartEndRandom() {
    Dungeon wrappingDungeon = new DungeonImpl(4, 4,
            2, true,
            0.25f, false, 2, getRandom());
    Dungeon nonWrappingDungeon = new DungeonImpl(4, 4,
            2, false,
            0.25f, false, 2, getRandom());

    assertTrue(findShortestPathDistance(wrappingDungeon.getStartNode(),
            wrappingDungeon.getEndNode()) >= 5);
    assertTrue(findShortestPathDistance(nonWrappingDungeon.getStartNode(),
            nonWrappingDungeon.getEndNode()) >= 5);
  }

  /**
   * Tests constructing a random wrapping dungeon.
   */
  @Test
  public void testConstructRandomWrappingDungeon() {
    Dungeon wrappingDungeon = new DungeonImpl(4, 4,
            2, true,
            0.25f, false, 2, getRandom());

    Node[][] graph = wrappingDungeon.getMaze();

    int edgeConnectionCount = 0;
    for (int i = 0; i < graph.length; i++) {
      for (int j = 0; j < graph[i].length; j++) {
        if ((i - 1) < 0 && graph[i][j].getTopNode() != null) {
          edgeConnectionCount++;
        }
        if ((i + 1) >= graph.length && graph[i][j].getBottomNode() != null) {
          edgeConnectionCount++;
        }
        if ((j - 1) < 0 && graph[i][j].getLeftNode() != null) {
          edgeConnectionCount++;
        }
        if ((j + 1) >= graph[i].length && graph[i][j].getRightNode() != null) {
          edgeConnectionCount++;
        }
      }
    }

    assertTrue(edgeConnectionCount > 0);
  }

  /**
   * Tests constructing a non-random non-wrapping dungeon.
   */
  @Test
  public void testConstructNonRandomNonWrappingDungeon() {
    Dungeon nonWrappingDungeon = new DungeonImpl(4, 4,
            2, false,
            0.25f, false, 2, getRandom());

    Node[][] graph = nonWrappingDungeon.getMaze();

    int edgeConnectionCount = 0;
    for (int i = 0; i < graph.length; i++) {
      for (int j = 0; j < graph[i].length; j++) {
        if ((i - 1) < 0 && graph[i][j].getTopNode() != null) {
          edgeConnectionCount++;
        }
        if ((i + 1) >= graph.length && graph[i][j].getBottomNode() != null) {
          edgeConnectionCount++;
        }
        if ((j - 1) < 0 && graph[i][j].getLeftNode() != null) {
          edgeConnectionCount++;
        }
        if ((j + 1) >= graph[i].length && graph[i][j].getRightNode() != null) {
          edgeConnectionCount++;
        }
      }
    }

    assertEquals(0, edgeConnectionCount);
  }

  /**
   * Tests constructing a random dungeon with correct percent of caves with treasures and
   * nodes with arrows.
   */
  @Test
  public void testConstructRandomDungeon() {
    Dungeon randomDungeon = new DungeonImpl(4, 4,
            2, true,
            0.25f, true, 2, getRandom());

    Node[][] graph = randomDungeon.getMaze();

    int treasureCount = 0;
    int arrowCount = 0;
    int caveCount = 0;
    for (int i = 0; i < graph.length; i++) {
      for (int j = 0; j < graph[i].length; j++) {
        if (graph[i][j].getType().equals("C")) {
          caveCount++;

          if (graph[i][j].getTreasures().size() != 0) {
            treasureCount++;
          }
        }

        if (graph[i][j].getNumArrows() != 0) {
          arrowCount++;
        }
      }
    }

    assertTrue(((double)treasureCount / (double)caveCount) >= 0.25);
    assertTrue(((double)arrowCount / (double)(graph.length * graph[0].length)) >= 0.25);
  }

  /**
   * Tests constructing a random dungeon with correct number of Otyughs placed.
   */
  @Test
  public void testConstructRandomDungeonOtyughs() {
    Dungeon randomDungeon = new DungeonImpl(4, 4,
            2, true,
            0.25f, true, 2, getRandom());

    Node[][] graph = randomDungeon.getMaze();

    int otyughCount = 0;
    for (int i = 0; i < graph.length; i++) {
      for (int j = 0; j < graph[i].length; j++) {
        if (graph[i][j].getType().equals("C") && graph[i][j].getOtyugh() != null) {
          otyughCount++;
        }
      }
    }

    assertEquals(2, otyughCount);
  }

  /**
   * Tests constructing a non-random dungeon with correct number of Otyughs placed.
   */
  @Test
  public void testConstructNonRandomDungeonOtyughs() {
    Node[][] graph = this.deterministicDungeon.getMaze();

    int otyughCount = 0;
    for (int i = 0; i < graph.length; i++) {
      for (int j = 0; j < graph[i].length; j++) {
        if (graph[i][j].getType().equals("C") && graph[i][j].getOtyugh() != null) {
          otyughCount++;
        }
      }
    }

    assertEquals(2, otyughCount);
  }

  /**
   * Tests whether a player can traverse the dungeon from start to end.
   */
  @Test
  public void testTraverseDungeon() {
    this.player.enterPlayerToDungeon(this.deterministicDungeon);
    assertEquals("7", this.deterministicDungeon.getCurrentPositionPlayer().getNodeName());

    makeSeriesOfMoves("R", "D", "L", "L", "D");
    this.player.shootArrow(this.deterministicDungeon, "L", 1);
    this.player.shootArrow(this.deterministicDungeon, "L", 1);
    makeSeriesOfMoves("L");

    assertEquals("13", this.deterministicDungeon.getCurrentPositionPlayer().getNodeName());
  }

  /**
   * Tests fetching start node of a dungeon.
   */
  @Test
  public void testGetStartNode() {
    assertEquals("7", this.deterministicDungeon.getStartNode().getNodeName());
  }

  /**
   * Tests fetching end node of a dungeon.
   */
  @Test
  public void testGetEndNode() {
    assertEquals("13", this.deterministicDungeon.getEndNode().getNodeName());
  }

  /**
   * Tests whether an Otyugh is placed in end node and never in start node.
   */
  @Test
  public void testOtyughPlacement() {
    for (int i = 0; i < 100; i++) {
      Dungeon dungeon = new DungeonImpl(6, 8,
              2, true,
              0.25f, true, 2, new Random());

      assertNotNull(dungeon.getEndNode().getOtyugh());

      if (dungeon.getStartNode().getType().equals("C")) {
        assertNull(dungeon.getStartNode().getOtyugh());
      }
    }
  }

  /**
   * Tests whether an Otyugh is placed in a tunnel in random dungeons or not.
   */
  @Test
  public void testOtyughPlacementInTunnel() {
    int otyughCount = 0;
    for (int i = 0; i < 100; i++) {
      Dungeon dungeon = new DungeonImpl(6, 8,
              2, true,
              0.25f, true, 2, getRandom());

      try {
        Node[][] graph = dungeon.getMaze();
        for (Node[] nodes : graph) {
          for (Node node : nodes) {
            if (node.getType().equals("T") && node.getOtyugh() != null) {
              otyughCount++;
            }
          }
        }
      } catch (UnsupportedOperationException ignored) {

      }

      assertEquals(0, otyughCount);
    }
  }

  /**
   * Tests whether an Otyugh eats a player when the player enters a location with Otuugh
   * and the Otyugh is not injured.
   */
  @Test
  public void testOtyughEatsPlayerOtyughNotInjured() {
    this.player.enterPlayerToDungeon(this.deterministicDungeon);

    // Moves from 7 to 13, 13 has an Otyugh
    makeSeriesOfMoves("R", "D", "L", "L", "D", "L");

    assertEquals(100, this.deterministicDungeon.getCurrentPositionPlayer().getOtyugh().getHealth());
    assertTrue(this.player.isPlayerDead());
  }

  /**
   * Tests whether an Otyugh eats a player when the player enters a location with Otuugh
   * and the Otyugh is injured.
   */
  @Test
  public void testOtyughEatsPlayerOtyughInjured() {
    this.player.enterPlayerToDungeon(this.deterministicDungeon);

    // Moves from 7 to 13, 13 has an Otyugh
    makeSeriesOfMoves("R", "D", "L", "L", "D");
    this.player.shootArrow(this.deterministicDungeon, "L", 1);
    makeSeriesOfMoves("L");

    assertEquals(50, this.deterministicDungeon.getCurrentPositionPlayer().getOtyugh().getHealth());
    assertTrue(this.player.isPlayerDead());
  }

  /**
   * Tests whether an Otyugh does not eat a player when the player enters a location with Otuugh
   * and the Otyugh is injured.
   */
  @Test
  public void testOtyughDoesNotEatPlayerOtyughInjured() {
    Node[][] graph = this.deterministicDungeon.getMaze();
    Dungeon dungeon = new DungeonImpl(graph, "7", "13",
            true, 2, graph[0][0], new Yugoloth(),
            graph[0][0], graph[0][0], new Random(21));

    this.player.enterPlayerToDungeon(dungeon);

    // Moves from 7 to 13, 13 has an Otyugh
    this.player.moveRight(dungeon);
    this.player.moveDown(dungeon);
    this.player.moveLeft(dungeon);
    this.player.moveLeft(dungeon);
    this.player.moveUp(dungeon);
    this.player.shootArrow(dungeon, "U", 1);
    this.player.moveUp(dungeon);

    assertEquals(50, dungeon.getCurrentPositionPlayer().getOtyugh().getHealth());
    assertFalse(this.player.isPlayerDead());
  }

  /**
   * Tests successful shooting in a wrapping dungeon.
   */
  @Test
  public void testShootingInWrappingDungeon() {
    Dungeon wrappingDungeon = new DungeonImpl(this.deterministicDungeon.getMaze(), "7", "13",
            true, 2, new GenericNode(), new Yugoloth(), new GenericNode(),
            new GenericNode(), getRandom());
    this.player.enterPlayerToDungeon(wrappingDungeon);

    // Moves from 7 to 13, 13 has an Otyugh
    this.player.moveRight(wrappingDungeon);
    this.player.moveDown(wrappingDungeon);
    this.player.moveLeft(wrappingDungeon);
    this.player.moveLeft(wrappingDungeon);

    int hitResult = this.player.shootArrow(wrappingDungeon, "D", 2).get(2);

    assertEquals(50, wrappingDungeon.getCurrentPositionPlayer()
            .getBottomNode().getBottomNode().getOtyugh().getHealth());
    assertEquals(0, hitResult);
  }

  /**
   * Tests constructing a non-random dungeon with negative number of Otyughs.
   */
  @Test
  public void testConstructNonRandomDungeonNegativeOtyughs() {
    try {
      new DungeonImpl(this.deterministicDungeon.getMaze(), "7", "13",
              true, -2, new GenericNode(), new Yugoloth(), new GenericNode(),
              new GenericNode(), getRandom());
      fail("Number of otyughs should be positive!");
    } catch (IllegalArgumentException e) {
      assertEquals("Number of otyughs is expected to be non-negative!", e.getMessage());
    }
  }

  /**
   * Tests constructing a random dungeon with negative number of Otyughs.
   */
  @Test
  public void testConstructRandomDungeonNegativeOtyughs() {
    try {
      new DungeonImpl(4, 4,
              2, true,
              0.25f, true, -2, getRandom());
      fail("Number of otyughs should be positive!");
    } catch (IllegalArgumentException e) {
      assertEquals("Number of otyughs is expected to be non-negative!", e.getMessage());
    }
  }

  /**
   * Tests constructing a non-random dungeon with very high number of Otyughs.
   */
  @Test
  public void testConstructNonRandomDungeonVeryHighOtyughs() {
    try {
      new DungeonImpl(this.deterministicDungeon.getMaze(), "7", "13",
              true, 100, new GenericNode(), new Yugoloth(), new GenericNode(),
              new GenericNode(), getRandom());
      fail("Number of otyughs should be less than the number of nodes!");
    } catch (IllegalArgumentException e) {
      assertEquals("Number of otyughs is expected to be less than or equal to "
              + "the number of nodes in the graph!", e.getMessage());
    }
  }

  /**
   * Tests constructing a random dungeon with very high number of Otyughs.
   */
  @Test
  public void testConstructRandomDungeonVeryHighOtyughs() {
    try {
      new DungeonImpl(4, 4,
              2, true,
              0.25f, true, 100, getRandom());
      fail("Number of otyughs should be less than the number of nodes!");
    } catch (IllegalArgumentException e) {
      assertEquals("Number of otyughs is expected to be less than or equal to "
              + "the number of nodes in the dungeon!", e.getMessage());
    }
  }

  /**
   * Tests get start node coordinates.
   */
  @Test
  public void testGetStartNodeCoordinates() {
    assertEquals(1, this.deterministicDungeon.getStartNodeI());
    assertEquals(1, this.deterministicDungeon.getStartNodeI());
  }

  /**
   * Tests get otyugh coordinates.
   */
  @Test
  public void testGetOtyughCoordinates() {
    assertEquals("[0, 3]", this.deterministicDungeon.getOtyughNodeIs().toString());
    assertEquals("[1, 0]", this.deterministicDungeon.getOtyughNodeJs().toString());
  }

  /**
   * Tests get treasure locations.
   */
  @Test
  public void testGetTreasureLocations() {
    assertEquals("{[2, 1]=[DIAMOND, DIAMOND], [2, 2]=[SAPPHIRE, SAPPHIRE], [3, 3]=[]," +
            " [0, 1]=[RUBY], [1, 2]=[RUBY, DIAMOND], [1, 3]=[], [3, 0]=[], [3, 1]=[]}",
            this.deterministicDungeon.getTreasureAndLocations().toString());
  }

  /**
   * Tests is wrapping dungeon.
   */
  @Test
  public void testIsWrappingDungeon() {
    assertTrue(this.deterministicDungeon.isWrapping());
  }

  /**
   * Tests num otyughs.
   */
  @Test
  public void testNumOtyughs() {
    assertEquals(2, this.deterministicDungeon.getNumOtyughs());
  }

  /**
   * Tests get yugoloth coordinates.
   */
  @Test
  public void testGetYugolothCoordinates() {
    assertEquals(0, this.deterministicDungeon.getYugolothNodeI());
    assertEquals(1, this.deterministicDungeon.getYugolothNodeJ());
  }
}
