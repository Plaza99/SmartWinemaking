package main.unipi.iot.coap.actuators;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.MediaTypeRegistry;



public class Cooling {
	private String ip;
    CoapClient coapClient;

    public Cooling(String ip) {
        this.ip = ip;
        System.out.println("Cooling with ip: "+ip);
        coapClient = new CoapClient("coap://[" + ip + "]/cooling");
    }

    public void sendMessage(String message) {
        coapClient.put(message, MediaTypeRegistry.TEXT_PLAIN);
    }

    
    public String getIp() {
        return ip;
    }
}
