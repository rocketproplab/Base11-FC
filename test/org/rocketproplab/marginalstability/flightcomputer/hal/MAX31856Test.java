package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.Time;

public class MAX31856Test {
  private MockSPI spi;
  private ThermoTime time;
  private MAX31856 max31856;
  
  private static final byte REG_TC_TEMP = 0x0C;
  
  public class ThermoTime extends Time {
	  public double getSystemTime() {
		  return 105;
	  }
  }
  
  @Before
  public void before() {
    this.spi = new MockSPI();
    this.time = new ThermoTime();
    this.max31856 = new MAX31856(this.spi, this.time);
  }
  
  
  @Test
  public void readTemp() throws IOException {
    this.spi.toReturn = new byte[] {0, 0};
    byte[] toReturnBytes = {REG_TC_TEMP, (byte)0b11111111, (byte)0b11111111, 0};
    this.spi.toReturnMap.put((int)REG_TC_TEMP, toReturnBytes);
    this.max31856.poll();
    assertEquals(-0.0625, this.max31856.getTemperature(), 0.0000001);
	  assertEquals(105.0, this.max31856.getLastMeasurementTime(), 0.000000001);
  }
  
  @Test
  public void getTempNegativeMax() throws IOException {
    this.spi.toReturn = new byte[] {0, 0};
    byte[] toReturnBytes = {REG_TC_TEMP, (byte)0b10000000, 0, 0};
    this.spi.toReturnMap.put((int)REG_TC_TEMP, toReturnBytes);
    this.max31856.poll();
    assertEquals(-2048, this.max31856.getTemperature(), 0.000005);
  }
  
  @Test
  public void getLastMeasurementTimeZero() {
    this.max31856 = new MAX31856(this.spi, null);
	  assertEquals(0.0, this.max31856.getLastMeasurementTime(), 0.000000001);
  }

  @Test
  public void readsOutsideOfBoundsOnStartup() {
    assertFalse(this.max31856.inUsableRange());
  }
}