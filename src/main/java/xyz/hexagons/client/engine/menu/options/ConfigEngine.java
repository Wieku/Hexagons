package xyz.hexagons.client.engine.menu.options;

import xyz.hexagons.client.engine.Section;
import xyz.hexagons.client.engine.Settings;
import xyz.hexagons.client.engine.menu.buttons.Element;
import xyz.hexagons.client.engine.menu.buttons.Slider;
import xyz.hexagons.client.engine.menu.buttons.State;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigEngine {

	private static ArrayList<Element<?>> elements = new ArrayList<>();

	public static HashMap<String,HashMap<String,ArrayList<Element<?>>>> searchMap(String phrase){

		HashMap<String,HashMap<String,ArrayList<Element<?>>>> map = new HashMap<>();

		phrase = phrase.toLowerCase();

		for(Element<?> el : elements) {

			if (el.match(phrase)) {

				HashMap<String, ArrayList<Element<?>>> sec = map.get(el.getSection());

				if (sec == null) {
					sec = new HashMap<String, ArrayList<Element<?>>>();
					map.put(el.getSection(), sec);
				}

				ArrayList<Element<?>> sub = sec.get("MAIN");

				if (sub == null) {
					sub = new ArrayList<Element<?>>();
					sec.put("MAIN", sub);
				}

				sub.add(el);
				sub.sort((e1,e2)->e1.getOrder().compareTo(e2.getOrder()));
			}
		}

		return map;
	}

	public static ArrayList<Element<?>> searchList(String phrase){

		ArrayList<Element<?>> arr = new ArrayList<Element<?>>();

		phrase = phrase.toLowerCase();

		for(Element<?> el : elements){
			if(el.match(phrase)){
				arr.add(el);
			}
		}

		return arr;
	}

	public static ArrayList<Element<?>> getElements(){
		return elements;
	}

	public static void addElement(Element<?> el){
		elements.add(el);
	}

	public static int matches(String phrase){
		phrase = phrase.toLowerCase();
		int i=0;

		for(Element<?> el : elements){

			if(el.match(phrase)){
				++i;
			}

		}

		return i;
	}

	public static void register() {
		for(Field f : Settings.instance.getClass().getFields()) {
			if((f.getModifiers() & Modifier.STATIC)==0) {
				try {
					Object fieldParent = f.get(Settings.instance);
					Section sec = fieldParent.getClass().getAnnotation(Section.class);
					for(Field f1 : fieldParent.getClass().getFields()) {
						System.out.println(f1.getName());
						Annotation an = f1.getAnnotations()[0];
						if(an instanceof Section.Slider) {
							Section.Slider sl = (Section.Slider) an;
							elements.add(new Slider(sec.name(), sec.enName(), sl.name(), sl.enName(), sl.model()[0], sl.model()[1], sl.model()[2], sl.model()[3], sl.order()));
						} else if (an instanceof Section.Switch) {
							Section.Switch sl = (Section.Switch) an;
							elements.add(new State(sec.name(), sec.enName(), sl.name(), sl.enName(), sl.def(), sl.order()));
						}
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public static boolean containsElement(Element<?> el){
		return elements.contains(el);
	}

}
