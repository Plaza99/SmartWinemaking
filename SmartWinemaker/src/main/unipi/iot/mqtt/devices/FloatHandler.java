package main.unipi.iot.mqtt.devices;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;

import main.unipi.iot.coap.ActuatorManager;
import main.unipi.iot.mqtt.TopicHandler;
import main.unipi.iot.mqtt.TopicMessage;
import main.unipi.iot.mqtt.devices.messages.FloatMessage;

public class FloatHandler implements TopicHandler {
	
	private static final Gson parser = new Gson();
	public TopicMessage parse(MqttMessage message) {
		return parser.fromJson(new String(message.getPayload()), FloatMessage.class);
	}

	public void callback(TopicMessage parsedMessage, ActuatorManager actManager) {
		FloatMessage message = (FloatMessage) parsedMessage;
		System.out.println(message);
		//PumpManager manager = (PumpManager) actManager;
		//DBDriver.getInstance().insertFloatLevelSample(message);
	}
}
