package main.unipi.iot.mqtt.devices.messages;

import main.unipi.iot.mqtt.TopicMessage;

public class FloatMessage implements TopicMessage {
	public long node; 		// Node ID
	public int floatLevel;	// Float level (0-low | 1-mid | 2-high)

	public Long getSensorId() {
		return node;
	}

	@Override
	public int getValue() {
		return floatLevel;
	}
}
