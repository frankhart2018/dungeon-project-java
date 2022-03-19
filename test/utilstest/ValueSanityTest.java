package utilstest;

import org.junit.Test;

import utils.ValueSanity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for the ValueSanity utility class.
 */
public class ValueSanityTest {
  /**
   * Tests check negative with null arg value.
   */
  @Test
  public void testCheckNegativeNull() {
    try {
      ValueSanity.checkNegative(null, 1);
      fail("Argument is expected to be non-null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Argument is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests check negative with negative arg value.
   */
  @Test
  public void testCheckNegativeNegative() {
    try {
      ValueSanity.checkNegative("Some value", -1);
      fail("Argument is expected to be non-negative!");
    } catch (IllegalArgumentException e) {
      assertEquals("Some value is expected to be positive!", e.getMessage());
    }
  }

  /**
   * Tests check negative with positive arg value.
   */
  @Test
  public void testCheckNegativePositive() {
    try {
      ValueSanity.checkNegative("Some value", 1);
    } catch (IllegalArgumentException e) {
      assertEquals("Some value is expected to be positive!", e.getMessage());
    }
  }

  /**
   * Tests check null with null arg.
   */
  @Test
  public void testCheckNullNull() {
    try {
      ValueSanity.checkNull(null, 100);
      fail("Argument is expected to be non-null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Argument cannot be null!", e.getMessage());
    }
  }

  /**
   * Tests check null with null value.
   */
  @Test
  public void testCheckNullValue() {
    try {
      ValueSanity.checkNull("Some value", null);
      fail("Some value is expected to be non-null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Some value is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests check null with non-null value.
   */
  @Test
  public void testCheckNullValueNonNull() {
    try {
      ValueSanity.checkNull("Some value", "Some value");
    } catch (IllegalArgumentException e) {
      assertEquals("Some value is expected to be non-null!", e.getMessage());
    }
  }
}
