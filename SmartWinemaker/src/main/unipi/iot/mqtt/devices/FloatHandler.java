package main.unipi.iot.mqtt.devices;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;

import main.unipi.iot.DBManager;
import main.unipi.iot.coap.actuators.manager.BypassManager;
import main.unipi.iot.mqtt.devices.messages.FloatMessage;
public class FloatHandler {

	private static final Gson parser = new Gson();
	private static int maxFloatLevel=99;
	private static int minFloatLevel=1;
	private int lastFloatLevel = 0;

	
	public FloatMessage parse(MqttMessage message) {
		return parser.fromJson(new String(message.getPayload()), FloatMessage.class);
	}

	
<<<<<<< Updated upstream
	public void callback(FloatMessage message, BypassManager actManager) {
		System.out.println("Current float value: "+ message.getValue() + " - Last float level: " + lastFloatLevel);
		int currFloatLevel = message.getValue();
		
		if (currFloatLevel == 2 && currFloatLevel != lastFloatLevel) { // Float level: high
			actManager.getAssociatedSensor(message.getSensorId()).sendMessage("UP");
			
		} else if (currFloatLevel == 0 && currFloatLevel != lastFloatLevel) { // Float level: low
			actManager.getAssociatedSensor(message.getSensorId()).sendMessage("DOWN");
		}
		lastFloatLevel = currFloatLevel;
		
=======
	public int callback(FloatMessage message, BypassManager actManager) {
		System.out.println("Current float value: "+ message.getValue() + " - Last float level: " + lastFloatLevel);
		int currFloatLevel = message.getValue();
		
		if (currFloatLevel > 70 && currFloatLevel != lastFloatLevel) { // Float level: high
			actManager.getAssociatedSensor(message.getSensorId()).sendMessage("UP");
			return 2;
		} else if (currFloatLevel < 40  && currFloatLevel != lastFloatLevel) { // Float level: low
			actManager.getAssociatedSensor(message.getSensorId()).sendMessage("DOWN");
			return 1;
		}
		
		lastFloatLevel = currFloatLevel;
>>>>>>> Stashed changes
		DBManager.getInstance().insertSampleFloat(message);
		return 0;
	}
}
