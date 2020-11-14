package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.events.FlightStateListener;
import org.rocketproplab.marginalstability.flightcomputer.events.ParachuteListener;
import org.rocketproplab.marginalstability.flightcomputer.events.PositionListener;
import org.rocketproplab.marginalstability.flightcomputer.hal.Barometer;
import org.rocketproplab.marginalstability.flightcomputer.hal.Solenoid;
import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;
import org.rocketproplab.marginalstability.flightcomputer.math.InterpolatingVector3;
import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;
import org.rocketproplab.marginalstability.flightcomputer.tracking.FlightMode;

import java.util.ArrayList;
import java.util.List;

/**
 * A subsystem that controls the solenoids for deploying the parachutes.
 *
 * @author Max Apodaca, Chi Chow
 */
public class ParachuteSubsystem
        implements FlightStateListener, PositionListener, Subsystem {

  private static final String             MAIN_CHUTE_TAG = "MainChute";
  private static       ParachuteSubsystem instance;

  public static ParachuteSubsystem getInstance() {
    if (instance == null) {
      instance = new ParachuteSubsystem(null, null, null, null);
    }
    return instance;
  }

  private Solenoid             mainChute;
  private Solenoid             drogueChute;
  private InterpolatingVector3 position;
  private Time                 time;
  private Barometer            barometer;
  private Looper               looper;

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
    this.time               = time;
    this.barometer          = barometer;
    this.parachuteListeners = new ArrayList<>();
  }

  /**
   * Register drogue chute and main chute open conditions as
   * events in the provided Looper.
   *
   * @param looper to register events to
   */
  @Override
  public void prepare(Looper looper) {
    this.looper = looper;
  }

  /**
   * Determine whether the flight mode should trigger the drogue chute to open.
   *
   * @param flightMode FlightMode to test
   * @return whether the drogue chute should open
   */
  private boolean shouldDrogueChuteOpenByFlightMode(FlightMode flightMode) {
    return flightMode.ordinal() >= FlightMode.Apogee.ordinal();
  }

  private boolean shouldMainChuteOpenByPressure() {
    Vector3 currentPos = this.position.getAt(time.getSystemTime());
    boolean b1 = currentPos.getZ() < Settings.MAIN_CHUTE_HEIGHT;
    boolean b2 = barometer.getPressure() < Settings.MAIN_CHUTE_PRESSURE;
    return b1 && b2;
//    return currentPos.getZ() < Settings.MAIN_CHUTE_HEIGHT &&
//            barometer.getPressure() >= Settings.MAIN_CHUTE_PRESSURE;
  }

  private boolean shouldMainChuteCheckPressure(FlightMode flightMode) {
    return this.position != null && flightMode == FlightMode.Falling;
  }

  /**
   * Set drogueChute to active and
   * emit drogue chute open event to all listeners.
   */
  private void drogueChuteOpen() {
    boolean wasActive = drogueChute.isActive();
    drogueChute.set(true);
    if (!wasActive) {
      for (ParachuteListener listener : parachuteListeners) {
        listener.onDrogueOpen();
      }
    }
  }

  /**
   * Set mainChute to active and
   * emit main chute open event to all listeners.
   */
  private void mainChuteOpen() {
    // remove main chute open event from Looper
    if (this.looper.removeEvent(MAIN_CHUTE_TAG) == null) {
      // TODO: Log that main chute was deployed because of pressure
    } else {
      // TODO: Log that main chute was deployed because of packet
    }

    boolean wasActive = mainChute.isActive();
    mainChute.set(true);
    if (!wasActive) {
      for (ParachuteListener listener : parachuteListeners) {
        listener.onMainChuteOpen();
      }
    }
  }

  @Override
  public void onFlightModeChange(FlightMode newMode) {
    if (shouldDrogueChuteOpenByFlightMode(newMode)) {
      drogueChuteOpen();
    }
    if (shouldMainChuteCheckPressure(newMode)) {
      looper.emitOnceIf(MAIN_CHUTE_TAG, Settings.MAIN_CHUTE_PRESSURE_TIME_THRESHOLD,
              this::shouldMainChuteOpenByPressure, (tag, from) -> mainChuteOpen());
    }
  }

  @Override
  public void onPositionEstimate(InterpolatingVector3 positionEstimate) {
    this.position = positionEstimate;
  }

  /**
   * Add a ParachuteListener to emit parachute changes
   *
   * @param parachuteListener ParachuteListener to emit callbacks to
   */
  public void addParachuteListener(ParachuteListener parachuteListener) {
    parachuteListeners.add(parachuteListener);
  }
}
