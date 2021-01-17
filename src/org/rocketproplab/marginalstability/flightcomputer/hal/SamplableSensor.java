package org.rocketproplab.marginalstability.flightcomputer.hal;

public interface SamplableSensor<E> {
  public boolean hasNewData();
  public E getNewData();
  public double getLastSampleTime();
}
