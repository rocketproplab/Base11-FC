package org.rocketproplab.marginalstability.flightcomputer;

public enum Errors {

  UNKNOWN_ERROR("Unexpected error occured!"),
  TOP_LEVEL_EXCEPTION("Exception in main loop occured"), 
  IMU_IO_ERROR("Unable to read IMU over SPI"),
  MAX14830_IO_ERROR("Unable to access /dev/spix.x via Pi4J"),
  LPS22HD_INITIALIZATION_ERROR("Unable to write from i2cDevice IO Exception"),
  LPS22HD_PRESSURE_IO_ERROR("Unable to read Pressure from i2cDevice IO Exception");
  
  private String errorMessage;
  
  /**
   * Sets the error message in the error
   * @param errorMessage
   */
  Errors(String errorMessage) {
    this.errorMessage = errorMessage;
  }
  
  @Override
  public String toString() {
    return this.errorMessage;
  }
  
}
