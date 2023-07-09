package main.unipi.iot.mqtt.devices.messages;


public class FloatMessage {
	public long node; 		// Node ID
	public int floatLevel;	// Float level (0-low | 1-mid | 2-high)

	public Long getSensorId() {
		return node;
	}

	
	public int getValue() {
		return floatLevel;
	}
}
