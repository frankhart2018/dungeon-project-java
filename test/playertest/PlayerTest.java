package playertest;

import org.junit.Before;
import org.junit.Test;

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
 * Tests for the Player representation.
 */
public class PlayerTest {
  private Player player;
  private Dungeon dungeon;

  @Before
  public void setUp() {
    this.player = new PlayerImpl("TestPlayer");
    this.dungeon = getDeterministicDungeon("2", "13");
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
    for (String otyughPosition : otyughPositions) {
      for (Node[] row : graph) {
        for (Node node : row) {
          if (node.getNodeName().equals(otyughPosition)) {
            node.placeOtyugh(new Otyugh());
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

    return new DungeonImpl(graph, "7", "13",
            true, 2, graph[0][0], new Yugoloth(), graph[0][0], graph[0][0]);
  }

  private boolean makeSeriesOfMoves(String... moves) {
    boolean isPlayerDead = false;
    for (String move : moves) {
      switch (move) {
        case "U":
          this.player.moveUp(this.dungeon);
          break;
        case "D":
          this.player.moveDown(this.dungeon);
          break;
        case "L":
          this.player.moveLeft(this.dungeon);
          break;
        case "R":
          this.player.moveRight(this.dungeon);
          break;
        default:
          break;
      }
    }

    return isPlayerDead;
  }

  /**
   * Tests constructing a player with null or empty name.
   */
  @Test
  public void testConstructorNullName() {
    try {
      new PlayerImpl(null);
      fail("Name of player cannot be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Name of player is expected to be non-null!", e.getMessage());
    }

    try {
      new PlayerImpl("");
      fail("Name of player cannot be empty!");
    } catch (IllegalArgumentException e) {
      assertEquals("Name of player cannot be empty!", e.getMessage());
    }
  }

  /**
   * Tests entering player to a dungeon when the dungeon object is null.
   */
  @Test
  public void testEnterPlayerToDungeonNullDungeon() {
    try {
      this.player.enterPlayerToDungeon(null);
      fail("Dungeon instance cannot be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Dungeon instance is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests entering player to a dungeon when the dungeon already has the player.
   */
  @Test
  public void testEnterPlayerToDungeonAlreadyInDungeon() {
    this.player.enterPlayerToDungeon(this.dungeon);

    try {
      this.player.enterPlayerToDungeon(this.dungeon);
      fail("Player is already in the dungeon!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot place same player in dungeon more than once!", e.getMessage());
    }
  }

  /**
   * Tests entering player to a dungeon when the dungeon already has another player.
   */
  @Test
  public void testEnterPlayerToDungeonAnotherPlayerInDungeon() {
    Player anotherPlayer = new PlayerImpl("anotherPlayer");
    anotherPlayer.enterPlayerToDungeon(this.dungeon);

    try {
      this.player.enterPlayerToDungeon(this.dungeon);
      fail("Another player is already in the dungeon!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot place more than one player in dungeon!", e.getMessage());
    }
  }

  /**
   * Tests entering a player to a dungeon and placed in start node.
   */
  @Test
  public void testEnterPlayerToDungeon() {
    this.player.enterPlayerToDungeon(this.dungeon);

    assertEquals("7", this.dungeon.getCurrentPositionPlayer().getNodeName());
  }

  /**
   * Tests moving a player to left when the dungeon object is null.
   */
  @Test
  public void testMoveLeftNullDungeon() {
    try {
      this.player.moveLeft(null);
      fail("Dungeon instance cannot be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Dungeon instance is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to left when no player is placed in the dungeon.
   */
  @Test
  public void testMoveLeftNoPlayerInDungeon() {
    try {
      this.player.moveLeft(this.dungeon);
      fail("Player is not in the dungeon!");
    } catch (IllegalArgumentException e) {
      assertEquals("No player placed in dungeon!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to left when another player is placed in the dungeon.
   */
  @Test
  public void testMoveLeftAnotherPlayerInDungeon() {
    Player anotherPlayer = new PlayerImpl("anotherPlayer");
    anotherPlayer.enterPlayerToDungeon(this.dungeon);

    try {
      this.player.moveLeft(this.dungeon);
      fail("Another player is already in the dungeon!");
    } catch (IllegalArgumentException e) {
      assertEquals("This player is not placed in the dungeon!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to left when there is no left path.
   */
  @Test
  public void testMoveLeftNoLeftPath() {
    this.player.enterPlayerToDungeon(this.dungeon);

    try {
      this.player.moveLeft(this.dungeon);
      fail("There is no left path!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot move west!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to left and the player does not die.
   */
  @Test
  public void testMoveLeftPlayerAlive() {
    this.player.enterPlayerToDungeon(this.dungeon);
    assertEquals("7", this.dungeon.getCurrentPositionPlayer().getNodeName());

    makeSeriesOfMoves("R");
    this.player.moveLeft(this.dungeon);

    assertEquals("7", this.dungeon.getCurrentPositionPlayer().getNodeName());
    assertFalse(this.player.isPlayerDead());
  }

  /**
   * Tests moving a player to left and the player dies.
   */
  @Test
  public void testMoveLeftPlayerDead() {
    this.player.enterPlayerToDungeon(this.dungeon);
    assertEquals("7", this.dungeon.getCurrentPositionPlayer().getNodeName());

    makeSeriesOfMoves("R", "D", "L", "L", "D");
    this.player.moveLeft(this.dungeon);

    assertEquals("13", this.dungeon.getCurrentPositionPlayer().getNodeName());
    assertTrue(this.player.isPlayerDead());
  }

  /**
   * Tests moving a player to left after the player is dead.
   */
  @Test
  public void testMoveLeftPlayerDeadAfterMove() {
    this.player.enterPlayerToDungeon(this.dungeon);
    makeSeriesOfMoves("R", "D", "L", "L", "D", "L");

    try {
      this.player.moveLeft(this.dungeon);
      fail("Player is dead!");
    } catch (IllegalArgumentException e) {
      assertEquals("Player is dead!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to right when the dungeon object is null.
   */
  @Test
  public void testMoveRightNullDungeon() {
    try {
      this.player.moveRight(null);
      fail("Dungeon instance cannot be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Dungeon instance is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to right when no player is placed in the dungeon.
   */
  @Test
  public void testMoveRightNoPlayerInDungeon() {
    try {
      this.player.moveRight(this.dungeon);
      fail("Player is not in the dungeon!");
    } catch (IllegalArgumentException e) {
      assertEquals("No player placed in dungeon!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to right when another player is placed in the dungeon.
   */
  @Test
  public void testMoveRightAnotherPlayerInDungeon() {
    Player anotherPlayer = new PlayerImpl("anotherPlayer");
    anotherPlayer.enterPlayerToDungeon(this.dungeon);

    try {
      this.player.moveRight(this.dungeon);
      fail("Another player is already in the dungeon!");
    } catch (IllegalArgumentException e) {
      assertEquals("This player is not placed in the dungeon!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to left when there is no left path.
   */
  @Test
  public void testMoveRightNoRightPath() {
    this.player.enterPlayerToDungeon(this.dungeon);
    makeSeriesOfMoves("R");

    try {
      this.player.moveRight(this.dungeon);
      fail("There is no right path!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot move east!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to right and the player does not die.
   */
  @Test
  public void testMoveRightPlayerAlive() {
    this.player.enterPlayerToDungeon(this.dungeon);
    assertEquals("7", this.dungeon.getCurrentPositionPlayer().getNodeName());

    this.player.moveRight(this.dungeon);

    assertEquals("8", this.dungeon.getCurrentPositionPlayer().getNodeName());
    assertFalse(this.player.isPlayerDead());
  }

  /**
   * Tests moving a player to right and the player dies.
   */
  @Test
  public void testMoveRightPlayerDead() {
    this.player.enterPlayerToDungeon(this.dungeon);
    assertEquals("7", this.dungeon.getCurrentPositionPlayer().getNodeName());

    makeSeriesOfMoves("R", "D", "L", "L", "L", "U", "U");
    this.player.moveRight(this.dungeon);

    assertEquals("2", this.dungeon.getCurrentPositionPlayer().getNodeName());
    assertTrue(this.player.isPlayerDead());
  }

  /**
   * Tests moving a player to right after the player is dead.
   */
  @Test
  public void testMoveRightPlayerDeadAfterMove() {
    this.player.enterPlayerToDungeon(this.dungeon);
    makeSeriesOfMoves("R", "D", "L", "L", "L", "U", "U", "R");

    try {
      this.player.moveRight(this.dungeon);
      fail("Player is dead!");
    } catch (IllegalArgumentException e) {
      assertEquals("Player is dead!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to up when the dungeon object is null.
   */
  @Test
  public void testMoveUpNullDungeon() {
    try {
      this.player.moveUp(null);
      fail("Dungeon instance cannot be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Dungeon instance is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to up when no player is placed in the dungeon.
   */
  @Test
  public void testMoveUpNoPlayerInDungeon() {
    try {
      this.player.moveUp(this.dungeon);
      fail("Player is not in the dungeon!");
    } catch (IllegalArgumentException e) {
      assertEquals("No player placed in dungeon!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to up when another player is placed in the dungeon.
   */
  @Test
  public void testMoveUpAnotherPlayerInDungeon() {
    Player anotherPlayer = new PlayerImpl("anotherPlayer");
    anotherPlayer.enterPlayerToDungeon(this.dungeon);

    try {
      this.player.moveUp(this.dungeon);
      fail("Another player is already in the dungeon!");
    } catch (IllegalArgumentException e) {
      assertEquals("This player is not placed in the dungeon!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to up when there is no up path.
   */
  @Test
  public void testMoveUpNoUpPath() {
    this.player.enterPlayerToDungeon(this.dungeon);

    try {
      this.player.moveUp(this.dungeon);
      fail("There is no up path!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot move north!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to up and the player does not die.
   */
  @Test
  public void testMoveUpPlayerAlive() {
    this.player.enterPlayerToDungeon(this.dungeon);
    assertEquals("7", this.dungeon.getCurrentPositionPlayer().getNodeName());

    makeSeriesOfMoves("R");
    this.player.moveUp(this.dungeon);

    assertEquals("4", this.dungeon.getCurrentPositionPlayer().getNodeName());
    assertFalse(this.player.isPlayerDead());
  }

  /**
   * Tests moving a player to up and the player dies.
   */
  @Test
  public void testMoveUpPlayerDead() {
    this.player.enterPlayerToDungeon(this.dungeon);
    assertEquals("7", this.dungeon.getCurrentPositionPlayer().getNodeName());

    makeSeriesOfMoves("R", "D", "L", "L", "U");
    this.player.moveUp(this.dungeon);

    assertEquals("2", this.dungeon.getCurrentPositionPlayer().getNodeName());
    assertTrue(this.player.isPlayerDead());
  }

  /**
   * Tests moving a player to up after the player is dead.
   */
  @Test
  public void testMoveUpPlayerDeadAfterMove() {
    this.player.enterPlayerToDungeon(this.dungeon);
    makeSeriesOfMoves("R", "D", "L", "L", "U", "U");

    try {
      this.player.moveUp(this.dungeon);
      fail("Player is dead!");
    } catch (IllegalArgumentException e) {
      assertEquals("Player is dead!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to down when the dungeon object is null.
   */
  @Test
  public void testMoveDownNullDungeon() {
    try {
      this.player.moveDown(null);
      fail("Dungeon instance cannot be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Dungeon instance is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to down when no player is placed in the dungeon.
   */
  @Test
  public void testMoveDownNoPlayerInDungeon() {
    try {
      this.player.moveDown(this.dungeon);
      fail("Player is not in the dungeon!");
    } catch (IllegalArgumentException e) {
      assertEquals("No player placed in dungeon!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to down when another player is placed in the dungeon.
   */
  @Test
  public void testMoveDownAnotherPlayerInDungeon() {
    Player anotherPlayer = new PlayerImpl("anotherPlayer");
    anotherPlayer.enterPlayerToDungeon(this.dungeon);

    try {
      this.player.moveDown(this.dungeon);
      fail("Another player is already in the dungeon!");
    } catch (IllegalArgumentException e) {
      assertEquals("This player is not placed in the dungeon!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to down when there is no down path.
   */
  @Test
  public void testMoveDownNoDownPath() {
    this.player.enterPlayerToDungeon(this.dungeon);

    try {
      this.player.moveDown(this.dungeon);
      fail("There is no down path!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot move south!", e.getMessage());
    }
  }

  /**
   * Tests moving a player to down and the player does not die.
   */
  @Test
  public void testMoveDownPlayerAlive() {
    this.player.enterPlayerToDungeon(this.dungeon);
    assertEquals("7", this.dungeon.getCurrentPositionPlayer().getNodeName());

    makeSeriesOfMoves("R");
    this.player.moveDown(this.dungeon);

    assertEquals("12", this.dungeon.getCurrentPositionPlayer().getNodeName());
    assertFalse(this.player.isPlayerDead());
  }

  /**
   * Tests moving a player to down and the player dies.
   */
  @Test
  public void testMoveDownPlayerDead() {
    this.player.enterPlayerToDungeon(this.dungeon);
    assertEquals("7", this.dungeon.getCurrentPositionPlayer().getNodeName());

    makeSeriesOfMoves("R", "D", "L", "L", "D");
    this.player.moveDown(this.dungeon);

    assertEquals("2", this.dungeon.getCurrentPositionPlayer().getNodeName());
    assertTrue(this.player.isPlayerDead());
  }

  /**
   * Tests moving a player to down after the player is dead.
   */
  @Test
  public void testMoveDownPlayerDeadAfterMove() {
    this.player.enterPlayerToDungeon(this.dungeon);
    makeSeriesOfMoves("R", "D", "L", "L", "D", "D");

    try {
      this.player.moveDown(this.dungeon);
      fail("Player is dead!");
    } catch (IllegalArgumentException e) {
      assertEquals("Player is dead!", e.getMessage());
    }
  }

  /**
   * Tests picking up treasure from the dungeon when either the dungeon object
   * or the treasure name is null.
   */
  @Test
  public void testPickUpTreasureNullDungeonOrTreasureName() {
    try {
      this.player.pickUpTreasure(null, "treasure");
      fail("Dungeon instance cannot be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Dungeon instance is expected to be non-null!", e.getMessage());
    }

    try {
      this.player.pickUpTreasure(this.dungeon, null);
      fail("Treasure name cannot be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Name of treasure is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests picking up treasure from dungeon when the current position of player is a tunnel.
   */
  @Test
  public void testPickUpTreasureTunnel() {
    this.player.enterPlayerToDungeon(this.dungeon);

    makeSeriesOfMoves("R", "D");
    try {
      this.player.pickUpTreasure(this.dungeon, "ruby");
      fail("Player is in a tunnel!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Tunnel does not have any treasure to remove!", e.getMessage());
    }
  }

  /**
   * Tests picking up treasure from dungeon when the treasure name is illegal.
   */
  @Test
  public void testPickUpTreasureIllegalTreasureName() {
    this.player.enterPlayerToDungeon(this.dungeon);

    try {
      this.player.pickUpTreasure(this.dungeon, "gold");
      fail("Treasure name is illegal!");
    } catch (IllegalArgumentException e) {
      assertEquals("Illegal treasure name GOLD!", e.getMessage());
    }
  }

  /**
   * Tests picking up treasure from dungeon when the cave has no treasure in it.
   */
  @Test
  public void testPickUpTreasureNoTreasure() {
    this.player.enterPlayerToDungeon(this.dungeon);

    makeSeriesOfMoves("R");
    try {
      this.player.pickUpTreasure(this.dungeon, "ruby");
      fail("Cave has no treasure!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot pick treasure from an empty treasure box!", e.getMessage());
    }
  }

  /**
   * Tests picking up treasure from dungeon when the treasure type is not present in the current
   * cave.
   */
  @Test
  public void testPickUpTreasureTreasureNotPresent() {
    this.player.enterPlayerToDungeon(this.dungeon);

    try {
      this.player.pickUpTreasure(this.dungeon, "sapphire");
      fail("Treasure is not present in the cave!");
    } catch (IllegalArgumentException e) {
      assertEquals("Treasure SAPPHIRE not found in treasure box!", e.getMessage());
    }
  }

  /**
   * Tests picking up treasure from dungeon when no player is placed in the dungeon.
   */
  @Test
  public void testPickUpTreasureNoPlayer() {
    try {
      this.player.pickUpTreasure(this.dungeon, "ruby");
      fail("Player is not placed in the dungeon!");
    } catch (IllegalArgumentException e) {
      assertEquals("No player placed in dungeon!", e.getMessage());
    }
  }

  /**
   * Tests picking up treasure from dungeon when another player is placed in the dungeon.
   */
  @Test
  public void testPickUpTreasureAnotherPlayer() {
    Player anotherPlayer = new PlayerImpl("anotherPlayer");
    anotherPlayer.enterPlayerToDungeon(this.dungeon);

    try {
      this.player.pickUpTreasure(this.dungeon, "ruby");
      fail("Another player is placed in the dungeon!");
    } catch (IllegalArgumentException e) {
      assertEquals("This player is not placed in the dungeon!", e.getMessage());
    }
  }

  /**
   * Tests picking up treasure from dungeon when the player is dead.
   */
  @Test
  public void testPickUpTreasurePlayerDead() {
    this.player.enterPlayerToDungeon(this.dungeon);
    makeSeriesOfMoves("R", "D", "L", "L", "D", "L");

    try {
      this.player.pickUpTreasure(this.dungeon, "ruby");
      fail("Player is dead!");
    } catch (IllegalArgumentException e) {
      assertEquals("Player is dead!", e.getMessage());
    }
  }

  /**
   * Tests picking up treasure from dungeon successfully.
   */
  @Test
  public void testPickUpTreasure() {
    this.player.enterPlayerToDungeon(this.dungeon);

    this.player.pickUpTreasure(this.dungeon, "ruby");
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following treasure count: \n" +
            "RUBY: 1\n", this.player.getPlayerTreasures());
  }

  /**
   * Tests fetching player treasures when the player has not picked up any treasure.
   */
  @Test
  public void testGetPlayerTreasuresNoTreasure() {
    this.player.enterPlayerToDungeon(this.dungeon);
    assertEquals("---------------------------------------------------\n" +
                    "TestPlayer now has the following treasure count: \n" +
                    "No treasure in bag.\n",
            this.player.getPlayerTreasures());
  }

  /**
   * Tests fetching player treasures when the player has picked up one treasure of a single type.
   */
  @Test
  public void testGetPlayerTreasuresOneTreasure() {
    this.player.enterPlayerToDungeon(this.dungeon);
    this.player.pickUpTreasure(this.dungeon, "ruby");
    assertEquals("---------------------------------------------------\n" +
                    "TestPlayer now has the following treasure count: \n" +
                    "RUBY: 1\n",
            this.player.getPlayerTreasures());
  }

  /**
   * Tests fetching player treasures when the player has picked up one treasure of multiple types.
   */
  @Test
  public void testGetPlayerTreasuresMultipleTreasure() {
    this.player.enterPlayerToDungeon(this.dungeon);
    this.player.pickUpTreasure(this.dungeon, "ruby");
    this.player.pickUpTreasure(this.dungeon, "diamond");
    assertEquals("---------------------------------------------------\n" +
                    "TestPlayer now has the following treasure count: \n" +
                    "DIAMOND: 1\n" +
                    "RUBY: 1\n",
            this.player.getPlayerTreasures());
  }

  /**
   * Tests fetching player treasures when the player has picked up multiple treasures of
   * multiple types.
   */
  @Test
  public void testGetPlayerTreasuresMultipleTreasureMultipleTypes() {
    this.player.enterPlayerToDungeon(this.dungeon);
    this.player.pickUpTreasure(this.dungeon, "ruby");
    this.player.pickUpTreasure(this.dungeon, "diamond");

    makeSeriesOfMoves("R", "D", "L", "L");
    this.player.pickUpTreasure(this.dungeon, "diamond");
    this.player.pickUpTreasure(this.dungeon, "diamond");

    assertEquals("---------------------------------------------------\n" +
                    "TestPlayer now has the following treasure count: \n" +
                    "DIAMOND: 3\n" +
                    "RUBY: 1\n",
            this.player.getPlayerTreasures());
  }

  /**
   * Tests picking up arrow from the dungeon with null dungeon instance.
   */
  @Test
  public void testPickUpArrowNullDungeon() {
    try {
      this.player.pickUpArrow(null);
      fail("Dungeon is null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Dungeon instance is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests picking up arrow from dungeon when there is no player in the dungeon.
   */
  @Test
  public void testPickUpArrowNoPlayer() {
    try {
      this.player.pickUpArrow(this.dungeon);
      fail("Player is not in the dungeon!");
    } catch (IllegalArgumentException e) {
      assertEquals("No player placed in dungeon!", e.getMessage());
    }
  }

  /**
   * Tests picking up arrow from dungeon when another player is in the dungeon.
   */
  @Test
  public void testPickUpArrowAnotherPlayer() {
    Player anotherPlayer = new PlayerImpl("anotherPlayer");
    anotherPlayer.enterPlayerToDungeon(this.dungeon);

    try {
      this.player.pickUpArrow(this.dungeon);
      fail("Player is not in the dungeon!");
    } catch (IllegalArgumentException e) {
      assertEquals("This player is not placed in the dungeon!", e.getMessage());
    }
  }

  /**
   * Tests picking up arrow from dungeon when the player is dead.
   */
  @Test
  public void testPickUpArrowDeadPlayer() {
    this.player.enterPlayerToDungeon(this.dungeon);
    makeSeriesOfMoves("R", "D", "L", "L", "D", "L");

    try {
      this.player.pickUpArrow(this.dungeon);
      fail("Player is dead!");
    } catch (IllegalArgumentException e) {
      assertEquals("Player is dead!", e.getMessage());
    }
  }

  /**
   * Tests picking up arrow from dungeon when there is no arrow in the current location.
   */
  @Test
  public void testPickUpArrowNoArrow() {
    this.player.enterPlayerToDungeon(this.dungeon);

    try {
      this.player.pickUpArrow(this.dungeon);
      fail("There is no arrow in the current location!");
    } catch (IllegalArgumentException e) {
      assertEquals("No arrows to remove!", e.getMessage());
    }
  }

  /**
   * Tests picking up arrow from dungeon successfully.
   */
  @Test
  public void testPickUpArrow() {
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "Arrows: 3\n", this.player.getPlayerArrows());


    this.player.enterPlayerToDungeon(this.dungeon);
    makeSeriesOfMoves("R");
    assertEquals(4, this.dungeon.getCurrentPositionPlayer().getNumArrows());
    this.player.pickUpArrow(this.dungeon);

    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "Arrows: 4\n", this.player.getPlayerArrows());
    assertEquals(3, this.dungeon.getCurrentPositionPlayer().getNumArrows());
  }

  /**
   * Tests get player arrows to check if the player has three arrows at beginning.
   */
  @Test
  public void testGetPlayerArrowsGreaterThanZero() {
    this.player.enterPlayerToDungeon(this.dungeon);

    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "Arrows: 3\n", this.player.getPlayerArrows());
  }

  /**
   * Tests get player arrows when the player has no arrow.
   */
  @Test
  public void testGetPlayerArrowsZero() {
    this.player.enterPlayerToDungeon(this.dungeon);
    this.player.shootArrow(dungeon, "R", 1);
    this.player.shootArrow(dungeon, "R", 1);
    this.player.shootArrow(dungeon, "R", 1);

    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "No arrows left.\n", this.player.getPlayerArrows());
  }

  /**
   * Tests get player arrows after picking up a single arrow.
   */
  @Test
  public void testGetPlayerArrowsAfterPickUp() {
    this.player.enterPlayerToDungeon(this.dungeon);
    makeSeriesOfMoves("R");
    this.player.pickUpArrow(this.dungeon);

    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "Arrows: 4\n", this.player.getPlayerArrows());
  }

  /**
   * Tests shooting arrow when the dungeon instance is null, or direction is null, or direction is
   * empty, or distane specified is negative, or distance specified is zero.
   */
  @Test
  public void testInvalidShootArrow() {
    try {
      this.player.shootArrow(null, "R", 1);
      fail("Dungeon is null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Dungeon instance is expected to be non-null!", e.getMessage());
    }

    try {
      this.player.shootArrow(this.dungeon, null, 1);
      fail("Direction is null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Direction in which to shoot is expected to be non-null!", e.getMessage());
    }

    try {
      this.player.shootArrow(this.dungeon, "", 1);
      fail("Direction is empty!");
    } catch (IllegalArgumentException e) {
      assertEquals("Direction cannot be empty!", e.getMessage());
    }

    try {
      this.player.shootArrow(this.dungeon, "R", -1);
      fail("Distance is negative!");
    } catch (IllegalArgumentException e) {
      assertEquals("Distance must be positive!", e.getMessage());
    }

    try {
      this.player.shootArrow(this.dungeon, "R", 0);
      fail("Distance is zero!");
    } catch (IllegalArgumentException e) {
      assertEquals("Distance must be positive!", e.getMessage());
    }
  }

  /**
   * Tests shooting arrow when the player is dead.
   */
  @Test
  public void testShootArrowPlayerDead() {
    this.player.enterPlayerToDungeon(this.dungeon);
    makeSeriesOfMoves("R", "D", "L", "L", "D", "L");

    try {
      this.player.shootArrow(this.dungeon, "R", 1);
      fail("Player is dead!");
    } catch (IllegalArgumentException e) {
      assertEquals("Player is dead!", e.getMessage());
    }
  }

  /**
   * Tests shooting arrow when the player does not have any arrow.
   */
  @Test
  public void testShootArrowNoArrow() {
    this.player.enterPlayerToDungeon(this.dungeon);
    this.player.shootArrow(dungeon, "R", 1);
    this.player.shootArrow(dungeon, "R", 1);
    this.player.shootArrow(dungeon, "R", 1);

    try {
      this.player.shootArrow(this.dungeon, "R", 1);
      fail("Player does not have any arrow!");
    } catch (IllegalArgumentException e) {
      assertEquals("No arrows to shoot, pickup arrows to shoot!", e.getMessage());
    }
  }

  /**
   * Tests shooting arrow when the player is not in the dungeon.
   */
  @Test
  public void testShootArrowNotInDungeon() {
    try {
      this.player.shootArrow(this.dungeon, "R", 1);
      fail("Player is not in the dungeon!");
    } catch (IllegalArgumentException e) {
      assertEquals("No player placed in dungeon!", e.getMessage());
    }
  }

  /**
   * Tests shooting arrow when the another player is in the dungeon.
   */
  @Test
  public void testShootArrowAnotherPlayerInDungeon() {
    Player anotherPlayer = new PlayerImpl("anotherPlayer");
    anotherPlayer.enterPlayerToDungeon(this.dungeon);

    try {
      this.player.shootArrow(this.dungeon, "R", 1);
      fail("Another player is in the dungeon!");
    } catch (IllegalArgumentException e) {
      assertEquals("This player is not placed in the dungeon!", e.getMessage());
    }
  }

  /**
   * Tests shooting arrow and the distance specified is less than the distance to Otyugh.
   */
  @Test
  public void testShootArrowDistanceLessThanOtyugh() {
    this.player.enterPlayerToDungeon(this.dungeon);
    makeSeriesOfMoves("R", "D", "L", "L", "U");

    int hitResult = this.player.shootArrow(dungeon, "D", 1).get(2);
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "Arrows: 2\n", this.player.getPlayerArrows());
    assertEquals(-1, hitResult);
  }

  /**
   * Tests shooting arrow and the distance specified is more than the distance to Otyugh.
   */
  @Test
  public void testShootArrowDistanceMoreThanOtyugh() {
    this.player.enterPlayerToDungeon(this.dungeon);
    makeSeriesOfMoves("R", "D", "L", "L");

    int hitResult = this.player.shootArrow(dungeon, "U", 3).get(2);
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "Arrows: 2\n", this.player.getPlayerArrows());
    assertEquals(-1, hitResult);
  }

  /**
   * Tests shooting arrow and the Otyugh is hit once.
   */
  @Test
  public void testShootArrowOtyughHitOnce() {
    this.player.enterPlayerToDungeon(this.dungeon);
    makeSeriesOfMoves("R", "D", "L", "L");

    int hitResult = this.player.shootArrow(dungeon, "U", 1).get(2);
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "Arrows: 2\n", this.player.getPlayerArrows());
    assertEquals(0, hitResult);
  }

  /**
   * Tests shooting arrow twice and killing the Otyugh.
   */
  @Test
  public void testShootArrowOtyughKill() {
    this.player.enterPlayerToDungeon(this.dungeon);
    makeSeriesOfMoves("R", "D", "L", "L");

    this.player.shootArrow(dungeon, "U", 1);
    int hitResult = this.player.shootArrow(dungeon, "U", 1).get(2);
    assertEquals("---------------------------------------------------\n" +
            "TestPlayer now has the following arrow count: \n" +
            "Arrows: 1\n", this.player.getPlayerArrows());
    assertEquals(1, hitResult);
  }

  /**
   * Tests is player dead when the player is alive.
   */
  @Test
  public void testIsPlayerDeadAlive() {
    this.player.enterPlayerToDungeon(this.dungeon);
    assertFalse(this.player.isPlayerDead());
  }

  /**
   * Tests is player dead when the player is dead.
   */
  @Test
  public void testIsPlayerDeadDead() {
    this.player.enterPlayerToDungeon(this.dungeon);
    makeSeriesOfMoves("R", "D", "L", "L", "U", "U");
    assertTrue(this.player.isPlayerDead());
  }
}
