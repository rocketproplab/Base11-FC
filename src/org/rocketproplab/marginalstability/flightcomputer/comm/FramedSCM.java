package org.rocketproplab.marginalstability.flightcomputer.comm;

import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;

import java.util.LinkedList;
import java.util.Queue;

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
 */
public class FramedSCM implements PacketListener<SCMPacket> {
  private Queue<String>         outputQueue;
  private String                activeString;
  private int                   frameLength;
  private PacketRelay           sCMOutput;
  private FramedPacketProcessor framedPacketOutput;

  /**
   * Create a new SCM de-framer. SCMOutput is used to send replied to incoming SCM
   * packets while framedOutput
   *
   * @param sCMOutput    the packet relay to send X0 and X1 packets to
   * @param framedOutput the callback to output framed packets to
   */
  public FramedSCM(PacketRelay sCMOutput, FramedPacketProcessor framedOutput) {
    this.outputQueue   = new LinkedList<String>();
    this.activeString  = "";
    this.frameLength   = -1;
    framedPacketOutput = framedOutput;
    this.sCMOutput     = sCMOutput;
  }

  @Override
  public void onPacket(PacketDirection direction, SCMPacket packet) {

    if (direction == PacketDirection.SEND) {

      return;
    }

    SCMPacket replypacket = this.processNextPacket(packet);
    if (this.hasCompletedMessage()) {
      String frompacket = this.getCompletedMessage();
      framedPacketOutput.processFramedPacket(frompacket);
    }
    sCMOutput.sendPacket(replypacket, PacketSources.CommandBox);

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
    boolean   completed    = (activeString.length() == frameLength);
    SCMPacket returnpacket = new SCMPacket(SCMPacketType.XB, "     ");
    String    finalmessage = "";
    if (!incomingPacket.isValid()) {
      return null;
    }
    if (incomingPacket.getID() == SCMPacketType.XS) {
      returnpacket = processXSPacket(incomingPacket);
      completed    = false;
    } else if (incomingPacket.getID() == SCMPacketType.X0) {
      returnpacket = processX0Packet(incomingPacket);

    } else {
      activeString += incomingPacket.getData().trim();
      returnpacket = new SCMPacket(SCMPacketType.XB, "     ");
    }

    if ((activeString.length() == frameLength) && (!completed)) {
      finalmessage = activeString;
      this.outputQueue.add(finalmessage);
    }
    return returnpacket;

  }

  private SCMPacket processX0Packet(SCMPacket incomingPacket) {
    int lengthofframeleft = frameLength - activeString.length();
    if (frameLength == -1) {
      String[] SCMmessagesplit = incomingPacket.getData().split("\\|");
      activeString += SCMmessagesplit[0];
      frameLength  = Integer.parseInt(activeString);
      activeString = SCMmessagesplit[1];
      // TODO Similar to XS, where if the frameLength is still less than 0 or
      // does not split into several indices
    } else if (lengthofframeleft < incomingPacket.getData().length()) {
      activeString += incomingPacket.getData().substring(0, lengthofframeleft);
    } else {
      activeString += incomingPacket.getData().trim();
    }
    return new SCMPacket(SCMPacketType.XA, "     ");
  }

  private SCMPacket processXSPacket(SCMPacket incomingPacket) {
    if (incomingPacket.getData().contains("|")) {
      String[] SCMmessagesplit = incomingPacket.getData().split("\\|");
      try {
        frameLength = Integer.parseInt(SCMmessagesplit[0]);
      } catch (NumberFormatException exception) {
        return null;
      }
      if (SCMmessagesplit.length == 2) {
        if (frameLength < 0) {
          return null;
        } else if (frameLength < SCMmessagesplit[1].length()) {
          activeString = SCMmessagesplit[1].substring(0, frameLength);
        } else {
          activeString = SCMmessagesplit[1];
        }
      }
    } else {
      String getint = incomingPacket.getData();
      activeString = "" + Integer.parseInt(getint);
      frameLength  = -1;
    }

    return new SCMPacket(SCMPacketType.XB, "     ");
  }

  /**
   * Determines if there is a completed message in the output queue.
   *
   * @return if there is a completed message to read by
   * {@link #getCompletedMessage()}
   */
  protected boolean hasCompletedMessage() {
    return !this.outputQueue.isEmpty();
  }

  /**
   * Returns the next completed message from the completed message queue.
   *
   * @return the next completed message
   */
  protected String getCompletedMessage() {
    return this.outputQueue.poll();
  }
}
