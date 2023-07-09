package main.unipi.iot.coap.actuators.manager;

import java.util.HashMap;
import java.util.Map;

import main.unipi.iot.coap.actuators.Bypass;

public class BypassManager{
	private final Map<Long, Bypass> sensorsToActuators = new HashMap<Long, Bypass>();

	public Bypass getAssociatedSensor(long id) {
		return sensorsToActuators.get(id);
	}

	public void registerNewActuator(long sensorId, String ip) {
		System.out.println("Actuator registered");
		sensorsToActuators.put(sensorId, new Bypass(ip));
	}

	public void deleteActuator(long sensorID) {
		sensorsToActuators.remove(sensorID);
	}
}
