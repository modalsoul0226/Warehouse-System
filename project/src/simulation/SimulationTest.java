package simulation;

import org.junit.Test;

public class SimulationTest {

  @Test
  public void testStartSimulation() {
    Warehouse warehouse = new Warehouse("Warehouse #1");
    WarehouseSystem ws = new WarehouseSystem(warehouse);
    Simulation experiment = new Simulation(ws);
    experiment.startSimulation("16orders.txt");
    //See log info. 
  }

}
