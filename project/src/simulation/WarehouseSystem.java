package simulation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * A warehouse system which gives instructions to workers.
 */
public class WarehouseSystem {
  private Warehouse warehouse;
  private Map<String, Request> requestDict = new HashMap<>();
  private Queue<Request> requestForPicker = new LinkedList<>();
  private Queue<Request> requestForLoader = new LinkedList<>();
  private Queue<Order> orders = new LinkedList<>();
  private Map<String, Picker> pickerDict = new HashMap<>();
  private Map<String, Sequencer> sequencerDict = new HashMap<>();
  private Map<String, Loader> loaderDict = new HashMap<>();
  private Map<String, String> translationDict = new HashMap<>();
  private Logger logger = Simulation.getLogger();

  /**
   * Create a new warehouse system.
   */
  public WarehouseSystem(Warehouse warehouse) {
    this.warehouse = warehouse;
    setTransDict("translation.csv");
  }

  public Queue<Request> getRequestForPicker() {
    return requestForPicker;
  }

  public Queue<Request> getRequestForLoader() {
    return requestForLoader;
  }

  public Map<String, Sequencer> getSequencerDict() {
    return sequencerDict;
  }

  public Map<String, Loader> getLoaderDict() {
    return loaderDict;
  }

  public Queue<Order> getOrders() {
    return orders;
  }

  /**
   * Receive an order from a customer, which is in form of a String read from a FAX paper in the
   * real world.
   */
  public void receiveOrder(String carInfo) {
    // Use the translation dictionary to get the sku numbers contained in
    // this order.
    String skus = translationDict.get(carInfo);
    orders.add(new Order(carInfo + "," + skus));
    // if there are four orders already (including this new one), use them
    // to
    // generate a new picking request, and delete them in the Queue orders.
    if (orders.size() == 4) {
      String reqId = setRequestId();
      Request request = new Request(orders, reqId);
      requestDict.put(request.getId(), request);
      requestForPicker.add(new Request(orders, reqId));
      requestForLoader.add(new Request(orders, reqId));
      orders.clear();
    }
  }

  /**
   * Get a worker ready and receive a request according to the name provided and his duty indicated
   * by parameter job.
   * 
   * @param name the name of the worker
   * @param job the job of the worker
   * 
   */
  public void getWorkerReady(String name, String job) {
    switch (job) {
      case "Picker":
        // when the picker is in the picker dictionary
        Request request = requestForPicker.poll();
        if (!pickerDict.containsKey(name)) {
          // obtain a request from the request list
          Picker picker = new Picker(warehouse, name);
          pickerDict.put(name, picker);
        }
        // get the worker from the dictionary according to the name
        Picker picker = pickerDict.get(name);
        if (request != null) {
          // link this worker to this request and get him ready to work
          logger.info("Picker " + name + " is ready.\n");
          request.setPicker(picker);
          for (String key : requestDict.keySet()) {
            if (key.equals(request.getId())) {
              Request req = requestDict.get(key);
              req.setPicker(picker);
              requestDict.put(key, req);
            }
          }
          for (Request each : requestForLoader) {
            if (request.equals(each)) {
              each.setPicker(picker);
            }
          }
          picker.receiveTask(request);
        } else {
          // If the picker is not in the dictionary.
          logger.info("System Message: No request is generated now.\n");
        }
        break;
      case "Sequencer":
        // the same as in case "Picker"
        if (!sequencerDict.containsKey(name)) {
          Sequencer sequencer = new Sequencer(warehouse, name);
          sequencerDict.put(name, sequencer);
        }
        Sequencer sequencer = sequencerDict.get(name);
        logger.info("Sequencer " + name + " is ready.\n");
        sequencer.receiveTask(requestDict.get(warehouse.nextSequenceId()));
        break;
      case "Loader":
        // the same as in case "Picker"
        if (!loaderDict.containsKey(name)) {
          Loader loader = new Loader(warehouse, name);
          loaderDict.put(name, loader);
        }
        Loader loader = loaderDict.get(name);
        logger.info("Loader " + name + " is ready.\n");
        loader.receiveTask(requestForLoader.poll());
        break;
      default:
        break;
    }
  }

  /**
   * Let the picker with provided name go to pick the next fascia, or tell the picker to go to the
   * marshalling area if he has finished picking all fascias in the current request.
   * 
   */
  public void goPick(String name, int mistake) {
    Picker worker = pickerDict.get(name);
    if (worker == null) {
      logger.info("Worker " + name + " doesn't call ready for work.\n");
    } else if (mistake == 0) {
      worker.work();
      // System.out.print("work");
    } else {
      worker.pickWrong(mistake);
    }
  }

  /**
   * Let the sequencerer with provided name go to sequence the pallet of fascias he got, or tell the
   * sequener to go to the loading area if he has finished sequencing all fascias in the current
   * request.
   * 
   */
  public void goSequence(String name, boolean rescan) {
    Sequencer worker = sequencerDict.get(name);
    if (worker == null) {
      logger.info("Worker " + name + " doesn't call ready for work.\n");
    } else if (rescan == true) {
      worker.rescan();
    } else {
      worker.work();
    }
  }

  /**
   * Let the loader with provided name go to load the pallets of fascias onto the truck, or tell the
   * loader to wait for another loading request if he has finished loading all fascias in the
   * current request.
   * 
   */
  public void goLoad(String name, boolean rescan) {
    Loader worker = loaderDict.get(name);
    if (worker == null) {
      logger.info("Worker " + name + " doesn't call ready for work.\n");
    } else if (rescan == true) {
      worker.rescan();
    } else {
      worker.work();
    }
  }

  /**
   * Scan the translation table in the file with the provided fileName, and then set up the
   * translation dictionary in the system to have the same content as in the file.
   * 
   */
  private void setTransDict(String fileName) {
    Scanner scanner = null;
    try {
      scanner = new Scanner(new FileReader(fileName));
      scanner.next();
      scanner.next();
      scanner.next();
      while (scanner.hasNext()) {
        String[] info = scanner.next().split(",");
        translationDict.put(info[0] + "," + info[1], info[2] + "," + info[3]);
      }
    } catch (FileNotFoundException exception) {
      exception.printStackTrace();
    } finally {
      scanner.close();
    }
  }

  /**
   * Generate a random request id for this request.
   */
  private String setRequestId() {
    String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    Random random = new Random();
    String ans = "";
    for (int i = 0; i < 7; i++) {
      int num = random.nextInt(base.length());
      ans += base.charAt(num);
    }
    return ans;
  }
}
