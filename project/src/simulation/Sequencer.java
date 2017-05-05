package simulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


/**
 * A sequencer who picks pallets of fascias from the marshalling area and then sequence them.
 */
public class Sequencer extends Worker {

  /** The correct order for the picking. */
  Queue<String> ordering = new LinkedList<>();
  ArrayList<String> orderingForRescan = new ArrayList<>();

  /**
   * Create a new sequencer with his name and the information of the warehouse he is working in.
   * 
   * @param warehouse The warehouse this sequencer is working in.
   * @param name The nae of this sequencer.
   */
  public Sequencer(Warehouse warehouse, String name) {
    super(warehouse, name);
    logger.info("Sequencer " + name + " is ready for sequencing.\n");
  }


  /**
   * Method of receiving a new request for the sequencer.
   * 
   * @param request the new request to be received.
   */
  @Override
  public void receiveTask(Request request) {
    // Obtain the new request from the marshalling area in the warehouse.
    currentWork = warehouse.nextToSequence();
    reqNowHandling = request;
    if (reqNowHandling != null) {
      for (String each : reqNowHandling.getCorrectOrder()) {
        orderingForRescan.add(each);
        // System.out.println(each);
        ordering.add(each);
      }
      logger.info("Sequencer " + name + " receives the 8 fascia with requestID: "
          + reqNowHandling.getId() + ".\n");
    } else {
      logger.info("Nothing for sequencer " + name + " to sequence yet.\n");
    }
  }


  /**
   * Sequence the received pallets and seperate the 8 fascias into two different new pallets, one
   * for front fascias, the other one for rear ones.
   */
  @Override
  public void work() {
    // Use the equal method implemented in class Request to check if the pallet
    // received from the marshalling area is match the request,
    // if not, discard all fascias call for a repick.
    if (currentWork != null) {
      if (ordering.size() == 8 && !currentWork.equals(reqNowHandling)) {
        // for (String each : reqNowHandling.locations) {
        // System.out.println(each);
        // }
        logger.info(
            "Picker " + reqNowHandling.getPicker().getName() + " has picked the wrong fascia. "
                + "Discard all 8 fascia and sending new request...\n");
        reqNowHandling.getPicker().receiveTask(reqNowHandling);
        currentWork = null;
        reqNowHandling = null;
        ordering = new LinkedList<>();
      } else {
        // The pallet contains the right fascias.
        // Then the sequencer will obtain the correct order of fascias stored in
        // the request object.
        // Place all front fascias in a pallet accroding to the correct order.
        String skuNum = ordering.poll();
        currentWork.remove(skuNum);
        if (ordering.size() > 4) {
          currentWork.load(skuNum, "front");
          logger.info("Correct! Front fascia: " + skuNum + " is scanned and sequenced.\n");
        } else {
          // Place all rear fascias in a pallet according to the correct ordrer.
          currentWork.load(skuNum, "rear");
          logger.info("Correct! Rear fascia: " + skuNum + " is scanned and sequenced.\n");
        }
        reqNowHandling.updateStatus(skuNum, "sequenced");
        // System.out.println(ordering.size());
        if (ordering.size() == 0) {
          moveToNextStage();
        }
      }
    } else {
      logger.info("Sequencer " + name + " has nothing to sequence.\n");
    }
  }

  /**
   * Rescan the fascia with the first SKU on the sequenced pallet.
   */
  public void rescan() {
    if (currentWork != null) {
      // Remove the fascia that is loaded on the pallet and start again with the first
      // SKU in the orderingForRescan.
      currentWork.removeAll();
      for (int i = 0; i < 4; i++) {
        String skuNum = orderingForRescan.get(i);
        currentWork.load(skuNum, "front");
        logger.info("Sequencer " + name + " has rescanned and sequenced the " + "front fascia "
            + skuNum + "\n");
      }
      for (int i = 4; i < 8; i++) {
        String skuNum = orderingForRescan.get(i);
        currentWork.load(skuNum, "rear");
        logger.info("Sequencer " + name + " has rescanned and sequenced the " + "rear fascia "
            + skuNum + "\n");
      }
      moveToNextStage();
    } else {
      logger.info("Sequencer " + name + " has nothing to rescan.");
    }
  }

  /**
   * Finish current work and go to the loading area.
   */
  @Override
  public void moveToNextStage() {
    // Move to the loading area.
    if (currentWork != null) {
      warehouse.moveToLoading(currentWork);
      logger.info("Sequencer " + name + " finished sequencing and moved the request "
          + reqNowHandling.getId() + " to the loading area.\n");
      // Clear the current work after leaving fascias at the loading area.
      currentWork = null;
      reqNowHandling = null;
      orderingForRescan = new ArrayList<String>();
    }
    logger.info("Sequencer " + name + " is ready for another sequencing request.\n");
  }
}
