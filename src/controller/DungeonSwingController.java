package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import dungeongame.Dungeon;
import dungeongame.DungeonImpl;
import dungeongame.Player;
import dungeongame.PlayerImpl;
import dungeongame.Yugoloth;
import node.Node;
import view.DungeonView;
import view.NodeType;

/**
 * Implementation of a dungeon controller with a GUI view.
 */
public class DungeonSwingController implements DungeonControllerWView {
  private Dungeon dungeon;
  private Player player;
  private final DungeonView view;
  private final Random random;
  private String playerName;
  private DungeonConfig currentDungeonConfig;

  private static class DungeonConfig {
    private final Node[][] maze;
    private final Node startNode;
    private final Node endNode;
    private final boolean isWrapping;
    private final int numOtyughs;
    private final Yugoloth yugoloth;
    private final Node yugolothPosition;
    private final Node thiefPosition;
    private final Node pitPosition;

    public DungeonConfig(Dungeon dungeon) {
      this.maze = dungeon.getInitialMaze();
      this.startNode = dungeon.getStartNode();
      this.endNode = dungeon.getEndNode();
      this.isWrapping = dungeon.isWrapping();
      this.numOtyughs = dungeon.getNumOtyughs();
      this.yugoloth = dungeon.getYugoloth();
      this.yugolothPosition = dungeon.getInitialYugolothPosition();
      this.thiefPosition = dungeon.getThiefPosition();
      this.pitPosition = dungeon.getPitPosition();
    }
  }

  /**
   * Constructs a new GUI dungeon controller.
   *
   * @param dungeon the dungeon to be controlled
   * @param player  the player to be controlled
   * @param view    the view to be used
   * @param random  the random number generator to be used
   */
  public DungeonSwingController(Dungeon dungeon, Player player, DungeonView view, Random random) {
    this.dungeon = dungeon;
    this.player = player;
    this.view = view;
    this.random = random;

    this.currentDungeonConfig = new DungeonConfig(dungeon);
  }

  /**
   * Starts the dungeon game with a dungeon instance and a player which actually plays
   * the game.
   *
   * @param dungeon the dungeon instance
   * @param player  the player instance
   */
  @Override
  public void playGame(Dungeon dungeon, Player player) {
    throw new UnsupportedOperationException("Console mode not available in GUI!");
  }

  private List<List<NodeType>> getNodeTypeFromMaze(Node[][] maze) {
    List<List<NodeType>> nodeTypes = new ArrayList<>();
    List<NodeType> row;
    List<String> neighbours;

    for (Node[] nodes : maze) {
      row = new ArrayList<>();
      for (Node node : nodes) {
        if (node.getType().equals("C")) {
          neighbours = new ArrayList<>();
          if (node.getTopNode() != null) {
            neighbours.add("N");
          }
          if (node.getBottomNode() != null) {
            neighbours.add("S");
          }
          if (node.getLeftNode() != null) {
            neighbours.add("W");
          }
          if (node.getRightNode() != null) {
            neighbours.add("E");
          }

          if (neighbours.size() == 1) {
            switch (neighbours.get(0)) {
              case "N":
                row.add(NodeType.CAVE_N);
                break;
              case "S":
                row.add(NodeType.CAVE_S);
                break;
              case "W":
                row.add(NodeType.CAVE_W);
                break;
              case "E":
                row.add(NodeType.CAVE_E);
                break;
              default:
                break;
            }
          } else if (neighbours.size() == 3) {
            if (neighbours.contains("N") && neighbours.contains("S") && neighbours.contains("E")) {
              row.add(NodeType.CAVE_NSE);
            } else if (neighbours.contains("N") && neighbours.contains("S")
                    && neighbours.contains("W")) {
              row.add(NodeType.CAVE_NSW);
            } else if (neighbours.contains("S") && neighbours.contains("E")
                    && neighbours.contains("W")) {
              row.add(NodeType.CAVE_SEW);
            } else if (neighbours.contains("N") && neighbours.contains("E")
                    && neighbours.contains("W")) {
              row.add(NodeType.CAVE_NEW);
            }
          } else if (neighbours.size() == 4) {
            row.add(NodeType.CAVE_NSEW);
          }
        } else if (node.getType().equals("T")) {
          neighbours = new ArrayList<>();

          if (node.getTopNode() != null) {
            neighbours.add("N");
          }
          if (node.getBottomNode() != null) {
            neighbours.add("S");
          }
          if (node.getLeftNode() != null) {
            neighbours.add("W");
          }
          if (node.getRightNode() != null) {
            neighbours.add("E");
          }

          if (neighbours.contains("N") && neighbours.contains("S")) {
            row.add(NodeType.TUNNEL_NS);
          } else if (neighbours.contains("E") && neighbours.contains("W")) {
            row.add(NodeType.TUNNEL_EW);
          } else if (neighbours.contains("N") && neighbours.contains("E")) {
            row.add(NodeType.TUNNEL_NE);
          } else if (neighbours.contains("N") && neighbours.contains("W")) {
            row.add(NodeType.TUNNEL_NW);
          } else if (neighbours.contains("S") && neighbours.contains("E")) {
            row.add(NodeType.TUNNEL_SE);
          } else if (neighbours.contains("S") && neighbours.contains("W")) {
            row.add(NodeType.TUNNEL_SW);
          }
        }
      }

      nodeTypes.add(row);
    }

    return nodeTypes;
  }

