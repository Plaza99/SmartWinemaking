package main.unipi.iot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import main.unipi.iot.mqtt.devices.messages.Co2Message;
import main.unipi.iot.mqtt.devices.messages.FloatMessage;
import main.unipi.iot.mqtt.devices.messages.TemperatureMessage;

public class DBManager {
//TODO da fare

	private static String MySqlIp = "127.0.0.1";
	private static int MySqlPort = 3306;
	private static String MySqlUsername = "root";
	private static String MySqlPassword = "PASSWORD"; //mysqlpass
	private static String MySqlDbName = "smart_wine";
	private static DBManager instance;

	// TODO set password,ip,name,username and port
	public static DBManager getInstance() {
		if (instance == null)
			instance = new DBManager();

		return instance;
	}

	private Connection getConnection() throws SQLException {

		return DriverManager.getConnection("jdbc:mysql://" + MySqlIp + ":" + MySqlPort + "/" + MySqlDbName
				+ "?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=CET", MySqlUsername, MySqlPassword);
	}

	public void insert(String tableName, int nArg, String[] attrNames, String[] values) {

		// preparo la stringa in formato INSERT INTO table name (attr1,attr2,...,attrn)
		// VALUES (v1,v2,....,vn)
		String statementString = "INSERT INTO " + tableName + " (";

		for (int i = 0; i < nArg; i++) {
			statementString = statementString + attrNames[i];
			if (i < nArg - 1) {
				statementString += ", ";
			}
		}
		statementString += ") VALUES (";
		for (int i = 0; i < nArg; i++) {
			statementString = statementString + values[i];
			if (i < nArg - 1) {
				statementString += ", ";
			}
		}
		statementString += ")";
		System.out.println(statementString);
		try (Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(statementString);) {
			statement.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertNewActuator(String ip, String type) {
		try (Connection connection = getConnection();
				PreparedStatement statement = connection
						.prepareStatement("REPLACE INTO `actuator`(`ip`, `type`) VALUES (?, ?)");) {
			statement.setString(1, ip);
			statement.setString(2, type);
			statement.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertSampleTemperature(TemperatureMessage m) {
		String[] attr = { "id", "value" };

		String[] values = { Long.toString(m.getSensorId()), Integer.toString(m.getValue()) };
		this.insert("temperature", 2, attr, values);
	}

	/*
	 * public void insertSampleCO2(CO2Message m) { String[] attr=
	 * {"id","temperature"};
	 * 
	 * String[] values= {
	 * Long.toString(m.getSensorId()),Integer.toString(m.getValue())};
	 * this.insert("temperature", 2, attr, values); }
	 */
	public void insertSampleFloat(FloatMessage m) {
		String[] attr = { "id", "float" };

		String[] values = { Long.toString(m.getSensorId()), Integer.toString(m.getValue()) };
		this.insert("float", 2, attr, values);
	}
	
	public void insertSampleCo2(Co2Message m) {
		String[] attr = { "id", "co2" };

		String[] values = { Long.toString(m.getSensorId()), Integer.toString(m.getValue()) };
		this.insert("float", 2, attr, values);
	}
}
