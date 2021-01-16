package org.rocketproplab.marginalstability.flightcomputer.commands;

/**
 * Provides Commands based on data in FramedSCM
 */
public interface FramedSCMCommandFactory {
  Command getCommandByFramedSCM(String framedSCMData);
}