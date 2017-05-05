package simulation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({LoaderTest.class, OrderTest.class, PalletTest.class, PickerTest.class,
    RequestTest.class, SequencerTest.class, WarehouseSystemTest.class, WarehouseTest.class, SimulationTest.class})
public class AllTests {

}
