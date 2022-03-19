package nodetest;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import dungeongame.Otyugh;
import node.GenericNode;
import node.Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Tests for the Node type.
 */
public class NodeTest {
  private static final int SEED = 42;

  private Node genericNode;
  private Node caveNode;
  private Node tunnelNode;

  /**
   * Sets up the test for Node.
   */
  @Before
  public void setUp() {
    genericNode = new GenericNode();
    caveNode = genericNode.castToCaveNode();
    tunnelNode = genericNode.castToTunnelNode();
  }

  private Random getRandom() {
    return new Random(SEED);
  }

  /**
   * Tests casting a cave node to a cave node.
   */
  @Test
  public void testCastToCaveNodeAlreadyCaveNode() {
    try {
      caveNode.castToCaveNode();
      fail("Cannot cast cave node to cave node!");
    } catch (IllegalArgumentException e) {
      assertEquals("Only a GenericNode can be cast to CaveNode!", e.getMessage());
    }
  }

  /**
   * Tests casting a tunnel node to a cave node.
   */
  @Test
  public void testCastToCaveNodeAlreadyTunnelNode() {
    try {
      tunnelNode.castToCaveNode();
      fail("Cannot cast tunnel node to cave node!");
    } catch (IllegalArgumentException e) {
      assertEquals("Only a GenericNode can be cast to CaveNode!", e.getMessage());
    }
  }

  /**
   * Tests casting a generic node to a cave node.
   */
  @Test
  public void testCastToCaveNode() {
    Node caveNodeFromGenericNode = genericNode.castToCaveNode();
    assertEquals("C", caveNodeFromGenericNode.getType());
  }

  /**
   * Tests casting a cave node to a tunnel node.
   */
  @Test
  public void testCastToTunnelNodeAlreadyCaveNode() {
    try {
      caveNode.castToTunnelNode();
      fail("Cannot cast cave node to tunnel node!");
    } catch (IllegalArgumentException e) {
      assertEquals("Only a GenericNode can be cast to TunnelNode!", e.getMessage());
    }
  }

  /**
   * Tests casting a tunnel node to a tunnel node.
   */
  @Test
  public void testCastToTunnelNodeAlreadyTunnelNode() {
    try {
      tunnelNode.castToTunnelNode();
      fail("Cannot cast tunnel node to tunnel node!");
    } catch (IllegalArgumentException e) {
      assertEquals("Only a GenericNode can be cast to TunnelNode!", e.getMessage());
    }
  }

  /**
   * Tests casting a generic node to a tunnel node.
   */
  @Test
  public void testCastToTunnelNode() {
    Node tunnelNodeFromGenericNode = genericNode.castToTunnelNode();
    assertEquals("T", tunnelNodeFromGenericNode.getType());
  }

  /**
   * Tests fetching room description of a generic node.
   */
  @Test
  public void testGetRoomDescriptionGenericNode() {
    try {
      genericNode.getRoomDescription();
      fail("Cannot get room description of generic node!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot get room description from G node!", e.getMessage());
    }
  }

  /**
   * Tests fetching room description of a cave node with no neighbours.
   */
  @Test
  public void testGetRoomDescriptionCaveNodeNoNeighbours() {
    try {
      caveNode.getRoomDescription();
      fail("Cannot get room description of cave node with no neighbours!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot get room description of a disconnected node, "
              + "there are no connections in any side!", e.getMessage());
    }
  }

  /**
   * Tests fetching room description of a tunnel node with no neighbours.
   */
  @Test
  public void testGetRoomDescriptionTunnelNodeNoNeighbours() {
    try {
      tunnelNode.getRoomDescription();
      fail("Cannot get room description of tunnel node with no neighbours!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot get room description of a disconnected node, "
              + "there are no connections in any side!", e.getMessage());
    }
  }

  /**
   * Tests fetching room description of a cave node with one neighbour.
   */
  @Test
  public void testGetRoomDescriptionCaveNodeOneNeighbour() {
    Node clone1 = caveNode.cloneNode();
    Node clone2 = caveNode.cloneNode();
    Node clone3 = caveNode.cloneNode();
    Node clone4 = caveNode.cloneNode();

    clone1.setTopNode(caveNode);
    clone2.setBottomNode(caveNode);
    clone3.setLeftNode(caveNode);
    clone4.setRightNode(caveNode);

    assertEquals("You are in a cave\n" +
            "Doors lead to the N", clone1.getRoomDescription());
    assertEquals("You are in a cave\n" +
            "Doors lead to the S", clone2.getRoomDescription());
    assertEquals("You are in a cave\n" +
            "Doors lead to the W", clone3.getRoomDescription());
    assertEquals("You are in a cave\n" +
            "Doors lead to the E", clone4.getRoomDescription());
  }

  /**
   * Tests fetching room description of a cave node with two neighbours.
   */
  @Test
  public void testGetRoomDescriptionCaveNodeTwoNeighbours() {
    Node clone1 = caveNode.cloneNode();
    Node clone2 = caveNode.cloneNode();
    Node clone3 = caveNode.cloneNode();
    Node clone4 = caveNode.cloneNode();
    Node clone5 = caveNode.cloneNode();

    clone1.setTopNode(clone2);
    clone1.setBottomNode(clone3);

    clone2.setTopNode(clone4);
    clone2.setRightNode(clone1);

    clone3.setTopNode(clone1);
    clone3.setLeftNode(clone4);

    clone4.setBottomNode(clone2);
    clone4.setRightNode(clone5);

    clone5.setBottomNode(clone3);
    clone5.setLeftNode(clone2);

    caveNode.setLeftNode(clone1);
    caveNode.setRightNode(clone2);

    try {
      clone1.getRoomDescription();
      fail("Cave can have either 1, 3 or 4 entrances!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cave can have either 1, 3 or 4 neighbours!", e.getMessage());
    }

    try {
      clone2.getRoomDescription();
      fail("Cave can have either 1, 3 or 4 entrances!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cave can have either 1, 3 or 4 neighbours!", e.getMessage());
    }

    try {
      clone3.getRoomDescription();
      fail("Cave can have either 1, 3 or 4 entrances!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cave can have either 1, 3 or 4 neighbours!", e.getMessage());
    }

    try {
      clone4.getRoomDescription();
      fail("Cave can have either 1, 3 or 4 entrances!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cave can have either 1, 3 or 4 neighbours!", e.getMessage());
    }

    try {
      clone4.getRoomDescription();
      fail("Cave can have either 1, 3 or 4 entrances!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cave can have either 1, 3 or 4 neighbours!", e.getMessage());
    }

    try {
      caveNode.getRoomDescription();
      fail("Cave can have either 1, 3 or 4 entrances!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cave can have either 1, 3 or 4 neighbours!", e.getMessage());
    }
  }

