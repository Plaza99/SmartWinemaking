package main.unipi.iot.mqtt.devices;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;

import main.unipi.iot.DBManager;
import main.unipi.iot.coap.actuators.manager.BypassManager;
import main.unipi.iot.mqtt.devices.messages.FloatMessage;
public class FloatHandler {

	private static final Gson parser = new Gson();
	private int lastFloatLevel = 0;
	private int activationLevelUpperBound = 70;
	private int activationLevelLowerBound = 30;

	public int getActivationLevelUpperBound() {
		return activationLevelUpperBound;
	}
	
	public int getActivationLevelLowerBound() {
		return activationLevelLowerBound;
	}

	public void setLastFloatLevel(int lastFloatLevel) {
		this.lastFloatLevel = lastFloatLevel;
	}

	public void setActivationLevelUpperBound(int activationLevelUpperBound) {
		if (this.activationLevelLowerBound < activationLevelUpperBound) {
			System.out.println("CONSOLE - Setted activation level upper bound to: " + activationLevelUpperBound);
			this.activationLevelUpperBound = activationLevelUpperBound;
		}
		else {
			System.out.println("CONSOLE - not possible to set upper activation level below or equal than lower bound ("+this.activationLevelLowerBound+")");
		}
	}
	
	public void setActivationLevelLowerBound(int activationLevelLowerBound) {
		if (activationLevelLowerBound < this.activationLevelUpperBound) {
			System.out.println("CONSOLE - Setted activation level lower bound to: " + activationLevelLowerBound);
			this.activationLevelLowerBound = activationLevelLowerBound;
		}
		else {
			System.out.println("CONSOLE - not possible to set lower activation level higher or equal than upper bound ("+this.activationLevelUpperBound+")");
		}
	}
	
	public FloatMessage parse(MqttMessage message) {
		
		return parser.fromJson(new String(message.getPayload()), FloatMessage.class);
	}

	public int callback(FloatMessage message, BypassManager actManager) {
		System.out.println("Current float value: "+ message.getValue() + " - Last float level: " + lastFloatLevel);
		int currFloatLevel = message.getValue();
		int ret=0;

		//bypass activation depending on 'activationLevel' setting
		if (currFloatLevel > activationLevelUpperBound && currFloatLevel > lastFloatLevel) { 		// Float level: high 
			actManager.getAssociatedSensor(message.getSensorId()).sendMessage("UP");
			ret=2;
			
		} else if (currFloatLevel < activationLevelLowerBound && currFloatLevel < lastFloatLevel) {	// Float level: low
			actManager.getAssociatedSensor(message.getSensorId()).sendMessage("DOWN");
			ret=1;
		}
		lastFloatLevel = currFloatLevel;
		DBManager.getInstance().insertSampleFloat(message);
		return ret;
	}

	public int getLastFloatLevel() {
		return lastFloatLevel;
	}
}
