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
 * A subsystem that listens for SCMPacket and FrameSCM to schedule commands.
 *
 * @author Chi Chow
 */
public class SCMCommandSubsystem implements Subsystem,
        PacketListener<SCMPacket>, FramedPacketProcessor {

  private Looper looper;

  /**
   * Map associating SCMPacketType to a Command factory.
   */
  private final HashMap<SCMPacketType, SCMCommandFactory> SCMMap;

  /**
   * Map associating FramedSCM data to a Command factory.
   */
  private final HashMap<String, FramedSCMCommandFactory> framedSCMMap;

  /**
   * Creates a new SCMCommandSubsystem
   */
  public SCMCommandSubsystem() {
    SCMMap       = new HashMap<>();
    framedSCMMap = new HashMap<>();
  }

  @Override
  public void prepare(Looper looper) {
    this.looper = looper;
  }

  /**
   * Associates SCMPacketType with a SCMCommandFactory.
   *
   * @param type    SCMPacketType to listen for
   * @param factory that processes this SCMPacketType
   */
  public void registerSCMCommand(SCMPacketType type, SCMCommandFactory factory) {
    SCMMap.put(type, factory);
  }

  /**
   * Associates FramedSCM data with a FramedSCMCommandFactory.
   *
   * @param framedSCMData FramedSCM data to listen for
   * @param factory       that processes this FramedSCM data
   */
  public void registerFramedSCMCommand(String framedSCMData, FramedSCMCommandFactory factory) {
    framedSCMMap.put(framedSCMData, factory);
  }

  /**
   * Listen to packets and callback if SCMPacketType is registered.
   *
   * @param packet with SCMPacketType corresponding to a SCMCommandFactory
   */
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

  /**
   * Listens to FramedSCM packets and callback if the data is registered.
   *
   * @param framedPacket with data corresponding to a FramedSCMCommandFactory
   */
  @Override
  public void processFramedPacket(String framedPacket) {
    String                  data    = extractFramedSCMData(framedPacket);
    FramedSCMCommandFactory factory = framedSCMMap.get(data);
    if (factory != null) {
      Command commandToSchedule = factory.getCommandByFramedSCM(data);
      if (commandToSchedule != null) {
        looper.scheduleCommand(commandToSchedule);
      }
    }
  }

  /**
   * Extract data from a FramedSCM packet.
   *
   * @param framedPacket to extract data from
   * @return data from the FramedSCM
   */
  public static String extractFramedSCMData(String framedPacket) {
    int index = framedPacket.indexOf('|');
    return framedPacket.substring(index + 1);
  }
}
