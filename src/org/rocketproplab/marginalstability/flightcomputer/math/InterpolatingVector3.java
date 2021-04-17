package org.rocketproplab.marginalstability.flightcomputer.math;

public interface InterpolatingVector3 {

  /**
   * Gets the interpolated vector at the given time
   *
   * @param time the time to get the vector at
   * @return the best guess vector at the given time
   */
  public Vector3 getAt(double time);
}
