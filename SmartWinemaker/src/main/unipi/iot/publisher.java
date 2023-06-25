package main.unipi.iot;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class publisher {

	private String topic = "alert";
	private String content = "Message from Java Client";
	private String broker = "tcp://127.0.0.1:1883";
	private String clientId = "JavaApp";

	public void publish() {
		try {
			MqttClient sampleClient = new MqttClient(broker, clientId);
			sampleClient.connect();
			MqttMessage message = new MqttMessage(content.getBytes());
			sampleClient.publish(topic, message);
			sampleClient.disconnect();
			System.out.print("published");
		} catch (MqttException me) {
			me.printStackTrace();
		}
	}

}
