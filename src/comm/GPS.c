#include "GPS.h"
#include <string.h>
#include <stdlib.h>
#include <libserialport.h>

#ifndef TRUE
#define TRUE 1
#endif
#ifndef FALSE
#define FALSE 0
#endif

#define NEMA_DELIM ","
#define NEMA_MAX_SIZE 1024

typedef struct GPS_Internal {
  struct sp_port * serialport;
  char nemaMessage[NEMA_MAX_SIZE];
  int currentNemaPos;
} GPS_Internal;

static GPSInfo currentInfo   = {0};
static GPSDebug currentDebug = {0};

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

/*
 * Tries to read the serial port for the NEMA string
 *
 * @param gpsState the internal state containing the NEMA string and port
 *
 * @return bytes read or negative on error
 */
int trySerialRead(GPS_Internal * gpsState){
  char * nemaBuf;
  int maxReadLen = NEMA_MAX_SIZE;

  nemaBuf     = ((char *) &gpsState->nemaMessage ) + gpsState->currentNemaPos;
  maxReadLen -= gpsState->currentNemaPos;

  return sp_nonblocking_read(gpsState->serialport, nemaBuf, maxReadLen);
}

/*
 * Checks if a full NEMA string has been read by the serial port
 *
 * @param gpsState used to read the nema string out of
 *
 * @return 0 if no NEMA is avaliable, otherwies positive.
 */
int isNEMAAvaliable(GPS_Internal * gpsState){
  return FALSE;
}


void GPSReadTask(){}
