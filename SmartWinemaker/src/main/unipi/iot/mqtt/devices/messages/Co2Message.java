package main.unipi.iot.mqtt.devices.messages;

import main.unipi.iot.mqtt.TopicMessage;

public class Co2Message implements TopicMessage{
	public long node; 		// Node ID
    public int co2;			// Co2 percentage
    
    public Long getSensorId() {
        return node;
    }

    @Override
    public int getValue() {
        return co2;
    }
}
