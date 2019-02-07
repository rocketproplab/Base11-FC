#include "GPS.h"
#include <string.h>
#include <stdlib.h>

#ifndef TRUE
#define TRUE 1
#endif
#ifndef FALSE
#define FALSE 0
#endif

void decodeNEMA(char *nema, GPSInfo *gpsInfo, GPSDebug *gpsDebug){
  printf("%s\n", nema);
  int len = strlen(nema);
  char * nemaCopy = malloc(len);
  strncpy(nemaCopy, nema, len);
  nema = nemaCopy;

  nema = strtok(nema, ",");
  nema = strtok(0, ",");
  char* time = strdup(nema);
  nema = strtok(0, ",");
  char* lat = strdup(nema);
  nema = strtok(0, ",");
  char* latDir = strdup(nema);
  nema = strtok(0, ",");
  char* lon = strdup(nema);
  nema = strtok(0, ",");
  char* lonDir = strdup(nema);
  nema = strtok(0, ",");
  char* qual = strdup(nema);
  nema = strtok(0, ",");
  char* sVCount = strdup(nema);
  nema = strtok(0, ",");
  char* dop = strdup(nema);
  nema = strtok(0, ",");
  char* alt = strdup(nema);
  nema = strtok(0, ",");
  char* altUnits = strdup(nema);

  gpsInfo->alt = strtod(alt, NULL);
  gpsInfo->lat = strtod(lat, NULL);
  gpsInfo->lon = strtod(lon, NULL);
  gpsInfo->t_b = strtod(time, NULL);
  gpsDebug->sVCount = strtod(sVCount, NULL);

  free(nemaCopy);
  free(time);
  free(lat);
  free(latDir);
  free(lon);
  free(lonDir);
  free(qual);
  free(sVCount);
  free(dop);
  free(alt);
  free(altUnits);
}
