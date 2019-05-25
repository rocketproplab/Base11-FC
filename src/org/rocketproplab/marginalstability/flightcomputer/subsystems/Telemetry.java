package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.rocketproplab.marginalstability.flightcomputer.Errors;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;

/**
 * A reporter for telemetry. It will sends the packet periodically to the
 * Command Box with the information given.
 * 
 * @author Max Apodaca
 *
 */
public class Telemetry {

  public void reportTelemetry(SCMPacketType type, double data) {
    
  }
  
  public void reportTelemetry(SCMPacketType type, int data, int radix) {
    
  }
  
  public void reportTelemetry(SCMPacketType type, int data) {

  }
  
  public void reportError(Errors error) {
    
  }

}
