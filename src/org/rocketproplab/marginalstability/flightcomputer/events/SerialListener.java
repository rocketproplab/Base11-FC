public interface SerialListener {
	/*
	 * Called each time the serial port receives a string
	*/
	public void onSerialData(String data);
}