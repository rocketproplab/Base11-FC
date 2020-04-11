package org.rocketproplab.marginalstability.flightcomputer.hal;

import java.io.IOException;

import org.rocketproplab.marginalstability.flightcomputer.Time;

import com.pi4j.io.i2c.I2CDevice;

public class LPS22HD implements Barometer, PollingSensor, Sensor {
	
	private I2CDevice i2cDevice;
	private int pressure;
	Time currTime;
	
	public LPS22HD(I2CDevice i2cDevice) {
		this.i2cDevice = i2cDevice;
	}
	
	@Override
	public void init() {
		try {
			i2cDevice.write(0xA, (byte)0b01100000);
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
		if (pressure >= 260 && pressure <= 1260) {
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
			return 0;
		}
	}
  
	public void poll() {
		try {
			pressure = i2cDevice.read(i2cDevice.getAddress());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currTime = new Time();
	}

}
