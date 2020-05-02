package org.rocketproplab.marginalstability.flightcomputer.hal;

import java.util.HashSet;
import java.util.Set;

import org.rocketproplab.marginalstability.flightcomputer.events.SerialListener;

public class SerialPortAdapter implements SerialPort {

  private Set<SerialListener> listeners;
  private SerialListener writeListener;

  public SerialPortAdapter(SerialListener writeListener) {
    this.listeners = new HashSet<>();
    this.writeListener = writeListener;
  }

  @Override
  public void registerListener(SerialListener listener) {
    this.listeners.add(listener);
  }

  @Override
  public void write(String data) {
    this.writeListener.onSerialData(data);
  }

  public void newMessage(String message) {
    for (SerialListener listener : this.listeners) {
      listener.onSerialData(message);
    }
  }

}
