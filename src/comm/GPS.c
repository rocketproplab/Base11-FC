#include "GPS.h"

#ifndef TRUE
#define TRUE 1
#endif
#ifndef FALSE
#define FALSE 0
#endif

/**
* The scanner to parse the NEMA code
*/
typedef struct Scanner {
  int index;
  char* string;
  int length;
} Scanner;

int scanner_getNext(Scanner *scanner);
int scanner_hasNext(Scanner *scanner);


int scanner_getNext(Scanner *scanner){
  int readChar;

  if(!scanner_hasNext(scanner)){
    return EOF;
  }

  readChar = scanner->string[scanner->index];
  scanner->index ++;

  return readChar;
}

int scanner_hasNext(Scanner *scanner){
  return scanner->index < scanner->length;
}

void decodeNEMA(char* nema, GPSInfo *gpsInfo, GPSDebug *gpsDebug){

}
