package org.rocketproplab.marginalstability.flightcomputer.hal;

import java.io.IOException;

import org.rocketproplab.marginalstability.flightcomputer.Time;

import com.pi4j.io.i2c.I2CDevice;

public class LPS22HD implements Barometer, PollingSensor, Sensor {
	
	private I2CDevice i2cDevice;
	private double pressure;
	private double pressureValue;
	private Time currTime;
	
	private final byte ON_MESSAGE = 0b01100000;
	private final int ON_ADDRESS = 0x10;
	private final int MINIMUM_RANGE = 260;
	private final int MAXIMUM_RANGE = 1260;
	private final double ZERO_TIME = 0.0;
	private final double SCALING_FACTOR = 4096;
	
	
	public LPS22HD(I2CDevice i2cDevice) {
		this.i2cDevice = i2cDevice;
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
		if (pressure >= MINIMUM_RANGE && pressure <= MAXIMUM_RANGE) {
			return true;
		} else  {
			return false;
		}
	}

	@Override
	public double getLastMeasurementTime() {
		if (currTime != null) {
			return currTime.getSystemTime();
		} else {
			return ZERO_TIME;
		}
	}
  
	public void poll() {
		try {
			pressureValue = i2cDevice.read(0x2A) + i2cDevice.read(0x29)
			+ i2cDevice.read(0x28);
			pressure = pressureValue/SCALING_FACTOR;
			System.out.println(pressure);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currTime = new Time();
	}

}
