package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.rocketproplab.marginalstability.flightcomputer.comm.FramedPacketProcessor;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;
import org.rocketproplab.marginalstability.flightcomputer.commands.Command;
import org.rocketproplab.marginalstability.flightcomputer.commands.FramedSCMCommandFactory;
import org.rocketproplab.marginalstability.flightcomputer.commands.SCMCommandFactory;
import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;
import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;

import java.util.HashMap;

/**
 * A subsystem that listens for SCMPacket and FrameSCM
 * to schedule commands.
 *
 * @author Chi Chow
 */
public class SCMCommandSubsystem implements Subsystem,
        PacketListener<SCMPacket>, FramedPacketProcessor {
  private static SCMCommandSubsystem instance;

  public static SCMCommandSubsystem getInstance() {
    if (instance == null) {
      instance = new SCMCommandSubsystem();
    }
    return instance;
  }

  private       Looper                                    looper;
  private final HashMap<SCMPacketType, SCMCommandFactory> SCMMap;
  private final HashMap<String, FramedSCMCommandFactory>  framedSCMMap;

  public SCMCommandSubsystem() {
    SCMMap = new HashMap<>();
    framedSCMMap = new HashMap<>();
  }

  @Override
  public void prepare(Looper looper) {
    this.looper = looper;
  }

  public void registerSCMCommand(SCMPacketType type, SCMCommandFactory factory) {
    SCMMap.put(type, factory);
  }

  public void registerFramedSCMCommand(String framedSCMData, FramedSCMCommandFactory factory) {
    framedSCMMap.put(framedSCMData, factory);
  }

  @Override
  public void onPacket(PacketDirection direction, SCMPacket packet) {
    SCMCommandFactory factory = SCMMap.get(packet.getID());
    if (factory != null) {
      Command commandToSchedule = factory.getCommandBySCM(packet.getID());
      if (commandToSchedule != null) {
        looper.scheduleCommand(commandToSchedule);
      }
    }
  }

  @Override
  public void processFramedPacket(String framedPacket) {
    // schedule command with CommandScheduler
    String data = extractFramedSCMData(framedPacket);
    FramedSCMCommandFactory factory = framedSCMMap.get(data);
    if (factory != null) {
      Command commandToSchedule = factory.getCommandByFramedSCM(data);
      if (commandToSchedule != null) {
        looper.scheduleCommand(commandToSchedule);
      }
    }
  }

  public static String extractFramedSCMData(String framedPacket) {
    int index = framedPacket.indexOf('|');
    return framedPacket.substring(index + 1);
  }
}
