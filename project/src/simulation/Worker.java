package simulation;

import java.util.logging.Logger;

/**
 * An abstract class Worker.
 */
public abstract class Worker {
  String name;
  Pallet currentWork;
  Request reqNowHandling;
  Warehouse warehouse;
  Logger logger = Simulation.getLogger();


  /**
   * Create a new worker.
   */
  public Worker(Warehouse warehouse, String name) {
    this.name = name;
    this.warehouse = warehouse;
  }


  /**
   * Receive a new request.
   */
  public abstract void receiveTask(Request request);


  /**
   * Go to work.
   */
  public abstract void work();


  /**
   * Handover the current finished work and call ready for a new request.
   */
  public abstract void moveToNextStage();


  /**
   * Return the name of the worker.
   */
  public String getName() {
    return name;
  }


  /**
   * The reqNowHandling.
   */
  public Request getReqNowHandling() {
    return reqNowHandling;
  }


  /**
   * Return the string representation of a worker.
   */
  public String toString() {
    return "Name: " + name + "\nEmployed at: " + warehouse.getName();
  }
}
