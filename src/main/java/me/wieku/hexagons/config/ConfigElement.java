package me.wieku.hexagons.config;

import me.wieku.hexagons.utils.Json;

import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class ConfigElement<T,V extends Actor>{
	
	private T value;
	private T defValue;
	private NamedNode section;
	private NamedNode subsection;
	private String nodeName;
	private String i18nName;
	private V actor;
	
	
	public ConfigElement(String nodeName, String name,T defValue){
		this.nodeName = nodeName;
		i18nName = name;
		this.defValue = defValue;
		this.value = this.defValue;
	}
	
	public abstract void updateActorValue(T value, V actor);
	public abstract T getActorValue(V actor);
	
	public void setActor(V actor){
		this.actor = actor;
	}
	
	public T getValue(){
		return value;
	}
	
	public String getName(){
		return i18nName;
	}
	
	public V getActor(){
		return actor;
	};
	
	public T getDefaultValue(){
		return defValue;
	};
	
	public void updateValue(){
		this.value = getActorValue(actor);
		System.out.println(value);
		//OptionChangeBus.getBus().call(new ConfigData(getFullNodeName(),this.value));
	};
	
	@SuppressWarnings("unchecked")
	public void load(Json json){
		if(!json.contains(nodeName)){
			json.set(getFullNodeName(), value = getDefaultValue());
		}
		value = (T) json.getObject(nodeName);
		updateActorValue(value, actor);
	}
	
	public void save(Json json){
		json.set(getFullNodeName(), value);
	}
	
	public boolean match(String phrase){
		phrase = phrase.toLowerCase();
		return (section !=null?section.getI18nName().toLowerCase().contains(phrase):false) || (subsection !=null?subsection.getI18nName().toLowerCase().contains(phrase):false) || i18nName.toLowerCase().contains(phrase);
	}
	
	public void register(NamedNode section, NamedNode subsection){
		if(!ConfigEngine.containsElement(this)){
			this.section = section;
			this.subsection = subsection;
			ConfigEngine.addElement(this);
			updateActorValue(value, actor);
		} else {
			System.err.println("Cannot reregister ConfigElement!");
		}
	}
	
	public NamedNode getSection(){
		return section;
	}
	
	public NamedNode getSubSection(){
		return subsection;
	}
	
	public String getNodeName(){
		return nodeName.toLowerCase();
	}
	
	public String getFullNodeName(){
		return getSection().getNodeName()+"."+getSubSection().getNodeName()+"."+getNodeName();
	}
	
}
