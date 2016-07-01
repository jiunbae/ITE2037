package nimf.manager;

import java.util.HashMap;

import nimf.object.Object;

public class ObjectManager {

	public static HashMap<String, Object> s;
	
	public static void put(Object o) {
		s.put(o.name, o);
	}
	
	public static Object get(String name) {
		return s.get(name);
	}
	
	public static String nextName() {
		return Manager.nextName(s);
	}
}
