package main.unipi.iot.coap;

public interface Actuator {
	void sendMessage(String message);
    String getIp();
}
