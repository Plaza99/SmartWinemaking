package main.unipi.iot.mqtt;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import main.unipi.iot.coap.ActuatorManager;

public interface TopicHandler {
	TopicMessage parse(MqttMessage message);
    void callback(TopicMessage parsedMessage, ActuatorManager m);
}
