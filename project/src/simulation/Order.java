package simulation;

import java.util.ArrayList;
import java.util.logging.Logger;


/**
 * An order that specifies a front and a rear fascia wanted by a customer, including the colour,
 * model and sku numbers of the fascias.
 */
public class Order {

  private String[] orderInfo;
  private Logger logger = Simulation.getLogger();


  /**
   * Create a new order, with parameter info including the colour, model and sku numbers of the
   * fascias.
   */
  public Order(String info) {
    orderInfo = info.split(",");
    logger.info("System has received an order: " + orderInfo[0] + "," + orderInfo[1] + ","
        + orderInfo[2] + "," + orderInfo[3] + ".\n");
  }


  /**
   * Return the order information in a proper string representation.
   */
  public String getOrderInfo() {
    return orderInfo[0] + "," + orderInfo[1] + "," + orderInfo[2] + "," + orderInfo[3];
  }


  /**
   * Return a list of sku numbers specified in this order.
   */
  public ArrayList<String> getSkus() {
    ArrayList<String> skus = new ArrayList<>();
    skus.add(orderInfo[2]);
    skus.add(orderInfo[3]);
    return skus;
  }

}
