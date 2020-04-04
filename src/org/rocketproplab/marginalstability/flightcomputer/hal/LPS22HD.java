package org.rocketproplab.marginalstability.flightcomputer.hal;

import com.pi4j.io.i2c.I2CDevice;

public class LPS22HD implements Barometer, PollingSensor {

  public LPS22HD(I2CDevice i2cDevice) {
    
  }
  
  @Override
  public double getPressure() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean inUsableRange() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public double getLastMeasurementTime() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  public void poll() {
    
  }

}
