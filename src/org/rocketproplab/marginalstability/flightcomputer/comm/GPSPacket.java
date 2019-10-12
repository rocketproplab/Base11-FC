package org.rocketproplab.marginalstability.flightcomputer.comm;

public class GPSPacket {

  private static final String NEMA_DELIMITER      = ",";
  private static final int    NEMA_PART_LENGTH    = 15;
  private static final int    NEMA_TIME_INDEX     = 1;
  private static final int    NEMA_LAT_INDEX      = 2;
  private static final int    NEMA_LON_INDEX      = 4;
  private static final int    NEMA_SV_COUNT_INDEX = 7;
  private static final int    NEMA_ALTITUDE_INDEX = 9;

  private boolean valid;
  private double  latitude;
  private double  longitude;
  private double  altitude;
  private double  time;
  private int     sVCount;

  /**
   * Create a new GPS Packet based on the NEMA String
   * 
   * @param nEMA the nema to make the packet of
   */
  public GPSPacket(String nEMA) {
    this.parseNEMA(nEMA);
  }

  /**
   * Internally parses the NEMA for the packet
   * 
   * @param nEMA the nema to assign this packet to
   */
  private void parseNEMA(String nEMA) {
    if (nEMA == null) {
      this.valid = false;
      return;
    }
    String[] nEMAParts = nEMA.split(NEMA_DELIMITER);
    if (nEMAParts.length != NEMA_PART_LENGTH) {
      this.valid = false;
      return;
    }
    this.valid = true;

    String timeString     = nEMAParts[NEMA_TIME_INDEX];
    String latString      = nEMAParts[NEMA_LAT_INDEX];
    String lonString      = nEMAParts[NEMA_LON_INDEX];
    String sVCountString  = nEMAParts[NEMA_SV_COUNT_INDEX];
    String altitudeString = nEMAParts[NEMA_ALTITUDE_INDEX];

    this.time      = Double.parseDouble(timeString);
    this.latitude  = Double.parseDouble(latString);
    this.longitude = Double.parseDouble(lonString);
    this.sVCount   = Integer.parseInt(sVCountString);
    this.altitude  = Double.parseDouble(altitudeString);
  }

  /**
   * @return if the packet is valid
   */
  public boolean isValid() {
    return valid;
  }

  /**
   * @return the latitude
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * @return the longitude
   */
  public double getLongitude() {
    return longitude;
  }

  /**
   * @return the altitude
   */
  public double getAltitude() {
    return altitude;
  }

  /**
   * @return the time at which packet was received by GPS in seconds
   */
  public double getTime() {
    return time;
  }

  /**
   * Used as debug information for how many satellite vehicles (SVs) are
   * connected to the GPS.
   * 
   * @return the number of satellite vehicles connected to the GPS
   */
  public int getSVCount() {
    return this.sVCount;
  }

}
