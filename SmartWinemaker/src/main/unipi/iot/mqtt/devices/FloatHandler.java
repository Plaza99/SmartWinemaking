package main.unipi.iot.mqtt.devices;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;

import main.unipi.iot.DBManager;
import main.unipi.iot.coap.ActuatorManager;
import main.unipi.iot.coap.actuators.manager.BypassManager;
import main.unipi.iot.mqtt.TopicHandler;
import main.unipi.iot.mqtt.TopicMessage;
import main.unipi.iot.mqtt.devices.messages.FloatMessage;

public class FloatHandler implements TopicHandler {

	private static final Gson parser = new Gson();
	private int lastFloatLevel = 0;

	@Override
	public TopicMessage parse(MqttMessage message) {
		return parser.fromJson(new String(message.getPayload()), FloatMessage.class);
	}

	@Override
	public void callback(TopicMessage parsedMessage, ActuatorManager actManager) {
		FloatMessage message = (FloatMessage) parsedMessage;
		BypassManager spoutManager = (BypassManager) actManager;
		System.out.println(message + " - last float level: " + lastFloatLevel);
		int currFloatLevel = parsedMessage.getValue();

		if (currFloatLevel == 2 && currFloatLevel != lastFloatLevel) { // Float level: high
			spoutManager.getAssociatedSensor(message.getSensorId()).sendMessage("UP");
			lastFloatLevel = currFloatLevel;
		} else if (currFloatLevel == 0 && currFloatLevel != lastFloatLevel) { // Float level: low
			spoutManager.getAssociatedSensor(message.getSensorId()).sendMessage("DOWN");
			lastFloatLevel = currFloatLevel;
		}
		//DBManager.getInstance().insertSampleFloat(message);
	}
}
