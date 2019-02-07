#include "GPS.h"
#include <string.h>
#include <stdlib.h>

#ifndef TRUE
#define TRUE 1
#endif
#ifndef FALSE
#define FALSE 0
#endif

#define NEMA_DELIM ","

void grabNext(char ** string);

/*
 * Reads the next double from the string tokenizer
 *
 * @param string the string which is to be read from after running through
 *        tokenizer
 *
 * @return the double representation of the number inputted
 */
double grabDouble(char ** string){
  grabNext(string);
  return strtod(*string, NULL);
}

/*
 * advances the string tokenizer one forward, modifies the *string ptr
 *
 * @param string the string to advance through the tokenizer
 */
void grabNext(char ** string){
  *string = strtok(0, NEMA_DELIM);
}

/*
 * decodes the NEMA string specified and places the data into the two structs
 * as appropriote. See
 * https://www.trimble.com/oem_receiverhelp/v4.44/en/nmea-0183messages_gga.html
 * for NEMA GGA spec.
 *
 * @param nema the NEMA GGA string to decode
 * @param gpsInfo the gps info to write to
 * @param gpsDebug the gps debug info to write to
 */
void decodeNEMA(char *nema, GPSInfo *gpsInfo, GPSDebug *gpsDebug){
  char *nemaCopy = strdup(nema);
  nema = nemaCopy;

  nema = strtok(nema, NEMA_DELIM);
  gpsInfo->t_b       = grabDouble(&nema);
  gpsInfo->lat       = grabDouble(&nema);
  grabNext(&nema);
  gpsInfo->lon       = grabDouble(&nema);
  grabNext(&nema);
  gpsDebug->accuracy = grabDouble(&nema);
  gpsDebug->sVCount  = grabDouble(&nema);
  grabNext(&nema);
  gpsInfo->alt       = grabDouble(&nema);

  free(nemaCopy);
}
