package main.unipi.iot.coap.actuators;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import main.unipi.iot.coap.Actuator;

public class Bypass implements Actuator{
	private String ip;
    CoapClient coapClient;

    public Bypass(String ip) {
        this.ip = ip;
        System.out.println("Bypass with ip: "+ip);
        coapClient = new CoapClient("coap://[" + ip + "]/bypass");
    }

    public void sendMessage(String message) {
        System.out.println(message);
        coapClient.put(message, MediaTypeRegistry.TEXT_PLAIN);
    }

    @Override
    public String getIp() {
        return ip;
    }
}
