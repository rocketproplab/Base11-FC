package org.rocketproplab.marginalstability.flightcomputer.commands;

public interface FramedSCMCommandFactory {

  Command getCommandByFramedSCM(String framedSCMData);
}