  private void addSmellToNode() {
    Node[][] maze = dungeon.getMaze();

    String roomDescription = this.dungeon.getCurrentPositionPlayer().getRoomDescription();
    int playerI = -1;
    int playerJ = -1;
    for (int i = 0; i < maze.length; i++) {
      for (int j = 0; j < maze[i].length; j++) {
        if (maze[i][j].getNodeName()
                .equals(this.dungeon.getCurrentPositionPlayer().getNodeName())) {
          playerI = i;
          playerJ = j;
        }
      }
    }

    if (roomDescription.contains("You smell a pungent smell")) {
      this.view.addSmellToNode(playerI, playerJ, "weak");
    } else if (roomDescription.contains("You smell a strong pungent smell")) {
      this.view.addSmellToNode(playerI, playerJ, "strong");
    }
  }

  /**
   * Starts the dungeon game with an interactive GUI and a player which actually plays
   * the game.
   */
  @Override
  public void playGuiGame() {
    this.view.refresh();
    Node[][] maze = this.dungeon.getMaze();
    this.view.addNodeTypes(getNodeTypeFromMaze(maze));
    this.view.makeVisible();
    this.view.makeNonResizable();

    this.view.addPlayerToNode(this.dungeon.getStartNodeI(), this.dungeon.getStartNodeJ());

    List<Integer> otyughNodeIs = this.dungeon.getOtyughNodeIs();
    List<Integer> otyughNodeJs = this.dungeon.getOtyughNodeJs();

    for (int i = 0; i < otyughNodeIs.size(); i++) {
      this.view.addOtyughToNode(otyughNodeIs.get(i), otyughNodeJs.get(i));
    }

    Map<List<Integer>, List<String>> treasureAndLocations = this.dungeon.getTreasureAndLocations();
    for (Map.Entry<List<Integer>, List<String>> entry : treasureAndLocations.entrySet()) {
      List<Integer> treasureLocation = entry.getKey();
      List<String> treasures = entry.getValue();

      this.view.addTreasureToNode(treasureLocation.get(0), treasureLocation.get(1), treasures);
    }

    for (int i = 0; i < maze.length; i++) {
      for (int j = 0; j < maze[i].length; j++) {
        if (maze[i][j].getNumArrows() > 0) {
          this.view.addArrowsToNode(i, j, maze[i][j].getNumArrows());
        }
      }
    }

    addSmellToNode();

    int yugolothI = this.dungeon.getYugolothNodeI();
    int yugolothJ = this.dungeon.getYugolothNodeJ();
    this.view.addYugolothToNode(yugolothI, yugolothJ);

    int thiefI = this.dungeon.getThiefNodeI();
    int thiefJ = this.dungeon.getThiefNodeJ();
    this.view.addThiefToNode(thiefI, thiefJ);

    int pitI = this.dungeon.getPitNodeI();
    int pitJ = this.dungeon.getPitNodeJ();
    this.view.addPitToNode(pitI, pitJ);

    int endNodeI = -1;
    int endNodeJ = -1;
    for (int i = 0; i < maze.length; i++) {
      for (int j = 0; j < maze[i].length; j++) {
        if (maze[i][j].getNodeName().equals(this.dungeon.getEndNode().getNodeName())) {
          endNodeI = i;
          endNodeJ = j;
        }
      }
    }
    this.view.markEndNode(endNodeI, endNodeJ);

    this.view.uncoverNode(this.dungeon.getStartNodeI(), this.dungeon.getStartNodeJ());

    this.view.asyncMoveYugoloth();
  }

