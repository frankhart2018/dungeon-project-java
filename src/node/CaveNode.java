package node;

import dungeongame.Otyugh;
import dungeongame.Yugoloth;
import utils.ValueSanity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
  Since the user does not need to create a cave node directly, only the GenericNode implementation
  of a Node is exposed to the user.
 */
class CaveNode extends AbstractNode {
  private static final int MAX_TREASURES = 4;

  private List<Treasure> treasures;

  public CaveNode(Node northNode, Node southNode, Node eastNode, Node westNode, String nodeName) {
    super(northNode, southNode, eastNode, westNode, nodeName);

    this.treasures = new ArrayList<>();
  }

  public CaveNode(Node northNode, Node southNode, Node eastNode, Node westNode, String nodeName,
                  List<Treasure> treasures, Otyugh otyugh, int numArrows, Yugoloth yugoloth,
                  boolean hasThief, boolean hasPit) {
    super(northNode, southNode, eastNode, westNode, nodeName);

    this.treasures = treasures;
    this.otyugh = otyugh;
    this.numArrows = numArrows;
    this.yugoloth = yugoloth;
    this.hasThief = hasThief;
    this.hasPit = hasPit;
  }

  @Override
  public void placeTreasure(Random random) {
    ValueSanity.checkNull("Random object", random);

    List<Treasure> treasures = new ArrayList<>();
    int numTreasures = random.nextInt((MAX_TREASURES - 1) + 1) + 1;

    int numTreasuresGenerated = 0;
    int randIdx;
    do {
      randIdx = random.nextInt(Treasure.values().length);
      treasures.add(Treasure.values()[randIdx]);
      numTreasuresGenerated++;
    }
    while (numTreasuresGenerated != numTreasures);

    this.treasures = treasures;
    this.hasThief = false;
  }

  @Override
  public void placeTreasure(String treasureName, int count) {
    ValueSanity.checkNull("Name of treasure", treasureName);
    if (treasureName.equals("")) {
      throw new IllegalArgumentException("Treasure name cannot be empty!");
    }

    if (count <= 0) {
      throw new IllegalArgumentException("Cannot place " + count + " " + treasureName + "!");
    }

    Treasure treasure;
    try {
      treasure = Treasure.valueOf(treasureName);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Illegal treasure name " + treasureName + "!");
    }

    for (int i = 0; i < count; i++) {
      this.treasures.add(treasure);
    }
  }

  @Override
  public void removeTreasure(String treasureName) {
    ValueSanity.checkNull("Name of treasure", treasureName);
    if (treasureName.equals("")) {
      throw new IllegalArgumentException("Treasure name cannot be empty!");
    }

    if (this.treasures.size() == 0) {
      throw new IllegalArgumentException("Cannot pick treasure from an empty treasure box!");
    }

    Treasure treasure;
    try {
      treasure = Treasure.valueOf(treasureName);
    } catch (Exception e) {
      throw new IllegalArgumentException("Illegal treasure name " + treasureName + "!");
    }

    if (!this.treasures.contains(treasure)) {
      throw new IllegalArgumentException("Treasure " + treasureName
              + " not found in treasure box!");
    }

    this.treasures.remove(treasure);
  }

  @Override
  public List<String> getTreasures() {
    List<String> treasureList = new ArrayList<>();
    for (Treasure treasure : treasures) {
      treasureList.add(treasure.toString());
    }

    return treasureList;
  }

  @Override
  public String getType() {
    return "C";
  }

  @Override
  public Node cloneNode() {
    return new CaveNode(this.topNode, this.bottomNode, this.rightNode, this.leftNode,
            this.getNodeName(), new ArrayList<>(this.treasures), this.otyugh, this.numArrows,
            this.yugoloth, this.hasThief, this.hasPit);
  }

  @Override
  public void placeOtyugh(Otyugh otyugh) {
    ValueSanity.checkNull("Otyugh", otyugh);

    if (this.otyugh != null) {
      throw new IllegalArgumentException("Another Otyugh already placed in this cave!");
    }

    this.otyugh = otyugh;
  }

  @Override
  public void removeOtyugh() {
    if (this.otyugh == null) {
      throw new IllegalArgumentException("No Otyugh to remove from cave!");
    }

    this.otyugh = null;
  }

  @Override
  public Otyugh getOtyugh() {
    if (this.otyugh == null) {
      return null;
    }

    return new Otyugh(this.otyugh);
  }

  @Override
  public void setOtyughHealth(int newHealth) {
    if (this.otyugh == null) {
      throw new IllegalArgumentException("No Otyugh to update health of!");
    }

    if (newHealth < 0 || newHealth > 100) {
      throw new IllegalArgumentException("Health must be between 0 and 100!");
    }

    this.otyugh.setHealth(newHealth);
  }

  @Override
  public void addArrows(Random random) {
    ValueSanity.checkNull("Random object", random);

    this.numArrows = random.nextInt((MAX_NUM_ARROWS - 1) + 1) + 1;
  }

  @Override
  public void addSingleArrow() {
    this.numArrows++;
  }

  @Override
  public void removeArrow() {
    if (this.numArrows == 0) {
      throw new IllegalArgumentException("No arrows to remove!");
    }

    this.numArrows--;
  }

  @Override
  public int getNumArrows() {
    return this.numArrows;
  }

  @Override
  public void placeYugoloth(Yugoloth yugoloth) {
    ValueSanity.checkNull("Yugoloth", yugoloth);
    this.yugoloth = yugoloth;
  }

  @Override
  public void removeYugoloth() {
    if (this.yugoloth == null) {
      throw new IllegalArgumentException("No Yugoloth to remove from location!");
    }

    this.yugoloth = null;
  }

  @Override
  public void placeThief() {
    this.hasThief = true;
  }

  @Override
  public void addPit() {
    this.hasPit = true;
  }
}
