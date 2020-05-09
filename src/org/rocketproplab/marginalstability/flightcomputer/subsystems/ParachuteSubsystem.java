package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.events.FlightStateListener;
import org.rocketproplab.marginalstability.flightcomputer.events.ParachuteListener;
import org.rocketproplab.marginalstability.flightcomputer.events.PositionListener;
import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;
import org.rocketproplab.marginalstability.flightcomputer.hal.Barometer;
import org.rocketproplab.marginalstability.flightcomputer.hal.Solenoid;
import org.rocketproplab.marginalstability.flightcomputer.math.InterpolatingVector3;
import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;
import org.rocketproplab.marginalstability.flightcomputer.tracking.FlightMode;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;

import java.util.ArrayList;
import java.util.List;

/**
 * A subsystem that controls the solenoids for deploying the parachutes.
 *
 * @author Max Apodaca
 */
public class ParachuteSubsystem
        implements FlightStateListener, PositionListener, Subsystem,
        PacketListener<SCMPacket> {

  private static ParachuteSubsystem instance;

  public static ParachuteSubsystem getInstance() {
    if (instance == null) {
      instance = new ParachuteSubsystem(null, null, null, null);
    }
    return instance;
  }

  private Solenoid             mainChute;
  private Solenoid             drogueChute;
  private FlightMode           lastMode;
  private InterpolatingVector3 position;
  private Time                 time;
  private Barometer            barometer;

  private double                  lastPressureBelowThresholdTime = -1;
  private List<ParachuteListener> parachuteListeners;

  /**
   * Create a new parachute subsystem
   *
   * @param mainChute   the solenoid to deploy the main chute
   * @param drogueChute the solenoid to deploy the drogue chute
   * @param time        the rocket time
   */
  public ParachuteSubsystem(Solenoid mainChute, Solenoid drogueChute,
                            Time time, Barometer barometer) {
    this.mainChute          = mainChute;
    this.drogueChute        = drogueChute;
    this.lastMode           = FlightMode.Sitting;
    this.time               = time;
    this.barometer          = barometer;
    this.parachuteListeners = new ArrayList<>();
  }

  public void addParachuteListener(ParachuteListener parachuteListener) {
    parachuteListeners.add(parachuteListener);
  }

  @Override
  public void onPacket(PacketDirection direction, SCMPacket packet) {
    if (packet.getID() == SCMPacketType.DD) {
      drogueChute.set(true);
      drogueChuteOpenCallback();
    } else if (packet.getID() == SCMPacketType.MD) {
      mainChute.set(true);
      mainChuteOpenCallback();
    }
  }

  @Override
  public void onFlightModeChange(FlightMode newMode) {
    if (newMode == FlightMode.Apogee ||
            newMode == FlightMode.Falling) {
      drogueChute.set(true);
      drogueChuteOpenCallback();
    }
    this.lastMode = newMode;
  }

  @Override
  public void onPositionEstimate(InterpolatingVector3 positionEstimate) {
    this.position = positionEstimate;
  }

  @Override
  public void update() {
    if (this.position == null ||
            this.lastMode != FlightMode.Falling) {
      return;
    }

    Vector3 currentPos = this.position.getAt(time.getSystemTime());
    if (currentPos.getZ() < Settings.MAIN_CHUTE_HEIGHT
            && sincePressureBelowThresholdTime() > Settings.MAIN_CHUTE_PRESSURE_TIME_THRESHOLD) {
      this.mainChute.set(true);
      mainChuteOpenCallback();
    }
  }

  private double sincePressureBelowThresholdTime() {
    if (barometer.getPressure() >= Settings.MAIN_CHUTE_PRESSURE) {
      lastPressureBelowThresholdTime = Double.NaN;
      return -1;
    }
    if (Double.isNaN(lastPressureBelowThresholdTime)) {
      lastPressureBelowThresholdTime = time.getSystemTime();
    }
    return time.getSystemTime() - lastPressureBelowThresholdTime;
  }

  private void mainChuteOpenCallback() {
    for (ParachuteListener listener : parachuteListeners) {
      listener.onMainChuteOpen();
    }
  }

  private void drogueChuteOpenCallback() {
    for (ParachuteListener listener : parachuteListeners) {
      listener.onDrogueOpen();
    }
  }
}
