#ifndef GPS_H
#define GPS_H

typedef struct GPSInfo {
  double x, y, z, t_b, lat, lon, alt;
} GPSInfo;

typedef struct GPSDebug {
  int sVCount;
  double accuracy;
} GPSDebug;

GPSInfo * getGPSInfo();
GPSDebug * getGPSDebug();

void GPSReadTask();
void GPSInit();

#endif
