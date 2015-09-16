package me.wieku.hexagons.config.changer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class OptionChangeBus {

	public ArrayList<Listener> listeners = new ArrayList<Listener>();
	
	public class Data{
		
		public Data(Method method, Listener parent){
			this.method = method;
			this.parent = parent;
		}
		
		public Method method;
		public Listener parent;		
		
		public void invoke(ConfigData data) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			method.setAccessible(true);
			method.invoke(parent, data);
		}

	}
	
	
	private static OptionChangeBus instance = new OptionChangeBus();
	private HashMap<String,ArrayList<Data>> datas = new HashMap<String,ArrayList<Data>>();
	
	
	public void register(Listener listener) {
		
		
		for(Method method : listener.getClass().getMethods()){
			if(method.isAnnotationPresent(ConfigHandler.class)){
				
				if(method.getParameters().length !=1){
					new Throwable("ConfigHandler method must have only one parameter!").printStackTrace();
				} else {
					
					Class<?> param = method.getParameters()[0].getType();
					
					if(ConfigData.class.isAssignableFrom(param)){
						
						Data data = new Data(method,listener);
						
						for(String node : method.getAnnotation(ConfigHandler.class).nodes()){
							
							if(!datas.containsKey(node)){
								datas.put(node, new ArrayList<Data>());
							}
							
							datas.get(node).add(data);
							
							if(!listeners.contains(listener)){
								listeners.add(listener);
							}
							
						}
						
						
					}
					
					
				}
				
			}
		
		}
	}

	public void call(ConfigData cdata) {
		
		if(datas.get(cdata.getNode()) == null){
			return;
		}

		Iterator<Data> list = datas.get(cdata.getNode()).iterator();
		
		while(list.hasNext()){
			//System.out.println("iter");
			Data d = list.next();
			
			try {
				if(!listeners.contains(d.parent)){
					System.out.println("rem");
					list.remove();
				}
				d.invoke(cdata);
			} catch (Exception e) {
				new Throwable(String.format("Failed to send data \"%s\" to \"%s\"", cdata.getClass().getName(), d.parent.getClass().getName()), e).printStackTrace();
			}
			
		}
	}

	
	public static OptionChangeBus getBus(){
		return instance;
	}

	public void unregisterAll() {
		listeners.clear();
		
	}

	public void unregister(Listener listener) {
		listeners.remove(listener);
	}
	
}
