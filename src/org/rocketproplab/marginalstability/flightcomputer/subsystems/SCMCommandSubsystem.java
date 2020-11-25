package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.rocketproplab.marginalstability.flightcomputer.comm.FramedPacketProcessor;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.commands.Command;
import org.rocketproplab.marginalstability.flightcomputer.commands.CommandFactory;
import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;
import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;

/**
 * A subsystem that listens for SCMPacket and FrameSCM
 * to schedule commands.
 *
 * @author Chi Chow
 */
public class SCMCommandSubsystem implements Subsystem,
        PacketListener<SCMPacket>, FramedPacketProcessor {
  private Looper looper;

  @Override
  public void prepare(Looper looper) {
    this.looper = looper;
  }

  @Override
  public void onPacket(PacketDirection direction, SCMPacket packet) {
    Command commandToSchedule = CommandFactory.getCommandBySCMPacket(packet);
    if (commandToSchedule != null) {
      looper.scheduleCommand(commandToSchedule);
    }
  }

  @Override
  public void processFramedPacket(String framedPacket) {
    // schedule command with CommandScheduler
    Command commandToSchedule = CommandFactory.getCommandByFramedSCM(framedPacket);
    if (commandToSchedule != null) {
      looper.scheduleCommand(commandToSchedule);
    }
  }
}
