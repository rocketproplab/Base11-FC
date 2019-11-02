package org.rocketproplab.marginalstability.flightcomputer;

public class Settings {

  // Flight State Settings

  /**
   * The speed we are at during our apogee period
   */
  public static double APOGEE_SPEED = 10; // m/s

  /**
   * The speed we need to be moving less than to be considered landed
   */
  public static double LANDED_SPEED = 1; // m/s

  // Parachute Deploy Settings

  /**
   * The height at which we should deploy the main chute in meters above sea
   * level
   */
  public static double MAIN_CHUTE_HEIGHT = 5000; // m

  // Unit conversions

  /**
   * Conversion constant for how many milliseconds are in a second
   */
  public static double MS_PER_SECOND = 1000; // ms/s
  
  /**
   * How far off we are allowed to be to be equal
   */
  public static double EQUALS_EPSILON = 0.00000001;
  
  /**
   * 'a' constant for pressures
   */
  public static double[] A_PT_CONSTANTS = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  
  /**
   * 'b' constant for pressures
   */
  public static double[] B_PT_CONSTANTS = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  
  /**
   * 'c' constant for pressures
   */
  public static double[] C_PT_CONSTANTS = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
}

