package view;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FontMetrics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.Timer;

import controller.DungeonControllerWView;

/**
 * Swing implementation of a GUI view of a dungeon.
 */
public class DungeonViewImpl extends JFrame implements DungeonView {
  private MazePanel mazePanel;
  private DungeonControllerWView listener;
  private int currentI;
  private int currentJ;
  private boolean isShootingArrow;
  private boolean inCheatMode;
  private Timer otyughMoveTimer;
  private boolean active;
  private JPanel sideBarContainer;

  /**
   * Constructs the frame for the dungeon.
   */
  public DungeonViewImpl() {
    super("Dungeon");

    setJFrameOptions();
    this.setPreferredSize(new Dimension(1200, 600));
    this.active = true;
  }

  private void renderMenu() {
    if (listener != null) {
      JMenuBar menuBar = new JMenuBar();
      JMenu menu = new JMenu("Options");

      List<Object> dungeonConfig = listener.getDungeonConfig();

      JMenuItem menuItem = new JMenuItem("New Game");
      menuItem.addActionListener(new OpenEditConfig(dungeonConfig, listener));
      menu.add(menuItem);

      menuItem = new JMenuItem("Restart Game");
      menuItem.addActionListener(e -> listener.restartGame());
      menu.add(menuItem);

      menuItem = new JMenuItem("Quit");
      menuItem.addActionListener(e -> System.exit(0));
      menu.add(menuItem);

      menuBar.add(menu);

      this.setJMenuBar(menuBar);
    }
  }

  private void toggleIsShootingArrow() {
    this.isShootingArrow = !this.isShootingArrow;
  }

  private int askForShootDistance() {
    return Integer.parseInt(JOptionPane.showInputDialog(null,
            "Enter distance: "));
  }

  private void shootInDirection(String direction) {
    try {
      List<Integer> result = listener.shoot(direction, askForShootDistance());

      if (result.get(2) == -1) {
        displayDialog("Error!", "You shot an arrow into darkness!");
      } else if (result.get(2) == 0) {
        displayDialog("Success!", "Otyugh was hit, hit again to kill it!");
        this.mazePanel.setOtyughHit(result.get(0), result.get(1));
      } else if (result.get(2) == 1) {
        displayDialog("Success!", "Otyugh was killed!");
        this.mazePanel.removeOtyugh(result.get(0), result.get(1));
      }
    } catch (IllegalArgumentException e) {
      displayDialog("Error", e.getMessage());
    }
    toggleIsShootingArrow();
  }

  /**
   * Moves the player to the given direction.
   *
   * @param direction the direction to move the player
   */
  @Override
  public void moveInDirection(String direction) {
    int nextI = currentI;
    int nextJ = currentJ;

    if (listener.move(direction)) {
      removePlayerFromNode();

      switch (direction) {
        case "U":
          nextI--;
          if (nextI < 0) {
            if (listener.isDungeonWrapping()) {
              nextI = listener.getDungeonHeight() - 1;
            } else {
              nextI = 0;
            }
          }
          break;
        case "D":
          nextI++;
          if (nextI >= listener.getDungeonHeight()) {
            if (listener.isDungeonWrapping()) {
              nextI = 0;
            } else {
              nextI = listener.getDungeonHeight() - 1;
            }
          }
          break;
        case "L":
          nextJ--;
          if (nextJ < 0) {
            if (listener.isDungeonWrapping()) {
              nextJ = listener.getDungeonWidth() - 1;
            } else {
              nextJ = 0;
            }
          }
          break;
        case "R":
          nextJ++;
          if (nextJ >= listener.getDungeonWidth()) {
            if (listener.isDungeonWrapping()) {
              nextJ = 0;
            } else {
              nextJ = listener.getDungeonWidth() - 1;
            }
          }
          break;
        default:
          break;
      }

      addPlayerToNode(nextI, nextJ);
    }
  }

  private void keyPresses() {
    this.mazePanel.requestFocus();
    this.mazePanel.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
        // Has to be overriden for KeyListener to work.
      }

      @Override
      public void keyPressed(KeyEvent e) {
        // Has to be overriden for KeyListener to work.
      }

