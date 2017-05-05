package simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

public class WarehouseTest {

  @Test
  public void testGetName() {
    Warehouse test = new Warehouse("test");
    assertEquals("test", test.getName());
  }

  @Test
  public void testGetNumOfFascia() {
    Warehouse test = new Warehouse("test");
    int num = test.getNumOfFascia("A010");
    assertEquals(5, num);

  }

  @Test
  public void testGetNumOfReserve() {
    Warehouse test = new Warehouse("test");
    int num = test.getNumOfReserve("10");
    assertEquals(100, num);

  }

  @Test
  public void testGetPicked() {
    Warehouse test = new Warehouse("test");
    test.getPicked("A010");
    int num = test.getNumOfFascia("A010");
    assertEquals(4, num);
  }

  @Test
  public void testGetReplenished() {
    Warehouse test = new Warehouse("test");
    test.getReplenished("A011", "6");
    int num = test.getNumOfFascia("A011");
    assertEquals(30, num);
    assertEquals(76, test.getNumOfReserve("6"));
  }

  @Test
  public void testGetSupply() {
    Warehouse test = new Warehouse("test");
    //Test regular case.
    test.getSupply("5", 15);
    int num = test.getNumOfReserve("5");
    assertEquals(115, num);
    //Test key not exist case.
    test.getSupply("test", 27);
    int amount=test.getNumOfReserve("test");
    assertEquals(27,amount);
  }

  @Test
  public void testMarshalingArea() {
    Warehouse test = new Warehouse("test");
    Pallet pallet = new Pallet("TEST");
    test.moveToMashaling(pallet);
    Pallet check = test.nextToSequence();
    assertEquals(pallet.getReqId(), check.getReqId());
  }

  @Test
  public void testLoadingArea() {
    Warehouse test = new Warehouse("test");
    Pallet pallet = new Pallet("TEST");
    test.moveToLoading(pallet);
    Pallet check = test.nextToLoad();
    assertEquals(pallet.getReqId(), check.getReqId());
  }

  @Test
  public void testNextSequenceId() {
    Warehouse test = new Warehouse("test");
    Pallet pallet = new Pallet("TEST");
    //Test null case.
    assertTrue(test.nextSequenceId()==null);
    //Test regular case.
    test.moveToMashaling(pallet);
    String testId = test.nextSequenceId();
    assertEquals("TEST", testId);
  }


  @Test
  public void testLoadOnTruck() {
    Warehouse test = new Warehouse("test");
    ArrayList<Pallet> palletSet = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      palletSet.add(new Pallet("TEST" + i));
    }
 
    for (Pallet pallet : palletSet) {
      test.loadOnTruck(pallet);
    }
   
   assertTrue(test.getTruck().isEmpty());
  }



  @Test
  public void testSetFinal() {
    Warehouse test = new Warehouse("TEST");
    test.setFinal();
    File file = new File("Final.csv");
    assertTrue(file.exists() && !file.isDirectory());
  }


}
