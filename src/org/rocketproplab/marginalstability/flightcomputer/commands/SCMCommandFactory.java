package org.rocketproplab.marginalstability.flightcomputer.commands;

import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;

public interface SCMCommandFactory {
  Command getCommandBySCM(SCMPacketType scmPacketType);
}
