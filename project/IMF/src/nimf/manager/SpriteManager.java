package nimf.manager;

import java.util.HashMap;

import nimf.object.SpriteObject;

public class SpriteManager {
	public static HashMap<String, SpriteObject> s = new HashMap<String, SpriteObject>();
	
	public static void put(SpriteObject o) {
		s.put(o.name, o);
	}
	
	public static SpriteObject get(String name) {
		return s.get(name);
	}
	
	public static String nextName() {
		return Manager.nextName(s);
	}
}