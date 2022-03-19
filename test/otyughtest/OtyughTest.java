package otyughtest;

import org.junit.Before;
import org.junit.Test;

import dungeongame.Otyugh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

/**
 * Tests for the Otyugh class.
 */
public class OtyughTest {
  private Otyugh otyugh;

  @Before
  public void setUp() {
    this.otyugh = new Otyugh();
  }

  /**
   * Tests constructing a new Otyugh.
   */
  @Test
  public void testConstructOtyugh() {
    Otyugh otyugh = new Otyugh();
    assertEquals(100, otyugh.getHealth());
  }

  /**
   * Tests constructing a new Otyugh from existing Otyugh with null Otyugh.
   */
  @Test
  public void testConstructOtyughNull() {
    try {
      new Otyugh(null);
      fail("Otyugh instance cannot be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Otyugh instance is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests constructing a new Otyugh from an existing Otyugh.
   */
  @Test
  public void testCopyConstructOtyugh() {
    Otyugh otyugh = new Otyugh();
    Otyugh otyugh2 = new Otyugh(otyugh);
    assertEquals(100, otyugh2.getHealth());
    assertNotEquals(otyugh, otyugh2);
  }

  /**
   * Tests constructing an Otyugh with null name.
   */
  @Test
  public void testConstructOtyughNullName() {
    try {
      new Otyugh(null, 100);
      fail("Otyugh name cannot be null!");
    } catch (IllegalArgumentException e) {
      assertEquals("Otyugh name is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests constructing an Otyugh with health less than 0 or greater than 100.
   */
  @Test
  public void testConstructOtyughInvalidHealth() {
    try {
      new Otyugh("Otyugh", -1);
      fail("Otyugh health cannot be less than 0!");
    } catch (IllegalArgumentException e) {
      assertEquals("Health must be between 0 and 100!", e.getMessage());
    }

    try {
      new Otyugh("Otyugh", 101);
      fail("Otyugh health cannot be greater than 100!");
    } catch (IllegalArgumentException e) {
      assertEquals("Health must be between 0 and 100!", e.getMessage());
    }
  }

  /**
   * Tests fetching name of an Otyugh.
   */
  @Test
  public void testGetName() {
    assertEquals("2", new Otyugh("2", 100).getName());
  }

  /**
   * Tests fetching health of an Otyugh.
   */
  @Test
  public void testGetHealth() {
    assertEquals(100, this.otyugh.getHealth());
  }

  /**
   * Tests setting health of an Otyugh, health is negative.
   */
  @Test
  public void testSetHealthNegative() {
    try {
      this.otyugh.setHealth(-1);
      fail("Health cannot be negative!");
    } catch (IllegalArgumentException e) {
      assertEquals("Health must be between 0 and 100!", e.getMessage());
    }
  }

  /**
   * Tests setting health of an Otyugh, health is greater than 100.
   */
  @Test
  public void testSetHealthGreaterThan100() {
    try {
      this.otyugh.setHealth(101);
      fail("Health cannot be greater than 100!");
    } catch (IllegalArgumentException e) {
      assertEquals("Health must be between 0 and 100!", e.getMessage());
    }
  }

  /**
   * Tests setting health of an Otyugh.
   */
  @Test
  public void testSetHealth() {
    this.otyugh.setHealth(50);
    assertEquals(50, this.otyugh.getHealth());
  }

  /**
   * Tests effect of getting hit by arrow.
   */
  @Test
  public void testGetHitArrow() {
    assertEquals(100, this.otyugh.getHealth());
    assertEquals(50, this.otyugh.gotHitByArrow());
    this.otyugh.setHealth(50);
    assertEquals(0, this.otyugh.gotHitByArrow());
  }
}
