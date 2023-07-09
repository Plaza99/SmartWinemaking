package main.unipi.iot.coap.actuators;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.MediaTypeRegistry;



public class Cooling {
	private String ip;
    CoapClient coapClient;

    public Cooling(String ip) {
        this.ip = ip;
        System.out.println("Cooling with ip: "+ip);
        coapClient = new CoapClient("coap://[" + ip + "]/bypass");
    }

    public void sendMessage(String message) {
        System.out.println(message);
        coapClient.put(message, MediaTypeRegistry.TEXT_PLAIN);
    }

    
    public String getIp() {
        return ip;
    }
}
