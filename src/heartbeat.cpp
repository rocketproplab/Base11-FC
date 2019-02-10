#include <string>
#include <cstdlib>

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

//Code to send heartbeat to the ECU
void deployHB (int destination) {

	std::string heartbeat = "";

	if(/*Destination == ECU*/){
		heartbeat = createECUHB();
	}
	else if(/*Destination == transceiver*/){
		heartbeat = createTransceiverHB();
	}

	//Deploy the heartbeat through GPIO pins or some method
}
