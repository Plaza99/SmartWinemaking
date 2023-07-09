package main.unipi.iot.coap.actuators.manager;

import java.util.HashMap;
import java.util.Map;

import main.unipi.iot.coap.Actuator;
import main.unipi.iot.coap.ActuatorManager;
import main.unipi.iot.coap.actuators.Bypass;

public class CoolingManager implements ActuatorManager{
	private final Map<Long, Actuator> sensorsToActuators = new HashMap<Long, Actuator>();

	public Actuator getAssociatedSensor(long id) {
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