  /**
   * Tests fetching room description of a cave node with three neighbours.
   */
  @Test
  public void testGetRoomDescriptionCaveNodeThreeNeighbours() {
    Node clone1 = caveNode.cloneNode();
    Node clone2 = caveNode.cloneNode();
    Node clone3 = caveNode.cloneNode();
    Node clone4 = caveNode.cloneNode();

    clone1.setTopNode(clone2);
    clone1.setBottomNode(clone3);
    clone1.setRightNode(clone4);

    clone2.setTopNode(clone1);
    clone2.setLeftNode(clone3);
    clone2.setRightNode(clone4);

    clone3.setTopNode(clone1);
    clone3.setBottomNode(clone2);
    clone3.setLeftNode(clone4);

    clone4.setBottomNode(clone1);
    clone4.setRightNode(clone2);
    clone4.setLeftNode(clone3);

    assertEquals("You are in a cave\n" +
            "Doors lead to the N, E, S", clone1.getRoomDescription());

    assertEquals("You are in a cave\n" +
            "Doors lead to the N, E, W", clone2.getRoomDescription());

    assertEquals("You are in a cave\n" +
            "Doors lead to the N, S, W", clone3.getRoomDescription());

    assertEquals("You are in a cave\n" +
            "Doors lead to the E, S, W", clone4.getRoomDescription());
  }

  /**
   * Tests fetching room description of a cave node with four neighbours.
   */
  @Test
  public void testGetRoomDescriptionCaveNodeFourNeighbours() {
    Node clone1 = caveNode.cloneNode();
    Node clone2 = caveNode.cloneNode();
    Node clone3 = caveNode.cloneNode();
    Node clone4 = caveNode.cloneNode();

    caveNode.setTopNode(clone1);
    caveNode.setBottomNode(clone2);
    caveNode.setLeftNode(clone3);
    caveNode.setRightNode(clone4);

    assertEquals("You are in a cave\n" +
            "Doors lead to the N, E, S, W", caveNode.getRoomDescription());
  }

  /**
   * Tests fetching room description of a tunnel node with one neighbour.
   */
  @Test
  public void testGetRoomDescriptionTunnelNodeOneNeighbour() {
    Node clone1 = tunnelNode.cloneNode();
    Node clone2 = tunnelNode.cloneNode();
    Node clone3 = tunnelNode.cloneNode();
    Node clone4 = tunnelNode.cloneNode();

    clone1.setTopNode(tunnelNode);
    clone2.setBottomNode(tunnelNode);
    clone3.setLeftNode(tunnelNode);
    clone4.setRightNode(tunnelNode);

    try {
      clone1.getRoomDescription();
      fail("Tunnel can have only 2 entrances!");
    } catch (IllegalArgumentException e) {
      assertEquals("Tunnel can have exactly 2 neighbours!", e.getMessage());
    }

    try {
      clone2.getRoomDescription();
      fail("Tunnel can have only 2 entrances!");
    } catch (IllegalArgumentException e) {
      assertEquals("Tunnel can have exactly 2 neighbours!", e.getMessage());
    }

    try {
      clone3.getRoomDescription();
      fail("Tunnel can have only 2 entrances!");
    } catch (IllegalArgumentException e) {
      assertEquals("Tunnel can have exactly 2 neighbours!", e.getMessage());
    }

    try {
      clone4.getRoomDescription();
      fail("Tunnel can have only 2 entrances!");
    } catch (IllegalArgumentException e) {
      assertEquals("Tunnel can have exactly 2 neighbours!", e.getMessage());
    }
  }

  /**
   * Tests fetching room description of a tunnel node with two neighbours.
   */
  @Test
  public void testGetRoomDescriptionTunnelNodeTwoNeighbours() {
    Node clone1 = tunnelNode.cloneNode();
    Node clone2 = tunnelNode.cloneNode();
    Node clone3 = tunnelNode.cloneNode();
    Node clone4 = tunnelNode.cloneNode();
    Node clone5 = tunnelNode.cloneNode();

    clone1.setTopNode(clone2);
    clone1.setBottomNode(clone3);

    clone2.setTopNode(clone4);
    clone2.setRightNode(clone1);

    clone3.setTopNode(clone1);
    clone3.setLeftNode(clone4);

    clone4.setBottomNode(clone3);
    clone4.setRightNode(clone2);

    clone5.setBottomNode(clone2);
    clone5.setLeftNode(clone3);

    tunnelNode.setLeftNode(clone1);
    tunnelNode.setRightNode(clone2);

    assertEquals("You are in a tunnel\n" +
            "that continues to the N, S", clone1.getRoomDescription());
    assertEquals("You are in a tunnel\n" +
            "that continues to the N, E", clone2.getRoomDescription());
    assertEquals("You are in a tunnel\n" +
            "that continues to the N, W", clone3.getRoomDescription());
    assertEquals("You are in a tunnel\n" +
            "that continues to the E, S", clone4.getRoomDescription());
    assertEquals("You are in a tunnel\n" +
            "that continues to the S, W", clone5.getRoomDescription());
    assertEquals("You are in a tunnel\n" +
            "that continues to the E, W", tunnelNode.getRoomDescription());
  }

