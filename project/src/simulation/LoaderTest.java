package simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;

public class LoaderTest {

  @Test
  public void testReceiveTaskNothingToLoad() {
    Warehouse warehouse = new Warehouse("TEST");
    Queue<Order> orderList = new LinkedList<>();
    Request request = new Request(orderList, "req#1");
    Loader loader = new Loader(warehouse, "Jayce");
    loader.receiveTask(request);
    assertEquals(loader.currentWork, null);



  }

  @Test
  public void testReceiveTaskNoWaitList() {
    Warehouse warehouse = new Warehouse("TEST");
    Queue<Order> orderList = new LinkedList<>();
    Request request = new Request(orderList, "req#2");
    Pallet pallet = new Pallet(request.getId());
    Loader loader = new Loader(warehouse, "Nancy");
    warehouse.moveToLoading(pallet);
    // Test a loader without current work receives a request.
    loader.receiveTask(request);
    assertEquals(request.getId(), loader.reqNowHandling.getId());


  }

  @Test
  public void testReceiveTaskWaitList() {
    Warehouse warehouse = new Warehouse("TEST");
    Queue<Order> orderList = new LinkedList<>();
    Request request = new Request(orderList, "req#3");
    Loader loader = new Loader(warehouse, "Harry");
    loader.receiveTask(request);
    Pallet pallet = new Pallet(request.getId());
    warehouse.moveToLoading(pallet);
    loader.receiveTask(request);
    assertTrue(pallet.getReqId() == request.getId());
  }

  @Test
  public void testReceiveTaskNotMatch() {
    Warehouse warehouse = new Warehouse("TEST");
    Queue<Order> orderList = new LinkedList<>();
    Request request1 = new Request(orderList, "req#4");
    Request request2 = new Request(orderList, "req#5");
    Pallet pallet = new Pallet(request2.getId());
    warehouse.moveToLoading(pallet);
    Loader loader = new Loader(warehouse, "Vlad");
    loader.receiveTask(request1);
    assertEquals(null, loader.currentWork);
  }

  @Test
  // Please be advised that this test causes a change to orders.csv.
  public void testWorkAndMoveToNextStage() {
    Order order1 = new Order("Red,S,17,18");
    Order order2 = new Order("Blue,SE,35,36");
    Order order3 = new Order("Beige,SEL,15,16");
    Order order4 = new Order("Green,SES,29,30");
    Queue<Order> orderList = new LinkedList<>();
    orderList.add(order1);
    orderList.add(order2);
    orderList.add(order3);
    orderList.add(order4);
    Request request = new Request(orderList, "req#6");
    Pallet pallet = new Pallet(request.getId());
    pallet.load("17", "front");
    pallet.load("35", "front");
    pallet.load("15", "front");
    pallet.load("29", "front");
    pallet.load("18", "back");
    pallet.load("36", "back");
    pallet.load("16", "back");
    pallet.load("30", "back");
    Warehouse warehouse = new Warehouse("TEST");
    warehouse.moveToLoading(pallet);
    Loader loader = new Loader(warehouse, "Frank");
    // Test case of nothing to load.
    loader.work();
    // Test work.
    loader.receiveTask(request);
    loader.work();
    File file = new File("orders.csv");
    assertTrue(file.exists() && !file.isDirectory());

    // Test moveToNextStage.
    loader.moveToNextStage();
    for (String sku : pallet.getFrontSku()) {
      assertEquals("loaded", request.getStatus().get(sku));
    }

    for (String sku : pallet.getBackSku()) {
      assertEquals("loaded", request.getStatus().get(sku));
    }
    assertEquals(null, loader.reqNowHandling);
    assertEquals(null, loader.currentWork);

  }

  @Test
  public void testRescan() {
    Queue<Order> orderList = new LinkedList<>();
    Request request = new Request(orderList, "req#7");
    Pallet pallet = new Pallet(request.getId());
    Warehouse warehouse = new Warehouse("TEST");
    Loader loader = new Loader(warehouse, "Jim");
    // Test the case for null current work
    loader.rescan();
    assertEquals(null, loader.currentWork);
    // Test the case for having current work
    warehouse.moveToLoading(pallet);
    loader.receiveTask(request);
    assertFalse(loader.currentWork == null);
    loader.rescan();
    assertTrue(loader.currentWork == null);
  }



}
