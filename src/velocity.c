#include "math.h"

#define SQUARED 2

//Need 3 dimensions of data and time
double velocityFromAltitude(/* TODO */) {

  double deltaX = x2 - x1;
  double deltaY = y2 - y1;
  double deltaZ = z2 - z1;
  double deltaT = t2 - t1;

  double velocityX = deltaX / deltaT;
  double velocityY = deltaY / deltaT;
  double velocityZ = deltaZ / deltaT;

  double velocity = sqrt(pow(velocityX,SQUARED) + pow(velocityY,SQUARED) + pow(velocityZ,SQUARED));

  return velocity;
}
