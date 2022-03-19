package utils;

/**
 * A utility class containing helpers for checking the validity of values.
 */
public class ValueSanity {
  /**
   * Checks if the given integer is negative or not.
   *
   * @param arg the name of the value to be checked
   * @param val the value to be checked
   * @throws IllegalArgumentException if the value is negative
   */
  public static void checkNegative(String arg, float val) {
    checkNull("Argument", arg);

    if (val < 0) {
      throw new IllegalArgumentException(arg + " is expected to be positive!");
    }
  }

  /**
   * Checks if the given value is null or not.
   *
   * @param arg the name of the value to be checked
   * @param val the value to be checked
   * @throws IllegalArgumentException if the value is null
   */
  public static void checkNull(String arg, Object val) {
    if (arg == null) {
      throw new IllegalArgumentException("Argument cannot be null!");
    }

    if (val == null) {
      throw new IllegalArgumentException(arg + " is expected to be non-null!");
    }
  }
}
