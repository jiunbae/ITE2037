package nimf.manager;

import java.util.HashMap;
import java.util.function.Consumer;

import nimf.object.ScriptObject;
import nimf.object.ScriptObject.Event;

public class ScriptManager implements IManager{

	public static HashMap<String, ScriptObject>[] s_;
	public static HashMap<String, ScriptObject> s;
	
	static {
		clear();
	}
	
	public static void put(ScriptObject o) {
		s.put(o.name, o);
	}
	
	public static ScriptObject get(String name) {
		return s.get(name);
	}

	public static HashMap<String, ScriptObject> get(Event state) {
		return s_[state.get()];
	}

	public static void forEach(Consumer<ScriptObject> func) {
		s.forEach((k,o)->func.accept(o));
	}
	
	@SuppressWarnings("unchecked")
	public static void clear() {
		s = new HashMap<String, ScriptObject>();
		s_ = new HashMap[Event.values().length];
		for(int i = 0; i < Event.values().length; ++i)
			s_[i] = new HashMap<String, ScriptObject>();
	}
}
