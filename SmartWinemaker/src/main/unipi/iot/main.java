package main.unipi.iot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.eclipse.californium.core.network.CoapEndpoint;

import main.unipi.iot.mqtt.devices.HumidityHandler;
import main.unipi.iot.mqtt.devices.messages.TemperatureMessage;

public class main {

	public static void main(String[] args)  throws SocketException, UnknownHostException{

		Coordinator coordinator = new Coordinator();
		InetAddress addr = InetAddress.getByName("0.0.0.0");
		InetSocketAddress bindToAddress = new InetSocketAddress(addr, 5683);
		coordinator.addEndpoint(new CoapEndpoint(bindToAddress));
		coordinator.start();
		
		HumidityHandler humidityHandler = (HumidityHandler) coordinator.getTopicManager("humidity");
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String command;
        String[] parts;
        //printCommandSelection();
		
		System.out.println("Welcome to SmartWinemaking!");	
		while(true) {
			System.out.print("Write a command:\n>");
			try {
				command = bufferedReader.readLine();
				
				if(command.equals("getH")) { 
					//System.out.print(humidityHandler.);
					
				}
				else if(command.equals("tryTemp")) {
					TemperatureMessage t=new TemperatureMessage();
					t.node=2;
					t.temperature=15;
					DBManager d=DBManager.getInstance();
					d.insertSampleTemperature(t);
				}
				else if(command.equals("exit")) {
					System.out.println("Closing application.");
					System.exit(0);
				}
				else{
					System.out.println("Command not recognised");
				}
				
			}
			catch(IOException e) {
				System.out.print(e.getMessage());
			}
		}
	}
}
