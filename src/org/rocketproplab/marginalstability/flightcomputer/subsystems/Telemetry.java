package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.rocketproplab.marginalstability.flightcomputer.Errors;
import org.rocketproplab.marginalstability.flightcomputer.Info;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketRelay;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketSources;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A reporter for telemetry. It will sends the packet periodically to the
 * Command Box with the information given.
 *
 * @author Max Apodaca
 */
public class Telemetry {
  public static final int BASE_10 = 10;
  public static final int BASE_16 = 16;
  public static final int MAX_PACKET_BASE_10 = (int) Math.round(Math.pow(BASE_10, SCMPacket.DATA_LENGTH)) - 1;
  public static final int MAX_PACKET_BASE_16 = (int) Math.round(Math.pow(BASE_16, SCMPacket.DATA_LENGTH)) - 1;

  public static final int MIN_PACKET_BASE_10 = (int) -Math.round(Math.pow(BASE_10, SCMPacket.DATA_LENGTH - 1)) + 1;
  public static final int MIN_PACKET_BASE_16 = (int) -Math.round(Math.pow(BASE_16, SCMPacket.DATA_LENGTH - 1)) + 1;

  public static final String INFINITY = "INF  ";
  public static final String NEG_INFINITY = "-INF ";
  private static final String INT_FORMAT = "%0" + SCMPacket.DATA_LENGTH + "d";
  private static final String HEX_FORMAT = "%0" + SCMPacket.DATA_LENGTH + "x";
  private static final String DOUBLE_FORMAT = "%0" + SCMPacket.DATA_LENGTH + "f";

  private Logger logger;
  private PacketRelay relay;

  /**
   * Creates a new telemetry subsystem that logs to the given logger and uses the
   * given packet reply to send its packets.
   *
   * @param logger the logger to use for info output
   * @param relay  the relay to use for sending packets
   */
  public Telemetry(Logger logger, PacketRelay relay) {
    this.logger = logger;
    this.relay  = relay;
  }

  /**
   * Reports a double to the command box
   *
   * @param type the packet type to send with the double
   * @param data the double to send
   */
  public void reportTelemetry(SCMPacketType type, double data) {
    String dataString = String.format(DOUBLE_FORMAT, data).toUpperCase();
    int    toPrint    = Math.min(dataString.length(), SCMPacket.DATA_LENGTH);
    int    padLen     = (SCMPacket.DATA_LENGTH - toPrint);

    dataString = dataString.substring(0, toPrint);
    if (padLen > 0) {
      dataString = String.format("%" + padLen + "s", dataString);
    }
    if (data > MAX_PACKET_BASE_10) {
      dataString = INFINITY;
    } else if (data < MIN_PACKET_BASE_10) {
      dataString = NEG_INFINITY;
    }
    SCMPacket packet = new SCMPacket(type, dataString);
    this.relay.sendPacket(packet, PacketSources.CommandBox);
    this.logger.log(Level.INFO, type.getName() + " is " + data);
  }

  /**
   * Reports an integer to the command box in base 16 to allow for greater data to
   * be set.
   *
   * @param type the data type to send
   * @param data the data to send, must fit in 5 characters
   */
  public void reportTelemetryHex(SCMPacketType type, int data) {
    this.reportTelemetry(type, data, HEX_FORMAT, MAX_PACKET_BASE_16, MIN_PACKET_BASE_16);
    this.logger.log(Level.INFO, type.getName() + " is " + Integer.toString(data, 16));
  }

  /**
   * Reports an integer to the command box. If greater than max then infinity is
   * sent.
   *
   * @param type the type of data to send
   * @param data the data to be sent, must fit in 5 character
   */
  public void reportTelemetry(SCMPacketType type, int data) {
    this.reportTelemetry(type, data, INT_FORMAT, MAX_PACKET_BASE_10, MIN_PACKET_BASE_10);
    this.logger.log(Level.INFO, type.getName() + " is " + data);
  }

  /**
   * Internally report the integer value using the given constraints
   *
   * @param type   the type of packet to send
   * @param data   the integer to be sent
   * @param format the format string to print this integer
   * @param max    the maximum value (if greater prints infinity)
   * @param min    the minimum value (if less prints - infinity)
   */
  private void reportTelemetry(SCMPacketType type, int data, String format, int max, int min) {
    String dataString = String.format(format, data).toUpperCase();
    if (data > max) {
      dataString = INFINITY;
    } else if (data < min) {
      dataString = NEG_INFINITY;
    }
    SCMPacket packet = new SCMPacket(type, dataString);
    this.relay.sendPacket(packet, PacketSources.CommandBox);
  }

  /**
   * Send the error to the Command Box
   *
   * @param error the error to inform the command box of
   */
  public void reportError(Errors error) {
    this.reportTelemetry(SCMPacketType.ER, error.ordinal());
    this.logger.log(Level.INFO, "Reporting Error: " + error.toString());
  }

  public void logInfo(Info info) {
    this.logger.log(Level.INFO, info.getDescription());
  }

  /**
   * Send the heartbeat to the command box
   */
  public void sendHeartbeat() {
    String    dataString = "00000";
    SCMPacket packet     = new SCMPacket(SCMPacketType.HB, dataString);
    this.relay.sendPacket(packet, PacketSources.CommandBox);
  }

}
