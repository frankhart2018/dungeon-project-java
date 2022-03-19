package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import controller.DungeonControllerWView;

class NodePanel extends JPanel {
  private final NodeType type;
  private boolean hasPlayer;
  private boolean hasOtyugh;
  private int numArrows;
  private String smellLevel;
  private List<String> treasures;
  private boolean isCovered;
  private final int r;
  private final int c;
  private final DungeonControllerWView listener;
  private boolean hasYugoloth;
  private MouseAdapter mouseListener;
  private boolean hasThief;
  private boolean hasPit;
  private boolean isEndNode;

  public NodePanel(NodeType type, boolean isCovered, int r, int c,
                   DungeonControllerWView listener) {
    this.setPreferredSize(new Dimension(150, 150));

    this.type = type;
    this.hasPlayer = false;
    this.hasOtyugh = false;
    this.numArrows = 0;
    this.smellLevel = "";
    this.treasures = null;
    this.isCovered = isCovered;
    this.r = r;
    this.c = c;
    this.listener = listener;
    this.hasYugoloth = false;
    this.hasThief = false;
    this.hasPit = false;

    addClickListener();
  }

  private void addClickListener() {
    this.mouseListener = new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
        NodePanel.this.listener.clickAndMove(r, c);
      }
    };
    this.addMouseListener(mouseListener);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Image nodePicture = getNodePictureByType(type);
    g.drawImage(nodePicture, 0, 0, 150, 150, this);

    if (this.hasPlayer) {
      Graphics2D g2d = (Graphics2D) g;
      Image playerImage = readImageWithException("/player.png");
      int imageWidth = 100;
      int imageHeight = 100;
      g2d.drawImage(playerImage, (this.getWidth() - imageWidth) / 2,
              (this.getHeight() - imageHeight) / 2, imageWidth, imageHeight, this);
    }

    if (this.hasOtyugh) {
      Graphics2D g2d = (Graphics2D) g;

      Image otyughImage;
      if (listener.isOtyughHit(r, c)) {
        otyughImage = readImageWithException("/otyugh-with-arrow.png");
      } else {
        otyughImage = readImageWithException("/otyugh.png");
      }
      int imageWidth = 50;
      int imageHeight = 50;
      g2d.drawImage(otyughImage, (this.getWidth() - imageWidth) / 2,
              (this.getHeight() - imageHeight) / 2, imageWidth, imageHeight, this);
    }

    if (this.treasures != null) {
      Graphics2D g2d = (Graphics2D) g;
      for (int i = 0; i < this.treasures.size(); i++) {
        Image treasureImage = readImageWithException("/"
                + this.treasures.get(i).toLowerCase() + ".png");
        int imageWidth = 25;
        int imageHeight = 25;
        g2d.drawImage(treasureImage, 0, (i * 25), imageWidth, imageHeight, this);
      }
    }

    if (this.numArrows > 0) {
      Graphics2D g2d = (Graphics2D) g;
      Image arrowImage = readImageWithException("/arrow-white.png");
      for (int i = 0; i < this.numArrows; i++) {
        int imageWidth = 30;
        int imageHeight = 15;
        g2d.drawImage(arrowImage, this.getWidth() - imageWidth, (i * 25), imageWidth,
                imageHeight, this);
      }
    }

    if (!this.smellLevel.equals("")) {
      Graphics2D g2d = (Graphics2D) g;
      Image smellImage = readImageWithException("/" + this.smellLevel.toLowerCase() + ".png");
      int imageWidth = this.getWidth();
      int imageHeight = this.getHeight();
      g2d.drawImage(smellImage, 0, 0, imageWidth, imageHeight, this);
    }

    if (hasYugoloth) {
      Graphics2D g2d = (Graphics2D) g;
      Image yugolothImage = readImageWithException("/yugoloth.png");
      int imageWidth = 100;
      int imageHeight = 100;
      g2d.drawImage(yugolothImage, (this.getWidth() - imageWidth) / 2,
              (this.getHeight() - imageHeight) / 2, imageWidth, imageHeight, this);
    }

    if (hasThief) {
      Graphics2D g2d = (Graphics2D) g;
      Image thiefImage = readImageWithException("/thief.png");
      int imageWidth = 100;
      int imageHeight = 100;
      g2d.drawImage(thiefImage, (this.getWidth() - imageWidth) / 2,
              (this.getHeight() - imageHeight) / 2, imageWidth, imageHeight, this);
    }

    if (hasPit) {
      Graphics2D g2d = (Graphics2D) g;
      Image pitImage = readImageWithException("/pit.png");
      int imageWidth = 100;
      int imageHeight = 100;
      g2d.drawImage(pitImage, (this.getWidth() - imageWidth) / 2,
              (this.getHeight() - imageHeight) / 2, imageWidth, imageHeight, this);
    }

    if (isEndNode) {
      Graphics2D g2d = (Graphics2D) g;
      Image endNodeImage = readImageWithException("/flag.png");
      int imageWidth = 50;
      int imageHeight = 50;
      g2d.drawImage(endNodeImage, 0, 0, imageWidth, imageHeight, this);
    }

    if (isCovered) {
      nodePicture = getNodePictureByType(NodeType.GENERIC);
      g.drawImage(nodePicture, 0, 0, 150, 150, this);
    }
  }

  private Image readImageWithException(String path) {
    try {
      return ImageIO.read(Objects.requireNonNull(NodePanel.class.getResource(path)));
    } catch (IOException e) {
      throw new IllegalArgumentException("Cannot find image with path: " + path);
    }
  }

  private Image getNodePictureByType(NodeType type) {
    switch (type) {
      case GENERIC:
        return readImageWithException("/blank.png");
      case CAVE_E:
        return readImageWithException("/E.png");
      case CAVE_N:
        return readImageWithException("/N.png");
      case CAVE_S:
        return readImageWithException("/S.png");
      case CAVE_W:
        return readImageWithException("/W.png");
      case CAVE_NSE:
        return readImageWithException("/NES.png");
      case CAVE_NSW:
        return readImageWithException("/SWN.png");
      case CAVE_SEW:
        return readImageWithException("/ESW.png");
      case CAVE_NEW:
        return readImageWithException("/NEW.png");
      case CAVE_NSEW:
        return readImageWithException("/NESW.png");
      case TUNNEL_NS:
        return readImageWithException("/NS.png");
      case TUNNEL_EW:
        return readImageWithException("/EW.png");
      case TUNNEL_NW:
        return readImageWithException("/WN.png");
      case TUNNEL_NE:
        return readImageWithException("/NE.png");
      case TUNNEL_SW:
        return readImageWithException("/SW.png");
      case TUNNEL_SE:
        return readImageWithException("/ES.png");
      default:
        break;
    }

    throw new IllegalArgumentException("Unknown node type: " + type);
  }

  public void setPlayer() {
    this.hasPlayer = true;
    this.repaint();
  }

  public void removePlayer() {
    this.hasPlayer = false;
    this.repaint();
  }

  public void setOtyugh() {
    this.hasOtyugh = true;
    this.repaint();
  }

  public void removeOtyugh() {
    this.hasOtyugh = false;
    this.repaint();
  }

  public void setTreasure(List<String> treasureNames) {
    this.treasures = new ArrayList<>(treasureNames);
    this.repaint();
  }

  public void removeTreasure(String treasureName) {
    this.treasures.remove(treasureName.toUpperCase());
    this.repaint();
  }

  public void setOtyughHit() {
    this.repaint();
  }

  public void setArrows(int numArrows) {
    this.numArrows = numArrows;
    this.repaint();
  }

  public void removeArrow() {
    this.numArrows--;
    this.repaint();
  }

  public void setSmell(String smellLevel) {
    this.smellLevel = smellLevel;
    this.repaint();
  }

  public void removeSmell() {
    this.smellLevel = "";
    this.repaint();
  }

  public void uncoverNode() {
    this.isCovered = false;
    this.repaint();
  }

  public void setYugoloth() {
    this.hasYugoloth = true;
    this.repaint();
  }

  public void removeYugoloth() {
    this.hasYugoloth = false;
    this.repaint();
  }

  public void disableClicks() {
    this.removeMouseListener(this.mouseListener);
  }

  public void setThief() {
    this.hasThief = true;
    this.repaint();
  }

  public void setPit() {
    this.hasPit = true;
    this.repaint();
  }

  public void markEndNode() {
    this.isEndNode = true;
    this.repaint();
  }
}
