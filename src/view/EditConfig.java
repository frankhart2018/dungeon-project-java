package view;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Box;

class EditConfig extends JPanel {
  private final JTextField numRows;
  private final JTextField numCols;
  private final JTextField interconnectivity;
  private final JTextField wrapping;
  private final JTextField percentCavesWithTreasures;
  private final JTextField forceInterconnectivity;
  private final JTextField numOtyughs;

  public EditConfig(List<Object> dungeonConfig) {
    this.setLayout(new GridLayout(7, 2));

    JLabel label = new JLabel("Enter number of rows: ");
    this.numRows = new JTextField(10);
    this.numRows.setText(String.valueOf(dungeonConfig.get(0)));
    this.add(label);
    this.add(this.numRows);
    this.add(Box.createVerticalStrut(15));

    label = new JLabel("Enter number of columns: ");
    this.numCols = new JTextField(10);
    this.numCols.setText(String.valueOf(dungeonConfig.get(1)));
    this.add(label);
    this.add(this.numCols);
    this.add(Box.createVerticalStrut(15));

    label = new JLabel("Enter interconnectivity:  ");
    this.interconnectivity = new JTextField(10);
    this.interconnectivity.setText(String.valueOf(dungeonConfig.get(2)));
    this.add(label);
    this.add(this.interconnectivity);
    this.add(Box.createVerticalStrut(15));

    label = new JLabel("Wrapping/Non-Wrapping (W/N): ");
    this.wrapping = new JTextField(10);
    if ((boolean) dungeonConfig.get(3)) {
      this.wrapping.setText("W");
    } else {
      this.wrapping.setText("N");
    }
    this.add(label);
    this.add(this.wrapping);
    this.add(Box.createVerticalStrut(15));

    label = new JLabel("Enter percent of caves with treasures (in 100): ");
    this.percentCavesWithTreasures = new JTextField(10);
    this.percentCavesWithTreasures.setText(String.valueOf(dungeonConfig.get(4)));
    this.add(label);
    this.add(this.percentCavesWithTreasures);
    this.add(Box.createVerticalStrut(15));

    label = new JLabel("Force interconnectivity? (Y/N): ");
    this.forceInterconnectivity = new JTextField(10);
    if ((boolean) dungeonConfig.get(5)) {
      this.forceInterconnectivity.setText("Y");
    } else {
      this.forceInterconnectivity.setText("N");
    }
    this.add(label);
    this.add(this.forceInterconnectivity);
    this.add(Box.createVerticalStrut(15));

    label = new JLabel("Number of otyughs: ");
    this.numOtyughs = new JTextField(10);
    this.numOtyughs.setText(String.valueOf(dungeonConfig.get(6)));
    this.add(label);
    this.add(this.numOtyughs);
    this.add(Box.createVerticalStrut(15));
  }

  public int getNumRows() {
    return Integer.parseInt(numRows.getText());
  }

  public int getNumCols() {
    return Integer.parseInt(numCols.getText());
  }

  public int getInterconnectivity() {
    return Integer.parseInt(interconnectivity.getText());
  }

  public boolean getWrapping() {
    String wrappingText = wrapping.getText();
    return wrappingText.equals("W");
  }

  public float getPercentCavesWithTreasures() {
    return Float.parseFloat(percentCavesWithTreasures.getText());
  }

  public boolean getForceInterconnectivity() {
    String forceInterconnectivityText = forceInterconnectivity.getText();
    return forceInterconnectivityText.equals("Y");
  }

  public int getNumOtyughs() {
    return Integer.parseInt(numOtyughs.getText());
  }
}
