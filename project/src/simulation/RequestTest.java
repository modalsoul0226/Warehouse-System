package simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;

public class RequestTest {

  @Test
  public void testSetAndGetPicker() {
    Warehouse warehouse = new Warehouse("TEST");
    Queue<Order> orderList = new LinkedList<>();
    Request request = new Request(orderList, "req#1");
    Picker picker = new Picker(warehouse, "Kevin");
    request.setPicker(picker);
    assertEquals("Kevin", request.getPicker().getName());

  }

  @Test
  public void testGetPickInfo() {
    // Test the null case
    Queue<Order> orderList = new LinkedList<>();
    Request request = new Request(orderList, "req#2");
    assertTrue(request.getPickInfo() == null);
    // Test the correctness of returning picking info.
    Order order = new Order("Red,S,17,18");
    orderList.add(order);
    request = new Request(orderList, "req#3");
    String[] info = request.getPickInfo();
    assertEquals("A110", info[0]);
    assertEquals("17", info[1]);

  }

  @Test
  public void testGetOrder() {
    Order order = new Order("Red,S,17,18");
    Queue<Order> orderList = new LinkedList<>();
    orderList.add(order);
    Request request = new Request(orderList, "req#4");
    assertEquals("Red,S,17,18", request.getOrder("17").getOrderInfo());
  }

  @Test
  public void testGetCorrectOrder() {
    Order order1 = new Order("Red,S,17,18");
    Order order2 = new Order("Blue,SE,35,36");
    Queue<Order> orderList = new LinkedList<>();
    orderList.add(order1);
    orderList.add(order2);
    Request request = new Request(orderList, "req#5");
    ArrayList<String> correctOrder = request.getCorrectOrder();
    assertEquals("17", correctOrder.get(0));
    assertEquals("35", correctOrder.get(1));
    assertEquals("18", correctOrder.get(2));
    assertEquals("36", correctOrder.get(3));
  }

  @Test
  public void testUpdateStatus() {
    Queue<Order> orderList = new LinkedList<>();
    Request request = new Request(orderList, "req#6");
    request.updateStatus("27", "Picking");
    assertEquals("Picking", request.getStatus().get("27"));
  }

}
