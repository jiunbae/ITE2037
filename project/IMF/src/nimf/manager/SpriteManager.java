package nimf.manager;

import java.util.HashMap;
import java.util.function.Consumer;

import nimf.object.PartnerObject;
import nimf.object.PlayerObject;
import nimf.object.SpriteObject;

public class SpriteManager implements IManager{
	public static HashMap<String, SpriteObject> s;
	public static PlayerObject player;
	public static PartnerObject partner;
	
	static {
		clear();
	}
	
	public static void put(SpriteObject o) {
		s.put(o.name, o);
	}
	
	public static SpriteObject get(String name) {
		return s.get(name);
	}

	public static void forEach(Consumer<SpriteObject> func) {
		s.forEach((k,o)->func.accept(o));
	}
	
	public static void clear() {
		s.clear();
		s = new HashMap<String, SpriteObject>();
		player = null;
		partner = null;
	}
}