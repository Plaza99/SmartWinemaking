package main.unipi.iot;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.CoapEndpoint;
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
import main.unipi.iot.mqtt.devices.messages.FloatMessage;
import main.unipi.iot.mqtt.devices.messages.TemperatureMessage;

public class Coordinator extends CoapServer implements MqttCallback {
	private static final String BROKER = "tcp://[::1]:1883";
	private static final String CLIENT_ID = "SmartWinemaker";
<<<<<<< Updated upstream
	private static final BypassManager bypassManager = new BypassManager();
=======
	private static final BypassManager bpm =new BypassManager();
>>>>>>> Stashed changes
	private static final CoolingManager cm= new CoolingManager();
	private static final FloatHandler fh=new FloatHandler();
	private static final TemperatureHandler th=new TemperatureHandler();
	private static final Co2Handler co2h=new Co2Handler();
	
/*	private static final Map<String, TopicHandler> TOPICS = new HashMap<String, TopicHandler>() {
		{
			put("float", new FloatHandler());
			put("temperature", new TemperatureHandler());
			put("co2", new Co2Handler());
		}
	};

	private static final Map<String, ActuatorManager> ACTUATORS = new HashMap<String, ActuatorManager>() {
		{
			put("bypass", new BypassManager());
			put("cooling",new CoolingManager());
		}
	};

	private static final Map<String, String> TOPIC_TO_ACTUATOR = new HashMap<String, String>() {
		{
			put("float", "bypass");
		}
	};

	public TopicHandler getTopicManager(String topic) {
		return TOPICS.get(topic);
	}

	public ActuatorManager getActuatorManager(String t) {
		return ACTUATORS.get(t);
	}
*/
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
				//ACTUATORS.get(m.deviceType).registerNewActuator(m.sensorId, ip);
				if(m.deviceType.equals("bypass")){
<<<<<<< Updated upstream
					bypassManager.registerNewActuator(m.sensorId, ip);
=======
					bpm.registerNewActuator(m.sensorId, ip);
>>>>>>> Stashed changes
				}
				if(m.deviceType.equals("cooling")){
					cm.registerNewActuator(m.sensorId, ip);
				}

				// DBDriver.getInstance().registerActuator(ip, m.deviceType);

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

			//ACTUATORS.get(m.deviceType).deleteActuator(m.sensorId);
			if(m.deviceType.equals("bypass")){
<<<<<<< Updated upstream
				bypassManager.deleteActuator(m.sensorId);
=======
				bpm.deleteActuator(m.sensorId);
>>>>>>> Stashed changes
			}
			if(m.deviceType.equals("cooling")){
				cm.deleteActuator(m.sensorId);
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
		/*TopicHandler manager = TOPICS.get(topic);
		TopicMessage m = manager.parse(mqttMessage);

		System.out.println(
				"Incoming message from " + m.getSensorId() + " with topic " + topic + " value=" + m.getValue());
		if (topic.equals("float")) {
			try {
				manager.callback(m, ACTUATORS.get(TOPIC_TO_ACTUATOR.get(topic)));
			} catch (Throwable e) {
				System.out.println("Failed to run callback() bc " + e.getMessage());
			}
		}*/
		if(topic.equals("float")) {
			FloatMessage m=fh.parse(mqttMessage);
			System.out.println(
				"Incoming message from " + m.getSensorId() + " with topic " + topic + " value=" + m.getValue());
			try {
<<<<<<< Updated upstream
				fh.callback(m, bypassManager);
			} catch (Throwable e) {
				e.printStackTrace();
			}
=======
				int res=fh.callback(m, bpm);
				if(res>0) {
					if(res==2) {
						publish("bypass","UP");
					}
					else if(res==1){
						publish("bypass","DOWN");
					}
				}
			} catch (Throwable e) {
				System.out.println("Failed to run callback() bc " + e.getMessage());
			}
		}
		if(topic.equals("temperature")) {
			TemperatureMessage m=th.parse(mqttMessage);
			System.out.println(
				"Incoming message from " + m.getSensorId() + " with topic " + topic + " value=" + m.getValue());
			try {
				int res=th.callback(m, cm);
				if(res>0) {
					if(res==2) {
						publish("cooling","ON");
					}
					else if(res==1){
						publish("cooling","OFF");
					}
				}
			} catch (Throwable e) {
				System.out.println("Failed to run callback() bc " + e.getMessage());
			}
		}
		
	}

	private void publish(String topic, String content) throws MqttException{
		try {
			MqttMessage message = new MqttMessage(content.getBytes());
			this.mqttClient.publish(topic, message);
			System.out.println("Ho mandato una publish");
		} catch(MqttException me) {
			me.printStackTrace();
>>>>>>> Stashed changes
		}
		if(topic.equals("temperature")) {
			TemperatureMessage m=th.parse(mqttMessage);
			System.out.println(
				"Incoming message from " + m.getSensorId() + " with topic " + topic + " value=" + m.getValue());
			try {
				th.callback(m, cm);
			} catch (Throwable e) {
				System.out.println("Failed to run callback() bc " + e.getMessage());
			}
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
				String[] topics = {"float","temperature","co2"};
				for (String topic : topics) {
					mqttClient.subscribe(topic);
					System.out.println("Subscribed to: " + topic);
				}
				
			} catch (MqttException me) {
				System.out.println("I could not connect, Retrying ...");
			}
		} while (!mqttClient.isConnected());

		// CoAP stuff
		this.add(new CoapRegistrationResource());
	}

	public static void main(String[] args) throws UnknownHostException {
		Coordinator coordinator = new Coordinator();
		InetAddress addr = InetAddress.getByName("0.0.0.0");
		InetSocketAddress bindToAddress = new InetSocketAddress(addr, 5683);
		coordinator.addEndpoint(new CoapEndpoint(bindToAddress));
		coordinator.start();
	}

}
