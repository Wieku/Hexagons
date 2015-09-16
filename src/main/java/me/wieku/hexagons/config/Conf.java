package me.wieku.hexagons.config;


import me.wieku.hexagons.config.elements.ComboBoxElement;
import me.wieku.hexagons.config.elements.SliderElement;
import me.wieku.hexagons.config.elements.SwitchElement;

public class Conf {

	public static void init(){

		NamedNode debug = new NamedNode("debug", "Debug");
		
		NamedNode sound = new NamedNode("sound","Sound");
		
		NamedNode general = new NamedNode("general", "Main");

		NamedNode volume = new NamedNode("volume","Volume");

		new SliderElement("masterVolume", "Master Volume", 0f, 100f, 100f).register(new NamedNode("sound","Sound"), new NamedNode("general", "Main"));
		new SliderElement("volume", "Volume", 0f, 100f, 100f).register(sound, general);
		new SliderElement("volume", "Volume", 0f, 100f, 100f).register(sound, general);
		//new SwitchElement("fpsdbg", "FPS Debug", "OFF", "ON", false).register(debug, general);



		new ComboBoxElement("fpsLimit", "resolution", "0", "60", "120", "240", "1000").register(new NamedNode("render","Render"), general);
	}
	
}
