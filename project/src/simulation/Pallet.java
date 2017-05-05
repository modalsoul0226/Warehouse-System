package simulation;

import java.util.ArrayList;

/**
 * A pallet containing the fascia.
 */
public class Pallet {

  private String requestId;
  private ArrayList<String> fascias = new ArrayList<>();
  private ArrayList<String> front = new ArrayList<>();
  private ArrayList<String> back = new ArrayList<>();

  /** One pallet can only contain one requestID i.e. 8 fascia. */
  public Pallet(String requestId) {
    this.requestId = requestId;
  }


  /**
   * Return the id of the request related to this pallet of fascias.
   */
  public String getReqId() {
    return requestId;
  }


  /**
   * Return the list of sku numbers of all sequenced front fascias in this pallet, if the fascias
   * have been sequenced, otherwise return an empty list.
   */
  public ArrayList<String> getFrontSku() {
    return front;
  }


  /**
   * Return the list of sku numbers of all sequenced rear fascias in this pallet, if the fascias
   * have been sequenced, otherwise return an empty list.
   */
  public ArrayList<String> getBackSku() {
    return back;
  }



  public ArrayList<String> getFascias() {
    return fascias;
  }


  /**
   * Remove all the fascia on this pallet.
   */
  public void removeAll() {
    fascias = new ArrayList<String>();
    front = new ArrayList<String>();
    back = new ArrayList<String>();
  }



  /**
   * Add a new fascia to the pallet.
   */

  public void load(String skuNum) {
    fascias.add(skuNum);
  }


  /**
   * Add a new fascia to the pallet of sequenced front fascias, or the pallet of sequenced rear
   * fascias, according to the parameter frontOrBack.
   */
  public void load(String skuNum, String frontOrBack) {
    if (frontOrBack.equals("front")) {
      front.add(skuNum);
    } else {
      back.add(skuNum);
    }
  }


  /**
   * Remove a fascia from the pallet, according to the sku number provided.
   */
  public void remove(String skuNum) {
    fascias.remove(skuNum);
  }



  /**
   * Return true if Object other is a request, and has exactly the same fascias in the request as
   * those in this pallet.
   */
  @Override
  public boolean equals(Object other) {
    if (other instanceof Request) {
      ArrayList<String> orders = ((Request) other).getCorrectOrder();
      if (front.isEmpty()) {
        return haveSameFascia(orders);
      } else {
        ArrayList<String> currOrder = new ArrayList<>();
        currOrder.addAll(front);
        currOrder.addAll(back);
        return currOrder.equals(orders);
      }
    } else {
      return false;
    }
  }


  /**
   * Return true if the arraylist orders contains exactly the same fascias as those in this pallet.
   */
  private boolean haveSameFascia(ArrayList<String> orders) {
    ArrayList<String> fasciaList1 = new ArrayList<>();
    ArrayList<String> fasciaList2 = new ArrayList<>();
    for (String each : fascias) {
      fasciaList1.add(each);
    }
    for (String each : orders) {
      fasciaList2.add(each);
    }
    for (String i : fascias) {
      for (String j : orders) {
        if (i.equals(j)) {
          fasciaList1.remove(i);
          fasciaList2.remove(i);
        }
      }
    }
    return fasciaList1.isEmpty() && fasciaList2.isEmpty();
  }
}