  private void removeSmellFromNode(boolean forced) {
    Node currentPlayerPosition = this.dungeon.getCurrentPositionPlayer();
    if (currentPlayerPosition.getRoomDescription().contains("smell") || forced) {
      int playerI = -1;
      int playerJ = -1;

      for (int i = 0; i < this.dungeon.getMaze().length; i++) {
        for (int j = 0; j < this.dungeon.getMaze()[i].length; j++) {
          if (this.dungeon.getMaze()[i][j].getNodeName()
                  .equals(currentPlayerPosition.getNodeName())) {
            playerI = i;
            playerJ = j;
          }
        }
      }

      this.view.removeSmellFromNode(playerI, playerJ);
    }
  }

  /**
   * Moves the player and returns if the move was successful or not.
   *
   * @param direction the direction to move in
   * @return true if the move was successful, false otherwise
   */
  @Override
  public boolean move(String direction) {
    boolean movedSuccessfully = true;

    // Remove smell from current node if any
    removeSmellFromNode(true);

    switch (direction) {
      case "U":
        try {
          this.player.moveUp(this.dungeon);
        } catch (IllegalArgumentException e) {
          this.view.displayDialog("Error!", "Cannot move to the north!");
          movedSuccessfully = false;
        }
        break;
      case "D":
        try {
          this.player.moveDown(this.dungeon);
        } catch (IllegalArgumentException e) {
          this.view.displayDialog("Error!", "Cannot move to the south!");
          movedSuccessfully = false;
        }
        break;
      case "L":
        try {
          this.player.moveLeft(this.dungeon);
        } catch (IllegalArgumentException e) {
          this.view.displayDialog("Error!", "Cannot move to the west!");
          movedSuccessfully = false;
        }
        break;
      case "R":
        try {
          this.player.moveRight(this.dungeon);
        } catch (IllegalArgumentException e) {
          this.view.displayDialog("Error!", "Cannot move to the east!");
          movedSuccessfully = false;
        }
        break;
      default:
        throw new IllegalArgumentException("Invalid direction");
    }

    if (this.player.isPlayerDead()) {
      killPlayer();
    }

    if (this.dungeon.getYugolothPosition() != null
            && this.dungeon.getYugolothPosition().getNodeName()
            .equals(this.dungeon.getCurrentPositionPlayer().getNodeName())) {
      this.view.stopYugolothMovement();
      if (this.dungeon.handToHandBattle()) {
        this.killPlayer();
      } else {
        this.killYugoloth();
      }
    }

    addSmellToNode();

    if (this.dungeon.getThiefPosition() != null
            && this.dungeon.getThiefPosition().getNodeName()
            .equals(this.dungeon.getCurrentPositionPlayer().getNodeName())) {
      this.player.treasureStolen();
      this.view.displayDialog("Success!", "All your treasure has been stolen!");
    }

    if (this.dungeon.getPitPosition() != null
            && this.dungeon.getPitPosition().getNodeName()
            .equals(this.dungeon.getCurrentPositionPlayer().getNodeName())) {
      this.killPlayer();
    }

    if (this.dungeon.getEndNode().getNodeName()
            .equals(this.dungeon.getCurrentPositionPlayer().getNodeName())) {
      this.view.displayDialog("Success!", "You have reached the end of the dungeon!");
      this.view.stopYugolothMovement();
      this.view.disableActions();
    }

    return movedSuccessfully;
  }

  /**
   * Checks if there is an item (treasure or arrow) in the given position.
   *
   * @param i the row
   * @param j the column
   * @return true if there is an item in the given position, false otherwise
   */
  @Override
  public boolean hasItemAtLocation(int i, int j) {
    Node[][] maze = this.dungeon.getMaze();
    return (maze[i][j].getType().equals("C") && maze[i][j].getTreasures().size() > 0)
            || (maze[i][j].getNumArrows() > 0);
  }

  /**
   * Picks up item with given name from the current position of player.
   *
   * @param itemName the name of the item to pick up
   */
  @Override
  public void pickUpItem(String itemName) {
    if (itemName.equalsIgnoreCase("ARROW")) {
      this.player.pickUpArrow(this.dungeon);
    } else {
      this.player.pickUpTreasure(this.dungeon, itemName);
    }
  }

  /**
   * Returns the current dungeon's configuration.
   *
   * @return the current dungeon's configuration
   */
  @Override
  public List<Object> getDungeonConfig() {
    return this.dungeon.getDungeonConfig();
  }

  private void startWithNewDungeon() {
    this.player = new PlayerImpl(this.playerName);
    this.player.enterPlayerToDungeon(this.dungeon);
    this.view.refresh();
    this.playGuiGame();
  }

