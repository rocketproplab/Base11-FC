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

  /**
   * The pressure at which we should deploy the main chute
   */
  public static double MAIN_CHUTE_PRESSURE = 0; // TODO: set main chute pressure

  /**
   * Time threshold needed to exceed to deploy the main chute
   */
  public static double MAIN_CHUTE_PRESSURE_TIME_THRESHOLD = 10; // TODO: set time exceeding the threshold needed to deploy main chute

  // Unit conversions

  /**
   * Conversion constant for how many milliseconds are in a second
   */
  public static double MS_PER_SECOND = 1000; // ms/s

  /**
   * Threshold for periodic heart beat signal
   */
  public static double HEARTBEAT_THRESHOLD = 1; // s

  /**
   * How far off we are allowed to be to be equal
   */
  public static double EQUALS_EPSILON = 0.00000001;

  /**
   * 'a' constant for pressures
   */
  public static double[] A_PT_CONSTANTS = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
      0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

  /**
   * 'b' constant for pressures
   */
  public static double[] B_PT_CONSTANTS = { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
      1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };

  /**
   * 'c' constant for pressures
   */
  public static double[] C_PT_CONSTANTS = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
      0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
}
