package main.unipi.iot.mqtt;

import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import main.unipi.iot.model.Temperature;

public class MqttHandler implements MqttCallback {

	private static final String BROKER = "tcp://127.0.0.1:1883";
	private static final String CLIENT_ID = "SmartWinemaking";

	private static final int SECONDS_TO_WAIT_FOR_RECONNECTION = 5;
	private static final int MAX_RECONNECTION_ITERATIONS = 10;

	private MqttClient mqttClient = null;
	// private Gson parser;
	// private HumidityCollector humidityCollector;

	private Temperature temperature = null;
	ArrayList<Integer> temperatureList = new ArrayList<>();

	public MqttHandler() {
		temperature = new Temperature();

		do {
			try {
				mqttClient = new MqttClient(BROKER, CLIENT_ID);
				System.out.println("Connecting to the broker: " + BROKER);
				mqttClient.setCallback(this);
				mqttClient.connect();
				mqttClient.subscribe(Temperature.TEMPERATURE_TOPIC);
				System.out.println("Subscribed to: " + Temperature.TEMPERATURE_TOPIC);
			} catch (MqttException me) {
				System.out.println("I could not connect, Retrying ...");
			}
		} while (!mqttClient.isConnected());
	}

	public void connectionLost(Throwable throwable) {
		System.out.println("Connection with the Broker lost!");
		// We have lost the connection, we have to try to reconnect after waiting some
		// time
		// At each iteration we increase the time waited
		int iter = 0;
		do {
			iter++; // first iteration iter=1
			if (iter > MAX_RECONNECTION_ITERATIONS) {
				System.err.println("Reconnection with the broker not possible!");
				System.exit(-1);
			}
			try {
				Thread.sleep(SECONDS_TO_WAIT_FOR_RECONNECTION * 1000 * iter);
				System.out.println("New attempt to connect to the broker...");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (!this.mqttClient.isConnected());
		System.out.println("Connection with the Broker restored!");
	}

	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
		System.out.println("Message correctly delivered");
	}

	public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
		if (topic.equals(Temperature.TEMPERATURE_TOPIC)) {
			temperatureList.add(Integer.parseInt(mqttMessage.toString()));
		}
		//System.out.println(String.format("[%s] %s", topic, new String(mqttMessage.getPayload())));
	}

	public void getTemperature() {
		System.out.println("Getting temperature...");
		int value = temperatureList.stream().mapToInt(Integer::intValue).sum();
		String msg = "Temperature is: " + value;
		System.out.println(msg);
	}

}
