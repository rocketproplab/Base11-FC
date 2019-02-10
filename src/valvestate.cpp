#include <string>
#include <cstdlib>

string createVS(int currentState) {
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

int process(int currentState) {
	//Helper function to process the
	//valve states
}

void deployVS(int destination, int currentState) {
	//Send VS
}
