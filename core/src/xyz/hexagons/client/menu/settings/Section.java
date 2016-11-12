package xyz.hexagons.client.menu.settings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Section {

	String name();
	String enName();
	int order();

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface Switch {
		String name();
		String enName();
		boolean def();
		int order();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface Slider{
		String name();
		String enName();
		int[] model();
		int order();
	}

}