  /**
   * Tests fetching room description of a tunnel node with three neighbours.
   */
  @Test
  public void testGetRoomDescriptionTunnelNodeThreeNeighbours() {
    Node clone1 = tunnelNode.cloneNode();
    Node clone2 = tunnelNode.cloneNode();
    Node clone3 = tunnelNode.cloneNode();
    Node clone4 = tunnelNode.cloneNode();

    clone1.setTopNode(clone2);
    clone1.setBottomNode(clone3);
    clone1.setRightNode(clone4);

    clone2.setTopNode(clone1);
    clone2.setLeftNode(clone3);
    clone2.setRightNode(clone4);

    clone3.setTopNode(clone1);
    clone3.setBottomNode(clone2);
    clone3.setLeftNode(clone4);

    clone4.setBottomNode(clone1);
    clone4.setRightNode(clone2);
    clone4.setLeftNode(clone3);

    try {
      clone1.getRoomDescription();
      fail("Tunnel can have only 2 entrances!");
    } catch (IllegalArgumentException e) {
      assertEquals("Tunnel can have exactly 2 neighbours!", e.getMessage());
    }

    try {
      clone2.getRoomDescription();
      fail("Tunnel can have only 2 entrances!");
    } catch (IllegalArgumentException e) {
      assertEquals("Tunnel can have exactly 2 neighbours!", e.getMessage());
    }

    try {
      clone3.getRoomDescription();
      fail("Tunnel can have only 2 entrances!");
    } catch (IllegalArgumentException e) {
      assertEquals("Tunnel can have exactly 2 neighbours!", e.getMessage());
    }

    try {
      clone4.getRoomDescription();
      fail("Tunnel can have only 2 entrances!");
    } catch (IllegalArgumentException e) {
      assertEquals("Tunnel can have exactly 2 neighbours!", e.getMessage());
    }
  }

  /**
   * Tests fetching room description of a tunnel node with four neighbours.
   */
  @Test
  public void testGetRoomDescriptionTunnelNodeFourNeighbours() {
    Node clone1 = tunnelNode.cloneNode();
    Node clone2 = tunnelNode.cloneNode();
    Node clone3 = tunnelNode.cloneNode();
    Node clone4 = tunnelNode.cloneNode();

    tunnelNode.setTopNode(clone1);
    tunnelNode.setBottomNode(clone2);
    tunnelNode.setLeftNode(clone3);
    tunnelNode.setRightNode(clone4);

    try {
      tunnelNode.getRoomDescription();
      fail("Tunnel can have only 2 entrances!");
    } catch (IllegalArgumentException e) {
      assertEquals("Tunnel can have exactly 2 neighbours!", e.getMessage());
    }
  }

  /**
   * Tests fetching room description of a cave node with a single ruby or diamond, or sapphire.
   */
  @Test
  public void testSingleTreasureInCave() {
    Node clone1 = caveNode.cloneNode();
    Node clone2 = caveNode.cloneNode();
    Node clone3 = caveNode.cloneNode();

    clone1.setTopNode(clone2);
    clone2.setTopNode(clone1);
    clone3.setTopNode(clone1);

    clone1.placeTreasure("RUBY", 1);
    clone2.placeTreasure("DIAMOND", 1);
    clone3.placeTreasure("SAPPHIRE", 1);

    assertEquals("You are in a cave\n" +
            "Doors lead to the N\n" +
            "You find 1 ruby here", clone1.getRoomDescription());

    assertEquals("You are in a cave\n" +
            "Doors lead to the N\n" +
            "You find 1 diamond here", clone2.getRoomDescription());

    assertEquals("You are in a cave\n" +
            "Doors lead to the N\n" +
            "You find 1 sapphire here", clone3.getRoomDescription());
  }

  /**
   * Tests fetching room description of a cave node with multiple rubies, diamonds, and sapphires.
   */
  @Test
  public void testMultipleTreasuresInCave() {
    Node clone1 = caveNode.cloneNode();
    Node clone2 = caveNode.cloneNode();
    Node clone3 = caveNode.cloneNode();

    clone1.setTopNode(clone2);
    clone2.setTopNode(clone1);
    clone3.setTopNode(clone1);

    clone1.placeTreasure("RUBY", 2);
    clone1.placeTreasure("DIAMOND", 3);

    clone2.placeTreasure("DIAMOND", 2);
    clone2.placeTreasure("SAPPHIRE", 3);

    clone3.placeTreasure("SAPPHIRE", 5);
    clone3.placeTreasure("RUBY", 3);

    assertEquals("You are in a cave\n" +
            "Doors lead to the N\n" +
            "You find 3 diamonds here\n" +
            "You find 2 rubies here", clone1.getRoomDescription());

    assertEquals("You are in a cave\n" +
            "Doors lead to the N\n" +
            "You find 3 sapphires here\n" +
            "You find 2 diamonds here", clone2.getRoomDescription());

    assertEquals("You are in a cave\n" +
            "Doors lead to the N\n" +
            "You find 5 sapphires here\n" +
            "You find 3 rubies here", clone3.getRoomDescription());
  }

  /**
   * Tests fetching room description of a cave or tunnel with a single arrow.
   */
  @Test
  public void testSingleArrowInCaveOrTunnel() {
    Node clone1 = caveNode.cloneNode();
    Node clone2 = tunnelNode.cloneNode();

    clone1.setTopNode(clone2);
    clone2.setTopNode(clone1);
    clone2.setBottomNode(caveNode);

    clone1.addSingleArrow();
    clone2.addSingleArrow();

    assertEquals("You are in a cave\n" +
            "Doors lead to the N\n" +
            "You find 1 arrow here", clone1.getRoomDescription());

    assertEquals("You are in a tunnel\n" +
            "that continues to the N, S\n" +
            "You find 1 arrow here", clone2.getRoomDescription());
  }

  /**
   * Tests fetching room description of a cave or tunnel with multiple arrows.
   */
  @Test
  public void testMultipleArrowsInCaveOrTunnel() {
    Node clone1 = caveNode.cloneNode();
    Node clone2 = tunnelNode.cloneNode();

    clone1.setTopNode(clone2);
    clone2.setTopNode(clone1);
    clone2.setBottomNode(caveNode);

    clone1.addSingleArrow();
    clone1.addSingleArrow();

    clone2.addSingleArrow();
    clone2.addSingleArrow();
    clone2.addSingleArrow();

    assertEquals("You are in a cave\n" +
            "Doors lead to the N\n" +
            "You find 2 arrows here", clone1.getRoomDescription());

    assertEquals("You are in a tunnel\n" +
            "that continues to the N, S\n" +
            "You find 3 arrows here", clone2.getRoomDescription());
  }

