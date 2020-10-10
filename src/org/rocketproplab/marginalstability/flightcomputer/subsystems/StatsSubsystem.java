package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import java.util.List;

import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.events.EngineEventListener;
import org.rocketproplab.marginalstability.flightcomputer.events.FlightStateListener;
import org.rocketproplab.marginalstability.flightcomputer.events.ParachuteListener;
import org.rocketproplab.marginalstability.flightcomputer.events.PositionListener;
import org.rocketproplab.marginalstability.flightcomputer.events.VelocityListener;
import org.rocketproplab.marginalstability.flightcomputer.hal.Barometer;
import org.rocketproplab.marginalstability.flightcomputer.hal.Thermocouple;
import org.rocketproplab.marginalstability.flightcomputer.math.InterpolatingVector3;
import org.rocketproplab.marginalstability.flightcomputer.math.StatisticCollector;
import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;
import org.rocketproplab.marginalstability.flightcomputer.tracking.FlightMode;

/**
 * Subsystem that contains all of the sensors that are only used to provide
 * statistics, also listens for events to provide statistics.
 * 
 * Contains all the Thermocouple Sensors, the Barometer, and the Pressure
 * Transducer Subsystem. Listens for parachute deployment, state change, and
 * navigation updates.
 * 
 * @author Max Apodaca
 *
 */
public class StatsSubsystem implements Subsystem, ParachuteListener, FlightStateListener, VelocityListener,
    PositionListener, EngineEventListener {
  
  private List<StatisticCollector> collectors;
  private Telemetry telemetry;

  public StatsSubsystem(Telemetry telemetry, PTSubsystem ptSubsystem, Barometer barometer,
      Thermocouple... thermocouples) {
//    ptSubsystem.getPTValue(ChannelIndex.CH0);
  }

  @Override
  public void onDrogueOpen() {
    // TODO Auto-generated method stub

  }

  @Override
  public void onDrougeCut() {
    // TODO Auto-generated method stub

  }

  @Override
  public void onMainChuteOpen() {
    // TODO Auto-generated method stub

  }

  @Override
  public void onEngineActivation() {
    // TODO Auto-generated method stub

  }

  @Override
  public void onEngineShutdown() {
    // TODO Auto-generated method stub

  }

  @Override
  public void onEngineData(EngineDataType type, double value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onPositionEstimate(InterpolatingVector3 positionEstimate) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onVelocityUpdate(Vector3 velocity, double time) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onFlightModeChange(FlightMode newMode) {
    // TODO Auto-generated method stub

  }

  @Override
  public void update() {
    // TODO Auto-generated method stub
    for(StatisticCollector collector: this.collectors) {
      if(collector.hasNext()) {
        SCMPacket packet = collector.getNext();
//        this.telemetry.reportTelemetry(type, data);
      }
    }
  }

}
