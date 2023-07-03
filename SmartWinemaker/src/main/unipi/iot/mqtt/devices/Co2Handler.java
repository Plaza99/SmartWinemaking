package main.unipi.iot.mqtt.devices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;

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

		public double average() {
			return data.stream().map(datum -> (double) datum.co2) // take only the humidity
					.reduce(0.0d, Double::sum) / data.size();
		}
	}

	private final Map<Long, Co2Handler.Statistics> sensorsStats = new HashMap<>();

	@Override
	public TopicMessage parse(MqttMessage message) {
		return parser.fromJson(new String(message.getPayload()), Co2Message.class);
	}

	private double avg = 0.0;

	public double getAvg() {
		return avg;
	}

	@Override
	public void callback(TopicMessage parsedMessage, ActuatorManager actManager) {
		Co2Message message = (Co2Message) parsedMessage;
		// HumidifierManager manager = (HumidifierManager) actManager;

		// @TODO Inserisci nella base di dati
		if (!sensorsStats.containsKey(message.getSensorId()))
			sensorsStats.put(message.getSensorId(), new Statistics());
		Co2Handler.Statistics sensorStats = sensorsStats.get(message.getSensorId());
		double oldAvg = sensorStats.average();
		sensorStats.add(message.co2);
		sensorStats.clean();
		avg = sensorStats.average();
		double midRange = (upperBoundHumidity + lowerBoundHumidity) / 2.0;
		String mes;
		if (avg < (lowerBoundHumidity + (midRange - lowerBoundHumidity)) / 2) {
			// INC
			mes = "INC";
		} else if (avg > (upperBoundHumidity - (upperBoundHumidity - midRange) / 2)) {
			// DEC
			mes = "DEC";
		} else {
			// OFF
			mes = "OFF";
		}
		// manager.getAssociatedSensor(message.getSensorId()).sendMessage();
		// DBDriver.getInstance().insertHumiditySample(message);
	}

}
