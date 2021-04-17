package org.rocketproplab.marginalstability.flightcomputer.comm;

import org.rocketproplab.marginalstability.flightcomputer.ErrorReporter;

/**
 * A packet for the SCMProtocol to convert between the object and string
 * representation. <br>
 * The Simple Checksum Messaging (SCM) protocol consists of three sections. The
 * id, data and checksum organized as a plaintext string. <br>
 *
 * <pre>
 * {@code | Id | Data  | Checksum |}
 * {@code   AA , BBBBB ,    FF    ;}
 * </pre>
 * <p>
 * An example of a heartbeat packet would be {@code HB,12345,81;} where the ID
 * is HB, data is 12345 and checksum is 81. <br>
 * The checksum is calculated by summing the char code of each character up the
 * and including the second comma and taking modulo 100. The result is stored as
 * a character sequence from 00 to 99 and then a semicolon is appended. See
 * {@link #calculateChecksum(String)} for implementation details of the checksum
 * algorithm.
 *
 * @author Daniel Walder, Max Apodaca
 */
public class SCMPacket {

  public static final int LAST_THREE_CHARS      = 3;
  public static final int NUM_CHARS_PACKET      = 12;
  public static final int NUM_COMPONENTS_PACKET = 3;
  public static final int DATA_LENGTH           = 5;

  private SCMPacketType id;
  private String        data;
  private boolean       isValid;

  /**
   * Constructor that passes the packet into it's components
   *
   * @param packet the packet to be processed
   */
  public SCMPacket(String packet) {
    parsepacket(packet);
  }

  /**
   * Create a new packet based off of the ID and data
   *
   * @param id   the id of the packet
   * @param data the data which the packet holds
   */
  public SCMPacket(SCMPacketType id, String data) {
    this.id   = id;
    this.data = data;
    this.validate();
  }

  /**
   * Takes the packet assigns its id and data to the instance variables Also calls
   * verifyChecksum to confirm packet accuracy
   *
   * @param packet the received packet to work with
   */
  private void parsepacket(String packet) {
    String[] packetComponents = packet.split(",");

    if (packet.toCharArray().length != NUM_CHARS_PACKET || packetComponents.length != NUM_COMPONENTS_PACKET) {
      this.isValid = false;
      return;
    }
    String strChecksum = packetComponents[2].substring(0, packetComponents[2].length() - 1);

    int checksum = -1;

    try {
      checksum = Integer.parseInt(strChecksum);
    } catch (NumberFormatException exception) {
      ErrorReporter errorReporter = ErrorReporter.getInstance();
      String        errorMsg      = "Unable to parse checksum in " + packet;
      errorReporter.reportError(null, exception, errorMsg);
      this.isValid = false;
      return;
    }

    if (calculateChecksum(packet) == checksum) {
      try {
        this.id = SCMPacketType.valueOf(packetComponents[0]);
      } catch (IllegalArgumentException illegalArg) {
        ErrorReporter errorReporter = ErrorReporter.getInstance();
        String        errorMsg      = "Got bad packet ID (" + packetComponents[0] + ")!";
        errorReporter.reportError(null, illegalArg, errorMsg);
      }
      this.data = packetComponents[1];
      this.validate();
    } else {
      this.isValid = false;
    }
  }

  /**
   * Sets the valid flag on this packet based on the id and data
   */
  private void validate() {
    if (this.id == null) {
      this.isValid = false;
      return;
    }

    if (this.data == null) {
      this.isValid = false;
      return;
    }

    if (this.data.length() != DATA_LENGTH) {
      this.isValid = false;
      return;
    }

    this.isValid = true;
  }

  /**
   * Helper method for verifyChecksum for calculating the strChecksum
   *
   * @param packet the packet in question
   * @return the checksum of the packet
   */
  private static int calculateChecksum(String packet) {
    // take everything of the packet before the checksum
    packet = packet.substring(0, packet.length() - LAST_THREE_CHARS);

    int addedASCII = 0;
    for (int i = 0; i < packet.length(); i++) {
      addedASCII += packet.charAt(i);
    }
    int calculatedChecksum = addedASCII % 100;

    return calculatedChecksum;
  }

  /**
   * Gets the ID of this packet. Only valid if {@link SCMPacket#isValid()} returns
   * true.
   *
   * @return the id of the packet
   */
  public SCMPacketType getID() {
    return this.id;
  }

  /**
   * Get the data in this packet. Only valid if {@link SCMPacket#isValid()}
   * returns true.
   *
   * @return the data in this packet
   */
  public String getData() {
    return this.data;
  }

  /**
   * Get whether or not this packet is valid. If invalid nothing is guarantee.
   *
   * @return if the data in the packet is valid
   */
  public boolean isValid() {
    return this.isValid;
  }

  /**
   * Encodes id and data into a packet string. Will attempt to encode even with an
   * invalid state.
   *
   * @return the packet as String
   */
  @Override
  public String toString() {
    String packet = this.id + "," + this.data + ",";

    int addedASCII = 0;
    for (int i = 0; i < packet.length(); i++) {
      addedASCII += packet.charAt(i);
    }
    int calculatedChecksum = addedASCII % 100;

    if (calculatedChecksum < 10) {
      packet = packet + "0" + Integer.toString(calculatedChecksum) + ";";
    } else {
      packet = packet + Integer.toString(calculatedChecksum) + ";";
    }

    return packet;
  }

  @Override
  public boolean equals(Object obj) {
    SCMPacket packet;
    if (obj instanceof SCMPacket) {
      packet = (SCMPacket) obj;
    } else {
      return false;
    }

    if (packet.isValid != this.isValid) {
      return false;
    }

    if (packet.id == null) {
      if (this.id != null) {
        return false;
      }
    } else if (!packet.id.equals(this.id)) {
      return false;
    }

    if (packet.data == null) {
      if (this.data != null) {
        return false;
      }
    } else if (!packet.data.equals(this.data)) {
      return false;
    }

    return true;
  }
}
