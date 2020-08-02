package org.rocketproplab.marginalstability.flightcomputer.comm;

public enum SCMPacketType {
  /**
   * Change the state of the valves, each index in data is a 1 or 0 that
   * Specifies valve 0-4.
   */
  V0("Valve State"), 
  
  /**
   * Change the state of the valves, each index in data is a 1 or 0 that
   * Specifies valve 5-7.
   */
  V1("Valve State"),
  
  V2("Valve State"), 
  
  V3("Valve State"), 
  
  HB("HB"), // TODO fill this packet in
  

  /**
   * Position of the GPS x coordinate. Bits are hex integer value (m)
   */
  GX("GPS Position X"),

  /**
   * Position of the GPS y coordinate. Bits are hex integer value (m)
   */
  GY("GPS Position Y"),

  /**
   * Position of the GPS z coordinate. Bits are hex integer value (m)
   */
  GZ("GPS Position Z"),

  /**
   * Computed velocity along x axis. Bits are floating point value (m/s)
   */
  VX("GPS Velocity X"),

  /**
   * Computed velocity along y axis. Bits are floating point value (m/s)
   */
  VY("GPS Velocity Y"),

  /**
   * Computed velocity along z axis. Bits are floating point value (m/s)
   */
  VZ("GPS Velocity Z"),

  /**
   * Reading of thermocouple 0. Bits are floating point value (C)
   */
  T0("Thermocouple 0"),

  /**
   * Reading of thermocouple 1. Bits are floating point value (C)
   */
  T1("Thermocouple 1"),

  /**
   * Reading of thermocouple 2. Bits are floating point value (C)
   */
  T2("Thermocouple 2"),

  /**
   * Reading of thermocouple 3. Bits are floating point value (C)
   */
  T3("Thermocouple 3"),

  /**
   * Reading of thermocouple 4. Bits are floating point value (C)
   */
  T4("Thermocouple 4"),

  /**
   * Reading of pressure transducer 0. Bits are integer value (PSI)
   */
  P0("Pressure Transducer 0"),

  /**
   * Reading of pressure transducer 1. Bits are integer value (PSI)
   */
  P1("Pressure Transducer 1"),

  /**
   * Reading of pressure transducer 2. Bits are integer value (PSI)
   */
  P2("Pressure Transducer 2"),

  /**
   * Reading of pressure transducer 3. Bits are integer value (PSI)
   */
  P3("Pressure Transducer 3"),

  /**
   * Reading of pressure transducer 4. Bits are integer value (PSI)
   */
  P4("Pressure Transducer 4"),
  
  /**
   * Reading of pressure transducer 5. Bits are integer value (PSI)
   */
  P5("Pressure Transducer 5"),
  
  /**
   * Reading of pressure transducer 6. Bits are integer value (PSI)
   */
  P6("Pressure Transducer 6"),

  /**
   * Reading of pressure transducer 7. Bits are integer value (PSI)
   */
  P7("Pressure Transducer 7"),
  
  /**
   * Reading of pressure transducer 8. Bits are integer value (PSI)
   */
  P8("Pressure Transducer 8"),
  
  /**
   * Reading of pressure transducer 9. Bits are integer value (PSI)
   */
  P9("Pressure Transducer 9"),
  
  /**
   * Reading of pressure transducer 10. Bits are integer value (PSI)
   */
  PA("Pressure Transducer 10"),
  
  /**
   * Reading of pressure transducer 11. Bits are integer value (PSI)
   */
  PB("Pressure Transducer 11"),
  
  /**
   * Reading of pressure transducer 12. Bits are integer value (PSI)
   */
  PC("Pressure Transducer 12"),
  
  /**
   * Reading of pressure transducer 13. Bits are integer value (PSI)
   */
  PD("Pressure Transducer 13"),
  
  /**
   * Reading of pressure transducer 14. Bits are integer value (PSI)
   */
  PE("Pressure Transducer 14"),
  
  /**
   * Reading of pressure transducer 15. Bits are integer value (PSI)
   */
  PF("Pressure Transducer 15"),
  
  /**
   * Error. The bits are the error code
   */
  ER("Error"),

  /**
   * Warning. The bits are the warning code
   */
  WA("Warning"), VS("VS"),
  
  /**
   * Drogue Chute Deploy
   */
  DD("Drogue Chute Deploy"),
  
  /**
   * Main Chute Deploy
   */
  MD("Main Chute Deploy");

  private String name;

  /**
   * Create a new SCMPacketType enum element with the appropriate name to print
   * when relevant
   * 
   * @param name the full name of the packet type
   */
  SCMPacketType(String name) {
    this.name = name;
  }

  /**
   * Returns the human readable name of the packet type
   * @return the human readable name of this packet type
   */
  public String getName() {
    return this.name;
  }
}
