package org.rocketproplab.marginalstability.flightcomputer.hal;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.rocketproplab.marginalstability.flightcomputer.mockPi4J.DummyGpioPinImpl;

import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinShutdown;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinListener;

public class DummyGpioSolenoid implements Solenoid, GpioPinDigitalOutput {

	PinState solenoidState;
	DummyGpioPinImpl solenoidPin;
	boolean active = true;
	
	public boolean isActive() {
		return this.active;
	}
	
	public void set(boolean active) {
		this.active = active;
		if (active) {
			high();
		} else if (!active) {
			low();
		}
	}

	@Override
	public PinState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isHigh() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLow() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isState(PinState arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addListener(GpioPinListener... arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addListener(List<? extends GpioPinListener> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearProperties() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void export(PinMode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void export(PinMode arg0, PinState arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<GpioPinListener> getListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PinMode getMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pin getPin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProperty(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProperty(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GpioProvider getProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PinPullResistance getPullResistance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GpioPinShutdown getShutdownOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getTag() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasListener(GpioPinListener... arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasProperty(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMode(PinMode arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPullResistance(PinPullResistance arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeAllListeners() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener(GpioPinListener... arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener(List<? extends GpioPinListener> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeProperty(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMode(PinMode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setName(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProperty(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPullResistance(PinPullResistance arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setShutdownOptions(GpioPinShutdown arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setShutdownOptions(Boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setShutdownOptions(Boolean arg0, PinState arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setShutdownOptions(Boolean arg0, PinState arg1, PinPullResistance arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setShutdownOptions(Boolean arg0, PinState arg1, PinPullResistance arg2, PinMode arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTag(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unexport() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Future<?> blink(long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> blink(long arg0, TimeUnit arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> blink(long arg0, PinState arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> blink(long arg0, long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> blink(long arg0, PinState arg1, TimeUnit arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> blink(long arg0, long arg1, TimeUnit arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> blink(long arg0, long arg1, PinState arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> blink(long arg0, long arg1, PinState arg2, TimeUnit arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void high() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void low() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Future<?> pulse(long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, TimeUnit arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, Callable<Void> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, PinState arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, Callable<Void> arg1, TimeUnit arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, boolean arg1, TimeUnit arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, boolean arg1, Callable<Void> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, PinState arg1, TimeUnit arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, PinState arg1, Callable<Void> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, PinState arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, boolean arg1, Callable<Void> arg2, TimeUnit arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, PinState arg1, Callable<Void> arg2, TimeUnit arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, PinState arg1, boolean arg2, TimeUnit arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, PinState arg1, boolean arg2, Callable<Void> arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> pulse(long arg0, PinState arg1, boolean arg2, Callable<Void> arg3, TimeUnit arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(PinState arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setState(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toggle() {
		// TODO Auto-generated method stub
		
	}
}
