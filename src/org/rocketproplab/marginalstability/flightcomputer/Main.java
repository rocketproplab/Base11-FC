package org.rocketproplab.marginalstability.flightcomputer;

/**
 * The main file contains the entry point to the software stack. It is
 * responsible for instantiating the
 * {@link org.rocketproplab.marginalstability.flightcomputer.FlightComputer
 * Flight Computer}, registering all the
 * {@link org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem
 * Subsystems}, and registering subsystems with the
 * {@link org.rocketproplab.marginalstability.flightcomputer.comm.PacketRouter
 * Packet Router}.
 * <p>
 * The application is split up into three main sections.
 * <ul>
 * <li><b>Subsystems</b> are classes which hold state for a particular part of
 * the rocket. For example there would be a subsystem responsible for the
 * parachutes. The subsystem is responsible for executing commands that relate
 * to the hardware it controls. For our parachute example this would entail
 * opening and closing the parachutes. Some subsystems need to receive periodic
 * calls to complete their actions. In this case the subsystem would implement
 * {@link org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem
 * Subsystem} and main would have to register it with the flight computer.</li>
 * <li><b>Commands</b> are responsible for coordinating subsystems to do a real
 * world action. For instance a launch command would tell the valve subsystem to
 * change it's state as well as resting the position in the position estimation
 * subsystem.</li>
 * <li><b>HAL Devices</b> are abstractions designed to promote testability. For
 * instance there would be a HAL interface for a temperature sensor which would
 * contain a method to read from the sensor. Since we don't want to concern
 * ourselves with the implementation details all of the subsystems would use the
 * interface type. However to implement the protocols we need a sensor specific
 * class. An example of this is the
 * {@link org.rocketproplab.marginalstability.flightcomputer.hal.LPS22HD
 * LPS22HD} which is a concrete implementation for a
 * {@link org.rocketproplab.marginalstability.flightcomputer.hal.Barometer
 * Barometer}. Some sensors need to poll occasionally to complete their task.
 * This means that they will need to be registered with a looper //TODO add
 * looper documentation.</li>
 * </ul>
 */
public class Main {
  public static void main(String[] args) {
    FlightComputer flightComputer = FlightComputer.create(args);
    flightComputer.initHighLevelObjects();

//    while(true) {
//      flightComputer.tick();
//    }
  }
}
