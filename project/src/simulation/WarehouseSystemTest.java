package simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class WarehouseSystemTest {

	@Test
	public void testReceiveOneOrder() throws SecurityException, IOException {
		Warehouse warehouse = new Warehouse("TEST");
		WarehouseSystem test = new WarehouseSystem(warehouse);
		test.receiveOrder("Beige,SEL");
		String orderInfo = test.getOrders().peek().getOrderInfo();
		String check = "Beige,SEL,15,16";
		assertEquals(check, orderInfo);

	}

	@Test
	public void testReceiveFourOrder() throws SecurityException, IOException {
		Warehouse warehouse = new Warehouse("TEST");
		WarehouseSystem test = new WarehouseSystem(warehouse);
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		assertFalse(test.getRequestForLoader().isEmpty());
		assertFalse(test.getRequestForPicker().isEmpty());
		assertTrue(test.getOrders().isEmpty());
	}

	@Test
	public void testGetPickerReadyWithRequest() throws SecurityException, IOException {
		Warehouse warehouse = new Warehouse("TEST");
		WarehouseSystem test = new WarehouseSystem(warehouse);
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		Request request = test.getRequestForPicker().peek();
		test.getWorkerReady("Alice", "Picker");
		String pickerName = request.getPicker().getName();
		assertTrue(pickerName == "Alice");
	}

	@Test
	public void testGetPickerReadyWithoutRequest(){
		Warehouse warehouse = new Warehouse("TEST");
		WarehouseSystem test = new WarehouseSystem(warehouse);
		test.getWorkerReady("Alice", "Picker");
		//Test default case
		test.getWorkerReady("", "");
		//Test get picker ready with no request. See log.
		Picker picker=new Picker(warehouse,"Alice");
		assertEquals(null,picker.reqNowHandling);
	}

	@Test
	public void testGetSequencerReady() throws SecurityException, IOException {
		Warehouse warehouse = new Warehouse("TEST");
		WarehouseSystem test = new WarehouseSystem(warehouse);
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		Request request = test.getRequestForPicker().poll();
		Pallet pallet = new Pallet(request.getId());
		warehouse.moveToMashaling(pallet);
		test.getWorkerReady("Bob", "Sequencer");
		String bobReqId = test.getSequencerDict().get("Bob").getReqNowHandling().getId();
		assertTrue(request.getId() == bobReqId);
		// Testing the case of an existing sequencer.
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		request = test.getRequestForPicker().poll();
		pallet = new Pallet(request.getId());
		warehouse.moveToMashaling(pallet);
		test.getWorkerReady("Bob", "Sequencer");
		bobReqId = test.getSequencerDict().get("Bob").getReqNowHandling().getId();
		assertTrue(request.getId() == bobReqId);
	}

	@Test
	public void testGetLoaderReady() throws SecurityException, IOException {
		Warehouse warehouse = new Warehouse("TEST");
		WarehouseSystem test = new WarehouseSystem(warehouse);
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		Request request = test.getRequestForLoader().peek();
		Pallet pallet = new Pallet(request.getId());
		warehouse.moveToLoading(pallet);
		test.getWorkerReady("Cassi", "Loader");
		String cassiReqId = test.getLoaderDict().get("Cassi").getReqNowHandling().getId();
		assertTrue(request.getId() == cassiReqId);
		// Testing the case of an existing loader.
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		request = test.getRequestForLoader().peek();
		pallet = new Pallet(request.getId());
		warehouse.moveToLoading(pallet);
		test.getWorkerReady("Cassi", "Loader");
		cassiReqId = test.getLoaderDict().get("Cassi").getReqNowHandling().getId();
		assertTrue(request.getId() == cassiReqId);
	}

//	@Test
//	public void testGoPickToMarshaling() throws SecurityException, IOException {
//		Warehouse warehouse = new Warehouse("TEST");
//		WarehouseSystem test = new WarehouseSystem(warehouse);
//		test.receiveOrder("White,SE");
//		test.receiveOrder("White,SE");
//		test.receiveOrder("White,SE");
//		test.receiveOrder("White,SE");
//		test.getWorkerReady("Nathan", "Picker");
//		//Test the null worker case.
//		test.goPick("", 0);
//		//Test the regular case.
//		test.goPick("Nathan", 0);
//		assertEquals(3, warehouse.getNumOfFascia("A002"));
//	}

	@Test
	public void testGoSequence(){
		Warehouse warehouse = new Warehouse("TEST");
		WarehouseSystem test = new WarehouseSystem(warehouse);
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		test.receiveOrder("Beige,SEL");
		Request request = test.getRequestForPicker().peek();
		Pallet pallet = new Pallet(request.getId());
		warehouse.moveToMashaling(pallet);
		test.getWorkerReady("Kayle", "Sequencer");
		//Test the non-ready worker case. See log.
		test.goSequence("Someone not exists", true);
		//Test the rescan case.
		test.goSequence("Kayle", true);
		Sequencer sequencer=test.getSequencerDict().get("Kayle");
		assertEquals(null, sequencer.currentWork);
		assertEquals(null, sequencer.reqNowHandling);
		assertTrue(sequencer.orderingForRescan.isEmpty());
		//Test the work case. See log(Have nothing to sequence.).
		test.goSequence("Kayle", false);
		assertEquals(null, sequencer.currentWork);
		
		

	}

	@Test
	public void testGoLoad() throws SecurityException, IOException {
		Warehouse warehouse = new Warehouse("TEST");
		WarehouseSystem test = new WarehouseSystem(warehouse);
		test.getWorkerReady("Diana", "Loader");
		Loader loader=test.getLoaderDict().get("Diana");
		//Test the non-ready loader case. See log.
		test.goLoad("Someone not exists", true);
		//Test the rescan case.
		test.goLoad("Diana", true);
		//Test the work case.
		assertEquals(null, loader.currentWork);
		assertEquals(null, loader.reqNowHandling);

	}

}
