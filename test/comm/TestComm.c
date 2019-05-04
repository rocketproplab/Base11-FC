#include "TestGPS.c"

int testComm(){
  int retVal = 0;
  retVal |= testGPS();

  return retVal;
}
