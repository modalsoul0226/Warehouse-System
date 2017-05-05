package simulation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class OrderTest {

  @Test
  public void testGetOrderInfo() {

    Order test = new Order("White,s,12,21");
    assertEquals("White,s,12,21", test.getOrderInfo());
 
  }

  @Test
  public void testGetSkus() {
    Order test = new Order("White,s,12,21");
    ArrayList<String> sku = new ArrayList<String>();
    sku.add("12");
    sku.add("21");
    assertEquals(sku, test.getSkus());
  }

}
