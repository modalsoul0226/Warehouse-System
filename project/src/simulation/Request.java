
package simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Logger;


/**
 * A request containing information of four orders of fascias which will be picked by workers.
 */
public class Request {
  private Picker picker;
  private String requestId;
  public Queue<String> locations = new LinkedList<>();
  private ArrayList<String> correctOrder = new ArrayList<>();
  private Map<String, String> status = new HashMap<>();
  private Map<String, Order> skuDict = new HashMap<>();

  private Logger logger = Simulation.getLogger();


  /**
   * Create a new request with 4 orders of fascias.
   */
  public Request(Queue<Order> orders, String reqId) {
    requestId = reqId;
    setLocations(orders);
    setCorrectOrders(orders);
    setSkuDict(orders);
    logger.info("Request: " + requestId + " generated\n");
  }


  /**
   * Return the picker who is in charge of this request.
   */
  public Picker getPicker() {
    return picker;
  }


  /**
   * return the Id of this request.
   */
  public String getId() {
    return requestId;
  }



  public Map<String, String> getStatus() {
    return status;
  }


  /**
   * Return a list of string containing inforamtion about the next picking location.
   */
  public String[] getPickInfo() {
    String info = locations.poll();
    if (info != null) {
      String[] ans = {info.substring(0, 4), info.substring(4, info.length())};
      return ans;
    } else {
      return null;
    }
  }

  /**
   * Return the next pick info.
   */
  public String[] peekNextInfo() {
    String info = locations.peek();
    if (info != null) {
      String[] ans = {info.substring(0, 4), info.substring(4, info.length())};
      return ans;
    } else {
      return null;
    }
  }


  /**
   * Return the related order accroiding to the sku number provided.
   */
  public Order getOrder(String skuNum) {
    return skuDict.get(skuNum);
  }


  /**
   * Update the status of a fascia in the map status according to the sku number and the action
   * provided.
   */
  public void updateStatus(String skuNum, String action) {
    status.put(skuNum, action);
  }


  /**
   * Return the correct order of skus in this request.
   */
  public ArrayList<String> getCorrectOrder() {
    return correctOrder;
  }


  /**
   * Set the related picker of this request.
   */
  public void setPicker(Picker picker) {
    this.picker = picker;
  }


  /**
   * Return a list of sku numbers of all wanted fascias mentioned in the provided queue of orders.
   */
  private ArrayList<String> generateSkus(Queue<Order> orders) {
    ArrayList<String> skuList = new ArrayList<>();
    for (Order each : orders) {
      skuList.addAll(each.getSkus());
    }
    return skuList;
  }


  /**
   * Return the string representation of a worker.
   */
  private void setLocations(Queue<Order> orders) {
    ArrayList<String> locationList = WarehousePicking.optimize(generateSkus(orders));
    if (locationList == null) {
      logger.info("Locationlist null.\n");
    }
    for (String each : locationList) {
      locations.add(each);
    }
  }


  /**
   * Set the correct order of skus in correctOrder to be they are in the queue of orders orders.
   */
  private void setCorrectOrders(Queue<Order> orders) {
    ArrayList<String> lst1 = new ArrayList<>();
    ArrayList<String> lst2 = new ArrayList<>();
    for (Order each : orders) {
      lst1.add(each.getSkus().get(0));
      lst2.add(each.getSkus().get(1));
    }
    lst1.addAll(lst2);
    correctOrder = lst1;
  }


  /**
   * Map every sku number mentioned in orders to the order they belongs to in the map skuDict.
   */
  private void setSkuDict(Queue<Order> orders) {
    for (Order each : orders) {
      skuDict.put(each.getSkus().get(0), each);
      skuDict.put(each.getSkus().get(1), each);
    }
  }

  /** Return whether two requests are equal. */
  public boolean equals(Request other) {
    return other.getId().equals(requestId);
  }
}
