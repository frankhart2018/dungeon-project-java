package controllertest;

import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.Random;

import controller.DungeonConsoleController;
import controller.DungeonController;
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for the console controller for dungeon.
 */
public class DungeonConsoleControllerTest {
  private static final int SEED = 42;

  private Dungeon wrappingDungeon;
  private Dungeon nonWrappingDungeon;
  private Player player;

  @Before
  public void setUp() {
    this.wrappingDungeon = getDeterministicWrappingDungeon("2", "11");
    this.nonWrappingDungeon = getDeterministicNonWrappingDungeon("4", "5");
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

  private Random getRandom() {
    return new Random(SEED);
  }

  private Dungeon getDeterministicWrappingDungeon(String... otyughPositions) {
    Node[][] graph = new Node[4][4];

    int k = 1;
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        graph[i][j] = new GenericNode(String.valueOf(k++));
      }
    }

    String[][][] connections = {{{"L", "U", "R"}, {"L", "R", "D"}, {"L", "U", "D"}, {"R", "D"}},
      {{"R", "D"}, {"L", "U", "D"}, {"U", "D"}, {"U", "D"}},
      {{"L", "U", "R"}, {"L", "U"}, {"U"}, {"U", "R"}},
      {{"L", "D"}, {"R"}, {"L", "D"}, {"R"}}};

    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        for (String direction : connections[i][j]) {
          addConnection(graph, i, j, direction);
        }
      }
    }

    String[][] nodeTypes = {{"C", "C", "C", "T"}, {"T", "C", "T", "T"}, {"C", "T", "C", "T"},
      {"T", "C", "T", "C"}};

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
    graph[0][0].placeTreasure("SAPPHIRE", 2); // 1
    graph[0][0].placeTreasure("DIAMOND", 1);
    graph[0][0].placeTreasure("RUBY", 1);

    graph[0][1].placeTreasure("SAPPHIRE", 1); // 2

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
    graph[0][1].addSingleArrow();

    graph[2][3].addSingleArrow(); // 12
    graph[2][3].addSingleArrow();
    graph[2][3].addSingleArrow();
    graph[2][3].addSingleArrow();

    graph[3][0].addSingleArrow(); // 13
    graph[3][0].addSingleArrow();

    graph[3][2].addSingleArrow(); // 15
    graph[3][2].addSingleArrow();
    graph[3][2].addSingleArrow();
    graph[3][2].addSingleArrow();

    return new DungeonImpl(graph, "8", "11", true, 2,
            graph[0][1], new Yugoloth(), graph[0][1], graph[0][1], getRandom());
  }

  private Dungeon getDeterministicNonWrappingDungeon(String... otyughPositions) {
    Node[][] graph = new Node[4][4];

    int k = 1;
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        graph[i][j] = new GenericNode(String.valueOf(k++));
      }
    }

    String[][][] connections = {{{"R", "D"}, {"L", "R"}, {"L", "D"}, {"D"}},
      {{"U", "R", "D"}, {"L", "R", "D"}, {"L", "U", "D"}, {"U", "D"}},
      {{"U", "R", "D"}, {"L", "U", "D"}, {"U", "R"}, {"L", "U"}},
      {{"U"}, {"U", "R"}, {"L", "R"}, {"L"}}};

    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        for (String direction : connections[i][j]) {
          addConnection(graph, i, j, direction);
        }
      }
    }

    String[][] nodeTypes = {{"T", "T", "T", "C"}, {"C", "C", "C", "T"}, {"C", "C", "T", "T"},
      {"C", "T", "T", "C"}};

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
    graph[1][1].placeTreasure("SAPPHIRE", 2); // 6
    graph[1][1].placeTreasure("DIAMOND", 2); // 6

    graph[3][0].placeTreasure("RUBY", 2); // 13
    graph[3][0].placeTreasure("SAPPHIRE", 1); // 13

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

    graph[1][1].addSingleArrow(); // 6
    graph[1][1].addSingleArrow();
    graph[1][1].addSingleArrow();
    graph[1][1].addSingleArrow();

    graph[1][3].addSingleArrow(); // 8
    graph[1][3].addSingleArrow();

    graph[3][3].addSingleArrow(); // 16
    graph[3][3].addSingleArrow();

    return new DungeonImpl(graph, "2", "4", false, 2,
            graph[0][1], new Yugoloth(), graph[0][1], graph[0][1], getRandom());
  }

  private String getConsoleControllerOutput(Dungeon dungeon, Player player, Readable in) {
    Appendable out = new StringBuffer();

    DungeonController controller = new DungeonConsoleController(in, out, getRandom());
    controller.playGame(dungeon, player);

    return out.toString();
  }

  private String getImpPartsControllerOutput(String output) {
    String[] lines = output.split("\n");

    int pruneTillLineNo = 22;
    String[] prunedLines = new String[lines.length - pruneTillLineNo];
    System.arraycopy(lines, pruneTillLineNo, prunedLines, 0, lines.length - pruneTillLineNo);

    return String.join("\n", prunedLines);
  }

  /**
   * Tests constructing dungeon console controller with null input.
   */
  @Test
  public void testConstructWithNullInput() {
    try {
      new DungeonConsoleController(null, new StringBuffer(), getRandom());
      fail("Input cannot be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Readable instance is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests constructing dungeon console controller with null output.
   */
  @Test
  public void testConstructWithNullOutput() {
    try {
      new DungeonConsoleController(new StringReader("1 2"), null, getRandom());
      fail("Output cannot be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Appendable instance is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests constructing dungeon console controller with null random.
   */
  @Test
  public void testConstructWithNullRandom() {
    try {
      new DungeonConsoleController(new StringReader("1 2"), new StringBuffer(), null);
      fail("Random cannot be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Random instance is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests player moves and does not shoot and gets killed by Otyugh for a wrapping dungeon.
   */
  @Test
  public void testPlayerMoveNoShootGetsKilledWrappingDungeon() {
    Readable in = new StringReader("M N M E M E");
    String actualString = getConsoleControllerOutput(this.wrappingDungeon, this.player,
            in);

    assertEquals("LET THE GAME START!\n" +
            "\n" +
            "You are in a tunnel\n" +
            "that continues to the N, S\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a tunnel\n" +
            "that continues to the E, S\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a cave\n" +
            "Doors lead to the N, E, W\n" +
            "You find 2 sapphires here\n" +
            "You find 1 diamond here\n" +
            "You find 1 ruby here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "Chomp, chomp, chomp, you are eaten by an Otyugh!\n" +
            "Better luck next time", getImpPartsControllerOutput(actualString));
    assertTrue(this.player.isPlayerDead());
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following treasure count: \n" +
            "No treasure in bag.\n", this.player.getPlayerTreasures());
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "Arrows: 3\n", this.player.getPlayerArrows());
  }

  /**
   * Tests player moves and does not shoot and gets killed by Otyugh for a non-wrapping dungeon.
   */
  @Test
  public void testPlayerMoveNoShootGetsKilledNonWrappingDungeon() {
    Readable in = new StringReader("M W M S");
    String actualString = getConsoleControllerOutput(this.nonWrappingDungeon, this.player,
            in);

    assertEquals("LET THE GAME START!\n" +
            "\n" +
            "You are in a tunnel\n" +
            "that continues to the E, W\n" +
            "You find 1 arrow here\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a tunnel\n" +
            "that continues to the E, S\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "Chomp, chomp, chomp, you are eaten by an Otyugh!\n" +
            "Better luck next time", getImpPartsControllerOutput(actualString));
    assertTrue(this.player.isPlayerDead());
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following treasure count: \n" +
            "No treasure in bag.\n", this.player.getPlayerTreasures());
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "Arrows: 3\n", this.player.getPlayerArrows());
  }

  /**
   * Tests player moves and collects treasures and does not shoot and gets killed by
   * Otyugh for a wrapping dungeon.
   */
  @Test
  public void testPlayerMovePickTreasureNoShootKilledW() {
    Readable in = new StringReader("M N M E P sapphire P diamond P ruby M E");
    String actualString = getConsoleControllerOutput(this.wrappingDungeon, this.player,
            in);

    assertEquals("LET THE GAME START!\n" +
            "\n" +
            "You are in a tunnel\n" +
            "that continues to the N, S\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a tunnel\n" +
            "that continues to the E, S\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a cave\n" +
            "Doors lead to the N, E, W\n" +
            "You find 2 sapphires here\n" +
            "You find 1 diamond here\n" +
            "You find 1 ruby here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? What? You pick up a SAPPHIRE\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the N, E, W\n" +
            "You find 1 sapphire here\n" +
            "You find 1 diamond here\n" +
            "You find 1 ruby here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? What? You pick up a DIAMOND\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the N, E, W\n" +
            "You find 1 sapphire here\n" +
            "You find 1 ruby here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? What? You pick up a RUBY\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the N, E, W\n" +
            "You find 1 sapphire here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "Chomp, chomp, chomp, you are eaten by an Otyugh!\n" +
            "Better luck next time", getImpPartsControllerOutput(actualString));
    assertTrue(this.player.isPlayerDead());
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following treasure count: \n" +
            "SAPPHIRE: 1\n" +
            "DIAMOND: 1\n" +
            "RUBY: 1\n", this.player.getPlayerTreasures());
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "Arrows: 3\n", this.player.getPlayerArrows());
  }

  /**
   * Tests player moves and collects treasures and does not shoot and gets killed by
   * Otyugh for a wrapping dungeon.
   */
  @Test
  public void testPlayerMovePickTreasureNoShootKilledNW() {
    Readable in = new StringReader("M E M S M W P sapphire P diamond M W");
    String actualString = getConsoleControllerOutput(this.nonWrappingDungeon, this.player,
            in);

    assertEquals("LET THE GAME START!\n" +
            "\n" +
            "You are in a tunnel\n" +
            "that continues to the E, W\n" +
            "You find 1 arrow here\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a tunnel\n" +
            "that continues to the S, W\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a cave\n" +
            "Doors lead to the N, S, W\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a cave\n" +
            "Doors lead to the E, S, W\n" +
            "You find 2 sapphires here\n" +
            "You find 2 diamonds here\n" +
            "You find 4 arrows here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? What? You pick up a SAPPHIRE\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the E, S, W\n" +
            "You find 1 sapphire here\n" +
            "You find 2 diamonds here\n" +
            "You find 4 arrows here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? What? You pick up a DIAMOND\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the E, S, W\n" +
            "You find 1 sapphire here\n" +
            "You find 1 diamond here\n" +
            "You find 4 arrows here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "Chomp, chomp, chomp, you are eaten by an Otyugh!\n" +
            "Better luck next time", getImpPartsControllerOutput(actualString));
    assertTrue(this.player.isPlayerDead());
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following treasure count: \n" +
            "SAPPHIRE: 1\n" +
            "DIAMOND: 1\n", this.player.getPlayerTreasures());
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "Arrows: 3\n", this.player.getPlayerArrows());
  }

  /**
   * Tests player moves and collects treasures and shoots arrow but does not hit Otyugh
   * and gets killed by an Otyugh in a wrapping dungeon.
   */
  @Test
  public void testPlayerMoveCollectTreasureShootKilledNW() {
    Readable in = new StringReader("M N M E P sapphire P diamond P ruby S 1 N M E");
    String actualString = getConsoleControllerOutput(this.wrappingDungeon, this.player,
            in);

    assertEquals("LET THE GAME START!\n" +
            "\n" +
            "You are in a tunnel\n" +
            "that continues to the N, S\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a tunnel\n" +
            "that continues to the E, S\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a cave\n" +
            "Doors lead to the N, E, W\n" +
            "You find 2 sapphires here\n" +
            "You find 1 diamond here\n" +
            "You find 1 ruby here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? What? You pick up a SAPPHIRE\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the N, E, W\n" +
            "You find 1 sapphire here\n" +
            "You find 1 diamond here\n" +
            "You find 1 ruby here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? What? You pick up a DIAMOND\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the N, E, W\n" +
            "You find 1 sapphire here\n" +
            "You find 1 ruby here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? What? You pick up a RUBY\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the N, E, W\n" +
            "You find 1 sapphire here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? No. of caves? Where to? " +
            "You shot an arrow into darkness\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the N, E, W\n" +
            "You find 1 sapphire here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "Chomp, chomp, chomp, you are eaten by an Otyugh!\n" +
            "Better luck next time", getImpPartsControllerOutput(actualString));
    assertTrue(this.player.isPlayerDead());
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following treasure count: \n" +
            "SAPPHIRE: 1\n" +
            "DIAMOND: 1\n" +
            "RUBY: 1\n", this.player.getPlayerTreasures());
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "Arrows: 2\n", this.player.getPlayerArrows());
  }

  /**
   * Tests player moves and collects treasures and shoots arrow but does not hit Otyugh
   * and gets killed by an Otyugh in a non-wrapping dungeon.
   */
  @Test
  public void testPlayerMoveCollectTreasureShootKilledW() {
    Readable in = new StringReader("M E M S M W P sapphire P diamond S 1 S M W");
    String actualString = getConsoleControllerOutput(this.nonWrappingDungeon, this.player,
            in);

    assertEquals("LET THE GAME START!\n" +
            "\n" +
            "You are in a tunnel\n" +
            "that continues to the E, W\n" +
            "You find 1 arrow here\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a tunnel\n" +
            "that continues to the S, W\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a cave\n" +
            "Doors lead to the N, S, W\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a cave\n" +
            "Doors lead to the E, S, W\n" +
            "You find 2 sapphires here\n" +
            "You find 2 diamonds here\n" +
            "You find 4 arrows here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? What? You pick up a SAPPHIRE\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the E, S, W\n" +
            "You find 1 sapphire here\n" +
            "You find 2 diamonds here\n" +
            "You find 4 arrows here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? What? You pick up a DIAMOND\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the E, S, W\n" +
            "You find 1 sapphire here\n" +
            "You find 1 diamond here\n" +
            "You find 4 arrows here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? No. of caves? Where to? " +
            "You shot an arrow into darkness\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the E, S, W\n" +
            "You find 1 sapphire here\n" +
            "You find 1 diamond here\n" +
            "You find 4 arrows here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "Chomp, chomp, chomp, you are eaten by an Otyugh!\n" +
            "Better luck next time", getImpPartsControllerOutput(actualString));
    assertTrue(this.player.isPlayerDead());
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following treasure count: \n" +
            "SAPPHIRE: 1\n" +
            "DIAMOND: 1\n", this.player.getPlayerTreasures());
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "Arrows: 2\n", this.player.getPlayerArrows());
  }

  /**
   * Tests player moves and shoots and kills Otyugh in a wrapping dungeon.
   */
  @Test
  public void testPlayerMoveShootInjureOtyughW() {
    Readable in = new StringReader("M S M E M E S 2 N M N S 1 N M N P arrow "
            + "M E S 1 S S 1 S M S M S");
    String actualString = getConsoleControllerOutput(this.wrappingDungeon, this.player,
            in);

    assertEquals("LET THE GAME START!\n" +
            "\n" +
            "You are in a tunnel\n" +
            "that continues to the N, S\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a tunnel\n" +
            "that continues to the N, E\n" +
            "You find 4 arrows here\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a cave\n" +
            "Doors lead to the N, E, W\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a tunnel\n" +
            "that continues to the N, W\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? No. of caves? Where to? " +
            "You shot the Otyugh, shoot it again to kill\n" +
            "\n" +
            "You are in a tunnel\n" +
            "that continues to the N, W\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a cave\n" +
            "Doors lead to the N, S, W\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? No. of caves? Where to? You killed the Otyugh\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the N, S, W\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a cave\n" +
            "Doors lead to the E, S, W\n" +
            "You find 1 sapphire here\n" +
            "You find 3 arrows here\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? What? You pick up an arrow\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the E, S, W\n" +
            "You find 1 sapphire here\n" +
            "You find 2 arrows here\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a cave\n" +
            "Doors lead to the N, S, W\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? No. of caves? Where to? " +
            "You shot the Otyugh, shoot it again to kill\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the N, S, W\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? No. of caves? Where to? You killed the Otyugh\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the N, S, W\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a tunnel\n" +
            "that continues to the N, S\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "Congratulations you have reached the end of the dungeon\n" +
            "Here are your treasure collections: \n" +
            "---------------------------------------------------\n" +
            "TestPlayer now has the following treasure count: \n" +
            "No treasure in bag.\n" +
            "\n" +
            "You are left with: \n" +
            "---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "No arrows left.", getImpPartsControllerOutput(actualString));
    assertFalse(this.player.isPlayerDead());
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following treasure count: \n" +
            "No treasure in bag.\n", this.player.getPlayerTreasures());
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "No arrows left.\n", this.player.getPlayerArrows());
  }

  /**
   * Tests player moves and shoots and kills Otyugh in a non-wrapping dungeon.
   */
  @Test
  public void testPlayerMoveShootInjureOtyughNW() {
    Readable in = new StringReader("M E M S S 2 W M W S 1 W P arrow M E M S M E S 1 N S 1 "
            + "N M N M N");
    String actualString = getConsoleControllerOutput(this.nonWrappingDungeon, this.player,
            in);

    assertEquals("LET THE GAME START!\n" +
            "\n" +
            "You are in a tunnel\n" +
            "that continues to the E, W\n" +
            "You find 1 arrow here\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a tunnel\n" +
            "that continues to the S, W\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a cave\n" +
            "Doors lead to the N, S, W\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? No. of caves? Where to? " +
            "You shot the Otyugh, shoot it again to kill\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the N, S, W\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a cave\n" +
            "Doors lead to the E, S, W\n" +
            "You find 2 sapphires here\n" +
            "You find 2 diamonds here\n" +
            "You find 4 arrows here\n" +
            "You smell a strong pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? No. of caves? Where to? You killed the Otyugh\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the E, S, W\n" +
            "You find 2 sapphires here\n" +
            "You find 2 diamonds here\n" +
            "You find 4 arrows here\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? What? You pick up an arrow\n" +
            "\n" +
            "You are in a cave\n" +
            "Doors lead to the E, S, W\n" +
            "You find 2 sapphires here\n" +
            "You find 2 diamonds here\n" +
            "You find 3 arrows here\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a cave\n" +
            "Doors lead to the N, S, W\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a tunnel\n" +
            "that continues to the N, E\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a tunnel\n" +
            "that continues to the N, W\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? No. of caves? Where to? " +
            "You shot the Otyugh, shoot it again to kill\n" +
            "\n" +
            "You are in a tunnel\n" +
            "that continues to the N, W\n" +
            "You smell a pungent smell\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? No. of caves? Where to? You killed the Otyugh\n" +
            "\n" +
            "You are in a tunnel\n" +
            "that continues to the N, W\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "You are in a tunnel\n" +
            "that continues to the N, S\n" +
            "You find 2 arrows here\n" +
            "\n" +
            "Move, Pickup, or Shoot (M-P-S)? Where to? \n" +
            "Congratulations you have reached the end of the dungeon\n" +
            "Here are your treasure collections: \n" +
            "---------------------------------------------------\n" +
            "TestPlayer now has the following treasure count: \n" +
            "No treasure in bag.\n" +
            "\n" +
            "You are left with: \n" +
            "---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "No arrows left.", getImpPartsControllerOutput(actualString));
    assertFalse(this.player.isPlayerDead());
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following treasure count: \n" +
            "No treasure in bag.\n", this.player.getPlayerTreasures());
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "No arrows left.\n", this.player.getPlayerArrows());
  }

  /**
   * Tests that GUI play game is not valid for CLI controller.
   */
  @Test
  public void testPlayGameGUI() {
    try {
      Readable in = new StringReader("");
      Appendable out = new StringBuilder();
      new DungeonConsoleController(in, out, getRandom()).playGuiGame();
    } catch (UnsupportedOperationException e) {
      assertEquals("GUI not supported in console!", e.getMessage());
    }
  }
}
