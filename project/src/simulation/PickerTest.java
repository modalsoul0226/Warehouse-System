package simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;

public class PickerTest {

  @Test
  public void testReceivedTask() {
    Warehouse warehouse = new Warehouse("TEST");
    Queue<Order> orderList = new LinkedList<>();
    Request request = new Request(orderList, "req#1");
    Picker picker = new Picker(warehouse, "Bob");
    picker.receiveTask(request);
    assertEquals(request.getId(), picker.reqNowHandling.getId());
    picker.receiveTask(request);
    assertFalse(picker.getWaitList() == null);

  }

  @Test
  public void testWork() {
    Warehouse warehouse = new Warehouse("TEST");
    Order order = new Order("White,SE,3,4");
    Queue<Order> orderList = new LinkedList<>();
    orderList.add(order);
    Picker picker = new Picker(warehouse, "Darius");
    Picker emptyReqPicker = new Picker(warehouse, "Liz");
    // Test no request case.
    picker.work();
    // Test no picking info case.
    Queue<Order> emptyList = new LinkedList<>();
    Request emptyReq = new Request(emptyList, "req#2");
    emptyReqPicker.receiveTask(emptyReq);
    emptyReqPicker.work();
    // Test normal work case.
    Request request = new Request(orderList, "req#3");
    picker.receiveTask(request);
    picker.work();
    // Test replenishing function.
    int stock = warehouse.getNumOfFascia("A002");
    assertEquals(29, stock);
    // Test work results.
    assertTrue(request.getStatus().get("3") == "picked.\n");
    // Test move to next stage.
    picker.work();
    assertEquals(null, picker.reqNowHandling);
    assertEquals(null, picker.currentWork);
  }

  @Test
  public void testPickWrong() {
    Warehouse warehouse = new Warehouse("TEST");
    Order order = new Order("White,SE,3,4");
    Queue<Order> orderList = new LinkedList<>();
    orderList.add(order);
    Request request = new Request(orderList, "req#4");
    Picker picker = new Picker(warehouse, "Dude");
    picker.receiveTask(request);
    picker.pickWrong(1);
    // Should see logging message.
    assertTrue(picker.reqNowHandling != null);
  }

  @Test
  public void testMoveToNextStage() {
    Warehouse warehouse = new Warehouse("TEST");
    Order order = new Order("White,SE,3,4");
    Queue<Order> orderList = new LinkedList<>();
    orderList.add(order);
    Request request1 = new Request(orderList, "req#5");
    Request request2 = new Request(orderList, "req#6");
    Pallet pallet = new Pallet(request1.getId());
    Picker picker = new Picker(warehouse, "Mary");
    picker.currentWork = pallet;
    picker.receiveTask(request1);
    // Test the non-empty waitlist case.
    picker.receiveTask(request2);
    picker.moveToNextStage();
    // Test the request has been moved to marshalling area.
    assertTrue(warehouse.nextToSequence() != null);
    assertEquals(request2.getId(), picker.reqNowHandling.getId());

  }

}
