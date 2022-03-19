package controllertest;

import java.io.IOException;
import java.util.List;

import controller.DungeonControllerWView;
import view.DungeonView;
import view.NodeType;

class MockDungeonView implements DungeonView {
  private Appendable out;

  public MockDungeonView(Appendable out) {
    this.out = out;
  }

  private void appendWithException(String... s) {
    for (String str : s) {
      try {
        out.append(str);
      } catch (IOException ignored) {

      }
    }
  }

  @Override
  public void moveInDirection(String direction) {
    appendWithException("Moved successfully to ", direction, "\n");
  }

  @Override
  public void addNodeTypes(List<List<NodeType>> nodeTypes) {
    appendWithException("Added node types and initialized the map\n");
  }

  @Override
  public void makeVisible() {
    appendWithException("View made visible\n");
  }

  @Override
  public void makeNonResizable() {
    appendWithException("View made non-resizable\n");
  }

  @Override
  public void addPlayerToNode(int i, int j) {
    appendWithException("Added player to location: (", String.valueOf(i), ", ",
            String.valueOf(j), ")\n");
  }

  @Override
  public void removePlayerFromNode() {
    appendWithException("Removed player from node\n");
  }

  @Override
  public void addListener(DungeonControllerWView listener) {
    appendWithException("Listener added to current view\n");
  }

  @Override
  public void asyncMoveYugoloth() {
    appendWithException("Yugoloth movement started asynchronously\n");
  }

  @Override
  public void addOtyughToNode(int i, int j) {
    appendWithException("Added otyugh to location: (", String.valueOf(i), ", ",
            String.valueOf(j), ")\n");
  }

  @Override
  public void removeOtyughFromNode(int i, int j) {
    appendWithException("Removed otyugh from location: (", String.valueOf(i), ", ",
            String.valueOf(j), ")\n");
  }

  @Override
  public void displayDialog(String header, String value) {
    appendWithException("Header: ", header, "\nValue: ", value, "\n");
  }

  @Override
  public void addTreasureToNode(int i, int j, List<String> treasureName) {
    appendWithException("Added treasure to location: (", String.valueOf(i), ", ",
            String.valueOf(j), ")\n");
    appendWithException("Treasures: ", treasureName.toString(), "\n");
  }

  @Override
  public void refresh() {
    appendWithException("Refreshed the view\n");
  }

  @Override
  public void addArrowsToNode(int i, int j, int numArrows) {
    appendWithException("Added arrows to location: (", String.valueOf(i), ", ",
            String.valueOf(j), ")\n");
    appendWithException("Number of arrows: ", String.valueOf(numArrows), "\n");
  }

  @Override
  public void addSmellToNode(int i, int j, String smellLevel) {
    appendWithException("Added smell to location: (", String.valueOf(i), ", ",
            String.valueOf(j), ")\n");
    appendWithException("Smell level: ", smellLevel, "\n");
  }

  @Override
  public void removeSmellFromNode(int playerI, int playerJ) {
    appendWithException("Removed smell from location: (", String.valueOf(playerI),
            ", ", String.valueOf(playerJ), ")\n");
  }

  @Override
  public void uncoverNode(int startNodeI, int startNodeJ) {
    appendWithException("Uncovered node: (", String.valueOf(startNodeI), ", ",
            String.valueOf(startNodeJ), ")\n");
  }

  @Override
  public void stopYugolothMovement() {
    appendWithException("Stopped yugoloth movement\n");
  }

  @Override
  public void removeYugolothFromNode(int i, int j) {
    appendWithException("Removed yugoloth from location: (", String.valueOf(i),
            ", ", String.valueOf(j), ")\n");
  }

  @Override
  public void addYugolothToNode(int i, int j) {
    appendWithException("Added yugoloth to location: (", String.valueOf(i),
            ", ", String.valueOf(j), ")\n");
  }

  @Override
  public void disableActions() {
    appendWithException("Disabled all user interactions\n");
  }

  @Override
  public void addThiefToNode(int i, int j) {
    appendWithException("Added thief to location: (", String.valueOf(i),
            ", ", String.valueOf(j), ")\n");
  }

  @Override
  public void addPitToNode(int pitI, int pitJ) {
    appendWithException("Added pit to location: (", String.valueOf(pitI),
            ", ", String.valueOf(pitJ), ")\n");
  }

  @Override
  public void markEndNode(int i, int j) {
    appendWithException("Marked end node: (", String.valueOf(i),
            ", ", String.valueOf(j), ")\n");
  }
}
