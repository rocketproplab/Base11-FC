package org.rocketproplab.marginalstability.flightcomputer.comm;

public enum SCMPacketType {
  /**
   * Change the state of the valves, each index in data is a 1 or 0 that
   * Specifies valve 0-4.
   */
  VS,
  HB,
  
  /**
   * Position of the GPS x coordinate. Bits are hex integer value (m)
   */
  GX,
  
  /**
   * Position of the GPS y coordinate. Bits are hex integer value (m)
   */
  GY,
  
  /**
   * Position of the GPS z coordinate. Bits are hex integer value (m)
   */
  GZ,
  
  /**
   * Computed velocity along x axis. Bits are floating point value (m/s)
   */
  VX,
  
  /**
   * Computed velocity along y axis. Bits are floating point value (m/s)
   */
  VY,
  
  /**
   * Computed velocity along z axis. Bits are floating point value (m/s)
   */
  VZ,
  
  /**
   * Reading of thermocouple 0. Bits are floating point value (C)
   */
  T0,
  
  /**
   * Reading of thermocouple 1. Bits are floating point value (C)
   */
  T1,
  
  /**
   * Reading of thermocouple 2. Bits are floating point value (C)
   */
  T2,
  
  /**
   * Reading of thermocouple 3. Bits are floating point value (C)
   */
  T3,
  
  /**
   * Reading of thermocouple 4. Bits are floating point value (C)
   */
  T4,
  
  /**
   * Reading of pressure transducer 0. Bits are integer value (PSI)
   */
  P0,
  
  /**
   * Reading of pressure transducer 1. Bits are integer value (PSI)
   */
  P1,
  
  /**
   * Reading of pressure transducer 2. Bits are integer value (PSI)
   */
  P2,
  
  /**
   * Reading of pressure transducer 3. Bits are integer value (PSI)
   */
  P3,
  
  /**
   * Reading of pressure transducer 4. Bits are integer value (PSI)
   */
  P4,
  
  /**
   * Error. The bits are the error code
   */
  ER,
  
  /**
   * Warning. The bits are the warning code
   */
  WA;
}
