package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

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
  }
}
