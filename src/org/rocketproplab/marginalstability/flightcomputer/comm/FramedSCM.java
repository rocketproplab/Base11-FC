package org.rocketproplab.marginalstability.flightcomputer.comm;

import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;

/**
 * A message to handle the framing of extra long SCM packets. The format for an
 * extra long SCM packet is as follows. <br>
 * <code>NN...NN|XX..XX</code><br>
 * N's represent a number between 0-9 and X is any data. The number before the
 * pipe character '|' is the length of the message. ie how many X's there
 * are.<br>
 * One example message could be <code>"11|Hello World"</code> where 11 denotes
 * that the message "Hello World" is 11 characters long.<br>
 * <br>
 * This protocol implements stop an wait. For our receiver this means we need to
 * ack each message but also let the client know if we acked an even or odd
 * message. We only care about even or odd because we will only have at most one
 * SCMPacket in flight at a time. This means we need to ack with a
 * {@link SCMPacketType#XB} in response to the {@link SCMPacketType#XS} and
 * {@link SCMPacketType#X1} types and with a {@link SCMPacketType#XA} in
 * response to the {@link SCMPacketType#X0} type.
 * 
 * @author Max Apodaca
 *
 */
public class FramedSCM implements PacketListener<SCMPacket> {

  /**
   * Create a new SCM de-framer. SCMOutput is used to send replied to incoming SCM packets while
   * framedOutput 
   * @param sCMOutput the packet relay to send X0 and X1 packets to
   * @param framedOutput the callback to output framed packets to
   */
  public FramedSCM(PacketRelay sCMOutput, FramedPacketProcessor framedOutput) {

  }

  @Override
  public void onPacket(PacketDirection direction, SCMPacket packet) {

  }

  /**
   * Process the given incoming packet. If the packet is the same as the previous
   * packet the ack should be repeated. The string should be built by this method.
   * See the class javadoc for info on the protocol.
   * 
   * @param incomingPacket the new packet from the ground.
   * @return the packet to reply with
   */
  protected SCMPacket processNextPacket(SCMPacket incomingPacket) {
    return new SCMPacket("");
  }

  /**
   * Determines if there is a completed message in the output queue.
   * @return if there is a completed message to read by {@link #getCompletedMessage()}
   */
  protected boolean hasCompletedMessage() {
    return false;
  }

  /**
   * Returns the next completed message from the completed message queue.
   * @return the next completed message
   */
  protected String getCompletedMessage() {
    return "";
  }

}
