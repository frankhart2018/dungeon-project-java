package dungeongame;

import java.util.ArrayList;
import java.util.List;

import node.Node;

class RandomMove implements MoveStrategy {
  @Override
  public Node move(Yugoloth yugoloth, Node location) {
    List<String> availableDirections = new ArrayList<>();

    if (location.getTopNode() != null) {
      availableDirections.add("U");
    }
    if (location.getBottomNode() != null) {
      availableDirections.add("D");
    }
    if (location.getLeftNode() != null) {
      availableDirections.add("L");
    }
    if (location.getRightNode() != null) {
      availableDirections.add("R");
    }

    if (availableDirections.size() > 0) {
      int randomIndex = (int) (Math.random() * availableDirections.size());
      String randomDirection = availableDirections.get(randomIndex);
      location.removeYugoloth();

      switch (randomDirection) {
        case "U":
          location = location.getTopNode();
          break;
        case "D":
          location = location.getBottomNode();
          break;
        case "L":
          location = location.getLeftNode();
          break;
        case "R":
          location = location.getRightNode();
          break;
        default:
          break;
      }

      location.placeYugoloth(yugoloth);
    }

    return location;
  }
}
