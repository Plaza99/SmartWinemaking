package main.unipi.iot.coap;

public interface ActuatorManager {
    Actuator getAssociatedSensor(long id);
    void registerNewActuator(long sensorId, String ip);
    void deleteActuator(long sensorId);
}
