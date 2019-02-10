#include <string>
#include <cstdlib>

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

void deployBackup(int destination) {
	std::string request = "";

	request = generateBackup();

	//Deploy the heartbeat through GPIO pins or some method
}
