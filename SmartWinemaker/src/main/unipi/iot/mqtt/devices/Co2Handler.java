package main.unipi.iot.mqtt.devices;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;

import main.unipi.iot.mqtt.devices.messages.Co2Message;

public class Co2Handler  {
	private static final Gson parser = new Gson();
	
	public Co2Message parse(MqttMessage message) {
		return parser.fromJson(new String(message.getPayload()), Co2Message.class);
	}
}
