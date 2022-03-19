package view;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;

class ButtonPanel extends JPanel {
  private final DungeonView view;

  public ButtonPanel(DungeonView view) {
    this.view = view;

    JButton upButton = new JButton();
    try {
      Image upImage = ImageIO.read(
              Objects.requireNonNull(ButtonPanel.class.getResource("/up.png")));
      upButton.setIcon(new ImageIcon(upImage));
    } catch (IOException ignored) {

    }
    upButton.addActionListener(new UpButtonListener());
    this.add(upButton);

    JButton downButton = new JButton();
    try {
      Image downImage = ImageIO.read(
              Objects.requireNonNull(ButtonPanel.class.getResource("/down.png")));
      downButton.setIcon(new ImageIcon(downImage));
    } catch (IOException ignored) {

    }
    downButton.addActionListener(new DownButtonListener());
    this.add(downButton);

    JButton leftButton = new JButton();
    try {
      Image leftImage = ImageIO.read(
              Objects.requireNonNull(ButtonPanel.class.getResource("/left.png")));
      leftButton.setIcon(new ImageIcon(leftImage));
    } catch (IOException ignored) {

    }
    leftButton.addActionListener(new LeftButtonListener());
    this.add(leftButton);

    JButton rightButton = new JButton();
    try {
      Image rightImage = ImageIO.read(
              Objects.requireNonNull(ButtonPanel.class.getResource("/right.png")));
      rightButton.setIcon(new ImageIcon(rightImage));
    } catch (IOException ignored) {

    }
    rightButton.addActionListener(new RightButtonListener());
    this.add(rightButton);
  }

  class UpButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      ButtonPanel.this.view.moveInDirection("U");
    }
  }

  class DownButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      ButtonPanel.this.view.moveInDirection("D");
    }
  }

  class LeftButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      ButtonPanel.this.view.moveInDirection("L");
    }
  }

  class RightButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      ButtonPanel.this.view.moveInDirection("R");
    }
  }
}
