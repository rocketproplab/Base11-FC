package org.rocketproplab.marginalstability.flightcomputer.math;

import org.rocketproplab.marginalstability.flightcomputer.Settings;

/**
 * A vector class
 *
 * @author Max Apodaca
 */
public class Vector3 {

  private double x;
  private double y;
  private double z;

  public Vector3() {
    this.x = 0.0;
    this.y = 0.0;
    this.z = 0.0;
  }

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

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Vector3)) {
      return false;
    }
    Vector3 otherVector = (Vector3) other;
    return (Math.abs(this.x - otherVector.x) < Settings.EQUALS_EPSILON) &&
            (Math.abs(this.y - otherVector.y) < Settings.EQUALS_EPSILON) &&
            (Math.abs(this.z - otherVector.z) < Settings.EQUALS_EPSILON);
  }

  @Override
  public String toString() {
    return "(" + this.x + ", " + this.y + ", " + this.z + ")";
  }

}
