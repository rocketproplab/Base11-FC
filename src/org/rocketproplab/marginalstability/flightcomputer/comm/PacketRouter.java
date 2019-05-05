package org.rocketproplab.marginalstability.flightcomputer.comm;

import java.util.ArrayList;
import java.util.HashMap;

import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;

/**
 * Routes packets of any type to their destination
 * 
 * @author Max Apodaca
 *
 */
public class PacketRouter {

  private HashMap<LookupTuple, ArrayList<PacketListener<?>>> listenerMap;

  public PacketRouter() {
    this.listenerMap = new HashMap<>();
  }

  public void sendPacket(Object o, PacketSources source) {
    this.dispatchPacket(o, source, PacketDirection.SEND);
  }

  public void recivePacket(Object o, PacketSources source) {
    this.dispatchPacket(o, source, PacketDirection.RECIVE);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void dispatchPacket(Object o, PacketSources source, PacketDirection direction) {
    LookupTuple lookup = new LookupTuple(o.getClass(), source);
    if (this.listenerMap.containsKey(lookup)) {
      for (PacketListener listener : this.listenerMap.get(lookup)) {
        listener.onPacket(direction, o);
      }
    }
  }

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
    
    public boolean equals(Object o) {
      if(o instanceof LookupTuple) {
        return o.hashCode() == this.hashCode;
      }
      return false;
    }
  }

}
