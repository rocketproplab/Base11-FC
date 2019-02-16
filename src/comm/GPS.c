#include <string.h>
#include <stdlib.h>
#include <wiringSerial.h>
#include <stdio.h>

#include "GPS.h"
#include "Coroutines.h"


#ifndef TRUE
#define TRUE 1
#endif
#ifndef FALSE
#define FALSE 0
#endif

#define NEMA_DELIM ","
#define NEMA_MAX_SIZE 1024
#define NEMA_START_CHAR '$'
#define NEMA_CHECKSUM_CHAR '*'
#define NULL_TERMINATOR_SIZE 1
#define NEMA_CHECKSUM_SIZE 2
#define BAUD_RATE 57600
// TODO fill it in!

typedef struct GPS_Internal {
  int serialPortID;
  char nemaMessage[NEMA_MAX_SIZE + NULL_TERMINATOR_SIZE];
  int currentNemaPos;
} GPS_Internal;

static GPSInfo currentInfo        = {0};
static GPSDebug currentDebug      = {0};
static GPS_Internal internalState = {0};

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
  int bytesRead  = 0;
  int dataAvaliable = serialDataAvail(gpsState->serialPortID);
  int serialRead = 0;

  nemaBuf     = ((char *) &gpsState->nemaMessage ) + gpsState->currentNemaPos;
  maxReadLen -= gpsState->currentNemaPos;

  while(dataAvaliable > 0){
    if(bytesRead >= maxReadLen){
      break;
    }

    serialRead = serialGetchar(gpsState->serialPortID);
    if(serialRead < 0){
      //TODO handle error
      break;
    }
    *nemaBuf = serialRead;
    nemaBuf ++;
    bytesRead ++;

    dataAvaliable = serialDataAvail(gpsState->serialPortID);
  }

  if(bytesRead > 0){
    gpsState->currentNemaPos += bytesRead;
    gpsState->nemaMessage[gpsState->currentNemaPos] = '\0';
  }

  return bytesRead;
}



/*
 * Checks if a full NEMA string has been read by the serial port
 *
 * @param gpsState used to read the nema string out of
 *
 * @return 0 if no NEMA is avaliable, otherwies positive.
 */
int isNEMAAvaliable(GPS_Internal * gpsState){
  char * startIndex;
  char * endIndex;
  char * nema = (char *) gpsState->nemaMessage;

  startIndex = strchr(nema, NEMA_START_CHAR);
  if(startIndex == NULL){
    return FALSE;
  }

  endIndex = strchr(startIndex, NEMA_CHECKSUM_CHAR);
  return endIndex != NULL;
}

/*
 * Locates the NEMA string within the currently read buffer, returns null if
 * no string can be found.
 *
 * @param gpsState the internal GPS state
 *
 * @return a new string on the heap containing only the nema string
 */
char * findNEMA(GPS_Internal * gpsState, int * startLoc){
  char * startIndex;
  char * endIndex;
  char * nema = (char *) gpsState->nemaMessage;
  char * returnStr;
  int returnStrSize;

  startIndex = strchr(nema, NEMA_START_CHAR);
  if(startIndex == NULL){
    return NULL;
  }

  endIndex = strchr(startIndex, NEMA_CHECKSUM_CHAR);
  if(endIndex == NULL){
    return NULL;
  }

  returnStrSize  = endIndex - startIndex + 1;
  returnStrSize += NULL_TERMINATOR_SIZE + NEMA_CHECKSUM_SIZE;
  returnStr      = malloc(returnStrSize);

  strncpy(returnStr, startIndex, returnStrSize - NULL_TERMINATOR_SIZE);
  returnStr[returnStrSize - 1] = '\0';

  if(startLoc){
    *startLoc = (int) ( startIndex - nema );
  }

  return returnStr;
}

/*
 * Parses a NEMA packet if there is one in the GPS state buffer.
 *
 * @param gpsState the gps state to read the packets from
 * @param gpsInfo the info to write the information to
 * @param gpsDebug the debug struct to write the info to
 */
void parseNEMA(GPS_Internal *gpsState, GPSInfo * gpsInfo, GPSDebug * gpsDebug){
  char * nema;
  int nemaLen;
  int nonNEMALen;
  int index;
  int startNemaOffset;

  if(!isNEMAAvaliable(gpsState)){
    return;
  }

  nema    = findNEMA(gpsState, &startNemaOffset);
  nemaLen = startNemaOffset + strlen(nema);

  decodeNEMA(nema, gpsInfo, gpsDebug);

  nonNEMALen = NEMA_MAX_SIZE - nemaLen;
  for(index = 0; index < nonNEMALen; index++){
    gpsState->nemaMessage[index] = gpsState->nemaMessage[index + nemaLen];
  }
  gpsState->currentNemaPos -= nemaLen;

  free(nema);
}

/*
 * get the current GPS info struct
 */
GPSInfo * getGPSInfo(){
  return &currentInfo;
}

/*
 * get the current global GPS debug struct
 */
GPSDebug * getGPSDebug(){
  return &currentDebug;
}

/*
 * the task for reading the GPS data from the USB serial
 */
void GPSReadTask(){
  while(TRUE){
    trySerialRead(&internalState);
    parseNEMA(&internalState, &currentInfo, &currentDebug);
    NextTask();
  }
}

/*
 * initializes the serial ports for the GPS
 */
void GPSInit(char * serial){
  int port = 0;
  port = serialOpen(serial, BAUD_RATE);
  if(port < 0){
    //TODO handle error
  }
  internalState.serialPortID = port;
}