  /**
   * Tests fetching room description of a cave or tunnel with a single otyugh
   * at a distance of one.
   */
  @Test
  public void testSingleOtyughInALocationWithDistanceOne() {
    Node clone1 = new GenericNode().castToCaveNode();
    Node clone2 = new GenericNode().castToCaveNode();
    Node clone3 = new GenericNode().castToCaveNode();

    clone1.setTopNode(clone2);
    clone2.setBottomNode(clone1);

    clone2.setTopNode(clone3);
    clone3.setBottomNode(clone2);

    clone2.placeOtyugh(new Otyugh());

    assertEquals("You are in a cave\n" +
            "Doors lead to the N\n" +
            "You smell a strong pungent smell", clone1.getRoomDescription());
  }

  /**
   * Tests fetching room description of a cave or tunnel with a single otyugh
   * at a distance of two.
   */
  @Test
  public void testSingleOtyughInALocationWithDistanceTwo() {
    Node clone1 = new GenericNode().castToCaveNode();
    Node clone2 = new GenericNode().castToCaveNode();
    Node clone3 = new GenericNode().castToCaveNode();

    clone1.setTopNode(clone2);
    clone2.setBottomNode(clone1);

    clone2.setTopNode(clone3);
    clone3.setBottomNode(clone2);

    clone3.placeOtyugh(new Otyugh());

    assertEquals("You are in a cave\n" +
            "Doors lead to the N\n" +
            "You smell a pungent smell", clone1.getRoomDescription());
  }

  /**
   * Tests fetching room description of a cave or tunnel with multiple otyughs
   * at a distance of two.
   */
  @Test
  public void testMultipleOtyughsInALocationWithDistanceTwo() {
    Node clone1 = new GenericNode().castToCaveNode();
    Node clone2 = new GenericNode().castToCaveNode();
    Node clone3 = new GenericNode().castToCaveNode();
    Node clone4 = new GenericNode().castToCaveNode();

    clone1.setTopNode(clone2);
    clone2.setBottomNode(clone1);

    clone2.setTopNode(clone3);
    clone3.setBottomNode(clone2);

    clone2.setLeftNode(clone4);
    clone4.setRightNode(clone2);

    clone3.placeOtyugh(new Otyugh());
    clone4.placeOtyugh(new Otyugh());

    assertEquals("You are in a cave\n" +
            "Doors lead to the N\n" +
            "You smell a strong pungent smell", clone1.getRoomDescription());
  }

  /**
   * Tests fetching room description of a node with multiple treasures, arrows,
   * and a strong pungent smell.
   */
  @Test
  public void testFullGetRoomDescription() {
    Node node = new GenericNode().castToCaveNode();
    Node node2 = new GenericNode().castToCaveNode();

    node.placeTreasure("RUBY", 2);
    node.placeTreasure("SAPPHIRE", 1);

    node.addSingleArrow();
    node.addSingleArrow();
    node.addSingleArrow();

    node.setTopNode(node2);
    node2.setBottomNode(node);

    node2.placeOtyugh(new Otyugh());

    assertEquals("You are in a cave\n" +
            "Doors lead to the N\n" +
            "You find 1 sapphire here\n" +
            "You find 2 rubies here\n" +
            "You find 3 arrows here\n" +
            "You smell a strong pungent smell", node.getRoomDescription());
  }

  /**
   * Tests fetching top node.
   */
  @Test
  public void testGetTopNode() {
    assertNull(genericNode.getTopNode());
    assertNull(caveNode.getTopNode());
    assertNull(tunnelNode.getTopNode());

    Node sampleNode = new GenericNode().castToCaveNode();

    genericNode.setTopNode(sampleNode);
    caveNode.setTopNode(sampleNode);
    tunnelNode.setTopNode(sampleNode);

    assertEquals(sampleNode, genericNode.getTopNode());
    assertEquals(sampleNode, caveNode.getTopNode());
    assertEquals(sampleNode, tunnelNode.getTopNode());
  }

  /**
   * Tests fetching bottom node.
   */
  @Test
  public void testGetBottomNode() {
    assertNull(genericNode.getBottomNode());
    assertNull(caveNode.getBottomNode());
    assertNull(tunnelNode.getBottomNode());

    Node sampleNode = new GenericNode().castToCaveNode();

    genericNode.setBottomNode(sampleNode);
    caveNode.setBottomNode(sampleNode);
    tunnelNode.setBottomNode(sampleNode);

    assertEquals(sampleNode, genericNode.getBottomNode());
    assertEquals(sampleNode, caveNode.getBottomNode());
    assertEquals(sampleNode, tunnelNode.getBottomNode());
  }

  /**
   * Tests fetching right node.
   */
  @Test
  public void testGetRightNode() {
    assertNull(genericNode.getRightNode());
    assertNull(caveNode.getRightNode());
    assertNull(tunnelNode.getRightNode());

    Node sampleNode = new GenericNode().castToCaveNode();

    genericNode.setRightNode(sampleNode);
    caveNode.setRightNode(sampleNode);
    tunnelNode.setRightNode(sampleNode);

    assertEquals(sampleNode, genericNode.getRightNode());
    assertEquals(sampleNode, caveNode.getRightNode());
    assertEquals(sampleNode, tunnelNode.getRightNode());
  }

  /**
   * Tests fetching left node.
   */
  @Test
  public void testGetLeftNode() {
    assertNull(genericNode.getLeftNode());
    assertNull(caveNode.getLeftNode());
    assertNull(tunnelNode.getLeftNode());

    Node sampleNode = new GenericNode().castToCaveNode();

    genericNode.setLeftNode(sampleNode);
    caveNode.setLeftNode(sampleNode);
    tunnelNode.setLeftNode(sampleNode);

    assertEquals(sampleNode, genericNode.getLeftNode());
    assertEquals(sampleNode, caveNode.getLeftNode());
    assertEquals(sampleNode, tunnelNode.getLeftNode());
  }

  /**
   * Tests fetching the name of the node.
   */
  @Test
  public void testGetNodeName() {
    assertEquals("24", genericNode.getNodeName());
    assertEquals("24", caveNode.getNodeName());
    assertEquals("24", tunnelNode.getNodeName());
  }

