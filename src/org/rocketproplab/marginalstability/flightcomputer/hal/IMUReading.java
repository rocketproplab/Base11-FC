package org.rocketproplab.marginalstability.flightcomputer.hal;

import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;

public class IMUReading {
  
  private Vector3 acc;
  private Vector3 rotation;
  
  public IMUReading(Vector3 acc, Vector3 rotation) {
    this.acc = acc;
    this.rotation = rotation;
  }

  public Vector3 getXYZRotation() {
    return this.rotation;
  }
  
  public Vector3 getXYZAcceleration() {
    return this.acc;
  }
  
}
