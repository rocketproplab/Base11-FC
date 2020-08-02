package org.rocketproplab.marginalstability.flightcomputer.commands;

import org.rocketproplab.marginalstability.flightcomputer.ErrorReporter;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;
import org.rocketproplab.marginalstability.flightcomputer.hal.Valves;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;

public class SetValveCommand implements Command {
  private SCMPacket scmpacket;
  private Valves    valves;

  SetValveCommand(SCMPacket scmpacket, Valves valves) {
    this.scmpacket = scmpacket;
    this.valves    = valves;
  }

  @Override
  public boolean isDone() {
    return true;
  }

  @Override
  public void execute() {
    try {
      boolean valveState = getValveState();
      valves.setValve(getValveID(), valveState);
    } catch (IllegalArgumentException dataOnOffException) {
      ErrorReporter errorReporter = ErrorReporter.getInstance();
      String errorMsg = "Data from SCMPacket was not On or Off. Data was actually: " + scmpacket.getData();
      errorReporter.reportError(null, dataOnOffException, errorMsg);
    }
  }

  @Override
  public void start() {

  }

  @Override
  public void end() {

  }

  @Override
  public Subsystem[] getDependencies() {
    return new Subsystem[0];
  }

  private int getValveID() {
    int currentvalve = this.scmpacket.getID().ordinal();
    int initialvalve = SCMPacketType.V0.ordinal();
    int valveindex   = currentvalve - initialvalve;
    return valveindex;
  }

  private boolean getValveState() throws IllegalArgumentException {
    String scmpacketdata = this.scmpacket.getData();
    if (scmpacketdata.contains("On")) {
      return true;
    }
    if (scmpacketdata.contains("Off")) {
      return false;
    }
    throw new IllegalArgumentException("Data Not On or Off");
  }
}
