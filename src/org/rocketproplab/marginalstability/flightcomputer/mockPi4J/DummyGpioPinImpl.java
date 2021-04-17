package org.rocketproplab.marginalstability.flightcomputer.mockPi4J;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DummyGpioPinImpl implements GpioPin {

  @Override
  public void addListener(GpioPinListener... arg0) {

  }

  @Override
  public void addListener(List<? extends GpioPinListener> arg0) {

  }

  @Override
  public void clearProperties() {

  }

  @Override
  public void export(PinMode arg0) {

  }

  @Override
  public void export(PinMode arg0, PinState arg1) {

  }

  public PinState getState() {
    return null;
  }

  @Override
  public Collection<GpioPinListener> getListeners() {
    return null;
  }

  @Override
  public PinMode getMode() {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public Pin getPin() {
    return null;
  }

  @Override
  public Map<String, String> getProperties() {
    return null;
  }

  @Override
  public String getProperty(String arg0) {
    return null;
  }

  @Override
  public String getProperty(String arg0, String arg1) {
    return null;
  }

  @Override
  public GpioProvider getProvider() {
    return null;
  }

  @Override
  public PinPullResistance getPullResistance() {
    return null;
  }

  @Override
  public GpioPinShutdown getShutdownOptions() {
    return null;
  }

  @Override
  public Object getTag() {
    return null;
  }

  @Override
  public boolean hasListener(GpioPinListener... arg0) {
    return false;
  }

  @Override
  public boolean hasProperty(String arg0) {
    return false;
  }

  @Override
  public boolean isExported() {
    return false;
  }

  @Override
  public boolean isMode(PinMode arg0) {
    return false;
  }

  @Override
  public boolean isPullResistance(PinPullResistance arg0) {
    return false;
  }

  @Override
  public void removeAllListeners() {

  }

  @Override
  public void removeListener(GpioPinListener... arg0) {

  }

  @Override
  public void removeListener(List<? extends GpioPinListener> arg0) {

  }

  @Override
  public void removeProperty(String arg0) {

  }

  @Override
  public void setMode(PinMode arg0) {

  }

  @Override
  public void setName(String arg0) {

  }

  @Override
  public void setProperty(String arg0, String arg1) {

  }

  @Override
  public void setPullResistance(PinPullResistance arg0) {

  }

  @Override
  public void setShutdownOptions(GpioPinShutdown arg0) {

  }

  @Override
  public void setShutdownOptions(Boolean arg0) {

  }

  @Override
  public void setShutdownOptions(Boolean arg0, PinState arg1) {

  }

  @Override
  public void setShutdownOptions(Boolean arg0, PinState arg1, PinPullResistance arg2) {

  }

  @Override
  public void setShutdownOptions(Boolean arg0, PinState arg1, PinPullResistance arg2, PinMode arg3) {

  }

  @Override
  public void setTag(Object arg0) {

  }

  @Override
  public void unexport() {

  }

  public void setState(PinState solenoidState) {

  }

}
