package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.hal.MAX14830.Port;

public class MAX14830Test {
  private MockSPI spi;
  private MAX14830 max14830;
  
  @Before
  public void before() {
    this.spi = new MockSPI();
    this.max14830 = new MAX14830(this.spi);
  }
  
  @Test
  public void txBufferLengthsReadCorrectly() throws IOException {
    this.spi.toReturn = new byte[] {0, 0x10};
    int txLen = this.max14830.getTXBufferLen(Port.UART0);
    assertEquals(0x10, txLen);
    assertEquals(2, this.spi.lastWritten.length);
    assertEquals(0x11, this.spi.lastWritten[0]);
    assertEquals(0, this.spi.lastWritten[1]);
    
    this.spi.toReturn = new byte[] {0, -0x80};
    txLen = this.max14830.getTXBufferLen(Port.UART1);
    assertEquals(128, txLen);
    assertEquals(2, this.spi.lastWritten.length);
    assertEquals(0b00100000 | 0x11, this.spi.lastWritten[0]);
    assertEquals(0, this.spi.lastWritten[1]);
    
    this.spi.toReturn = new byte[] {0};
    txLen = this.max14830.getTXBufferLen(Port.UART1);
    assertEquals(-1, txLen);
  }
  
  @Test
  public void rxBufferLengthsReadCorrectly() throws IOException {
    this.spi.toReturn = new byte[] {0, 0x15};
    int txLen = this.max14830.getRXBufferLen(Port.UART3);
    assertEquals(0x15, txLen);
    assertEquals(2, this.spi.lastWritten.length);
    assertEquals(0b01100000 | 0x12, this.spi.lastWritten[0]);
    assertEquals(0, this.spi.lastWritten[1]);
    
    this.spi.toReturn = new byte[] {0, 0x7F};
    txLen = this.max14830.getRXBufferLen(Port.UART2);
    assertEquals(127, txLen);
    assertEquals(2, this.spi.lastWritten.length);
    assertEquals(0b01000000 | 0x12, this.spi.lastWritten[0]);
    assertEquals(0, this.spi.lastWritten[1]);
    
    this.spi.toReturn = new byte[] {0};
    txLen = this.max14830.getRXBufferLen(Port.UART1);
    assertEquals(-1, txLen);
  }
  
  @Test
  public void writeToTxFifoWritesSingleCharacter() throws IOException {
    this.spi.toReturn = new byte[] {0, 0};
    this.max14830.writeToPort(Port.UART0, "Hello World");
    this.max14830.writeToTxFifo(Port.UART0, 1);
    byte[] written = this.spi.lastWritten;
    assertEquals(2, written.length);
    assertEquals((byte)0b10000000, written[0]);
    assertEquals('H', written[1]);
  }
  
  @Test
  public void writeToTxFifoSlectsBasedOnUartPort() throws IOException {
    this.spi.toReturn = new byte[] {0, 0};
    this.max14830.writeToPort(Port.UART0, "Hello World");
    this.max14830.writeToPort(Port.UART1, "Bye World");
    this.max14830.writeToTxFifo(Port.UART1, 1);
    byte[] written = this.spi.lastWritten;
    assertEquals(2, written.length);
    assertEquals((byte)0b10100000, written[0]);
    assertEquals('B', written[1]);
  }
  
  @Test
  public void writeToTxFifoInSuccessionWritesNextChar() throws IOException {
    this.spi.toReturn = new byte[] {0, 0};
    this.max14830.writeToPort(Port.UART0, "Hello World");
    this.max14830.writeToTxFifo(Port.UART0, 1);
    this.max14830.writeToTxFifo(Port.UART0, 1);
    byte[] written = this.spi.lastWritten;
    assertEquals(2, written.length);
    assertEquals((byte)0b10000000, written[0]);
    assertEquals('e', written[1]);
  }
  
  @Test
  public void writeToTxFifoWritesAtMostBufferLen() throws IOException {
    this.spi.toReturn = new byte[] {0, 0};
    this.max14830.writeToPort(Port.UART0, "Hello");
    int writeLen = this.max14830.writeToTxFifo(Port.UART0, 10);
    byte[] written = this.spi.lastWritten;
    assertEquals(6, written.length);
    assertEquals((byte)0b10000000, written[0]);
    String testString = new String(written, 1, 5);
    assertEquals("Hello", testString);
    assertEquals(5, writeLen);
  }
  
  @Test
  public void writeToSerialPortHasSameEffectAsWriteToPort() throws IOException {
    this.spi.toReturn = new byte[] {0, 0};
    SerialPort port = this.max14830.getPort(Port.UART0);
    port.write("Hello");
    int writeLen = this.max14830.writeToTxFifo(Port.UART0, 10);
    byte[] written = this.spi.lastWritten;
    assertEquals(6, written.length);
    assertEquals((byte)0b10000000, written[0]);
    String testString = new String(written, 1, 5);
    assertEquals("Hello", testString);
    assertEquals(5, writeLen);
  }
  
  @Test
  public void pollFillsTxBufferToFull() throws IOException {
    this.spi.toReturn = new byte[] {0, 0};
    this.spi.toReturnMap.put(0x11, new byte[] {0, 123});
    SerialPort port = this.max14830.getPort(Port.UART0);
    port.write("Hello World");
    this.max14830.poll();
    byte[] written = this.spi.lastWrittenMap.get(-0x80);
    assertEquals(6, written.length);
    assertEquals((byte)0b10000000, written[0]);
    String testString = new String(written, 1, 5);
    assertEquals("Hello", testString);
  }
  
  @Test
  public void doublePollReadsOnce() throws IOException {
    this.spi.toReturn = new byte[] {0, 0};
    this.spi.toReturnMap.put(0x11, new byte[] {0, 110});
    SerialPort port = this.max14830.getPort(Port.UART0);
    port.write("Hello World");
    this.max14830.poll();
    port.write("Hello");
    this.spi.lastWrittenMap.clear();
    this.spi.toReturnMap.put(0x11, new byte[] {0, 121});
    this.max14830.poll();
    byte[] written = this.spi.lastWrittenMap.get(-0x80);
    assertEquals(6, written.length);
    assertEquals((byte)0b10000000, written[0]);
    String testString = new String(written, 1, 5);
    assertEquals("Hello", testString);
    assertFalse(this.spi.lastWrittenMap.containsKey(0x11));
  }
  
  @Test
  public void readRxFifoReadsFromFifo() throws IOException {
    this.spi.toReturn = new byte[] {0, 0};
    byte[] bytes = "\0Hello World".getBytes(Charset.forName("US-ASCII"));
    this.spi.toReturnMap.put(0, bytes);
    byte[] data = this.max14830.readFromRxFifo(Port.UART0, 11);
    assertEquals(12, data.length);
    assertEquals(bytes, data);
  }
  
  @Test
  public void listenerCalledWithNewString() throws IOException {
    this.spi.toReturn = new byte[] {0, 0};
    final ArrayList<String> writeList = new ArrayList<>();
    this.max14830.getPort(Port.UART0).registerListener(writeList::add);
    this.spi.toReturnMap.put(0x12, new byte[] {0, 11});
    this.spi.toReturnMap.put(0, "\0Hello World".getBytes(Charset.forName("US-ASCII")));
    this.max14830.poll();
    assertEquals(1, writeList.size());
    assertEquals("Hello World", writeList.get(0));
  }
  
  @Test
  public void setBuadrateSetsBaudrateAccordingTofREF() {
    this.spi.toReturn = new byte[] {0,0};
    this.max14830.setBaudrate(Port.UART0, 115200);
  }
}
