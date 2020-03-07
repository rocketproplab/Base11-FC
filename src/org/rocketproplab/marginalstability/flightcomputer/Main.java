package org.rocketproplab.marginalstability.flightcomputer;

import org.rocketproplab.marginalstability.flightcomputer.comm.PacketRouter;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketSources;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.PTSubsystem;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.ParachuteSubsystem;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Telemetry;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.ValveStateSubsystem;

public class Main {

  public static void main(String[] args) {
    FlightComputer flightComputer = new FlightComputer(Telemetry.getInstance());
    Main.registerSubsystems(flightComputer);
    Main.registerPacketListeners();
    
    while(true) {
      flightComputer.tick();
    }
  }

  private static void registerSubsystems(FlightComputer flightComputer) {
    flightComputer.registerSubsystem(ParachuteSubsystem.getInstance());
    ValveStateSubsystem.getInstance();
  }

  private static void registerPacketListeners() {
    PacketRouter.getInstance().addListener(PTSubsystem.getInstance(),
        SCMPacket.class, PacketSources.EngineControllerUnit);
    PacketRouter.getInstance().addListener(ValveStateSubsystem.getInstance(),
        SCMPacket.class, PacketSources.EngineControllerUnit);
  }

}
