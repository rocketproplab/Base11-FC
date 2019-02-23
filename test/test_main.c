#include "stdio.h"
#include "comm/TestComm.c"
#include "test_FlightMode.c"


int main( int argc, char** argv){
  int retVal = 0;
  retVal |= testComm();
  retVal |= test_FlightMode();

  return retVal;
}
