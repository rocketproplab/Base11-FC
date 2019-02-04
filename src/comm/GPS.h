#ifndef GPS_H
#define GPS_H

typedef struct GPSInfo {
  double x, y, z, t_b, lat, lon, alt;
} GPSInfo;

typedef struct GPSDebug {
  int sVCount;
  double accuracy;
} GPSDebug;

// void decodeNEMA(char* nema, &GPSInfo gpsInfo, &GPSDebug gpsDebug);

#endif
