package main.unipi.iot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import main.unipi.iot.mqtt.MqttHandler;

public class main {

	static MqttHandler mqtt = new MqttHandler();
	
	public static void main(String[] args) {
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		String command;
		
		System.out.println("Welcome to SmartWinemaking!");	
		while(true) {
			System.out.print("Write a command:\n>");
			try {
				command = bufferedReader.readLine();
				
				if(command.equals("getTemperature")) {
					mqtt.getTemperature();
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
