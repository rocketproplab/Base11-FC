#include "stdio.h"
#include "comm/TestComm.c"

int main( int argc, char** argv){
  testComm();

  printf("Your tests have now been run\n");
  return 2;
}
