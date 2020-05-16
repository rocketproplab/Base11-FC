package org.rocketproplab.marginalstability.flightcomputer.hal;

import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;

/**
 * An IMUReading is the data read by an IMU at an instant. It consists of an
 * acceleration vector as well as a rotation difference. <br>
 * 
 * The rotation vector specified how many degrees were rotated in the last
 * timestep in the local reference frame. <br>
 * The acceleration vector is the acceleration felt by the IMU in the local
 * reference frame in meters / second^2. If the IMU is lying flat on a table it
 * would read (0, 0, 9.81).
 * 
 * @author Max Apodaca
 *
 */
public class IMUReading {

  private Vector3 acc;
  private Vector3 rotation;

  /**
   * Construct a new IMU reading to store the acceleration and rotation.
   * 
   * @param acc
   * @param rotation
   */
  public IMUReading(Vector3 acc, Vector3 rotation) {
    this.acc      = acc;
    this.rotation = rotation;
  }

  /**
   * Get the rotation delta in the given reference frame in degrees.
   * 
   * @return the difference in rotation from the last reading.
   */
  public Vector3 getXYZRotation() {
    return this.rotation;
  }

  /**
   * Get the acceleration at the given sample time in m/s^2.
   * 
   * @return the acceleration in m/s^2 as a 3 component x y z vector
   */
  public Vector3 getXYZAcceleration() {
    return this.acc;
  }

}