  /**
   * Creates a new random dungeon from the given configuration.
   *
   * @param numRows                     the number of rows in the dungeon
   * @param numCols                     the number of columns in the dungeon
   * @param interconnectivity           the interconnectivity of the dungeon
   * @param isWrapping                  whether the dungeon wraps around
   * @param percentCavesWithTreasures   the percentage of caves that have treasures
   * @param forceInterconnectivityRange whether to force interconnectivity or not
   * @param numOtyughs                  the number of otyughs in the dungeon
   */
  @Override
  public void getNewDungeon(int numRows, int numCols, int interconnectivity,
                            boolean isWrapping, float percentCavesWithTreasures,
                            boolean forceInterconnectivityRange, int numOtyughs) {
    this.dungeon = new DungeonImpl(numRows, numCols, interconnectivity, isWrapping,
            percentCavesWithTreasures, forceInterconnectivityRange, numOtyughs, this.random);

    startWithNewDungeon();

    this.currentDungeonConfig = new DungeonConfig(dungeon);
  }

  /**
   * Shoots an arrow in a given direction and to a specified distance.
   *
   * @param direction the direction to shoot the arrow
   * @param distance  the distance to shoot the arrow
   * @return the result of the shooting
   */
  @Override
  public List<Integer> shoot(String direction, int distance) {
    List<Integer> result = this.player.shootArrow(this.dungeon, direction, distance);
    if (result.get(2) == 1) {
      removeSmellFromNode(true);
    }

    return result;
  }

  /**
   * Checks if the dungeon is wrapping or not.
   *
   * @return true if the dungeon is wrapping, false otherwise
   */
  @Override
  public boolean isDungeonWrapping() {
    return this.dungeon.isWrapping();
  }

  /**
   * Returns the height of the dungeon.
   *
   * @return the height of the dungeon
   */
  @Override
  public int getDungeonHeight() {
    return (int) this.dungeon.getDungeonConfig().get(0);
  }

  /**
   * Returns the width of the dungeon.
   *
   * @return the width of the dungeon
   */
  @Override
  public int getDungeonWidth() {
    return (int) this.dungeon.getDungeonConfig().get(1);
  }

  /**
   * Returns the name of the player playing the game.
   *
   * @return the name of the player playing the game
   */
  @Override
  public String getPlayerName() {
    return this.player.getName();
  }

  /**
   * Returns the number of arrows left in the player's inventory.
   *
   * @return the number of arrows left in the player's inventory
   */
  @Override
  public int getArrowsLeft() {
    return this.player.getNumArrows();
  }

  /**
   * Returns the treasures collected by the player.
   *
   * @return the treasures collected by the player
   */
  @Override
  public List<String> getTreasuresCollected() {
    return this.player.getPlayerTreasuresList();
  }

  /**
   * Returns the current position type (cave/tunnel) of the player.
   *
   * @return the current position type (cave/tunnel) of the player
   */
  @Override
  public String getCurrentPositionType() {
    String nodeType = this.dungeon.getCurrentPositionPlayer().getType();

    if (nodeType.equals("C")) {
      return "CAVE";
    } else if (nodeType.equals("T")) {
      return "TUNNEL";
    }

    return "";
  }

  /**
   * Returns the list of treasures in the current location of player.
   *
   * @return the list of treasures in the current location of player
   */
  @Override
  public List<String> getTreasuresInNode() {
    if (this.dungeon.getCurrentPositionPlayer().getType().equals("C")) {
      Map<String, Integer> treasures = new HashMap<>();
      for (String treasure : this.dungeon.getCurrentPositionPlayer().getTreasures()) {
        if (treasures.containsKey(treasure)) {
          treasures.put(treasure, treasures.get(treasure) + 1);
        } else {
          treasures.put(treasure, 1);
        }
      }

      List<String> treasuresList = new ArrayList<>();
      for (String treasure : treasures.keySet()) {
        treasuresList.add(treasure + ": " + treasures.get(treasure));
      }

      return treasuresList;
    }

    return new ArrayList<>();
  }

  /**
   * Returns the number of arrows in the current location of player.
   *
   * @return the number of arrows in the current location of player
   */
  @Override
  public int getNumArrows() {
    return this.dungeon.getCurrentPositionPlayer().getNumArrows();
  }

