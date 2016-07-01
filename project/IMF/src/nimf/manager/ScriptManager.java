package nimf.manager;

import java.util.HashMap;

import nimf.object.ScriptObject;

public class ScriptManager {
	public static enum State {
		NONE(0), ALWAYS(1), HOVER(2), CLICK(3), INTERACT(4), COLLISION(5);
		private final int val;
		
		State(int val)		{ this.val = val; }
		public int get()	{ return this.val; }
		
		public static State toState(String state) {
			switch (state) {
				case "none": return NONE;
				case "always": return ALWAYS;
				case "hover": return HOVER;
				case "click": return CLICK;
				case "interact": return INTERACT;
				case "collision": return COLLISION;
			}
			return State.NONE;
		}
		public static String toString(State state) {
			switch (state) {
				case NONE: return "none";
				case ALWAYS: return "always";
				case HOVER: return "hover";
				case CLICK: return "click";
				case INTERACT: return "interact";
				case COLLISION: return "collision";
			}
			return "";
		}
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, ScriptObject>[] s_ = new HashMap[State.values().length];
	public static HashMap<String, ScriptObject> s = new HashMap<String, ScriptObject>();
	
	static {
		for(int i = 0; i < State.values().length; ++i)
			s_[i] = new HashMap<String, ScriptObject>();
	}
	
	public static void put(ScriptObject o) {
		s.put(o.name, o);
		s_[o.state.get()].put(o.name, o);
	}
	
	public static ScriptObject get(String name) {
		return s.get(name);
	}

	public static HashMap<String, ScriptObject> get(State state) {
		return s_[state.get()];
	}
	
	public static String nextName() {
		return Manager.nextName(s);
	}
}
