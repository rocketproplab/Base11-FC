#include "stdio.h"
#include "comm/TestComm.c"


int main( int argc, char** argv){
  int retVal = 0;
  retVal |= testComm();

  return retVal;
}
