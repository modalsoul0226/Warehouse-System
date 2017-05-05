/**
 * 
 */

package simulation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * A warehouse consists of the floor, reserve room, marshaling area and loading area.
 *
 */
public class Warehouse {

  /** The name of the warehouse. */
  private String name;

  /** Contains pallets of fascia waiting to be sequenced. */
  private Queue<Pallet> marshalingArea = new LinkedList<>();

  /** Contains pallets of fascia waiting to be loaded. */
  private Queue<Pallet> loadingArea = new LinkedList<>();

  /**
   * Contains racks of fascia. Keys are strings of locations and values are the number of fascia at
   * that location.
   */
  private Map<String, Integer> floorStack = new HashMap<>();

  /**
   * Contains the reserved fascia which is ready to get replenished to the floor. Keys are SKUs of
   * fascia and values are the number of that kind of fascia.
   */
  private Map<String, Integer> reserveStack = new HashMap<>();

  private Queue<Pallet> truck = new LinkedList<>();

  private Logger logger = Simulation.getLogger();

  /**
   * Initiate a warehouse.
   */
  public Warehouse(String name) {
    this.name = name;
    setFloorStack("initial.csv");
    setReserveStack(48);
  }


  /**
   * Return the name of the warehouse.
   */
  public String getName() {
    return name;
  }


  /**
   * Reduce the number of fascias by 1 at the given location.
   *
   */
  public void getPicked(String location) {
    floorStack.put(location, floorStack.get(location) - 1);
  }


  /**
   * Get the truck.
   */
  public Queue<Pallet> getTruck() {
    return truck;
  }


  /**
   * Return the current number of fascias at the given location.
   *
   */
  public int getNumOfFascia(String location) {
    return floorStack.get(location);
  }

  /**
   * Return the current number of fascias reserved.
   */
  public int getNumOfReserve(String sku) {
    return reserveStack.get(sku);
  }

  /**
   * Replenish the number of fascias to 30 at the given location.
   *
   */
  public void getReplenished(String location, String skuNum) {
    int increment = 30 - floorStack.get(location);
    floorStack.put(location, 30);
    logger.info("Location " + location + " has been replenished.\n");
    reserveStack.put(skuNum, reserveStack.get(skuNum) - increment);
  }


  /**
   * Receive the supply of fascias and add them into the reserve stack.
   *
   */
  public void getSupply(String skuNum, int numFascia) {
    // Avoid NullPointerException.
    if (reserveStack.get(skuNum) != null) {
      reserveStack.put(skuNum, numFascia + reserveStack.get(skuNum));
    } else {
      reserveStack.put(skuNum, numFascia);
    }
  }

  /** Produce the final.csv */
  public void setFinal() {
    try {
      FileWriter writer = new FileWriter("final.csv", true);
      for (String each : floorStack.keySet()) {
        if (floorStack.get(each) < 30) {
          String location = each.substring(0, 1) + "," + each.substring(1, 2) + ","
              + each.substring(2, 3) + "," + each.substring(3, 4);
          writer.append(location + "," + Integer.toString(floorStack.get(each)) + "\r\n");
        }
      }
      writer.flush();
      writer.close();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }



  /**
   * Add a pallet from a picker to the marshalling area stack.
   *
   */
  public void moveToMashaling(Pallet pallet) {
    marshalingArea.add(pallet);
  }


  /**
   * Add the pallets from a sequencer to the loading area stack.
   *
   */
  public void moveToLoading(Pallet pallet) {
    loadingArea.add(pallet);
  }


  /**
   * Pop the next pallet of fascias to be sequenced to a sequencer.
   *
   */
  public Pallet nextToSequence() {
    return marshalingArea.poll();

  }


  /**
   * Return the related request ID of the next pallet of fascias to be sequenced in the marshalling
   * area.
   */
  public String nextSequenceId() {
    Pallet nextPallet = marshalingArea.peek();
    if (nextPallet != null) {
      return marshalingArea.peek().getReqId();
    }
    return null;
  }


  /**
   * Pop the next pallets of fascias to be loaded to a loader.
   *
   */
  public Pallet nextToLoad() {
    return loadingArea.poll();

  }


  /**
   * Load a pallet of fascias onto the truck.
   *
   */
  public void loadOnTruck(Pallet pallet) {
    truck.add(pallet);
    if (truck.size() == 20) {
      logger.info(
          "80 orders are loaded on the truck\n " + "Truck will leave for the automotive factory.");
      truck.clear();
    }
  }



  /**
   * Scan the provided file and set up the floor stack.
   *
   */
  private void setFloorStack(String fileName) {
    Scanner scanner = null;
    try {
      scanner = new Scanner(new FileReader(fileName));
      while (scanner.hasNext()) {
        String[] info = scanner.next().split(",");
        floorStack.put(info[0] + info[1] + info[2] + info[3], Integer.valueOf(info[4]));
      }
    } catch (FileNotFoundException exception) {
      exception.printStackTrace();
    } finally {
      scanner.close();
      setFloorLeft();
    }
  }

  /**
   * Set the racks left that have 30 fascia.
   */
  private void setFloorLeft() {
    Scanner scanner = null;
    try {
      scanner = new Scanner(new FileReader("traversal_table.csv"));
      while (scanner.hasNext()) {
        String[] info = scanner.next().split(",");
        String location = info[0] + info[1] + info[2] + info[3];
        if (!floorStack.containsKey(location)) {
          floorStack.put(location, 30);
        }
      }
    } catch (FileNotFoundException exception) {
      exception.printStackTrace();
    } finally {
      scanner.close();
    }
  }


  /**
   * Set all fascias in reserve stack to be of amount 100.
   *
   */
  private void setReserveStack(int amountOfSkus) {
    for (int i = 1; i <= amountOfSkus; i++) {
      reserveStack.put(Integer.toString(i), 100);
    }
  }
}