  /**
   * Tests setting top node as the same node.
   */
  @Test
  public void testSetTopNodeThisNode() {
    try {
      caveNode.setTopNode(caveNode);
      fail("Cannot set top node as this!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot set top node as this node!", e.getMessage());
    }

    try {
      tunnelNode.setTopNode(tunnelNode);
      fail("Cannot set top node as this");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot set top node as this node!", e.getMessage());
    }
  }

  /**
   * Tests setting top node successfully.
   */
  @Test
  public void testSetTopNode() {
    Node sampleNode = new GenericNode().castToCaveNode();

    genericNode.setTopNode(sampleNode);
    caveNode.setTopNode(sampleNode);
    tunnelNode.setTopNode(sampleNode);

    assertEquals(sampleNode, genericNode.getTopNode());
    assertEquals(sampleNode, caveNode.getTopNode());
    assertEquals(sampleNode, tunnelNode.getTopNode());
  }

  /**
   * Tests setting bottom node as the same node.
   */
  @Test
  public void testSetBottomNodeThisNode() {
    try {
      caveNode.setBottomNode(caveNode);
      fail("Cannot set bottom node as this!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot set bottom node as this node!", e.getMessage());
    }

    try {
      tunnelNode.setBottomNode(tunnelNode);
      fail("Cannot set bottom node as this");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot set bottom node as this node!", e.getMessage());
    }
  }

  /**
   * Tests setting bottom node successfully.
   */
  @Test
  public void testSetBottomNode() {
    Node sampleNode = new GenericNode().castToCaveNode();

    genericNode.setBottomNode(sampleNode);
    caveNode.setBottomNode(sampleNode);
    tunnelNode.setBottomNode(sampleNode);

    assertEquals(sampleNode, genericNode.getBottomNode());
    assertEquals(sampleNode, caveNode.getBottomNode());
    assertEquals(sampleNode, tunnelNode.getBottomNode());
  }

  /**
   * Tests setting left node as the same node.
   */
  @Test
  public void testSetLeftNodeThisNode() {
    try {
      caveNode.setLeftNode(caveNode);
      fail("Cannot set left node as this!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot set left node as this node!", e.getMessage());
    }

    try {
      tunnelNode.setLeftNode(tunnelNode);
      fail("Cannot set left node as this");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot set left node as this node!", e.getMessage());
    }
  }

  /**
   * Tests setting left node successfully.
   */
  @Test
  public void testSetLeftNode() {
    Node sampleNode = new GenericNode().castToCaveNode();

    genericNode.setLeftNode(sampleNode);
    caveNode.setLeftNode(sampleNode);
    tunnelNode.setLeftNode(sampleNode);

    assertEquals(sampleNode, genericNode.getLeftNode());
    assertEquals(sampleNode, caveNode.getLeftNode());
    assertEquals(sampleNode, tunnelNode.getLeftNode());
  }

  /**
   * Tests setting right node as the same node.
   */
  @Test
  public void testSetRightNodeThisNode() {
    try {
      caveNode.setRightNode(caveNode);
      fail("Cannot set right node as this!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot set right node as this node!", e.getMessage());
    }

    try {
      tunnelNode.setRightNode(tunnelNode);
      fail("Cannot set right node as this");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot set right node as this node!", e.getMessage());
    }
  }

  /**
   * Tests setting right node successfully.
   */
  @Test
  public void testSetRightNode() {
    Node sampleNode = new GenericNode().castToCaveNode();

    genericNode.setRightNode(sampleNode);
    caveNode.setRightNode(sampleNode);
    tunnelNode.setRightNode(sampleNode);

    assertEquals(sampleNode, genericNode.getRightNode());
    assertEquals(sampleNode, caveNode.getRightNode());
    assertEquals(sampleNode, tunnelNode.getRightNode());
  }

