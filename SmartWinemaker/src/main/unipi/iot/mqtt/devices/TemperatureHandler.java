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
import main.unipi.iot.mqtt.devices.messages.TemperatureMessage;

public class TemperatureHandler implements TopicHandler {

	private static final Gson parser = new Gson();

	private static class Statistics {
		public static class Datum {
			public int temperature;
			public long timestamp;
		}

		public static final int lowerBoundTemperature = 15;
		public static final int upperBoundTemperature = 25;
		private final List<Datum> data = new ArrayList<>();

		public void add(int temperature) {
			Datum datum = new Datum();
			datum.temperature = temperature;
			datum.timestamp = System.currentTimeMillis();
			data.add(datum);
		}

		public int getLowerBoundTemperature() {
			return lowerBoundTemperature;
		}

		public int getUpperBoundTemperature() {
			return upperBoundTemperature;
		}

		public void clean() {
			// 30 seconds
			long thirtysecondsago = System.currentTimeMillis() - 30 * 1000L;
			data.removeIf(datum -> datum.timestamp < thirtysecondsago);
		}

		public double getAverage() {
			return data.stream().map(datum -> (double) datum.temperature) // take only the humidity
					.reduce(0.0d, Double::sum) / data.size();
		}

		public double midRange() {
			return (lowerBoundTemperature + upperBoundTemperature) / 2;
		}
	}

	private final Map<Long, Statistics> sensorsStats = new HashMap<>();

	@Override
	public TopicMessage parse(MqttMessage message) {
		return parser.fromJson(new String(message.getPayload()), TemperatureMessage.class);
	}

	@Override
	public void callback(TopicMessage parsedMessage, ActuatorManager actManager) {
		TemperatureMessage message = (TemperatureMessage) parsedMessage;
		//AcManager manager = (AcManager) actManager;

		if (!sensorsStats.containsKey(message.getSensorId()))
			sensorsStats.put(message.getSensorId(), new Statistics());

		Statistics sensorStats = sensorsStats.get(message.getSensorId());
		double oldAvg = sensorStats.getAverage();
		sensorStats.add(message.temperature);
		sensorStats.clean();
		double avg = sensorStats.getAverage();
		double midRange = sensorStats.midRange();
		String mes;
		if (avg < (sensorStats.getLowerBoundTemperature() + (midRange - sensorStats.getLowerBoundTemperature()) / 2)) {
			// INC
			mes = "INC";
		} else if (avg > (sensorStats.getUpperBoundTemperature()
				- (sensorStats.getUpperBoundTemperature() - midRange) / 2)) {
			// DEC
			mes = "DEC";
		} else {
			// OFF
			mes = "OFF";
		}
		//manager.getAssociatedSensor(message.getSensorId()).sendMessage(mes);
		DBManager.getInstance().insertSampleTemperature(message);
	}

}
