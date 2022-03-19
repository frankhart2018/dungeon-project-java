package controllertest;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import controller.DungeonControllerWView;
import controller.DungeonSwingController;
import dungeongame.Dungeon;
import dungeongame.DungeonImpl;
import dungeongame.Otyugh;
import dungeongame.Player;
import dungeongame.PlayerImpl;
import dungeongame.Yugoloth;
import node.GenericNode;
import node.Node;
import view.DungeonView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the GUI version of dungeon controller.
 */
public class DungeonSwingControllerTest {
  private static final int SEED = 42;

  private Player player;
  private Dungeon nonWrappingDungeon;
  private Dungeon wrappingDungeon;

  @Before
  public void setUp() {
    this.player = new PlayerImpl("Player");
    this.nonWrappingDungeon = getDeterministicNonWrappingDungeon();
    this.wrappingDungeon = getDeterministicWrappingDungeon();
  }

  private Random getRandom() {
    return new Random(SEED);
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

    ////////////////////////////////////////////
    // Place yugoloth
    ////////////////////////////////////////////
    Yugoloth yugoloth = new Yugoloth();
    graph[0][0].placeYugoloth(yugoloth);

    ////////////////////////////////////////////
    // Place thief
    ////////////////////////////////////////////
    graph[0][3].placeThief();

    ////////////////////////////////////////////
    // Place pit
    ////////////////////////////////////////////
    graph[1][1].addPit();

    return new DungeonImpl(graph, "8", "11", true, 2,
            graph[0][0], yugoloth, graph[0][3], graph[1][1], getRandom());
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

    ////////////////////////////////////////////
    // Place yugoloth
    ////////////////////////////////////////////
    Yugoloth yugoloth = new Yugoloth();
    graph[0][0].placeYugoloth(yugoloth);

    ////////////////////////////////////////////
    // Place thief
    ////////////////////////////////////////////
    graph[1][3].placeThief();

    ////////////////////////////////////////////
    // Place pit
    ////////////////////////////////////////////
    graph[1][0].addPit();

    return new DungeonImpl(graph, "2", "4", false, 2,
            graph[0][0], yugoloth, graph[1][3], graph[1][0], getRandom());
  }

  /**
   * Tests play game which is only supported for CLI controller.
   */
  @Test
  public void testPlayGame() {
    try {
      new DungeonSwingController(this.wrappingDungeon, this.player,
              new MockDungeonView(new StringBuilder()),
              getRandom()).playGame(this.wrappingDungeon, this.player);
    } catch (UnsupportedOperationException e) {
      assertEquals("Console mode not available in GUI!", e.getMessage());
    }
  }

  /**
   * Tests playing the GUI game with a mock view.
   */
  @Test
  public void testPlayGuiGame() {
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    this.player.enterPlayerToDungeon(this.wrappingDungeon);
    DungeonControllerWView controller = new DungeonSwingController(this.wrappingDungeon,
            this.player, view, getRandom());

    controller.playGuiGame();

    assertEquals("Refreshed the view\n" +
            "Added node types and initialized the map\n" +
            "View made visible\n" +
            "View made non-resizable\n" +
            "Added player to location: (1, 3)\n" +
            "Added treasure to location: (0, 0)\n" +
            "Treasures: [SAPPHIRE, SAPPHIRE, DIAMOND, RUBY]\n" +
            "Added treasure to location: (1, 1)\n" +
            "Treasures: []\n" +
            "Added treasure to location: (2, 2)\n" +
            "Treasures: []\n" +
            "Added treasure to location: (3, 3)\n" +
            "Treasures: []\n" +
            "Added treasure to location: (0, 1)\n" +
            "Treasures: [SAPPHIRE]\n" +
            "Added treasure to location: (0, 2)\n" +
            "Treasures: []\n" +
            "Added treasure to location: (2, 0)\n" +
            "Treasures: []\n" +
            "Added treasure to location: (3, 1)\n" +
            "Treasures: []\n" +
            "Added arrows to location: (0, 1)\n" +
            "Number of arrows: 3\n" +
            "Added arrows to location: (2, 3)\n" +
            "Number of arrows: 4\n" +
            "Added arrows to location: (3, 0)\n" +
            "Number of arrows: 2\n" +
            "Added arrows to location: (3, 2)\n" +
            "Number of arrows: 4\n" +
            "Added yugoloth to location: (0, 0)\n" +
            "Added thief to location: (0, 3)\n" +
            "Added pit to location: (1, 1)\n" +
            "Marked end node: (2, 2)\n" +
            "Uncovered node: (1, 3)\n" +
            "Yugoloth movement started asynchronously\n", out.toString());
  }

