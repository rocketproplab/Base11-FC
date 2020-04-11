package org.rocketproplab.marginalstability.flightcomputer.hal;

public interface IMU {

  public IMUReading getNext();
  public boolean hasNext();
  
}
