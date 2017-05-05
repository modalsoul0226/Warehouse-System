package simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;



/**
 * A simulation for events happening in the warehouse.
 */
public class Simulation {

  private WarehouseSystem ws;
  private static Handler handler;
  private static Logger logger = Logger.getLogger("workerLogger");


  /**
   * Create a new simulation.
   */
  public Simulation(WarehouseSystem ws) {
    this.ws = ws;

    File filePath = new File("systemLog.txt");
    try {
      if (!filePath.exists()) {
        filePath.createNewFile();
      }
      Simulation.handler = new FileHandler("systemLog.txt");
      logger.addHandler(handler);
      SimpleFormatter formatter = new SimpleFormatter();
      Simulation.handler.setFormatter(formatter);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }


  /**
   * Start a new simulation according to the provided file.
   */
  public void startSimulation(String fileName) {
    Scanner scanner = null;
    try {
      scanner = new Scanner(new FileReader(fileName));
      while (scanner.hasNext()) {
        String prompt = scanner.nextLine();
        logger.info("INPUT: " + prompt + "\n");
        executeLine(prompt.split(" "));
      }
    } catch (FileNotFoundException exception) {
      exception.printStackTrace();
    } finally {
      scanner.close();
    }
  }


  /**
   * Read the file line by line to give different instructions to workers by calling methods in the
   * warehouse system.
   */
  private void executeLine(String[] prompt) {
    String title = prompt[0];
    String name = prompt[1];
    String action = prompt[2];
    // new order coming in.
    if (title.equals("Order")) {
      ws.receiveOrder(prompt[2] + "," + prompt[1]);
    } else if (title.equals("Picker")) {
      // instructions for picker.
      if (action.equals("ready")) {
        ws.getWorkerReady(name, title);
      } else {
        if (prompt[3].equals("wrong")) {
          ws.goPick(name, Integer.valueOf((prompt[4])));
        } else {
          ws.goPick(name, 0);
        }
      }
    } else if (title.equals("Sequencer")) {
      // instructions for sequencer
      if (action.equals("ready")) {
        ws.getWorkerReady(name, title);
      } else {
        ws.goSequence(name, action.equals("rescans") | action.equals("rescan"));
      }
    } else if (title.equals("Loader")) {
      // instructions for loader.
      if (action.equals("ready")) {
        ws.getWorkerReady(name, title);
      } else {
        ws.goLoad(name, action.equals("rescans") | action.equals("rescan"));
      }
    }
  }

  /**
   * Start the simulation. (with one warehouse initially)
   */
  public static void main(String[] args) {
    Warehouse warehouse = new Warehouse("Warehouse #1");
    WarehouseSystem ws = new WarehouseSystem(warehouse);
    Simulation experiment = new Simulation(ws);
    experiment.startSimulation(args[0]);
    warehouse.setFinal();
  }


  public static Handler getHandler() {
    return handler;
  }


  public static void setHandler(Handler handler) {
    Simulation.handler = handler;
  }


  public static Logger getLogger() {
    return logger;
  }


  public static void setLogger(Logger logger) {
    Simulation.logger = logger;
  }
}
