package main.unipi.iot.coap.actuators.manager;

import java.util.HashMap;
import java.util.Map;

import main.unipi.iot.coap.actuators.Bypass;
import main.unipi.iot.coap.actuators.Cooling;

public class CoolingManager{
	private final Map<Long, Cooling> sensorsToActuators = new HashMap<Long, Cooling>();

	public Cooling getAssociatedSensor(long id) {
		return sensorsToActuators.get(id);
	}

	public void registerNewActuator(long sensorId, String ip) {
		System.out.println("Actuator registered");
		sensorsToActuators.put(sensorId, new Cooling(ip));
	}

	public void deleteActuator(long sensorID) {
		sensorsToActuators.remove(sensorID);
	}
}
