package org.rocketproplab.marginalstability.flightcomputer.comm;

public class SCMPacket {

  public String  id;
  public String  data;
  public boolean isValid;

  public static int LAST_THREE_CHARS      = 3;
  public static int NUM_COMPONENTS_PACKET = 3;

  /**
   * Constructor that passes the packet into it's components
   * 
   * @param packet the packet to be processed
   */
  public SCMPacket(String packet) {
    parsepacket(packet);
  }

  /**
   * Takes the packet assigns its id and data to the instance variables Also
   * calls verifyChecksum to confirm packet accuracy
   * 
   * @param packet the received packet to work with
   */
  public void parsepacket(String packet) {
    String[] packetComponents = packet.split(",");

    if (packetComponents.length != NUM_COMPONENTS_PACKET) {
      this.isValid = false;
      return;
    }
    String strChecksum = packetComponents[2].substring(0,
        packetComponents[2].length() - 1);
    int    checksum    = Integer.parseInt(strChecksum);

    if (verifyChecksum(packet, checksum)) {
      this.id   = packetComponents[0];
      this.data = packetComponents[1];
      this.isValid   = true;
    } else {
      this.isValid = false;
    }
  }

  /**
   * Takes the packet and calculates its checksum It then compares the
   * calculated checksum to the checksum sent with the packetComponents
   * 
   * @param packet   The packet in question
   * @param checksum The checksum sent with the packetComponents
   * @return true if the sent checksum and the calculated checksum match
   */
  private boolean verifyChecksum(String packet, int checksum) {
    if (calculateChecksum(packet) == checksum) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Helper method for verifyChecksum for calculating the strChecksum
   * 
   * @param packet the packet in question
   * @return the checksum of the packet
   */
  private int calculateChecksum(String packet) {
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
   * toString method that returns the original packet and the components
   * 
   * @return original message and components formatted in a String
   */
}
