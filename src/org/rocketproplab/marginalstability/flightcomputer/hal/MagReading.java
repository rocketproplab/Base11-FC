package org.rocketproplab.marginalstability.flightcomputer.hal;

import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;

/**
 * A MagReading is the magnetometer data read by an IMU at an instant. It
 * consists of a magnetic field vector that gives us the direction and
 * strength of the magnetic field at the current location.
 * Note that the magnetic field vector from the magnetometer is in the body
 * frame (measured x, y, and z are relative to the orientation of the sensor).
 * 
 * @author Enlil Odisho
 *
 */
public class MagReading {
  
  private Vector3 magField;
  
  /**
   * Construct a new MagReading to store the magnetic field vector.
   * 
   * @param magField the magnetic field vector measured from the IMU
   */
  public MagReading(Vector3 magField) {
    this.magField = magField;
  }
  
  /**
   * @return the magnetic field vector
   */
  public Vector3 getMagneticField() {
    return this.magField;
  }
}
