package main.unipi.iot;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MyClient implements MqttCallback {
	
	private String topic = "alert";
	private String content = "Message from Java Client";
	private String broker = "tcp://127.0.0.1:1883";
	private String clientId = "JavaApp";

	public MyClient() throws MqttException {
		MqttClient mqttClient = new MqttClient(broker, clientId);
		mqttClient.setCallback(this);
		mqttClient.connect();
		mqttClient.subscribe(topic);
	}

	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		System.out.println(String.format("[%s] %s", topic, new String(message.getPayload())));
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
	}
}
