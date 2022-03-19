package dungeongame;

import node.Node;

/**
 * Represents a Yugoloth which is a monster that moves around the
 * dungeon and whe a player comes in contact with it, then the player
 * has to fight it with bare hands, there is a 50% chance that the player
 * will win the fight.
 */
public class Yugoloth {
  private MoveStrategy moveStrategy;

  /**
   * Constructor for the Yugoloth class.
   */
  public Yugoloth() {
    this.moveStrategy = null;
  }

  /**
   * Constructs a new Yugoloth from existing yugoloth.
   *
   * @param yugoloth the yugoloth to copy.
   */
  public Yugoloth(Yugoloth yugoloth) {
    this.moveStrategy = yugoloth.moveStrategy;
  }

  /**
   * Sets the move strategy of this Yugoloth.
   *
   * @param moveStrategy the move strategy to set.
   */
  public void hookMoveStrategy(MoveStrategy moveStrategy) {
    this.moveStrategy = moveStrategy;
  }

  /**
   * Makes a move in the dungeon.
   *
   * @param location the current location of the Yugoloth.
   * @return the new location of the Yugoloth.
   */
  public Node move(Node location) {
    return moveStrategy.move(this, location);
  }
}