  /**
   * Checks if the current location of player has any smell or not.
   *
   * @return YES if the current location of player has any smell, NO otherwise
   */
  @Override
  public String hasSmell() {
    return this.dungeon.getCurrentPositionPlayer().getRoomDescription().contains("smell")
            ? "YES" : "NO";
  }

  /**
   * Sets the player's name to the given name.
   *
   * @param playerName the name to set the player's name to
   */
  @Override
  public void setPlayerName(String playerName) {
    this.playerName = playerName;
    this.player.updateName(playerName);
  }

  private void killPlayer() {
    this.view.removePlayerFromNode();
    this.view.displayDialog("Game Over!", "Game over, you died!");
    removeSmellFromNode(true);
    this.player.killPlayer();
    this.view.disableActions();
    this.view.stopYugolothMovement();
  }

  /**
   * Move the Yugoloths around the dungeon.
   */
  @Override
  public void moveMonsters() {
    int yugolothNodeI = this.dungeon.getYugolothNodeI();
    int yugolothNodeJ = this.dungeon.getYugolothNodeJ();

    this.view.removeYugolothFromNode(yugolothNodeI, yugolothNodeJ);

    this.dungeon.moveYugoloth();

    yugolothNodeI = this.dungeon.getYugolothNodeI();
    yugolothNodeJ = this.dungeon.getYugolothNodeJ();

    this.view.addYugolothToNode(yugolothNodeI, yugolothNodeJ);

    if (this.dungeon.getYugolothPosition().getNodeName()
            .equals(this.dungeon.getCurrentPositionPlayer().getNodeName())) {
      this.view.stopYugolothMovement();
      if (this.dungeon.handToHandBattle()) {
        killPlayer();
      } else {
        killYugoloth();
      }
    }
  }

  private void killYugoloth() {
    int yugolothNodeI = this.dungeon.getYugolothNodeI();
    int yugolothNodeJ = this.dungeon.getYugolothNodeJ();
    this.dungeon.killYugoloth();
    this.view.removeYugolothFromNode(yugolothNodeI, yugolothNodeJ);
    this.view.displayDialog("Success!", "You killed the Yugoloth!");
  }

  /**
   * Click and move the player in the dungeon.
   *
   * @param r the row to move to
   * @param c the column to move to
   */
  @Override
  public void clickAndMove(int r, int c) {
    Node currentPositionPlayer = this.dungeon.getCurrentPositionPlayer();
    Node[][] maze = this.dungeon.getMaze();

    int currentPlayerI = -1;
    int currentPlayerJ = -1;
    for (int i = 0; i < maze.length; i++) {
      for (int j = 0; j < maze[i].length; j++) {
        if (maze[i][j].getNodeName().equals(currentPositionPlayer.getNodeName())) {
          currentPlayerI = i;
          currentPlayerJ = j;
        }
      }
    }

    int manhattanDistance = Math.abs(currentPlayerI - r) + Math.abs(currentPlayerJ - c);

    if (manhattanDistance != 1) {
      this.view.displayDialog("Error!", "Cannot move to a node that is farther than" +
              " 1 step away from the current position.");
    } else {
      int rowDiff = currentPlayerI - r;
      int colDiff = currentPlayerJ - c;

      if (rowDiff < 0) {
        this.view.moveInDirection("D");
      } else if (rowDiff > 0) {
        this.view.moveInDirection("U");
      } else if (colDiff < 0) {
        this.view.moveInDirection("R");
      } else if (colDiff > 0) {
        this.view.moveInDirection("L");
      }
    }
  }

  /**
   * Check if the otyugh at position is hit with a single arrow or not.
   *
   * @param r the row of the otyugh
   * @param c the column of the otyugh
   * @return true if the otyugh is hit, false otherwise
   */
  @Override
  public boolean isOtyughHit(int r, int c) {
    Node node = this.dungeon.getMaze()[r][c];
    return node.getOtyugh() != null && node.getOtyugh().isHit();
  }

  /**
   * Restarts the game with the current dungeon.
   */
  @Override
  public void restartGame() {
    this.view.stopYugolothMovement();

    this.dungeon = new DungeonImpl(this.currentDungeonConfig.maze,
            this.currentDungeonConfig.startNode.getNodeName(),
            this.currentDungeonConfig.endNode.getNodeName(), this.currentDungeonConfig.isWrapping,
            this.currentDungeonConfig.numOtyughs, this.currentDungeonConfig.yugolothPosition,
            this.currentDungeonConfig.yugoloth, this.currentDungeonConfig.thiefPosition,
            this.currentDungeonConfig.pitPosition,
            this.random);

    startWithNewDungeon();
  }
}
