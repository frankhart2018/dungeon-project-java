package view;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import controller.DungeonControllerWView;

class MazePanel extends JPanel {
  private final List<List<NodePanel>> nodes;

  public MazePanel(List<List<NodeType>> nodeTypes, boolean inCheatMode,
                   DungeonControllerWView listener) {

    this.setLayout(new GridLayout(nodeTypes.size(), nodeTypes.get(0).size()));

    boolean isCovered = !inCheatMode;
    this.nodes = new ArrayList<>();
    for (int i = 0; i < nodeTypes.size(); i++) {
      List<NodePanel> row = new ArrayList<>();
      for (int j = 0; j < nodeTypes.get(i).size(); j++) {
        NodePanel node = new NodePanel(nodeTypes.get(i).get(j), isCovered, i, j, listener);
        row.add(node);
      }
      nodes.add(row);
    }

    for (List<NodePanel> row : nodes) {
      JPanel rowContainer = new JPanel();
      for (NodePanel node : row) {
        rowContainer.add(node);
      }
      this.add(rowContainer);
    }
  }

  public void addPlayer(int i, int j) {
    nodes.get(i).get(j).setPlayer();
  }

  public void removePlayer(int i, int j) {
    nodes.get(i).get(j).removePlayer();
  }

  public void addOtyugh(int i, int j) {
    nodes.get(i).get(j).setOtyugh();
  }

  public void removeOtyugh(int i, int j) {
    nodes.get(i).get(j).removeOtyugh();
  }

  public void addTreasure(int i, int j, List<String> treasureNames) {
    nodes.get(i).get(j).setTreasure(treasureNames);
  }

  public void removeTreasure(int i, int j, String treasureName) {
    nodes.get(i).get(j).removeTreasure(treasureName);
  }

  public void setOtyughHit(int i, int j) {
    nodes.get(i).get(j).setOtyughHit();
  }

  public void addArrows(int i, int j, int numArrows) {
    nodes.get(i).get(j).setArrows(numArrows);
  }

  public void removeArrow(int currentI, int currentJ) {
    nodes.get(currentI).get(currentJ).removeArrow();
  }

  public void addSmell(int i, int j, String smellLevel) {
    nodes.get(i).get(j).setSmell(smellLevel);
  }

  public void removeSmell(int playerI, int playerJ) {
    nodes.get(playerI).get(playerJ).removeSmell();
  }

  public void uncoverNode(int startNodeI, int startNodeJ) {
    nodes.get(startNodeI).get(startNodeJ).uncoverNode();
  }

  public void removeYugoloth(int i, int j) {
    nodes.get(i).get(j).removeYugoloth();
  }

  public void addYugoloth(int i, int j) {
    nodes.get(i).get(j).setYugoloth();
  }

  public void disableClicks() {
    for (List<NodePanel> row : nodes) {
      for (NodePanel node : row) {
        node.disableClicks();
      }
    }
  }

  public void addThief(int i, int j) {
    nodes.get(i).get(j).setThief();
  }

  public void addPit(int pitI, int pitJ) {
    nodes.get(pitI).get(pitJ).setPit();
  }

  public void markEndNode(int i, int j) {
    nodes.get(i).get(j).markEndNode();
  }
}
