package org.rocketproplab.marginalstability.flightcomputer.events;

import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;

/**
 * Listens for a particular type of packet in a particular direction
 * 
 * @author Max Apodaca
 *
 * @param <E> The type of packet to listen for (should be GPSPacket or
 *            SCMPacket)
 */
public interface PacketListener<E> {

	public void onPacket(PacketDirection direction, E packet);

}
