package main.unipi.iot.mqtt.devices;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;

import main.unipi.iot.DBManager;
import main.unipi.iot.mqtt.devices.messages.Co2Message;

public class Co2Handler  {
	
	private int lastCo2Value;
	private static final Gson parser = new Gson();
	
	public Co2Message parse(MqttMessage message) {
		Co2Message ret = parser.fromJson(new String(message.getPayload()), Co2Message.class);
		setlastCo2Value(ret.getValue());	//save last co2 value
		DBManager.getInstance().insertSampleCo2(ret);
		return ret;
	}
	
	public int getlastCo2Value() {
		return lastCo2Value;
	}

	public void setlastCo2Value(int value) {
		this.lastCo2Value = value;
	}
}
