package org.rocketproplab.marginalstability.flightcomputer.comm;

import java.util.ArrayList;
import java.util.HashMap;

import org.rocketproplab.marginalstability.flightcomputer.ErrorReporter;
import org.rocketproplab.marginalstability.flightcomputer.Errors;
import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;

/**
 * Routes packets of any type to their destination. <br>
 * The routing works by having a Map between the type and source of the packet
 * to a list of listeners. Each time sendPacket or receivePacket is called the
 * router looks up the listenerArray and iterates through with the given packet.
 * <br>
 * {@link #sendPacket(Object, PacketSources)} and
 * {@link #recivePacket(Object, PacketSources)} specify the PacketDirection for
 * the packet but both broadcast to all the listeners for that packet type (the
 * class of the object) and source. See
 * {@link #dispatchPacket(Object, PacketSources, PacketDirection)} for more
 * information.
 * 
 * @author Max Apodaca
 *
 */
public class PacketRouter implements PacketRelay {

  private HashMap<LookupTuple, ArrayList<PacketListener<?>>> listenerMap;

  /**
   * Create a new packet router and initialize internal state
   */
  public PacketRouter() {
    this.listenerMap = new HashMap<>();
  }

  @Override
  public void sendPacket(Object o, PacketSources source) {
    this.dispatchPacket(o, source, PacketDirection.SEND);
  }

  /**
   * Send a packet to all of the receivers
   * 
   * @param o      the packet which is being sent (must be of packet type)
   * @param source
   */
  public void recivePacket(Object o, PacketSources source) {
    this.dispatchPacket(o, source, PacketDirection.RECIVE);
  }

  /**
   * Dispatch a packet with the given direction to all of the appropriate
   * listeners. The packet listeners are looked up based off of a combination
   * between the direction and class hash.
   * 
   * @param o         The packet to be transmitted
   * @param source    the sender of the packet
   * @param direction what direction the packet is being sent in
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void dispatchPacket(Object o, PacketSources source, PacketDirection direction) {
    try {
      LookupTuple lookup = new LookupTuple(o.getClass(), source);
      if (this.listenerMap.containsKey(lookup)) {
        for (PacketListener listener : this.listenerMap.get(lookup)) {
          listener.onPacket(direction, o);
        }
      }
    } catch (ClassCastException classExecption) {
      ErrorReporter errorReporter = ErrorReporter.getInstance();
      String errorMsg = "Packet " + o + " is not of suitable type";
      errorReporter.reportError(null, classExecption, errorMsg);
    }

  }

  /**
   * Add a listener for a specific type of packet. Note that the listeners are
   * mapped using the hash of the Class object associated with that packet.
   * 
   * @param listener the packet listener listening to this packet
   * @param type     the class which will be hashed to make lookup easier
   * @param source   what source to listen from
   */
  public void addListener(PacketListener<?> listener, Class<?> type, PacketSources source) {
    LookupTuple lookup = new LookupTuple(type, source);
    if (!this.listenerMap.containsKey(lookup)) {
      this.listenerMap.put(lookup, new ArrayList<>());
    }
    ArrayList<PacketListener<?>> listeners = this.listenerMap.get(lookup);
    listeners.add(listener);
  }

  /**
   * A class to get a nice hash code for the lookup map
   * 
   * @author Max Apodaca
   *
   */
  private class LookupTuple {

    private int hashCode;

    /**
     * Create a new LookupTuple for the type and source
     * 
     * @param type   the type of packet to look for
     * @param source the origin of the packet
     */
    public LookupTuple(Class<?> type, PacketSources source) {
      this.hashCode = type.hashCode() ^ source.ordinal() << 16;
    }

    @Override
    public int hashCode() {
      return this.hashCode;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof LookupTuple) {
        return o.hashCode() == this.hashCode;
      }
      return false;
    }
  }

}
