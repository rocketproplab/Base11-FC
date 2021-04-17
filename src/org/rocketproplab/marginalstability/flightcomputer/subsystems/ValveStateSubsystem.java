package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.rocketproplab.marginalstability.flightcomputer.comm.*;
import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;

/*
 *
 *
 *
 *
 * @author Clara Chun
 *
 */
public class ValveStateSubsystem implements PacketListener<SCMPacket> {

  private SCMPacket     packet;
  private int[]         valveStates = {-1, -1, -1, -1, -1, -1, -1, -1};
  private String        data;
  private SCMPacketType id;
  private PacketRelay   relay;

  public ValveStateSubsystem(PacketRelay relay) {
    this.relay = relay;
  }

  public void onPacket(PacketDirection direction, SCMPacket packet) {
    this.packet = packet;
    this.data   = packet.getData();
    this.id     = packet.getID();
    setStates();
  }

  private void setStates() {
    int trackArray  = 0; //the value we're starting at on the valveStates array
    int maxNum      = 5; //what number to end at on the array
    int trackString = 0; //what position on the data string we're at.
    if (id == SCMPacketType.V1) {
      trackArray = 5;
      maxNum     = 8;
    }

    while (trackArray < maxNum) {
      if (data.substring(trackString, trackString + 1).equals("0")) {
        valveStates[trackArray] = 0;
      } else if (data.substring(trackString, trackString + 1).equals("1")) {
        valveStates[trackArray] = 1;
      } else {
        //TODO: report as error if occurs
        valveStates[trackArray] = -1;
      }
      trackString++;
      trackArray++;
    }
    sendPacket(id);
  }

  public void setValve(int valve, int state) {
    valveStates[valve - 1] = state;
    sendPacket(valve);
  }

  private void sendPacket(int valve) {
    if (valve < 5) {
      sendPacket(SCMPacketType.V0);
    } else {
      sendPacket(SCMPacketType.V1);
    }
  }

  private void sendPacket(SCMPacketType type) {
    SCMPacketType id   = type;
    String        data = "";
    SCMPacket     packet;

    int endValue   = 5; //value on the valveStates array to stop at when creating a data string
    int beginValue = 0; // value on the valveStates array to begin at when creating a data string
    if (id == SCMPacketType.V1) {
      endValue   = 8;
      beginValue = 5;
    }
    for (; beginValue < endValue; beginValue++) {
      if (valveStates[beginValue] == -1) {
        return;
      }
      data += "" + valveStates[beginValue];
    }
    if (id == SCMPacketType.V1) {
      data += "00";
    }

    packet = new SCMPacket(id, data);
    this.relay.sendPacket(packet, PacketSources.EngineControllerUnit);
  }


}
