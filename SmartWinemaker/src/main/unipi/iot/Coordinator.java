package main.unipi.iot;

import java.nio.charset.StandardCharsets;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;

import main.unipi.iot.coap.actuators.manager.BypassManager;
import main.unipi.iot.coap.actuators.manager.CoolingManager;
import main.unipi.iot.mqtt.devices.Co2Handler;
import main.unipi.iot.mqtt.devices.FloatHandler;
import main.unipi.iot.mqtt.devices.TemperatureHandler;
import main.unipi.iot.mqtt.devices.messages.Co2Message;
import main.unipi.iot.mqtt.devices.messages.FloatMessage;
import main.unipi.iot.mqtt.devices.messages.TemperatureMessage;

public class Coordinator extends CoapServer implements MqttCallback {
	private static final String BROKER = "tcp://[::1]:1883";
	private static final String CLIENT_ID = "SmartWinemaker";

	private static final BypassManager bypassManager = new BypassManager();
	private static final CoolingManager coolingManager = new CoolingManager();
	private static final FloatHandler floatHandler = new FloatHandler();
	private static final TemperatureHandler temperatureHandler = new TemperatureHandler();
	private static final Co2Handler co2Handler = new Co2Handler();

	public BypassManager getBypassmanager() {
		return bypassManager;
	}

	public CoolingManager getCoolingmanager() {
		return coolingManager;
	}

	public FloatHandler getFloathandler() {
		return floatHandler;
	}

	public TemperatureHandler getTemperaturehandler() {
		return temperatureHandler;
	}

	public Co2Handler getCo2handler() {
		return co2Handler;
	}

	private static class CoapRegistrationResource extends CoapResource {
		public CoapRegistrationResource() {
			super("registration");
		}

		private Gson gson = new Gson();

		private static class RegistrationMessage {
			public String deviceType;
			public long sensorId;
		}

		@Override
		public void handlePOST(CoapExchange exchange) {
			final String ip = exchange.getSourceAddress().getHostAddress();
			try {
				System.err.println(exchange.getRequestText());
				RegistrationMessage m = gson.fromJson(exchange.getRequestText(), RegistrationMessage.class);

				System.out.println("New actuator at " + ip + " its sensor is " + m.sensorId + " payload is "
						+ exchange.getRequestText());

				if (m.deviceType.equals("bypass")) {
					bypassManager.registerNewActuator(m.sensorId, ip);
				}
				if (m.deviceType.equals("cooling")) {
					coolingManager.registerNewActuator(m.sensorId, ip);
				}

				//TODO DBManager.getInstance().registerActuator(ip, m.deviceType);

				exchange.respond(CoAP.ResponseCode.CREATED, "Success".getBytes(StandardCharsets.UTF_8));
			} catch (Throwable e) {
				e.printStackTrace();
				System.out.println("Unable to register coap actuator! " + ip);
				exchange.respond(CoAP.ResponseCode.NOT_ACCEPTABLE, "Unsuccessful".getBytes(StandardCharsets.UTF_8));
			}

		}

		@Override
		public void handleDELETE(CoapExchange exchange) {
			RegistrationMessage m = gson.fromJson(exchange.getRequestText(), RegistrationMessage.class);
			String ip = exchange.getSourceAddress().getHostAddress();
			System.out.println(
					"Actuator at " + ip + " is leaving the network, leaving sensor " + m.sensorId + " orphan!");

			if (m.deviceType.equals("bypass")) {
				bypassManager.deleteActuator(m.sensorId);
			}
			if (m.deviceType.equals("cooling")) {
				coolingManager.deleteActuator(m.sensorId);
			}
		}
	}

	private MqttClient mqttClient = null;

	public void connectionLost(Throwable throwable) {
		throwable.printStackTrace();
		System.out.println(throwable.getMessage());
		System.out.println("CONNECTION LOST");
		System.exit(-1);
	}

	public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

		if (topic.equals("float")) {
			FloatMessage m = floatHandler.parse(mqttMessage);
			System.out.println(
					"Incoming message from " + m.getSensorId() + " with topic " + topic + " value = " + m.getValue());
			try {
				floatHandler.callback(m, bypassManager);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		else if (topic.equals("temperature")) {
			TemperatureMessage m = temperatureHandler.parse(mqttMessage);
			System.out.println(
					"Incoming message from " + m.getSensorId() + " with topic " + topic + " value = " + m.getValue());
			try {
				temperatureHandler.callback(m, coolingManager);
			} catch (Throwable e) {
				System.out.println("Failed to run callback");
			}
		}

		else if (topic.equals("co2")) {
			Co2Message m = co2Handler.parse(mqttMessage);
			System.out.println(
					"Incoming message from " + m.getSensorId() + " with topic " + topic + " value = " + m.getValue());
		}

		else {
			System.out.println("Message arrived from unrecognized topic - ignored message");
		}

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
	}

	public Coordinator() {
		do {
			try {
				mqttClient = new MqttClient(BROKER, CLIENT_ID);
				System.out.println("Connecting to the broker: " + BROKER);
				mqttClient.setCallback(this);
				mqttClient.connect();
				String[] topics = { "float", "temperature", "co2" };
				for (String topic : topics) {
					mqttClient.subscribe(topic);
					System.out.println("Subscribed to: " + topic);
				}
			} catch (MqttException me) {
				System.out.println("I could not connect, Retrying ...");
			}
		} while (!mqttClient.isConnected());
		this.add(new CoapRegistrationResource());
	}

}
