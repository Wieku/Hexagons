package me.wieku.hexagons.config.changer;

public class ConfigData {
	private String node;
	private Object object;
	
	public ConfigData(String node, Object value) {
		this.node = node;
		object = value;
	}
	
	public String getNode() {
		return node;
	}
	
	public Object getValue() {
		return object;
	}
}
