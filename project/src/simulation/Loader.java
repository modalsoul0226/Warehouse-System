package simulation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


/**
 * A loader who is in charge of picking up sequenced pallets of fascias from the loading place,
 * loading them onto the truck in the correct order, and sending the truck away.
 */
public class Loader extends Worker {

  private Queue<Request> waitList = new LinkedList<>();
  private Queue<String> loadingInfo = new LinkedList<>();
  private ArrayList<String> rescanInfo = new ArrayList<>();


  /**
   * Create a new loader, with parameters warehouse and name that specify the working place and name
   * of this loader.
   */
  public Loader(Warehouse warehouse, String name) {
    super(warehouse, name);
    logger.info("Loader " + name + " is ready for loading.");
  }


  /**
   * Receive a new loading request.
   */
  @Override
  public void receiveTask(Request request) {
    currentWork = warehouse.nextToLoad();
    if (currentWork != null) {
      setLoadingInfo();
      logger.info(
          "Loader " + name + " receives the pallet with reqId: " + currentWork.getReqId() + ".\n");
      if (waitList.isEmpty()) {
        reqNowHandling = request;
        logger.info("Loader " + name + " receives the request " + request.getId() + ".\n");
      } else {
        reqNowHandling = waitList.poll();
        waitList.add(request);
        logger.info("Loader " + name + " receives the request " + reqNowHandling.getId() + ".\n");
      }
      if (!currentWork.getReqId().equals(reqNowHandling.getId())) {
        logger.info("Loader " + name + " is waiting to load the request: " + reqNowHandling.getId()
            + "...\n");
        warehouse.moveToLoading(currentWork);
        waitList.add(reqNowHandling);
        currentWork = null;
        loadingInfo = new LinkedList<String>();
        rescanInfo = new ArrayList<String>();
      }
    } else {
      if (request != null) {
        waitList.add(request);
      }
      logger.info("Loader" + name + " has nothing to load yet.\n");
    }
  }

  /**
   * Private helper to set the loading info.
   */
  private void setLoadingInfo() {
    for (String each : currentWork.getFrontSku()) {
      loadingInfo.add(each);
      rescanInfo.add(each);
    }
    for (String each : currentWork.getBackSku()) {
      loadingInfo.add(each);
      rescanInfo.add(each);
    }
  }

  /**
   * Load the front and rear fascias in the current pallets onto the truck.
   */
  @Override
  public void work() {
    if (currentWork != null) {
      if (loadingInfo.size() > 0) {
        logger.info("Loader " + name + " has scanned the fascia " + loadingInfo.poll() + ".\n");
      }
      if (loadingInfo.size() == 0) {
        moveToNextStage();
      }
    } else {
      logger.info("Loader " + name + " has nothing to load.\n");
    }
  }


  /**
   * Clear the current work and get ready for the next request after loading all required pallets of
   * fascias onto the truck in the last request.
   */
  @Override
  public void moveToNextStage() {
    if (currentWork != null) {
      // Load all fascias onto the truck.
      for (String each : currentWork.getFrontSku()) {
        try {
          FileWriter writer = new FileWriter("orders.csv", true);
          writer.append(reqNowHandling.getOrder(each).getOrderInfo() + "\r\n");
          writer.flush();
          writer.close();
          reqNowHandling.updateStatus(each, "loaded");
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
      // Update the status of fascia.
      for (String each : currentWork.getBackSku()) {
        reqNowHandling.updateStatus(each, "loaded");
      }
      logger.info("Loader " + name + " has loaded the request " + reqNowHandling.getId() + ".\n");
      warehouse.loadOnTruck(currentWork);
    }
    currentWork = null;
    reqNowHandling = null;
    rescanInfo = new ArrayList<String>();
    logger.info("Loader " + name + " is ready for another loading request.\n");
  }


  /**
   * Rescan the fascia if the loader wishes to.
   */
  public void rescan() {
    if (currentWork != null && currentWork.equals(reqNowHandling)) {
      for (String each : rescanInfo) {
        logger.info("Loader " + name + " has rescanned the fascia " + each + "\n");
      }
      moveToNextStage();
    } else {
      logger.info("Loader " + name + " has nothing to rescan.\n");
    }
  }
}
