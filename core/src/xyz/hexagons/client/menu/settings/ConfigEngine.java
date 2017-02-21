package xyz.hexagons.client.menu.settings;

import xyz.hexagons.client.menu.settings.elements.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class ConfigEngine {

	private static ArrayList<Element<?>> elements = new ArrayList<>();
	private static HashMap<String, Integer> orders = new HashMap<>();

	public static Map<String,HashMap<String,ArrayList<Element<?>>>> searchMap(String phrase){

		Map<String,HashMap<String,ArrayList<Element<?>>>> map = new TreeMap<>((o1, o2) -> orders.get(o1).compareTo(orders.get(o2)));

		phrase = phrase.toLowerCase();

		for(Element<?> el : elements) {

			if (el.match(phrase)) {

				HashMap<String, ArrayList<Element<?>>> sec = map.get(el.getSection());

				if (sec == null)
					map.put(el.getSection(), sec = new HashMap<>());

				ArrayList<Element<?>> sub = sec.get("MAIN");

				if (sub == null)
					sec.put("MAIN", sub = new ArrayList<>());

				sub.add(el);

				Collections.sort(sub, (e1,e2)->e1.getOrder().compareTo(e2.getOrder()));
			}
		}

		return map;
	}

	public static ArrayList<Element<?>> searchList(String phrase){
		ArrayList<Element<?>> arr = new ArrayList<>();

		for(Element<?> el : elements)
			if(el.match(phrase))
				arr.add(el);

		return arr;
	}

	public static int matches(String phrase){
		int i=0;

		for(Element<?> el : elements)
			if(el.match(phrase))
				++i;

		return i;
	}

	public static void register() {
		for(Field f : Settings.instance.getClass().getFields()) {
			if((f.getModifiers() & Modifier.STATIC)==0) {
				try {
					Object fieldParent = f.get(Settings.instance);
					Section sec = fieldParent.getClass().getAnnotation(Section.class);
					orders.put(sec.enName(), sec.order());
					for(Field f1 : fieldParent.getClass().getFields()) {
						if(f1.getAnnotations().length > 0) {
							Annotation an = f1.getAnnotations()[0];
							if (an instanceof Section.Slider) {
								Section.Slider sl = (Section.Slider) an;
								elements.add(new Slider(sec.name(), sec.enName(), sl.name(), sl.enName(), sl.model()[0], sl.model()[1], sl.model()[2], sl.model()[3], sl.order()));
							} else if (an instanceof Section.Switch) {
								Section.Switch sl = (Section.Switch) an;
								elements.add(new State(sec.name(), sec.enName(), sl.name(), sl.enName(), sl.def(), sl.order()));
							} else if (an instanceof Section.Combo) {
								Section.Combo sl = (Section.Combo) an;
								elements.add(new Combo(sec.name(), sec.enName(), sl.name(), sl.enName(), sl.model(), sl.def(), sl.order()));
							}  else if (an instanceof Section.Account) {
								Section.Account sl = (Section.Account) an;
								elements.add(new Account(sec.name(), sec.enName(), sl.name(), sl.enName(), sl.order()));
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
