package org.rocketproplab.marginalstability.flightcomputer.events;

public interface EngineEventListener {

  /**
   * Called when the engine is turned on
   */
  public void onEngineActivation();

  /**
   * Called when the engine is turned off
   */
  public void onEngineShutdown();

  /**
   * Called when new engine data is received
   *
   * @param type  the type of data which was received
   * @param value the value of the data which was received
   */
  public void onEngineData(EngineDataType type, double value);

  /**
   * The different types of data which relate to the engine
   *
   * @author Max Apodaca
   */
  public enum EngineDataType {
    /**
     * Temperature of the engine bell
     */
    Temperature
  }

  ;

}
