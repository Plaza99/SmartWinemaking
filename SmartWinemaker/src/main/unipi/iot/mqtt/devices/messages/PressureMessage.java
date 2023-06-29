package main.unipi.iot.mqtt.devices.messages;

import main.unipi.iot.mqtt.TopicMessage;

public class PressureMessage implements TopicMessage {
	public long node; // Node ID
	public int humidity;

	public Long getSensorId() {
		return node;
	}

	@Override
	public int getValue() {
		return humidity;
	}
}
