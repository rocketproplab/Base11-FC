#include <string>
#include <cstdlib>

/*
VS: Valve State, we send 5 Digital H/L one in each bit of data (right is least significant)
HB: Heart Beat, repeat back data received / create random data on master
T0-9; Thermocouple 0-9, 10 bit value in data field
P0-9;  Pressure Transducer 0-9, same
RP: Repeat Packet
ER: Error
OK: Everything ok

Master List:

VS - Send only
HB - Send and Receive
T0-9 - Receive Only
P0-9 - Receive Only
RP: Receive Only
ER: Send and Receive
OK: Receive Only
*/

void packageGeneration(std::string packageID,int destination,int data) {
  if (packageID == "VS") {
	  deployVS(destination,data);
  }
  else if (packageID == "HB") {
	  deployHB(destination);
  }
  else if (packageID == "ER") {
	  deployBackup(destination);
  }
  else {
    //TODO Package not identified
  }
}
