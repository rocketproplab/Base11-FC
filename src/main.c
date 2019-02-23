#include <stdio.h>
#include <string.h>
#include "commandOps.h"
/* Will be using wiringPi library for access to
GPIO pins on the Raspberry Pi 3 B+. The other options
consist of other libraries or direct register access,
none of which are more optimal (or safe). Furthermore,
this library is necessitated since the direct language
support in Raspberry Pi's are Scratch and Python. Thus,
a wrapper library is necessary. This library replicates the
Arduino library very well.
*/

int main(){
  std::cout("Im a flight computer!\n");


}
