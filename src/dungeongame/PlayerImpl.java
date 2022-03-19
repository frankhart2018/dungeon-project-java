package dungeongame;

import utils.ValueSanity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is an implementation of the Player, a player
 * moves in four directions (left, right, up, down) inside the dungeon
 * and can pick up treasures in the caves till it finds the end node
 * at which point the game terminates.
 */
public class PlayerImpl implements Player {
  private String name;
  private Map<String, Integer> treasure;
  private int numArrows;
  private boolean isPlayerDead;

  /**
   * Constructs a new player.
   *
   * @param name The name of the player.
   * @throws IllegalArgumentException If the name is null or empty
   */
  public PlayerImpl(String name) {
    performChecks(name);

    this.name = name;

    this.treasure = new HashMap<>();
    this.numArrows = 3;
    this.isPlayerDead = false;
  }

  private void performChecks(String name) {
    ValueSanity.checkNull("Name of player", name);

    if (name.equals("")) {
      throw new IllegalArgumentException("Name of player cannot be empty!");
    }
  }

  /**
   * Places the player at the starting node of the dungeon.
   *
   * @param dungeon the dungeon in which the player is placed
   * @throws IllegalArgumentException if the dungeon object is null, or
   *                                  if the player is already placed in dungeon, or
   *                                  if another player is already in the dungeon
   */
  @Override
  public void enterPlayerToDungeon(Dungeon dungeon) {
    ValueSanity.checkNull("Dungeon instance", dungeon);

    isPlayerDead = ((DungeonImpl) dungeon).placePlayerInDungeon(this);
  }

  /**
   * Moves the player to the left from its current position in the dungeon.
   *
   * @param dungeon the dungeon in which the player is placed
   * @throws IllegalArgumentException if the dungeon object is null, or
   *                                  there is no path on the left in current position,
   *                                  if the player is not placed in the dungeon, or
   *                                  if another player is already in the dungeon, or
   *                                  if the player is dead
   */
  @Override
  public void moveLeft(Dungeon dungeon) {
    ValueSanity.checkNull("Dungeon instance", dungeon);

    if (this.isPlayerDead) {
      throw new IllegalArgumentException("Player is dead!");
    }

    this.isPlayerDead = ((DungeonImpl) dungeon).movePlayer("L", this);
  }

  /**
   * Moves the player to the right from its current position in the dungeon.
   *
   * @param dungeon the dungeon in which the player is placed
   * @throws IllegalArgumentException if the dungeon object is null, or
   *                                  there is no path on the right in current position,
   *                                  if the player is not placed in the dungeon, or
   *                                  if another player is already in the dungeon, or
   *                                  if the player is dead
   */
  @Override
  public void moveRight(Dungeon dungeon) {
    ValueSanity.checkNull("Dungeon instance", dungeon);

    if (this.isPlayerDead) {
      throw new IllegalArgumentException("Player is dead!");
    }

    this.isPlayerDead = ((DungeonImpl) dungeon).movePlayer("R", this);
  }

  /**
   * Moves the player to the up from its current position in the dungeon.
   *
   * @param dungeon the dungeon in which the player is placed
   * @throws IllegalArgumentException if the dungeon object is null, or
   *                                  there is no path towards the top in current position,
   *                                  if the player is not placed in the dungeon, or
   *                                  if another player is already in the dungeon, or
   *                                  if the player is dead
   */
  @Override
  public void moveUp(Dungeon dungeon) {
    ValueSanity.checkNull("Dungeon instance", dungeon);

    if (this.isPlayerDead) {
      throw new IllegalArgumentException("Player is dead!");
    }

    this.isPlayerDead = ((DungeonImpl) dungeon).movePlayer("U", this);
  }

  /**
   * Moves the player to the down from its current position in the dungeon.
   *
   * @param dungeon the dungeon in which the player is placed
   * @throws IllegalArgumentException if the dungeon object is null, or
   *                                  there is no path towards the bottom in current
   *                                  position,
   *                                  if the player is not placed in the dungeon, or
   *                                  if another player is already in the dungeon, or
   *                                  if the player is dead
   */
  @Override
  public void moveDown(Dungeon dungeon) {
    ValueSanity.checkNull("Dungeon instance", dungeon);

    if (this.isPlayerDead) {
      throw new IllegalArgumentException("Player is dead!");
    }

    this.isPlayerDead = ((DungeonImpl) dungeon).movePlayer("D", this);
  }

  /**
   * Picks up treasure from the current location of the player if any.
   *
   * @param dungeon      the dungeon in which the player is placed
   * @param treasureName the name of the treasure to be picked up
   * @throws IllegalArgumentException if the dungeon object is null, or
   *                                  if the treasure name is null, or
   *                                  if the current position is a tunnel, or
   *                                  if the name of treasure is illegal, or
   *                                  if the cave has no treasure, or
   *                                  if the cave does not have that treasure, or
   *                                  if the player is not placed in the dungeon, or
   *                                  if another player is already in the dungeon, or
   *                                  if the player is dead
   */
  @Override
  public void pickUpTreasure(Dungeon dungeon, String treasureName) {
    ValueSanity.checkNull("Dungeon instance", dungeon);
    ValueSanity.checkNull("Name of treasure", treasureName);

    if (this.isPlayerDead) {
      throw new IllegalArgumentException("Player is dead!");
    }

    treasureName = treasureName.toUpperCase();
    ((DungeonImpl) dungeon).pickUpTreasure(treasureName, this);
    if (this.treasure.containsKey(treasureName)) {
      this.treasure.put(treasureName, this.treasure.get(treasureName) + 1);
    } else {
      this.treasure.put(treasureName, 1);
    }
  }

