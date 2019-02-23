#include <string.h>
#include <stdlib.h>
#include <wiringPi.h>

/* Until needed most functions will be housed in this file. Here
is the directory:

	1. Valve state
	2. Packet Generation
	3. Flight Calculations
	4. External Communications
	5. Miscellaneous*/

/* 1. Valve State */

string createValveState(int currentState) {
	std::string package = "";
	std::string state = "";
	std::string convertedData = "";

	//Need to know how I will expect incoming data
	//from transceiver

	//convertedData = std::to_string(process(currentState));

	package = "VS" + "," + convertedData + ",";
	checkSum = calculateCheckSum(package);
	state = package + std::to_string(checkSum) + ";";

	return state;
}

/*Valve State*/

/* 2. Packet Generation*/

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
  else (packageID == "ER") {
	  deployBackup(destination);
  }
}

  string createECUHB() {
  	std::string package = "";
  	std::string pulse = "";
  	int checkSum = 0;
  	int randKey = rand() % 100000;
  	std::string convertedKey = std::to_string(randKey);

  	package = "HB" + "," convertedKey + ",";
  	checkSum = calculateCheckSum(package);
  	pulse = package + std::to_string(checkSum) + ";";

  	return pulse;
  }

  string createTransceiverHB() {
  	std::pulse = "";

  	//Same as ECU heartbeat?
  }

  string generateBackup() {
  	std::string package = "";
  	std::string backup = "";
  	int checkSum = 0;
  	int randKey = rand() % 100000;
  	std::string convertedKey = std::to_string(randKey);

  	package = "ER" + "," convertedKey + ",";
  	checkSum = calculateCheckSum(package);
  	pulse = package + std::to_string(checkSum) + ";";

  	return backup;
  }


/*Packet Generation*/

/* 3. Flight Calculations */





/* Flight Calculations */

/* 4. External Communications */
void deployVS() {

}

void deployHB(int destination) {

  std::string heartbeat = "";

  if(/*Destination == ECU*/) {
	  heartbeat = createECUHB();
  }
  else if(/*Destination == transceiver*/){
	  heartbeat = createTransceiverHB();
  }
 }

void deployBackup(int destination) {
	std::string request = "";

	request = generateBackup();

	//Deploy the heartbeat through GPIO pins or some method
}
/* External Communications */

/*Miscellaneous*/
int calculateCheckSum(String package) {
	int asciiValue = 0;

	for(int i = 0; i < package.length(); i++){
		asciiValue += int(package[i]);
	}
	int totalCheckSumValue = asciiValue % 100;
	return totalCheckSumValue;
}
/*Miscellaneous*/