  private String getLastString(String str, int negIdx) {
    String[] lines = str.split("\n");
    return lines[lines.length - negIdx];
  }

  /**
   * Tests moving up in a wrapping dungeon.
   */
  @Test
  public void testMoveUpWrappingDungeon() {
    this.player.enterPlayerToDungeon(this.wrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.wrappingDungeon,
            this.player, view, getRandom());
    controller.playGuiGame();

    Node[][] graph = this.wrappingDungeon.getMaze();
    assertEquals(graph[1][3].getNodeName(),
            this.wrappingDungeon.getCurrentPositionPlayer().getNodeName());

    controller.move("U");
    view.moveInDirection("U");
    assertEquals(graph[0][3].getNodeName(),
            this.wrappingDungeon.getCurrentPositionPlayer().getNodeName());
    assertEquals("Moved successfully to U", getLastString(out.toString(), 1));
  }

  /**
   * Tests moving up in a non-wrapping dungeon.
   */
  @Test
  public void testMoveUpNonWrappingDungeon() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());
    controller.playGuiGame();

    Node[][] graph = this.nonWrappingDungeon.getMaze();
    controller.move("R");
    controller.move("D");

    assertEquals(graph[1][2].getNodeName(),
            this.nonWrappingDungeon.getCurrentPositionPlayer().getNodeName());

    controller.move("U");
    view.moveInDirection("U");
    assertEquals(graph[0][2].getNodeName(),
            this.nonWrappingDungeon.getCurrentPositionPlayer().getNodeName());
    assertEquals("Moved successfully to U", getLastString(out.toString(), 1));
  }

  /**
   * Tests moving down in a wrapping dungeon.
   */
  @Test
  public void testMoveDownWrappingDungeon() {
    this.player.enterPlayerToDungeon(this.wrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.wrappingDungeon,
            this.player, view, getRandom());
    controller.playGuiGame();

    Node[][] graph = this.wrappingDungeon.getMaze();
    assertEquals(graph[1][3].getNodeName(),
            this.wrappingDungeon.getCurrentPositionPlayer().getNodeName());

    controller.move("D");
    view.moveInDirection("D");
    assertEquals(graph[2][3].getNodeName(),
            this.wrappingDungeon.getCurrentPositionPlayer().getNodeName());
    assertEquals("Moved successfully to D", getLastString(out.toString(), 1));
  }

  /**
   * Tests moving down in a non-wrapping dungeon.
   */
  @Test
  public void testMoveDownNonWrappingDungeon() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());
    controller.playGuiGame();

    Node[][] graph = this.nonWrappingDungeon.getMaze();
    controller.move("R");

    assertEquals(graph[0][2].getNodeName(),
            this.nonWrappingDungeon.getCurrentPositionPlayer().getNodeName());

    controller.move("D");
    view.moveInDirection("D");
    assertEquals(graph[1][2].getNodeName(),
            this.nonWrappingDungeon.getCurrentPositionPlayer().getNodeName());
    assertEquals("Moved successfully to D", getLastString(out.toString(), 1));
  }

  /**
   * Tests moving left in a wrapping dungeon.
   */
  @Test
  public void testMoveLeftWrappingDungeon() {
    this.player.enterPlayerToDungeon(this.wrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.wrappingDungeon,
            this.player, view, getRandom());
    controller.playGuiGame();

    Node[][] graph = this.wrappingDungeon.getMaze();
    controller.move("D");
    controller.move("R");

    assertEquals(graph[2][0].getNodeName(),
            this.wrappingDungeon.getCurrentPositionPlayer().getNodeName());

    controller.move("L");
    view.moveInDirection("L");
    assertEquals(graph[2][3].getNodeName(),
            this.wrappingDungeon.getCurrentPositionPlayer().getNodeName());
    assertEquals("Moved successfully to L", getLastString(out.toString(), 1));
  }

  /**
   * Tests moving left in a non-wrapping dungeon.
   */
  @Test
  public void testMoveLeftNonWrappingDungeon() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());
    controller.playGuiGame();

    Node[][] graph = this.nonWrappingDungeon.getMaze();
    assertEquals(graph[0][1].getNodeName(),
            this.nonWrappingDungeon.getCurrentPositionPlayer().getNodeName());

    controller.move("L");
    view.moveInDirection("L");
    assertEquals(graph[0][0].getNodeName(),
            this.nonWrappingDungeon.getCurrentPositionPlayer().getNodeName());
    assertEquals("Moved successfully to L", getLastString(out.toString(), 1));
  }

  /**
   * Tests moving right in a wrapping dungeon.
   */
  @Test
  public void testMoveRightWrappingDungeon() {
    this.player.enterPlayerToDungeon(this.wrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.wrappingDungeon,
            this.player, view, getRandom());
    controller.playGuiGame();

    Node[][] graph = this.wrappingDungeon.getMaze();
    controller.move("D");

    assertEquals(graph[2][3].getNodeName(),
            this.wrappingDungeon.getCurrentPositionPlayer().getNodeName());

    controller.move("R");
    view.moveInDirection("R");
    assertEquals(graph[2][0].getNodeName(),
            this.wrappingDungeon.getCurrentPositionPlayer().getNodeName());
    assertEquals("Moved successfully to R", getLastString(out.toString(), 1));
  }

  /**
   * Tests moving right in a non-wrapping dungeon.
   */
  @Test
  public void testMoveRightNonWrappingDungeon() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());
    controller.playGuiGame();

    Node[][] graph = this.nonWrappingDungeon.getMaze();
    assertEquals(graph[0][1].getNodeName(),
            this.nonWrappingDungeon.getCurrentPositionPlayer().getNodeName());

    controller.move("R");
    view.moveInDirection("R");
    assertEquals(graph[0][2].getNodeName(),
            this.nonWrappingDungeon.getCurrentPositionPlayer().getNodeName());
    assertEquals("Moved successfully to R", getLastString(out.toString(), 1));
  }

  /**
   * Tests approaching yugoloth in a dungeon.
   */
  @Test
  public void testMoveToYugolothPosition() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());
    controller.playGuiGame();

    controller.move("L");

    assertTrue(out.toString().contains("Game over, you died!"));
  }

  /**
   * Tests moving to a thief in a dungeon.
   */
  @Test
  public void testMoveToTheifPosition() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());
    controller.playGuiGame();

    controller.move("R");
    controller.move("D");
    controller.move("L");

    controller.pickUpItem("DIAMOND");
    controller.pickUpItem("SAPPHIRE");

    assertEquals("[SAPPHIRE: 1, DIAMOND: 1]", this.player.getPlayerTreasuresList().toString());

    controller.move("R");
    controller.move("D");
    controller.move("R");
    controller.move("U");

    assertEquals("[]", this.player.getPlayerTreasuresList().toString());
    assertTrue(out.toString().contains("All your treasure has been stolen!"));
  }

  /**
   * Tests moving to a pit in a dungeon.
   */
  @Test
  public void testMoveToPitPosition() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());
    controller.playGuiGame();

    controller.move("R");
    controller.move("D");
    controller.move("L");
    controller.move("L");

    assertTrue(out.toString().contains("Game over, you died!"));
  }

  /**
   * Tests moving to end node to win the game.
   */
  @Test
  public void testMoveToEndNode() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());
    controller.playGuiGame();

    controller.move("R");
    controller.move("D");
    controller.move("D");
    controller.move("R");
    controller.move("U");
    controller.move("U");

    assertTrue(out.toString().contains("You have reached the end of the dungeon!"));
  }

  /**
   * Tests picking up an item.
   */
  @Test
  public void testPickUpItem() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());
    controller.playGuiGame();

    controller.move("R");
    controller.move("D");
    controller.move("L");

    controller.pickUpItem("DIAMOND");
    controller.pickUpItem("SAPPHIRE");

    assertEquals("[SAPPHIRE: 1, DIAMOND: 1]", this.player.getPlayerTreasuresList().toString());
  }

  /**
   * Tests picking up arrow.
   */
  @Test
  public void testPickUpArrow() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());
    controller.playGuiGame();

    controller.move("R");
    controller.move("D");
    controller.move("L");

    controller.pickUpItem("ARROW");

    assertEquals(4, this.player.getNumArrows());
  }

  /**
   * Tests shooting an arrow.
   */
  @Test
  public void testShootArrow() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());
    controller.playGuiGame();

    controller.move("R");
    controller.move("D");
    controller.move("L");

    assertEquals("[2, 1, -1]", controller.shoot("D", 1).toString());
  }

  /**
   * Tests is dungeon wrapping for a wrapping dungeon.
   */
  @Test
  public void testIsWrapping() {
    this.player.enterPlayerToDungeon(this.wrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.wrappingDungeon,
            this.player, view, getRandom());

    assertTrue(controller.isDungeonWrapping());
  }

  /**
   * Tests is dungeon wrapping for a non-wrapping dungeon.
   */
  @Test
  public void testIsNotWrapping() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());

    assertFalse(controller.isDungeonWrapping());
  }

  /**
   * Tests get dungeon height.
   */
  @Test
  public void testGetDungeonHeight() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());

    assertEquals(4, controller.getDungeonHeight());
  }

  /**
   * Tests get dungeon width.
   */
  @Test
  public void testGetDungeonWidth() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());

    assertEquals(4, controller.getDungeonWidth());
  }

  /**
   * Tests get player name.
   */
  @Test
  public void testGetPlayerName() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());

    assertEquals("Player", controller.getPlayerName());
  }

  /**
   * Tests get arrows left.
   */
  @Test
  public void testGetArrowsLeft() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());

    assertEquals(3, controller.getArrowsLeft());
  }

  /**
   * Tests get current position.
   */
  @Test
  public void testGetCurrentPositionForCave() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());

    assertEquals("TUNNEL", controller.getCurrentPositionType());

    controller.move("R");
    controller.move("D");

    assertEquals("CAVE", controller.getCurrentPositionType());
  }

  /**
   * Tests get treasure in node.
   */
  @Test
  public void testGetTreasureInNode() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());

    assertEquals("[]", controller.getTreasuresInNode().toString());

    controller.move("R");
    controller.move("D");
    controller.move("L");

    assertEquals("[SAPPHIRE: 2, DIAMOND: 2]", controller.getTreasuresInNode().toString());
  }

  /**
   * Tests get number of arrows.
   */
  @Test
  public void testGetNumberOfArrows() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());

    controller.move("R");
    controller.move("D");
    controller.move("L");
    assertEquals(4, controller.getNumArrows());
  }

  /**
   * Tests if current location has smell or not.
   */
  @Test
  public void testHasSmell() {
    Dungeon nonWrappingDungeon = getDeterministicNonWrappingDungeon("7");
    this.player.enterPlayerToDungeon(nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(nonWrappingDungeon,
            this.player, view, getRandom());

    assertEquals("YES", controller.hasSmell());
  }

  /**
   * Tests setting player name.
   */
  @Test
  public void testSetPlayerName() {
    this.player.enterPlayerToDungeon(this.nonWrappingDungeon);
    Appendable out = new StringBuilder();
    DungeonView view = new MockDungeonView(out);
    DungeonControllerWView controller = new DungeonSwingController(this.nonWrappingDungeon,
            this.player, view, getRandom());

    controller.setPlayerName("Test");
    assertEquals("Test", controller.getPlayerName());
  }
}
