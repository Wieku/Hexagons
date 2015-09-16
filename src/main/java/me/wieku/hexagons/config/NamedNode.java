package me.wieku.hexagons.config;

import com.google.common.base.Objects;

public class NamedNode {

	private String nodeName;
	private String i18nName;

	public NamedNode(String nodeName, String i18nName){
		this.nodeName = nodeName;
		this.i18nName = i18nName;
	}

	public String getNodeName() {
		return nodeName.toLowerCase();
	}
	
	public String getI18nName() {
		return i18nName;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(nodeName, i18nName);
	}
	
}
