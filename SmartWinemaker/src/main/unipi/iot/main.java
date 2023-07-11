package main.unipi.iot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.eclipse.californium.core.network.CoapEndpoint;

import main.unipi.iot.mqtt.devices.messages.TemperatureMessage;

public class main {

	public static void main(String[] args) throws SocketException, UnknownHostException {

		Coordinator coordinator = new Coordinator();
		InetAddress addr = InetAddress.getByName("0.0.0.0");
		InetSocketAddress bindToAddress = new InetSocketAddress(addr, 5683);
		coordinator.addEndpoint(new CoapEndpoint(bindToAddress));
		coordinator.start();

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		String command;
		System.out.println("****Welcome to SmartWinemaking!****");

		while (true) {
			try {
				command = bufferedReader.readLine();
				String[] parts = command.split(" ");

				// SETTERS
				if (parts[0].equals("set")) {

					// SET UPPER BOUND TEMPERATURE
					if (parts[1].equals("lowTemp") && parts.length == 3) {
						try {
							coordinator.getTemperaturehandler().setLowerBoundTemperature(Integer.parseInt(parts[2]));
						} catch (Exception e) {
							System.out.println("CONSOLE - Command not recognised");
						}

						// SET LOWER BOUND TEMPERATURE
					} else if (parts[1].equals("upTemp") && parts.length == 3) {
						try {
							coordinator.getTemperaturehandler().setUpperBoundTemperature(Integer.parseInt(parts[2]));
						} catch (Exception e) {
							System.out.println("CONSOLE - Command not recognised");
						}

						// SET FLOAT LEVEL BYPASS ACTIVATION UPPER BOUND
					} else if (parts[1].equals("activationUp") && parts.length == 3) {
						try {
							int newActivationLevel = Integer.parseInt(parts[2]);
							coordinator.getFloathandler().setActivationLevelUpperBound(newActivationLevel);
						} catch (Exception e) {
							System.out.println("CONSOLE - Command not recognised");
						}

					}
					// SET FLOAT LEVEL BYPASS ACTIVATION LOWER BOUND
					else if (parts[1].equals("activationLow") && parts.length == 3) {
						try {
							int newActivationLevel = Integer.parseInt(parts[2]);
							coordinator.getFloathandler().setActivationLevelLowerBound(newActivationLevel);
						} catch (Exception e) {
							System.out.println("CONSOLE - Command not recognised");
						}

					} else { // invalid command parts
						System.out.println("CONSOLE - Command not recognised");
					}

					// GETTERS
				} else if (parts[0].equals("get")) {

					// GET TEMPERATURE BOUNDS VALUES
					if (parts[1].equals("temp") && parts.length == 2) {
						int low = coordinator.getTemperaturehandler().getLowerBoundTemperature();
						int up = coordinator.getTemperaturehandler().getUpperBoundTemperature();
						System.out.println("CONSOLE - Temperature lower bound: " + low + " and upper bound: " + up);
					}

					// GET BYPASS CURRENT ACTIVATION FROM FLOAT LEVEL
					else if (parts[1].equals("activation") && parts.length == 2) {
						int low = coordinator.getFloathandler().getActivationLevelLowerBound();
						int up = coordinator.getFloathandler().getActivationLevelUpperBound();
						System.out.println("CONSOLE - Float level activation lower bound: " + low + " and upper bound: " + up);
					}
					
					// GET LAST CO2 VALUE
					else if (parts[1].equals("co2") && parts.length == 2) {
						int co2 = coordinator.getCo2handler().getlastCo2Value();
						System.out.println("CONSOLE - Last Co2 value registered is: " + co2 + "%");
					} else { // invalid command parts
						System.out.println("CONSOLE - Command not recognised");
					}
				}

				// EXIT APPLICATION
				else if (command.equals("exit")) {
					System.out.println("CONSOLE - Closing application.");
					System.exit(0);

					// COMMAND NOT RECOGNIZED
				} else {
					System.out.println("CONSOLE - Command not recognised");
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
