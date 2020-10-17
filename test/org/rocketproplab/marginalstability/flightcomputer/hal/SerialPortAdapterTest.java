package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class SerialPortAdapterTest {

  @Test
  public void dispatchWriteSendsToListeners() {
    SerialPortAdapter adapter = new SerialPortAdapter(null);
    ArrayList<String> stringList = new ArrayList<>();
    adapter.registerListener(stringList::add);
    adapter.newMessage("Test");
    assertEquals(1, stringList.size());
    assertEquals("Test", stringList.get(0));
  }
  
  @Test
  public void writeTriggersEventHandler() {
    ArrayList<String> stringList = new ArrayList<>();
    SerialPortAdapter adapter = new SerialPortAdapter(stringList::add);
    adapter.write("Hello World");
    assertEquals(1, stringList.size());
    assertEquals("Hello World", stringList.get(0));
  }
 
}
