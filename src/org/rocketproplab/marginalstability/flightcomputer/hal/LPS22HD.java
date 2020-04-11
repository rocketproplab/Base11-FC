package org.rocketproplab.marginalstability.flightcomputer.hal;

import java.io.IOException;

import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.hal.LPS22HDTest.BarometerTime;

import com.pi4j.io.i2c.I2CDevice;

public class LPS22HD implements Barometer, PollingSensor, Sensor {
	
	private I2CDevice i2cDevice;
	private double pressure;
	private double pressureValue;
	private long time;
	private Time currTime;
	
	private final byte ON_MESSAGE = 0b01100000;
	private final int ON_ADDRESS = 0x10;
	private final double MINIMUM_RANGE = 259;
	private final double MAXIMUM_RANGE = 1261;
	private final double ZERO_TIME = 0.0;
	private final double SCALING_FACTOR = 4096;
	private final int ADDRESS_ONE = 0x2A;
	private final int ADDRESS_TWO = 0x29;
	private final int ADDRESS_THREE = 0x28;
	
	
	public LPS22HD(I2CDevice i2cDevice, Time time) {
		this.i2cDevice = i2cDevice;
		this.currTime = time;
	}
	
	@Override
	public void init() {
		try {
			i2cDevice.write(ON_ADDRESS, ON_MESSAGE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
  
	@Override
	public double getPressure() {
		return pressure;
	}

	@Override
	public boolean inUsableRange() {
		if ((pressure > MINIMUM_RANGE) && (pressure < MAXIMUM_RANGE)) {
			return true;
		} else  {
			return false;
		}
	}

	@Override
	public double getLastMeasurementTime() {
		
		if (currTime != null) {
			return time;
		} else {
			return ZERO_TIME;
		}
	}
  
	public void poll() {
		try {
			pressureValue = i2cDevice.read(ADDRESS_ONE)
					+ (i2cDevice.read(ADDRESS_TWO)<<8)
					+ (i2cDevice.read(ADDRESS_THREE)<<16);
			pressure = pressureValue/SCALING_FACTOR;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		time = (long)((BarometerTime) currTime).getTime();
	}

}
