package me.wieku.hexagons.config;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfigEngine {

	private static HashMap<String,ConfigElement<?,?>> elements = new HashMap<String,ConfigElement<?,?>>();
	
	public static HashMap<String,HashMap<String,ArrayList<ConfigElement<?,?>>>> searchMap(String phrase){
		
		HashMap<String,HashMap<String,ArrayList<ConfigElement<?,?>>>> map = new HashMap<String,HashMap<String,ArrayList<ConfigElement<?,?>>>>();
		
		phrase = phrase.toLowerCase();
		
		for(ConfigElement<?,?> el : elements.values()){
			
			if(el.match(phrase)){
				
				HashMap<String,ArrayList<ConfigElement<?,?>>> sec = map.get(el.getSection().getI18nName());
				
				if(sec == null){
					sec = new HashMap<>();
					map.put(el.getSection().getI18nName(), sec);
				}
				
				ArrayList<ConfigElement<?,?>> sub = sec.get(el.getSubSection().getI18nName());
				
				if(sub == null){
					sub = new ArrayList<>();
					sec.put(el.getSubSection().getI18nName(), sub);
				}
				
				sub.add(el);
				
			}
			
		}
		
		return map;
	}
	
	public static ArrayList<ConfigElement<?,?>> searchList(String phrase){
		
		ArrayList<ConfigElement<?,?>> arr = new ArrayList<ConfigElement<?,?>>();
		
		phrase = phrase.toLowerCase();
		
		for(ConfigElement<?,?> el : elements.values()){
			if(el.match(phrase)){
				arr.add(el);
			}
		}
		
		return arr;
	}
	
	public static ArrayList<ConfigElement<?,?>> getElements(){
		return (ArrayList<ConfigElement<?,?>>) elements.values();
	}
	
	public static ConfigElement<?,?> getElement(String nodeName){
		return elements.get(nodeName.toLowerCase());
	}
	
	public static void addElement(ConfigElement<?,?> sec){
		elements.put(sec.getSection()+"."+sec.getSubSection()+"."+sec.getNodeName(),sec);
	}
	
	public static int matches(String phrase){
		phrase = phrase.toLowerCase();
		int i=0;
		
		for(ConfigElement<?,?> el : elements.values()){
			
			if(el.match(phrase)){
				++i;
			}
			
		}

		return i;
	}
	
	public static boolean containsElement(ConfigElement<?,?> el){
		return elements.containsValue(el);
	}
	
}
