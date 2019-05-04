#include "stdio.h"

int a(){
  int k = 0;
  k ++;
  printf("im in a %d\n", k);
  b();
}

int b(){
  int z = 0;
  int l = 324;
  z ++;
  z = l + z;
  printf("im in b %d\n", z);
}


int main(){
  a();
  b();
  printf("Im a flight computer!\n");
}