      @Override
      public void keyReleased(KeyEvent e) {
        if (DungeonViewImpl.this.active) {
          switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
              if (!isShootingArrow) {
                moveInDirection("U");
              } else {
                shootInDirection("U");
              }
              break;
            case KeyEvent.VK_DOWN:
              if (!isShootingArrow) {
                moveInDirection("D");
              } else {
                shootInDirection("D");
              }
              break;
            case KeyEvent.VK_LEFT:
              if (!isShootingArrow) {
                moveInDirection("L");
              } else {
                shootInDirection("L");
              }
              break;
            case KeyEvent.VK_RIGHT:
              if (!isShootingArrow) {
                moveInDirection("R");
              } else {
                shootInDirection("R");
              }
              break;
            case KeyEvent.VK_P:
              if (listener.hasItemAtLocation(currentI, currentJ)) {
                String itemName = JOptionPane.showInputDialog(null, "What to pick up?");

                try {
                  listener.pickUpItem(itemName);
                } catch (IllegalArgumentException err) {
                  displayDialog("Error", err.getMessage());
                }
                if (itemName.equals("arrow")) {
                  removeArrowFromNode(currentI, currentJ);
                } else {
                  removeTreasureFromNode(currentI, currentJ, itemName);
                }
              } else {
                displayDialog("Error!", "No treasure at location!");
              }
              break;
            case KeyEvent.VK_S:
              toggleIsShootingArrow();
              break;
            default:
              break;
          }
        }
      }
    });
  }

  private void removeArrowFromNode(int currentI, int currentJ) {
    this.mazePanel.removeArrow(currentI, currentJ);
  }

  private void setJFrameOptions() {
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLayout(new BorderLayout());
  }

  /*
    Scrollbar fix (scrolls pretty slowly) taken from:
    https://stackoverflow.com/questions/10119587/how-to-increase-the-slow-scroll-speed-on-a-jscrollpane
   */
  private void fixScrolling(JScrollPane scrollpane) {
    JLabel systemLabel = new JLabel();
    FontMetrics metrics = systemLabel.getFontMetrics(systemLabel.getFont());
    int lineHeight = metrics.getHeight();
    int charWidth = metrics.getMaxAdvance();

    JScrollBar systemVBar = new JScrollBar(JScrollBar.VERTICAL);
    JScrollBar systemHBar = new JScrollBar(JScrollBar.HORIZONTAL);
    int verticalIncrement = systemVBar.getUnitIncrement();
    int horizontalIncrement = systemHBar.getUnitIncrement();

    scrollpane.getVerticalScrollBar().setUnitIncrement(2 * lineHeight * verticalIncrement);
    scrollpane.getHorizontalScrollBar().setUnitIncrement(2 * charWidth * horizontalIncrement);
  }

  private void addSideBar() {
    this.sideBarContainer = new JPanel();
    this.sideBarContainer.setLayout(new GridLayout(3, 1));

    ButtonPanel buttonPanel = new ButtonPanel(this);
    this.sideBarContainer.add(buttonPanel);

    PlayerInfoPanel playerInfoPanel = new PlayerInfoPanel(this.listener);
    this.sideBarContainer.add(playerInfoPanel);

    NodeInfoPanel nodeInfoPanel = new NodeInfoPanel(this.listener);
    this.sideBarContainer.add(nodeInfoPanel);

    this.add(this.sideBarContainer, BorderLayout.LINE_END);
  }

  /**
   * Adds the nodes to render the maze.
   *
   * @param nodeTypes the node types to render
   */
  @Override
  public void addNodeTypes(List<List<NodeType>> nodeTypes) {
    this.mazePanel = new MazePanel(nodeTypes, inCheatMode, this.listener);
    JScrollPane scrollPane = new JScrollPane(mazePanel);
    fixScrolling(scrollPane);
    scrollPane.setPreferredSize(new Dimension(1000, 600));
    this.add(scrollPane, BorderLayout.CENTER);
    this.mazePanel.setFocusable(true);

    this.mazePanel.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        // Has to be overriden for MouseListener to work.
      }

      @Override
      public void mousePressed(MouseEvent e) {
        // Has to be overriden for MouseListener to work.
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        DungeonViewImpl.this.mazePanel.requestFocus();
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        // Has to be overriden for MouseListener to work.
      }

      @Override
      public void mouseExited(MouseEvent e) {
        // Has to be overriden for MouseListener to work.
      }
    });

    addSideBar();

    keyPresses();
  }

  /**
   * Make the frame visible.
   */
  @Override
  public void makeVisible() {
    this.setVisible(true);
    this.pack();
  }

  /**
   * Make the frame non-resizable.
   */
  @Override
  public void makeNonResizable() {
    this.setResizable(false);
  }

  /**
   * Adds player to node with given coordinates.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   */
  @Override
  public void addPlayerToNode(int i, int j) {
    this.currentI = i;
    this.currentJ = j;
    this.mazePanel.addPlayer(i, j);
    this.mazePanel.uncoverNode(currentI, currentJ);
  }

  /**
   * Removes player from the current node.
   */
  @Override
  public void removePlayerFromNode() {
    this.mazePanel.removePlayer(currentI, currentJ);
  }

  /**
   * Adds a controller to listen to events.
   *
   * @param listener the controller to add
   */
  @Override
  public void addListener(DungeonControllerWView listener) {
    this.listener = listener;

    String playerName = JOptionPane.showInputDialog(null, "What is your name?");
    listener.setPlayerName(playerName);

    String cheatMode = (String) JOptionPane.showInputDialog(null,
            "Y/N?", "Enter cheat mode?", JOptionPane.QUESTION_MESSAGE, null,
            new String[]{"Y", "N"}, "N");
    this.inCheatMode = cheatMode.equals("Y");

    renderMenu();
  }

  /**
   * Moves the yugoloth asynchronously in the background.
   */
  @Override
  public void asyncMoveYugoloth() {
    this.otyughMoveTimer = new Timer(1000, e -> {
      DungeonViewImpl.this.listener.moveMonsters();
    });
    otyughMoveTimer.start();
  }

  /**
   * Adds an otyguh to the node with the given coordinates.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   */
  @Override
  public void addOtyughToNode(int i, int j) {
    this.mazePanel.addOtyugh(i, j);
  }

  /**
   * Removes the otyugh from the current node.
   */
  @Override
  public void removeOtyughFromNode(int i, int j) {
    this.mazePanel.removeOtyugh(i, j);
  }

  /**
   * Displays a dialog box with the given message and title.
   *
   * @param header the title of the dialog box
   * @param value the message to display
   */
  @Override
  public void displayDialog(String header, String value) {
    JOptionPane.showMessageDialog(this, value, header, JOptionPane.WARNING_MESSAGE);
  }

  /**
   * Adds treasure to a node with the given coordinates.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   * @param treasureName the name of the treasure
   */
  @Override
  public void addTreasureToNode(int i, int j, List<String> treasureName) {
    this.mazePanel.addTreasure(i, j, treasureName);
  }

  /**
   * Refreshes the frame.
   */
  @Override
  public void refresh() {
    this.getContentPane().removeAll();
    this.renderMenu();
    this.isShootingArrow = false;
  }

  /**
   * Adds arrows to the node with the given coordinates.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   * @param numArrows the number of arrows to add
   */
  @Override
  public void addArrowsToNode(int i, int j, int numArrows) {
    this.mazePanel.addArrows(i, j, numArrows);
  }

  /**
   * Adds smell to the node with the given coordinates.
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   * @param smellLevel the smell level to add
   */
  @Override
  public void addSmellToNode(int i, int j, String smellLevel) {
    this.mazePanel.addSmell(i, j, smellLevel);
  }

  /**
   * Removes smell from the node with the given coordinates.
   *
   * @param playerI the x coordinate of the node
   * @param playerJ the y coordinate of the node
   */
  @Override
  public void removeSmellFromNode(int playerI, int playerJ) {
    this.mazePanel.removeSmell(playerI, playerJ);
  }

  /**
   * Uncovers the node with given coordinates.
   *
   * @param startNodeI the x coordinate of the node
   * @param startNodeJ the y coordinate of the node
   */
  @Override
  public void uncoverNode(int startNodeI, int startNodeJ) {
    this.mazePanel.uncoverNode(startNodeI, startNodeJ);
  }

  /**
   * Stops the asynchronous movement of the yugoloth.
   */
  @Override
  public void stopYugolothMovement() {
    this.otyughMoveTimer.stop();
  }

  /**
   * Removes yugoloth from the node with the given coordinates.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   */
  @Override
  public void removeYugolothFromNode(int i, int j) {
    this.mazePanel.removeYugoloth(i, j);
  }

  /**
   * Adds yugoloth to the node with the given coordinates.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   */
  @Override
  public void addYugolothToNode(int i, int j) {
    this.mazePanel.addYugoloth(i, j);
  }

  /**
   * Disables actions on the frame.
   */
  @Override
  public void disableActions() {
    this.active = false;
    this.sideBarContainer.removeAll();
    this.mazePanel.disableClicks();
  }

  /**
   * Adds thief to the node with the given coordinates.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   */
  @Override
  public void addThiefToNode(int i, int j) {
    this.mazePanel.addThief(i, j);
  }

  /**
   * Adds pit to the node with the given coordinates.
   *
   * @param pitI the x coordinate of the node
   * @param pitJ the y coordinate of the node
   */
  @Override
  public void addPitToNode(int pitI, int pitJ) {
    this.mazePanel.addPit(pitI, pitJ);
  }

  /**
   * Marks the node as end node.
   *
   * @param i the x coordinate of the node
   * @param j the y coordinate of the node
   */
  @Override
  public void markEndNode(int i, int j) {
    this.mazePanel.markEndNode(i, j);
  }

  private void removeTreasureFromNode(int i, int j, String treasureName) {
    this.mazePanel.removeTreasure(i, j, treasureName);
  }
}
