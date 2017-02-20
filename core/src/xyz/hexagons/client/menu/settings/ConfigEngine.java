package xyz.hexagons.client.menu.settings;

import xyz.hexagons.client.menu.settings.elements.Combo;
import xyz.hexagons.client.menu.settings.elements.Element;
import xyz.hexagons.client.menu.settings.elements.Slider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ConfigEngine {

	private static ArrayList<xyz.hexagons.client.menu.settings.elements.Element<?>> elements = new ArrayList<>();

	public static HashMap<String,HashMap<String,ArrayList<xyz.hexagons.client.menu.settings.elements.Element<?>>>> searchMap(String phrase){

		HashMap<String,HashMap<String,ArrayList<xyz.hexagons.client.menu.settings.elements.Element<?>>>> map = new HashMap<>();

		phrase = phrase.toLowerCase();

		for(xyz.hexagons.client.menu.settings.elements.Element<?> el : elements) {

			if (el.match(phrase)) {

				HashMap<String, ArrayList<xyz.hexagons.client.menu.settings.elements.Element<?>>> sec = map.get(el.getSection());

				if (sec == null) {
					sec = new HashMap<String, ArrayList<xyz.hexagons.client.menu.settings.elements.Element<?>>>();
					map.put(el.getSection(), sec);
				}

				ArrayList<xyz.hexagons.client.menu.settings.elements.Element<?>> sub = sec.get("MAIN");

				if (sub == null) {
					sub = new ArrayList<xyz.hexagons.client.menu.settings.elements.Element<?>>();
					sec.put("MAIN", sub);
				}

				sub.add(el);

				Collections.sort(sub, (e1,e2)->e1.getOrder().compareTo(e2.getOrder()));
			}
		}

		return map;
	}

	public static ArrayList<xyz.hexagons.client.menu.settings.elements.Element<?>> searchList(String phrase){

		ArrayList<xyz.hexagons.client.menu.settings.elements.Element<?>> arr = new ArrayList<xyz.hexagons.client.menu.settings.elements.Element<?>>();

		phrase = phrase.toLowerCase();

		for(xyz.hexagons.client.menu.settings.elements.Element<?> el : elements){
			if(el.match(phrase)){
				arr.add(el);
			}
		}

		return arr;
	}

	public static ArrayList<xyz.hexagons.client.menu.settings.elements.Element<?>> getElements(){
		return elements;
	}

	public static void addElement(xyz.hexagons.client.menu.settings.elements.Element<?> el){
		elements.add(el);
	}

	public static int matches(String phrase){
		phrase = phrase.toLowerCase();
		int i=0;

		for(xyz.hexagons.client.menu.settings.elements.Element<?> el : elements){

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
						if(f1.getAnnotations().length > 0) {
							Annotation an = f1.getAnnotations()[0];
							if (an instanceof Section.Slider) {
								Section.Slider sl = (Section.Slider) an;
								elements.add(new Slider(sec.name(), sec.enName(), sl.name(), sl.enName(), sl.model()[0], sl.model()[1], sl.model()[2], sl.model()[3], sl.order()));
							} else if (an instanceof Section.Switch) {
								Section.Switch sl = (Section.Switch) an;
								elements.add(new xyz.hexagons.client.menu.settings.elements.State(sec.name(), sec.enName(), sl.name(), sl.enName(), sl.def(), sl.order()));
							} else if (an instanceof Section.Combo) {
								Section.Combo sl = (Section.Combo) an;
								elements.add(new Combo(sec.name(), sec.enName(), sl.name(), sl.enName(), sl.model(), sl.def(), sl.order()));
							}
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
