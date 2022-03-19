package dungeongame;

import utils.ValueSanity;

/**
 * Representation of an Otyugh, these are solitary creatures
 * that reside in caves in the dungeon and can eat players if
 * they are in the same cave.
 */
public class Otyugh {
  private static int counter = 1;
  private final String name;
  private int health;

  /**
   * Constructs a new Otyugh with auto-incremented numbers used as names.
   */
  public Otyugh() {
    this.name = String.valueOf(counter++);
    this.health = 100;
  }

  /**
   * Constructs a new Otyugh from an existing Otyugh instance.
   *
   * @param otyugh the Otyugh instance to copy
   */
  public Otyugh(Otyugh otyugh) {
    performChecks(otyugh);

    this.name = otyugh.getName();
    this.health = otyugh.getHealth();
  }

  /**
   * Constructs a new Otyugh with a given name and health.
   *
   * @param name   the name of the Otyugh
   * @param health the health of the Otyugh
   * @throws IllegalArgumentException if the name is null or the health is not
   *                                  between 0 and 100
   */
  public Otyugh(String name, int health) {
    performChecks(name, health);

    this.name = name;
    this.health = health;
  }

  private void performChecks(Otyugh otyugh) {
    ValueSanity.checkNull("Otyugh instance", otyugh);
  }

  private void performChecks(String name, int health) {
    ValueSanity.checkNull("Otyugh name", name);

    if (health < 0 || health > 100) {
      throw new IllegalArgumentException("Health must be between 0 and 100!");
    }
  }

  /**
   * Gets the name of the Otyugh.
   *
   * @return the name of the Otyugh
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the health of the Otyugh.
   *
   * @return the health of the Otyugh
   */
  public int getHealth() {
    return health;
  }

  /**
   * Sets the health of the Otyugh.
   *
   * @param health the health to be set
   */
  public void setHealth(int health) {
    if (health < 0 || health > 100) {
      throw new IllegalArgumentException("Health must be between 0 and 100!");
    }

    this.health = health;
  }

  /**
   * Gets the health after Otyugh is hit by an arrow.
   *
   * @return the health after being hit by an arrow
   */
  public int gotHitByArrow() {
    if (health == 100) {
      return 50;
    } else {
      return 0;
    }
  }

  /**
   * Check if the Otyugh is hit once or not.
   *
   * @return true if the Otyugh is hit once, false otherwise
   */
  public boolean isHit() {
    return health == 50;
  }
}
