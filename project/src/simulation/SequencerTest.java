package simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;

public class SequencerTest {

  @Test
  public void testReceiveTask() {
    Warehouse warehouse = new Warehouse("TEST");
    Order order = new Order("White,SE,3,4");
    Queue<Order> orderList = new LinkedList<>();
    orderList.add(order);
    Sequencer sequencer = new Sequencer(warehouse, "Ezreal");
    // Test null request case.See log message.
    Request request = new Request(orderList, "req#1");
    sequencer.receiveTask(null);
    // Test receiving a request.
    sequencer.receiveTask(request);
    assertEquals(request.getId(), sequencer.reqNowHandling.getId());
    assertTrue(sequencer.ordering!=null);

  }

  @Test
  public void testWorkFindMistake() {
    Warehouse warehouse = new Warehouse("TEST");
    Order order1 = new Order("Red,S,17,18");
    Order order2 = new Order("Blue,SE,35,36");
    Order order3 = new Order("Beige,SEL,15,16");
    Order order4 = new Order("Green,SES,29,30");
    Queue<Order> orderList = new LinkedList<>();
    orderList.add(order1);
    orderList.add(order2);
    orderList.add(order3);
    orderList.add(order4);
    Request request = new Request(orderList, "Req#2");
    Pallet pallet = new Pallet(request.getId());
    pallet.load("17");
    Sequencer sequencer = new Sequencer(warehouse, "Hill");
    Picker picker = new Picker(warehouse, "Mist");
    sequencer.receiveTask(request);
    request.setPicker(picker);
    sequencer.currentWork = pallet;
    sequencer.work();
    //Sequencer should discard all fascias and reset
    assertTrue(sequencer.currentWork==null);
    assertTrue(sequencer.reqNowHandling==null);
    //The picker should go re-pick the involved request.
    assertEquals(request.getId(),picker.reqNowHandling.getId());
  }

  @Test
  public void testWorkNoMistake() {
    Order order1 = new Order("Red,S,17,18");
    Order order2 = new Order("Blue,SE,35,36");
    Order order3 = new Order("Beige,SEL,15,16");
    Order order4 = new Order("Green,SES,29,30");
    Queue<Order> orderList = new LinkedList<>();
    orderList.add(order1);
    orderList.add(order2);
    orderList.add(order3);
    orderList.add(order4);
    Request request = new Request(orderList, "req#3");
    Pallet pallet = new Pallet(request.getId());
    pallet.load("17");
    pallet.load("35");
    pallet.load("15");
    pallet.load("29");
    pallet.load("18");
    pallet.load("36");
    pallet.load("16");
    pallet.load("30");
    Warehouse warehouse = new Warehouse("TEST");
    warehouse.moveToMashaling(pallet);
    Sequencer sequencer = new Sequencer(warehouse, "Ford");
    //Test null request case for sequencer.See logger.
    sequencer.work();
    //Test properly work case.
    sequencer.receiveTask(request);
    for (int i=1; i<=3;i++){
    sequencer.work();
    assertEquals(i, sequencer.currentWork.getFrontSku().size());
    }
    for(int i=1;i<=3;i++){
      sequencer.work();
      assertEquals(i,sequencer.currentWork.getBackSku().size());
    }
 


  }
  
  @Test
  public void testRescan(){
    Order order1 = new Order("Red,S,17,18");
    Order order2 = new Order("Blue,SE,35,36");
    Order order3 = new Order("Beige,SEL,15,16");
    Order order4 = new Order("Green,SES,29,30");
    Queue<Order> orderList = new LinkedList<>();
    orderList.add(order1);
    orderList.add(order2);
    orderList.add(order3);
    orderList.add(order4);
    Request request = new Request(orderList, "req#4");
    Pallet pallet = new Pallet(request.getId());
    pallet.load("17");
    pallet.load("35");
    pallet.load("15");
    pallet.load("29");
    pallet.load("18");
    pallet.load("36");
    pallet.load("16");
    pallet.load("30");
    Warehouse warehouse = new Warehouse("TEST");
    warehouse.moveToMashaling(pallet);
    Sequencer sequencer = new Sequencer(warehouse, "Malphie");
    //Test null current work case. See log.
    sequencer.rescan();
    //Test regular rescan case. See log.
    sequencer.receiveTask(request);
    sequencer.rescan();
    assertEquals(null, sequencer.currentWork);
    assertEquals(null, sequencer.reqNowHandling);
    assertTrue(sequencer.orderingForRescan.isEmpty());
    
  }

  @Test
  public void testMoveToNextStage() {
    Warehouse warehouse = new Warehouse("TEST");
    Queue<Order> orderList = new LinkedList<>();
    Request request = new Request(orderList, "req#5");
    Pallet pallet = new Pallet(request.getId());
    warehouse.moveToMashaling(pallet);
    Sequencer sequencer = new Sequencer(warehouse, "Fisher");
    //Insert a toString test here for worker class.
    String seqInfo="Name: " + sequencer.getName() + "\nEmployed at: " + warehouse.getName();
    assertEquals(seqInfo,sequencer.toString());
    sequencer.receiveTask(request);
    sequencer.moveToNextStage();
    assertEquals(null, sequencer.currentWork);
    assertEquals(null, sequencer.reqNowHandling);
    assertTrue(sequencer.orderingForRescan.isEmpty());

  }

 

}
