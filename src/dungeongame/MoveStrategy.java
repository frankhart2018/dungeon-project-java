package dungeongame;

import node.Node;

interface MoveStrategy {
  Node move(Yugoloth yugoloth, Node location);
}
