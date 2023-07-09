package main.unipi.iot.mqtt.devices.messages;



public class Co2Message{
	public long node; 		// Node ID
    public int co2;			// Co2 percentage
    
    public Long getSensorId() {
        return node;
    }

    
    public int getValue() {
        return co2;
    }
}
