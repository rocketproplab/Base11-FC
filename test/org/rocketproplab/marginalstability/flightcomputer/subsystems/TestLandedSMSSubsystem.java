package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.comm.GPSPacket;
import org.rocketproplab.marginalstability.flightcomputer.hal.SMSSender;
import org.rocketproplab.marginalstability.flightcomputer.tracking.FlightMode;


public class TestLandedSMSSubsystem {
  private static class TestSMSSender implements SMSSender {
    private boolean smsSent = false;

    @Override
    public void sendTextMessage(String number, String message) {
      smsSent = true;
      String expected = String.format("Landed! https://www.google.com/maps/place/%f+%f",
              TestGPSPacket.LAT, TestGPSPacket.LON);
      assertEquals(expected, message);
    }
  }

  private static class TestSMSTime extends Time {
    private double time = 0;

    public void addTime(double add) {
      this.time += add;
    }

    public void setTime(double time) {
      this.time = time;
    }

    @Override
    public double getSystemTime() {
      return time;
    }
  }

  private static class TestGPSPacket extends GPSPacket {
    public static final double LAT = 11, LON = 12;

    public TestGPSPacket() {
      super(null);
    }

    @Override
    public double getLatitude() {
      return LAT;
    }

    @Override
    public double getLongitude() {
      return LON;
    }
  }

  private TestSMSSender      smsSender;
  private TestSMSTime        smsTime;
  private LandedSMSSubsystem landedSMSSubsystem;

  @Before
  public void init() {
    this.smsSender          = new TestSMSSender();
    this.smsTime            = new TestSMSTime();
    this.landedSMSSubsystem = new LandedSMSSubsystem(null, smsSender, smsTime);
  }

  @Test
  public void onlySendSMSOnLanded() {
    landedSMSSubsystem.onPacket(null, new TestGPSPacket());

    for (FlightMode flightMode : FlightMode.values()) {

      smsTime.addTime(LandedSMSSubsystem.SMS_INTERVAL + 1);
      smsSender.smsSent = false;
      landedSMSSubsystem.onFlightModeChange(flightMode);
      landedSMSSubsystem.update();

      if (flightMode != FlightMode.Landed && smsSender.smsSent ||
              flightMode == FlightMode.Landed && !smsSender.smsSent) {
        fail();
      }
    }
  }

  @Test
  public void sendSMSInterval() {
    landedSMSSubsystem.onPacket(null, new TestGPSPacket());
    landedSMSSubsystem.onFlightModeChange(FlightMode.Landed);

    // start time
    smsSender.smsSent = false;
    landedSMSSubsystem.update();
    if (smsSender.smsSent) fail();

    // add time and test if sms sent
    smsTime.addTime(LandedSMSSubsystem.SMS_INTERVAL + 1);
    smsSender.smsSent = false;
    landedSMSSubsystem.update();
    if (!smsSender.smsSent) fail();

    // immediately update again and test if sms sent
    smsSender.smsSent = false;
    landedSMSSubsystem.update();
    if (smsSender.smsSent) fail();
  }
}
