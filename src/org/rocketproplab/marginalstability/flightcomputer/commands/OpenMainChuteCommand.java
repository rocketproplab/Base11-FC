package org.rocketproplab.marginalstability.flightcomputer.commands;

import org.rocketproplab.marginalstability.flightcomputer.comm.PacketRelay;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketSources;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.ParachuteSubsystem;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;

public class OpenMainChuteCommand implements Command {
  private ParachuteSubsystem parachuteSubsystem;
  private PacketRelay relay;

  private OpenMainChuteCommand(ParachuteSubsystem parachuteSubsystem, PacketRelay relay) {
    this.parachuteSubsystem = parachuteSubsystem;
    this.relay = relay;
  }

  @Override
  public boolean isDone() {
    return true;
  }

  @Override
  public void execute() {
    boolean success = parachuteSubsystem.attemptMainChuteOpen();
    relay.sendPacket(new SCMPacket(
                    SCMPacketType.MD, success ? "    1" : "    0"),
            PacketSources.CommandBox);
  }

  @Override
  public void start() {
  }

  @Override
  public void end() {
  }

  @Override
  public Subsystem[] getDependencies() {
    return new Subsystem[]{parachuteSubsystem};
  }

  public static class OpenMainChuteFactory {
    /**
     * Get a new OpenMainChuteCommand
     *
     * @param parachuteSubsystem the ParachuteSubsystem this command will operate on
     * @param relay              the PacketRelay to send the success status packet through
     * @return a new OpenMainChuteCommand
     */
    public static OpenMainChuteCommand getOpenMainChuteCommand(
            ParachuteSubsystem parachuteSubsystem, PacketRelay relay) {
      return new OpenMainChuteCommand(parachuteSubsystem, relay);
    }
  }

}
