package simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;

public class PalletTest {

  @Test
  public void testGetReqId() {
    Order order1 = new Order("Beige,SEL,15,16");
    Order order2 = new Order("Beige,SEL,15,16");
    Order order3 = new Order("Beige,SEL,15,16");
    Order order4 = new Order("Beige,SEL,15,16");
    Queue<Order> orderList = new LinkedList<>();
    orderList.add(order1);
    orderList.add(order2);
    orderList.add(order3);
    orderList.add(order4);
    Request request = new Request(orderList, "req#1");
    Pallet pallet = new Pallet(request.getId());
    assertTrue(pallet.getReqId() == request.getId());

  }

  @Test
  public void testGetSku() {
    Pallet pallet = new Pallet("TEST");
    pallet.load("15", "front");
    pallet.load("16", "back");
    pallet.load("21", "front");
    pallet.load("22", "back");
    pallet.load("31", "front");
    pallet.load("32", "back");
    pallet.load("41", "front");
    pallet.load("42", "back");
    ArrayList<String> frontExpected = new ArrayList<>();
    ArrayList<String> backExpected = new ArrayList<>();
    frontExpected.add("15");
    backExpected.add("16");
    frontExpected.add("21");
    backExpected.add("22");
    frontExpected.add("31");
    backExpected.add("32");
    frontExpected.add("41");
    backExpected.add("42");
    assertEquals(frontExpected, pallet.getFrontSku());
    assertEquals(backExpected, pallet.getBackSku());

  }

  @Test
  public void testLoadFascia() {
    Pallet pallet = new Pallet("TEST");
    pallet.load("27");
    assertEquals("27", pallet.getFascias().get(0));
  }

  @Test
  public void testLoadRemove() {
    Pallet pallet = new Pallet("TEST");
    pallet.load("27");
    pallet.load("16");
    pallet.remove("27");
    assertEquals("16", pallet.getFascias().get(0));
  }
  
  @Test
  public void testRemoveAll(){
    Pallet pallet = new Pallet("TEST");
    pallet.load("15", "front");
    pallet.load("16", "back");
    assertFalse(pallet.getFrontSku().isEmpty());
    assertFalse(pallet.getBackSku().isEmpty());
    pallet.removeAll();
    assertTrue(pallet.getFrontSku().isEmpty());
    assertTrue(pallet.getBackSku().isEmpty());
  }
  /*
   * @Test public void testToString(){
   * 
   * }
   */


  @Test
  public void testEquals() {
    Order order1 = new Order("Red,S,17,18");
    Order order2 = new Order("Beige,SEL,15,16");
    Order order3 = new Order("White,S,1,2");
    Order order4 = new Order("Blue,S,33,34");
    Queue<Order> orderList = new LinkedList<>();
    orderList.add(order1);
    orderList.add(order2);
    orderList.add(order3);
    orderList.add(order4);
    Request request = new Request(orderList, "req#2");
    Pallet pallet = new Pallet(request.getId());
    // Test the case that object is not a request.
    assertFalse(pallet.equals("FALSE_TEST"));
    // Test the case that fascia list equals to the request.
    pallet.load(order1.getSkus().get(0));
    pallet.load(order2.getSkus().get(0));
    pallet.load(order3.getSkus().get(0));
    pallet.load(order4.getSkus().get(0));
    pallet.load(order1.getSkus().get(1));
    pallet.load(order2.getSkus().get(1));
    pallet.load(order3.getSkus().get(1));
    pallet.load(order4.getSkus().get(1));
    assertTrue(pallet.equals(request));
    // Test the case that fascias are loaded in correct order.
    pallet.load(order1.getSkus().get(0), "front");
    pallet.load(order2.getSkus().get(0), "front");
    pallet.load(order3.getSkus().get(0), "front");
    pallet.load(order4.getSkus().get(0), "front");
    pallet.load(order1.getSkus().get(1), "back");
    pallet.load(order2.getSkus().get(1), "back");
    pallet.load(order3.getSkus().get(1), "back");
    pallet.load(order4.getSkus().get(1), "back");
    assertTrue(pallet.equals(request));

  }


}
