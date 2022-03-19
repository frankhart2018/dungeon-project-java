package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.BorderFactory;

import controller.DungeonControllerWView;

class NodeInfoPanel extends JPanel {
  private final DungeonControllerWView listener;

  public NodeInfoPanel(DungeonControllerWView listener) {
    this.listener = listener;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g;

    this.setBorder(BorderFactory.createLineBorder(Color.BLACK));

    g2d.setFont(new Font("Arial", Font.BOLD, 16));
    g2d.drawString("Current position: ", 10, 20);

    g2d.setFont(new Font("Arial", Font.PLAIN, 14));

    String currentPositionType = "Type: " + this.listener.getCurrentPositionType();
    g2d.drawString(currentPositionType,
            (this.getWidth()
                    - g2d.getFontMetrics(g2d.getFont()).stringWidth(currentPositionType)) / 2,
            50);

    String numArrows = "Arrows: " + this.listener.getNumArrows();
    g2d.drawString(numArrows,
            (this.getWidth() - g2d.getFontMetrics(g2d.getFont()).stringWidth(numArrows)) / 2,
            70);

    String treasuresCollected = "Treasures: ";
    g2d.drawString(treasuresCollected,
            (this.getWidth()
                    - g2d.getFontMetrics(g2d.getFont()).stringWidth(treasuresCollected)) / 2,
            90);

    List<String> treasures = listener.getTreasuresInNode();
    int y = 110;
    for (String treasure : treasures) {
      g2d.drawString(treasure,
              (this.getWidth() - g2d.getFontMetrics(g2d.getFont()).stringWidth(treasure)) / 2,
              y);
      y += 20;
    }

    if (treasures.isEmpty()) {
      g2d.drawString("None",
              (this.getWidth() - g2d.getFontMetrics(g2d.getFont()).stringWidth("None")) / 2,
              y);
    }

    String hasSmell = "Has smell? " + this.listener.hasSmell();
    g2d.drawString(hasSmell,
            (this.getWidth() - g2d.getFontMetrics(g2d.getFont()).stringWidth(hasSmell)) / 2,
            (y == 110) ? y + 20 : y);
  }
}
