package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.BorderFactory;

import controller.DungeonControllerWView;

class PlayerInfoPanel extends JPanel {
  private final DungeonControllerWView listener;

  public PlayerInfoPanel(DungeonControllerWView listener) {
    this.listener = listener;
  }

  /*
    Adding border to JPanel taken from:-
    https://stackoverflow.com/questions/14869426/graphical-artifact-with-borderfactory-in-paintcomponent
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g;

    this.setBorder(BorderFactory.createLineBorder(Color.BLACK));

    g2d.setFont(new Font("Arial", Font.BOLD, 16));
    g2d.drawString("Player information: ", 10, 20);

    g2d.setFont(new Font("Arial", Font.PLAIN, 14));

    String playerName = "Name: " + listener.getPlayerName();
    g2d.drawString(playerName,
            (this.getWidth() - g2d.getFontMetrics(g2d.getFont()).stringWidth(playerName)) / 2,
            50);

    String numArrows = "Arrows left: " + listener.getArrowsLeft();
    g2d.drawString(numArrows,
            (this.getWidth() - g2d.getFontMetrics(g2d.getFont()).stringWidth(numArrows)) / 2,
            70);

    String treasuresCollected = "Treasures collected: ";
    g2d.drawString(treasuresCollected,
            (this.getWidth()
                    - g2d.getFontMetrics(g2d.getFont()).stringWidth(treasuresCollected)) / 2,
            90);

    List<String> treasures = listener.getTreasuresCollected();
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
  }
}
