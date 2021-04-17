package org.rocketproplab.marginalstability.flightcomputer.tracking;

import org.rocketproplab.marginalstability.flightcomputer.comm.GPSPacket;
import org.rocketproplab.marginalstability.flightcomputer.events.VelocityListener;
import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;

public class VelocityCalculator implements VelocityListener {

  @Override
  public void onVelocityUpdate(Vector3 velocity, double time) {
    // TODO Auto-generated method stub

  }

  public static Vector3 getVelocity(GPSPacket prevGPSPacket,
                                    GPSPacket curGPSPacket) {
    double deltaLat     = curGPSPacket.getLatitude() - prevGPSPacket.getLatitude();
    double deltaLon     = curGPSPacket.getLongitude() - prevGPSPacket.getLongitude();
    double deltaTime    = curGPSPacket.getTime() - prevGPSPacket.getTime();
    double angVelocityX = deltaLon / deltaTime;
    double angVelocityY = deltaLat / deltaTime;
    int    radiusEarth  = 6378100;
    double velocityX    = angVelocityX * radiusEarth;
    double velocityY    = angVelocityY * radiusEarth;
    double velocityZ    = curGPSPacket.getAltitude() - prevGPSPacket.getAltitude();
    return new Vector3(velocityX, velocityY, velocityZ);

  }

}