  /**
   * Tests placing treasure in a cave node when the random object is null.
   */
  @Test
  public void testPlaceTreasureCaveNodeNullRandom() {
    try {
      caveNode.placeTreasure(null);
      fail("Cannot place treasure with null random!");
    } catch (IllegalArgumentException e) {
      assertEquals("Random object is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests placing treasure in a cave node.
   */
  @Test
  public void testPlaceTreasureCaveNode() {
    caveNode.placeTreasure(getRandom());

    assertEquals("[SAPPHIRE, SAPPHIRE, RUBY]", caveNode.getTreasures().toString());
  }

  /**
   * Tests placing treasure in a generic node.
   */
  @Test
  public void testPlaceTreasureGenericNode() {
    try {
      genericNode.placeTreasure(getRandom());
      fail("Cannot place treasure in generic node!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Cannot place treasure in a generic node!", e.getMessage());
    }
  }

  /**
   * Tests placing treasure in a tunnel node.
   */
  @Test
  public void testPlaceTreasureTunnelNode() {
    try {
      tunnelNode.placeTreasure(getRandom());
      fail("Cannot place treasure in tunnel node!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Cannot place treasure in a tunnel node!", e.getMessage());
    }
  }

  /**
   * Tests placing treasure deterministically with null or empty treasure name.
   */
  @Test
  public void testPlaceTreasureNullTreasureName() {
    try {
      caveNode.placeTreasure(null, 1);
      fail("Cannot place treasure with null treasure name!");
    } catch (IllegalArgumentException e) {
      assertEquals("Name of treasure is expected to be non-null!", e.getMessage());
    }

    try {
      caveNode.placeTreasure("", 1);
      fail("Cannot place treasure with empty treasure name!");
    } catch (IllegalArgumentException e) {
      assertEquals("Treasure name cannot be empty!", e.getMessage());
    }
  }

  /**
   * Tests placing treasure deterministically with invalid number of treasures.
   */
  @Test
  public void testPlaceTreasureInvalidNumberOfTreasures() {
    try {
      caveNode.placeTreasure("SAPPHIRE", -1);
      fail("Cannot place treasure with invalid number of treasures!");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot place -1 SAPPHIRE!", e.getMessage());
    }
  }

  /**
   * Tests placing treasure deterministically with invalid treasure name.
   */
  @Test
  public void testPlaceTreasureInvalidTreasureName() {
    try {
      caveNode.placeTreasure("INVALID", 1);
      fail("Cannot place treasure with invalid treasure name!");
    } catch (IllegalArgumentException e) {
      assertEquals("Illegal treasure name INVALID!", e.getMessage());
    }
  }

  /**
   * Tests placing treasure deterministically in a generic node.
   */
  @Test
  public void testPlaceTreasureDeterministicallyGenericNode() {
    try {
      genericNode.placeTreasure("SAPPHIRE", 1);
      fail("Cannot place treasure in generic node!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Cannot place treasure in a generic node!", e.getMessage());
    }
  }

  /**
   * Tests placing treasure deterministically in a tunnel node.
   */
  @Test
  public void testPlaceTreasureDeterministicallyTunnelNode() {
    try {
      tunnelNode.placeTreasure("SAPPHIRE", 1);
      fail("Cannot place treasure in tunnel node!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Cannot place treasure in a tunnel node!", e.getMessage());
    }
  }

  /**
   * Tests placing treasure deterministically.
   */
  @Test
  public void testPlaceTreasureDeterministically() {
    caveNode.placeTreasure("SAPPHIRE", 2);
    caveNode.placeTreasure("RUBY", 1);

    assertEquals("[SAPPHIRE, SAPPHIRE, RUBY]", caveNode.getTreasures().toString());
  }


  /**
   * Tests removing treasure from cave node with null treasure name.
   */
  @Test
  public void testRemoveTreasureCaveNodeNullName() {
    try {
      caveNode.removeTreasure(null);
      fail("Cannot remove treasure with null treasure name!");
    } catch (IllegalArgumentException e) {
      assertEquals("Name of treasure is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests removing treasure from cave node with empty treasure name.
   */
  @Test
  public void testRemoveTreasureCaveNodeEmptyName() {
    try {
      caveNode.removeTreasure("");
      fail("Cannot remove treasure with empty treasure name!");
    } catch (IllegalArgumentException e) {
      assertEquals("Treasure name cannot be empty!", e.getMessage());
    }
  }

  /**
   * Tests removing treasure from cave node which has no treasure.
   */
  @Test
  public void testRemoveTreasureCaveNodeWithNoTreasures() {
    try {
      caveNode.removeTreasure("");
      fail("Cannot remove treasure from cave node which has no treasure!");
    } catch (IllegalArgumentException e) {
      assertEquals("Treasure name cannot be empty!", e.getMessage());
    }
  }

  /**
   * Tests removing treasure from cave node with an illegal treasure name.
   */
  @Test
  public void testRemoveTreasureCaveNodeIllegalTreasureName() {
    try {
      caveNode.placeTreasure(getRandom());
      caveNode.removeTreasure("Platinum");
      fail("Cannot remove treasure from cave node with an illegal treasure name!");
    } catch (IllegalArgumentException e) {
      assertEquals("Illegal treasure name Platinum!", e.getMessage());
    }
  }

  /**
   * Tests removing treasure from a cave node when the treasure is not in current cave node.
   */
  @Test
  public void testRemoveTreasureCaveNodeTreasureNameNotFound() {
    try {
      caveNode.placeTreasure(getRandom());
      caveNode.removeTreasure("DIAMOND");
      fail("Cannot remove treasure from cave node when the treasure is not in current cave node!");
    } catch (IllegalArgumentException e) {
      assertEquals("Treasure DIAMOND not found in treasure box!", e.getMessage());
    }
  }

  /**
   * Tests removing treasure from a tunnel node which cannot have treasures in it.
   */
  @Test
  public void testRemoveTreasureTunnelNode() {
    try {
      tunnelNode.removeTreasure("RUBY");
      fail("Cannot remove treasure from tunnel node which cannot have treasures in it!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Tunnel does not have any treasure to remove!", e.getMessage());
    }
  }

  /**
   * Tests removing treasure from a generic node which cannot have treasures in it.
   */
  @Test
  public void testRemoveTreasureGenericNode() {
    try {
      genericNode.removeTreasure("RUBY");
      fail("Cannot remove treasure from generic node which cannot have treasures in it!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Generic node does not have any treasure to remove!", e.getMessage());
    }
  }

  /**
   * Tests removing treasure from a cave node successfully.
   */
  @Test
  public void testRemoveTreasureCaveNode() {
    caveNode.placeTreasure(getRandom());
    caveNode.removeTreasure("RUBY");
    assertEquals("[SAPPHIRE, SAPPHIRE]", caveNode.getTreasures().toString());
  }


  /**
   * Tests fetching the treasure list from a cave node that has no treasures.
   */
  @Test
  public void testGetTreasuresCaveNodeEmptyList() {
    assertEquals("[]", caveNode.getTreasures().toString());
  }

  /**
   * Tests fetching the treasure list from cave node that has treasures.
   */
  @Test
  public void testGetTreasuresCaveNode() {
    caveNode.placeTreasure(getRandom());
    assertEquals("[SAPPHIRE, SAPPHIRE, RUBY]", caveNode.getTreasures().toString());
  }

  /**
   * Tests fetching the treasure list from tunnel node which can never have any treasures.
   */
  @Test
  public void testGetTreasuresTunnelNode() {
    try {
      tunnelNode.getTreasures();
      fail("Cannot fetch treasure list from tunnel node which can never have any treasures!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Tunnel does not have any treasure!", e.getMessage());
    }
  }

  /**
   * Tests fetching the treasure list from generic node which can never have any treasures.
   */
  @Test
  public void testGetTreasuresGenericNode() {
    try {
      genericNode.getTreasures();
      fail("Cannot fetch treasure list from generic node which can never have any treasures!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Generic node does not have any treasure!", e.getMessage());
    }
  }

  /**
   * Tests get type of cave node.
   */
  @Test
  public void testGetTypeCaveNode() {
    assertEquals("C", caveNode.getType());
  }

  /**
   * Tests get type of tunnel node.
   */
  @Test
  public void testGetTypeTunnelNode() {
    assertEquals("T", tunnelNode.getType());
  }

  /**
   * Tests get type of generic node.
   */
  @Test
  public void testGetTypeGenericNode() {
    assertEquals("G", genericNode.getType());
  }

  /**
   * Tests cloning a tunnel node.
   */
  @Test
  public void testCloneNodeTunnelNode() {
    Node cloneTunnelNode = tunnelNode.cloneNode();
    assertNotEquals(cloneTunnelNode, tunnelNode);
  }

  /**
   * Tests cloning a cave node.
   */
  @Test
  public void testCloneNodeCaveNode() {
    Node cloneCaveNode = caveNode.cloneNode();
    assertNotEquals(cloneCaveNode, caveNode);
  }

  /**
   * Tests cloning a generic node.
   */
  @Test
  public void testCloneNodeGenericNode() {
    Node cloneGenericNode = genericNode.cloneNode();
    assertNotEquals(cloneGenericNode, genericNode);
  }

  /**
   * Tests updating node's type in all neighbouring nodes.
   */
  @Test
  public void testUpdateCastInNeighbours() {
    Node node = new GenericNode();

    node.setTopNode(tunnelNode);
    tunnelNode.setBottomNode(node);

    assertEquals("G", tunnelNode.getBottomNode().getType());

    node = node.castToCaveNode();
    node.updateCastInNeighbours();

    assertEquals("C", tunnelNode.getBottomNode().getType());
  }

  /**
   * Tests placing a null Otyugh in a cave node.
   */
  @Test
  public void testPlaceNullOtyugh() {
    try {
      caveNode.placeOtyugh(null);
      fail("Cannot place null Otyugh in cave node!");
    } catch (IllegalArgumentException e) {
      assertEquals("Otyugh is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests placing an Otyugh in a cave node which already has an Otyugh in it.
   */
  @Test
  public void testPlaceOtyughCaveNodeThatHasOtyugh() {
    caveNode.placeOtyugh(new Otyugh());
    try {
      caveNode.placeOtyugh(new Otyugh());
      fail("Cannot place Otyugh in cave node which already has an Otyugh in it!");
    } catch (IllegalArgumentException e) {
      assertEquals("Another Otyugh already placed in this cave!", e.getMessage());
    }
  }

  /**
   * Tests placing an Otyugh in a tunnel node.
   */
  @Test
  public void testPlaceOtyughTunnelNode() {
    try {
      tunnelNode.placeOtyugh(new Otyugh());
      fail("Cannot place Otyugh in tunnel node!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Tunnel cannot have Otyugh!", e.getMessage());
    }
  }

  /**
   * Tests placing an Otyugh in a generic node.
   */
  @Test
  public void testPlaceOtyughGenericNode() {
    try {
      genericNode.placeOtyugh(new Otyugh());
      fail("Cannot place Otyugh in generic node!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Generic node cannot have an Otyugh!", e.getMessage());
    }
  }

  /**
   * Tests placing an Otyugh in a cave node.
   */
  @Test
  public void testPlaceOtyughCaveNode() {
    caveNode.placeOtyugh(new Otyugh());
    assertNotNull(caveNode.getOtyugh());
    assertEquals(100, caveNode.getOtyugh().getHealth());
  }

  /**
   * Tests removing Otyugh from cave that does not have any Otyugh.
   */
  @Test
  public void testRemoveOtyughCaveNodeThatDoesNotHaveOtyugh() {
    try {
      caveNode.removeOtyugh();
      fail("Cannot remove Otyugh from cave node that does not have any Otyugh!");
    } catch (IllegalArgumentException e) {
      assertEquals("No Otyugh to remove from cave!", e.getMessage());
    }
  }

  /**
   * Tests removing Otyugh from a tunnel node.
   */
  @Test
  public void testRemoveOtyughTunnelNode() {
    try {
      tunnelNode.removeOtyugh();
      fail("Cannot remove Otyugh from tunnel node!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Tunnel cannot have Otyugh!", e.getMessage());
    }
  }

  /**
   * Tests removing Otyugh from a generic node.
   */
  @Test
  public void testRemoveOtyughGenericNode() {
    try {
      genericNode.removeOtyugh();
      fail("Cannot remove Otyugh from generic node!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Generic node does not have an Otyugh!", e.getMessage());
    }
  }

  /**
   * Tests removing Otyugh from a cave node.
   */
  @Test
  public void testRemoveOtyughCaveNode() {
    caveNode.placeOtyugh(new Otyugh());
    caveNode.removeOtyugh();
    assertNull(caveNode.getOtyugh());
  }

  /**
   * Tests fetching Otyugh from a tunnel node.
   */
  @Test
  public void testGetOtyughTunnelNode() {
    try {
      tunnelNode.getOtyugh();
      fail("Cannot fetch Otyugh from tunnel node!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Tunnel cannot have Otyugh!", e.getMessage());
    }
  }

  /**
   * Tests fetching Otyugh from a generic node.
   */
  @Test
  public void testGetOtyughGenericNode() {
    try {
      genericNode.getOtyugh();
      fail("Cannot fetch Otyugh from generic node!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Generic node does not have an Otyugh!", e.getMessage());
    }
  }

  /**
   * Tests fetching Otyugh from a cave node which does not have any Otyugh.
   */
  @Test
  public void testGetOtyughCaveNodeThatDoesNotHaveOtyugh() {
    assertNull(caveNode.getOtyugh());
  }

  /**
   * Tests fetching Otyugh from a cave node.
   */
  @Test
  public void testGetOtyughCaveNode() {
    caveNode.placeOtyugh(new Otyugh());
    assertNotNull(caveNode.getOtyugh());
    assertEquals(100, caveNode.getOtyugh().getHealth());
  }

  /**
   * Tests setting health of an Otyugh in a tunnel node.
   */
  @Test
  public void testSetHealthOtyughTunnelNode() {
    try {
      tunnelNode.setOtyughHealth(100);
      fail("Cannot set health of Otyugh in tunnel node!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Tunnel cannot have Otyugh!", e.getMessage());
    }
  }

  /**
   * Tests setting health of an Otyugh in a generic node.
   */
  @Test
  public void testSetHealthOtyughGenericNode() {
    try {
      genericNode.setOtyughHealth(100);
      fail("Cannot set health of Otyugh in generic node!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Generic node does not have an Otyugh!", e.getMessage());
    }
  }

  /**
   * Tests setting health of an Otyugh in a cave node which does not have any Otyugh.
   */
  @Test
  public void testSetHealthOtyughCaveNodeThatDoesNotHaveOtyugh() {
    try {
      caveNode.setOtyughHealth(100);
      fail("Cannot set health of Otyugh in cave node that does not have any Otyugh!");
    } catch (IllegalArgumentException e) {
      assertEquals("No Otyugh to update health of!", e.getMessage());
    }
  }

  /**
   * Tests setting health of an Otyugh in a cave node with negative or greater than 100 health.
   */
  @Test
  public void testSetHealthOtyughCaveNodeWithInvalidHealth() {
    caveNode.placeOtyugh(new Otyugh());
    try {
      caveNode.setOtyughHealth(-1);
      fail("Cannot set health of Otyugh in cave node with negative health!");
    } catch (IllegalArgumentException e) {
      assertEquals("Health must be between 0 and 100!", e.getMessage());
    }

    try {
      caveNode.setOtyughHealth(101);
      fail("Cannot set health of Otyugh in cave node with greater than 100 health!");
    } catch (IllegalArgumentException e) {
      assertEquals("Health must be between 0 and 100!", e.getMessage());
    }
  }

  /**
   * Tests setting health of an Otyugh in a cave node.
   */
  @Test
  public void testSetHealthOtyughCaveNode() {
    caveNode.placeOtyugh(new Otyugh());
    caveNode.setOtyughHealth(50);
    assertEquals(50, caveNode.getOtyugh().getHealth());
  }

  /**
   * Tests placing random number of arrows with null random object in a cave node.
   */
  @Test
  public void testPlaceArrowsCaveNodeWithNullRandom() {
    try {
      caveNode.addArrows(null);
      fail("Cannot place arrows with null random object in cave node!");
    } catch (IllegalArgumentException e) {
      assertEquals("Random object is expected to be non-null!", e.getMessage());
    }
  }

  /**
   * Tests placing random number of arrows in a generic node.
   */
  @Test
  public void testPlaceArrowsGenericNode() {
    try {
      genericNode.addArrows(new Random());
      fail("Cannot place arrows in generic node!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Generic node cannot have any arrows!", e.getMessage());
    }
  }

  /**
   * Tests placing random number of arrows in a tunnel node.
   */
  @Test
  public void testPlaceArrowsTunnelNode() {
    tunnelNode.addArrows(getRandom());
    assertEquals(3, tunnelNode.getNumArrows());
  }

  /**
   * Tests placing random number of arrows in a cave node.
   */
  @Test
  public void testPlaceArrowsCaveNode() {
    caveNode.addArrows(getRandom());
    assertEquals(3, caveNode.getNumArrows());
  }

  /**
   * Tests adding a single arrow in a generic node.
   */
  @Test
  public void testAddArrowGenericNode() {
    try {
      genericNode.addSingleArrow();
      fail("Cannot add arrow in generic node!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Generic node cannot have any arrows!", e.getMessage());
    }
  }

  /**
   * Tests adding a single arrow in a tunnel node.
   */
  @Test
  public void testAddArrowTunnelNode() {
    tunnelNode.addSingleArrow();
    assertEquals(1, tunnelNode.getNumArrows());
  }

  /**
   * Tests adding a single arrow in a cave node.
   */
  @Test
  public void testAddArrowCaveNode() {
    caveNode.addSingleArrow();
    assertEquals(1, caveNode.getNumArrows());
  }

  /**
   * Tests removing arrow from a cave node that does not have any arrows.
   */
  @Test
  public void testRemoveArrowCaveNodeThatDoesNotHaveArrows() {
    try {
      caveNode.removeArrow();
      fail("Cannot remove arrow from cave node that does not have any arrows!");
    } catch (IllegalArgumentException e) {
      assertEquals("No arrows to remove!", e.getMessage());
    }
  }

  /**
   * Tests removing arrow from a cave node that does not have any arrows.
   */
  @Test
  public void testRemoveArrowTunnelNodeThatDoesNotHaveArrows() {
    try {
      tunnelNode.removeArrow();
      fail("Cannot remove arrow from cave node that does not have any arrows!");
    } catch (IllegalArgumentException e) {
      assertEquals("No arrows to remove!", e.getMessage());
    }
  }

  /**
   * Tests removing arrow from a generic node which cannot have any arrows.
   */
  @Test
  public void testRemoveArrowGenericNode() {
    try {
      genericNode.removeArrow();
      fail("Cannot remove arrow from generic node which cannot have any arrows!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Generic node cannot have any arrows!", e.getMessage());
    }
  }

  /**
   * Tests removing arrow from a tunnel node.
   */
  @Test
  public void testRemoveArrowTunnelNode() {
    tunnelNode.addSingleArrow();
    tunnelNode.addSingleArrow();
    tunnelNode.removeArrow();
    assertEquals(1, tunnelNode.getNumArrows());
  }

  /**
   * Tests removing arrow from a cave node.
   */
  @Test
  public void testRemoveArrowCaveNode() {
    caveNode.addSingleArrow();
    caveNode.addSingleArrow();
    caveNode.addSingleArrow();
    caveNode.removeArrow();
    assertEquals(2, caveNode.getNumArrows());
  }

  /**
   * Tests fetching number of arrows from a generic node.
   */
  @Test
  public void testGetNumArrowsGenericNode() {
    try {
      genericNode.getNumArrows();
      fail("Cannot get number of arrows from generic node!");
    } catch (UnsupportedOperationException e) {
      assertEquals("Generic node cannot have any arrows!", e.getMessage());
    }
  }

  /**
   * Tests fetching number of arrows from a tunnel node.
   */
  @Test
  public void testGetNumArrowsTunnelNode() {
    assertEquals(0, tunnelNode.getNumArrows());
    tunnelNode.addSingleArrow();
    tunnelNode.addSingleArrow();
    assertEquals(2, tunnelNode.getNumArrows());
  }

  /**
   * Tests fetching number of arrows from a cave node.
   */
  @Test
  public void testGetNumArrowsCaveNode() {
    assertEquals(0, caveNode.getNumArrows());
    caveNode.addSingleArrow();
    caveNode.addSingleArrow();
    caveNode.addSingleArrow();
    assertEquals(3, caveNode.getNumArrows());
  }
}
