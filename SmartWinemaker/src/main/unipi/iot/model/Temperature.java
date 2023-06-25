package main.unipi.iot.model;

public class Temperature implements Topic{
	
	public static final String TEMPERATURE_TOPIC = "temperature";
	private Long nodeId;
	private int value;
	
	public Long getNodeId(){
		return nodeId;
	}
		
	public int getValue(){
		return 30;
	}
}
