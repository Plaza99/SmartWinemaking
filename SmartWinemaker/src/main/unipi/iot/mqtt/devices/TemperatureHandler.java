package main.unipi.iot.mqtt.devices;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;

import main.unipi.iot.DBManager;
import main.unipi.iot.coap.actuators.manager.CoolingManager;
import main.unipi.iot.mqtt.devices.messages.TemperatureMessage;

public class TemperatureHandler {

	private static final Gson parser = new Gson();
	private int lowerBoundTemperature = 23;
	private int upperBoundTemperature = 27;

	public void setLowerBoundTemperature(int lowerBoundTemperature) {
		if (lowerBoundTemperature < this.upperBoundTemperature) {
			System.out.println("CONSOLE - Setted lower bound temperature to: " + lowerBoundTemperature);
			this.lowerBoundTemperature = lowerBoundTemperature;
		}
		else {
			System.out.println("CONSOLE - not possible to set lower bound higher or equal than upper bound ("+this.upperBoundTemperature+")");
		}
	}

	public void setUpperBoundTemperature(int upperBoundTemperature) {
		if (this.lowerBoundTemperature < upperBoundTemperature) {
			System.out.println("CONSOLE - Setted upper bound temperature to: " + upperBoundTemperature);
			this.upperBoundTemperature = upperBoundTemperature;
		}
		else {
			System.out.println("CONSOLE - not possible to set upper bound below or equal than lower bound ("+this.lowerBoundTemperature+")");
		}
	}
	
	public int getLowerBoundTemperature() {
		return lowerBoundTemperature;
	}

	public int getUpperBoundTemperature() {
		return upperBoundTemperature;
	}
	
	public TemperatureMessage parse(MqttMessage message) {
		return parser.fromJson(new String(message.getPayload()), TemperatureMessage.class);
	}

	public int callback(TemperatureMessage parsedMessage, CoolingManager actManager) {
		
		int value = parsedMessage.getValue();
		int ret=0;
		
		if (value > upperBoundTemperature) { 		// turn on Cooling system
			actManager.getAssociatedSensor(parsedMessage.getSensorId()).sendMessage("ON");
			ret=2;
		} else if (value < lowerBoundTemperature) { // turn off Cooling system
			actManager.getAssociatedSensor(parsedMessage.getSensorId()).sendMessage("OFF");
			ret=1;
		}
		
		DBManager.getInstance().insertSampleTemperature(parsedMessage);
		return ret;
	}

}
