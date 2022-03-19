package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;

import controller.DungeonControllerWView;

class OpenEditConfig implements ActionListener {
  private final List<Object> dungeonConfig;
  private final DungeonControllerWView listener;

  public OpenEditConfig(List<Object> dungeonConfig, DungeonControllerWView listener) {
    this.dungeonConfig = dungeonConfig;
    this.listener = listener;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    EditConfig editConfig = new EditConfig(this.dungeonConfig);
    int result = JOptionPane.showConfirmDialog(null, editConfig,
            "New Game Configuration", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      this.listener.getNewDungeon(editConfig.getNumRows(), editConfig.getNumCols(),
              editConfig.getInterconnectivity(), editConfig.getWrapping(),
              editConfig.getPercentCavesWithTreasures(), editConfig.getForceInterconnectivity(),
              editConfig.getNumOtyughs());
    }
  }
}
