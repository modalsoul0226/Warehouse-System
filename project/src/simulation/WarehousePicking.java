package simulation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WarehousePicking {

  /**
   * Based on the Integer SKUs in List 'skus', return a List of locations, where each location is a
   * String containing 5 pieces of information: the zone character (in the range ['A'..'B']), the
   * aisle number (an integer in the range [0..1]), the rack number (an integer in the range
   * ([0..2]), and the level on the rack (an integer in the range [0..3]), and the SKU number.
   * 
   * @param skus the list of SKUs to retrieve.
   * @return the List of locations.
   */
  public static ArrayList<String> optimize(List<String> skus) {
    // Generate a new array list to store the new location information.
    ArrayList<String> locations = new ArrayList<>();
    Scanner scanner = null;
    try {
      // Use the scanner to read the traversal table file and get the location
      // for every sku in skus.
      scanner = new Scanner(new FileReader("traversal_table.csv"));
      while (scanner.hasNext()) {
        String[] info = scanner.next().split(",");
        for (String sku : skus) {
          if (sku.equals(info[4])) {
            locations.add(info[0] + info[1] + info[2] + info[3] + info[4]);
          }
        }
      }
    } catch (FileNotFoundException exception) {
      exception.printStackTrace();
    } finally {
      scanner.close();
    }
    return locations;
  }
}
