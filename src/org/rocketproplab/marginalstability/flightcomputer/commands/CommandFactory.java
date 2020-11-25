package org.rocketproplab.marginalstability.flightcomputer.commands;

import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;

public class CommandFactory {
  public static Command getCommandBySCMPacket(SCMPacket scmPacket) {
    //TODO: implement command to schedule from SCM
    return null;
  }

  public static Command getCommandByFramedSCM(String framedPacket) {
    //TODO: implement command to schedule from FramedSCM
    return null;
  }
}