  /**
   * Returns the string representing bag of treasures of the player.
   *
   * @return the string representing bag of treasures of the player
   */
  @Override
  public String getPlayerTreasures() {
    StringBuilder sb = new StringBuilder();
    sb.append("---------------------------------------------------\n");
    sb.append(this.name).append(" now has the following treasure count: \n");

    if (this.treasure.isEmpty()) {
      sb.append("No treasure in bag.\n");
    } else {
      for (String treasureName : this.treasure.keySet()) {
        sb.append(treasureName).append(": ").append(this.treasure.get(treasureName)).append("\n");
      }
    }

    return sb.toString();
  }

  /**
   * Picks up an arrow from the current location if any.
   *
   * @param dungeon the dungeon in which the player is placed
   * @throws IllegalArgumentException if the dungeon object is null, or
   *                                  if there are no arrows in the current position, or
   *                                  if the player is not placed in the dungeon, or
   *                                  if another player is already in the dungeon, or
   *                                  if the player is dead
   */
  @Override
  public void pickUpArrow(Dungeon dungeon) {
    ValueSanity.checkNull("Dungeon instance", dungeon);

    if (this.isPlayerDead) {
      throw new IllegalArgumentException("Player is dead!");
    }

    ((DungeonImpl) dungeon).pickUpArrow(this);

    this.numArrows++;
  }

  /**
   * Returns the representation of number or arrows the player has.
   *
   * @return the representation of number or arrows the player has
   */
  @Override
  public String getPlayerArrows() {
    StringBuilder sb = new StringBuilder();
    sb.append("---------------------------------------------------\n");
    sb.append(this.name).append(" now has the following arrow count: \n");

    if (this.numArrows > 0) {
      sb.append("Arrows: ").append(this.numArrows).append("\n");
    } else {
      sb.append("No arrows left.\n");
    }

    return sb.toString();
  }

  /**
   * Shoots an arrow in given direction and at given distance.
   *
   * @param dungeon   the dungeon in which the player is placed
   * @param direction the direction of the arrow
   * @param distance  the distance of the arrow
   * @return list containing the current arrow position followed by an integer,
   *         1 if the Otyugh dies, 0 if the Otyugh is injured, -1 if the arrow does not
   *         hit the Otyugh
   * @throws IllegalArgumentException if the dungeon object is null, or
   *                                  if the direction is null, or
   *                                  if the distance is negative or zero, or
   *                                  if the player is not placed in the dungeon, or
   *                                  if another player is already in the dungeon, or
   *                                  if the player is dead, or
   *                                  if the player has no arrows
   */
  @Override
  public List<Integer> shootArrow(Dungeon dungeon, String direction, int distance) {
    ValueSanity.checkNull("Dungeon instance", dungeon);
    ValueSanity.checkNull("Direction in which to shoot", direction);
    if (direction.equals("")) {
      throw new IllegalArgumentException("Direction cannot be empty!");
    }
    if (distance <= 0) {
      throw new IllegalArgumentException("Distance must be positive!");
    }

    if (this.isPlayerDead) {
      throw new IllegalArgumentException("Player is dead!");
    }

    if (this.numArrows == 0) {
      throw new IllegalArgumentException("No arrows to shoot, pickup arrows to shoot!");
    }

    this.numArrows--;

    try {
      return ((DungeonImpl) dungeon).shootArrow(direction, distance, this);
    } catch (IllegalArgumentException e) {
      this.numArrows++;
      throw e;
    }
  }

  /**
   * Checks if the player is alive.
   *
   * @return true if the player is alive, false otherwise
   */
  @Override
  public boolean isPlayerDead() {
    return this.isPlayerDead;
  }

  /**
   * Returns the name of the player.
   *
   * @return the name of the player
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Returns the number of arrows the player has.
   *
   * @return the number of arrows the player has
   */
  @Override
  public int getNumArrows() {
    return this.numArrows;
  }

  /**
   * Returns the list of treasures the player has.
   *
   * @return the list of treasures the player has
   */
  @Override
  public List<String> getPlayerTreasuresList() {
    List<String> treasures = new ArrayList<>();

    for (String treasure : this.treasure.keySet()) {
      treasures.add(treasure + ": " + this.treasure.get(treasure));
    }

    return treasures;
  }

  /**
   * Updates the name of the player to the given name.
   *
   * @param playerName the new name of the player
   */
  @Override
  public void updateName(String playerName) {
    this.name = playerName;
  }

  /**
   * Kills the player.
   */
  @Override
  public void killPlayer() {
    this.isPlayerDead = true;
  }

  /**
   * Steals all the treasures from the player.
   */
  @Override
  public void treasureStolen() {
    this.treasure = new HashMap<>();
  }
}
