package xyz.hexagons.launcher.util;

public class ReflectionUtil {
	public static <T> Class getArrayClass(T... param){
		return param.getClass();
	}
}
