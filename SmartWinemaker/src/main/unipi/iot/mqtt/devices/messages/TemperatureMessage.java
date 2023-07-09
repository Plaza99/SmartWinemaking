package main.unipi.iot.mqtt.devices.messages;


public class TemperatureMessage {
	public long node; 			// Node ID
	public int temperature;		// Temperature

	public Long getSensorId() {
		return node;
	}


	public int getValue() {
		return temperature;
	}
}
