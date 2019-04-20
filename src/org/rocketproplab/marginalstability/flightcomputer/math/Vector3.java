package org.rocketproplab.marginalstability.flightcomputer.math;

/**
 * A vector class
 * @author Max Apodaca
 *
 */
public class Vector3 {
  
  private double x;
  private double y;
  private double z;
  
  public Vector3(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * @return the x component
   */
  public double getX() {
    return x;
  }

  /**
   * @return the y component
   */
  public double getY() {
    return y;
  }

  /**
   * @return the z component
   */
  public double getZ() {
    return z;
  }
  
  
}
