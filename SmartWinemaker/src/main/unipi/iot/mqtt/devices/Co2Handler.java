package main.unipi.iot.mqtt.devices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;

import main.unipi.iot.DBManager;
import main.unipi.iot.coap.ActuatorManager;
import main.unipi.iot.mqtt.TopicHandler;
import main.unipi.iot.mqtt.TopicMessage;
import main.unipi.iot.mqtt.devices.messages.Co2Message;

public class Co2Handler implements TopicHandler {
	public static int lowerBoundHumidity = 40;
	public static int upperBoundHumidity = 60;
	private static final Gson parser = new Gson();

	private static class Statistics {
		public static class Datum {
			public int co2;
			public long timestamp;
		}

		private final List<Datum> data = new ArrayList<>();

		public void add(int co2) {
			Datum datum = new Datum();
			datum.co2 = co2;
			datum.timestamp = System.currentTimeMillis();

			data.add(datum);
		}

		public void clean() {
			// ~ 30 secondi
			long thirtysecondsago = System.currentTimeMillis() - 30 * 1000L;
			data.removeIf(datum -> datum.timestamp < thirtysecondsago);
		}
	}

	private final Map<Long, Co2Handler.Statistics> sensorsStats = new HashMap<>();

	@Override
	public TopicMessage parse(MqttMessage message) {
		return parser.fromJson(new String(message.getPayload()), Co2Message.class);
	}

	@Override
	public void callback(TopicMessage parsedMessage, ActuatorManager actManager) {
		Co2Message message = (Co2Message) parsedMessage;
		if (!sensorsStats.containsKey(message.getSensorId()))
			sensorsStats.put(message.getSensorId(), new Statistics());
		Co2Handler.Statistics sensorStats = sensorsStats.get(message.getSensorId());
		sensorStats.add(message.co2);
		sensorStats.clean();
		DBManager.getInstance().insertSampleCo2(message);
	}
}
