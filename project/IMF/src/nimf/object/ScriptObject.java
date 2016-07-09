package nimf.object;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import imf.Const;
import imf.utility.Pair;
import imf.utility.is;
import imf.validate.ValidateException;
import loot.graphics.NamedObject;
import nimf.data.DataObject;
import nimf.data.DataObject.Type;
import nimf.data.ResourceManager;
import nimf.event.TimerEvent;
import nimf.manager.ScriptManager;
import nimf.manager.SpriteManager;

public class ScriptObject extends NamedObject{
	public enum Attribute {
		
	}
	public static enum Event {
		NEVER(0), ALWAYS(1), TIMER(2), 
		oENTER(2), oLEAVE(3), oJOIN(4), oINTERACT(5),
		mENTER(2), mLEAVE(3), mJOIN(4), mINTERACT(5);
		private final int val;
		
		Event(int val)		{ this.val = val; }
		public int get()	{ return this.val; }
		
		public static Event toEvent(String state) {
			switch (state) {
				case "never": return NEVER;
				case "always": return ALWAYS;
				case "timer": return TIMER;
				case "object@enter": return oENTER;
				case "object@leave": return oLEAVE;
				case "object@join": return oJOIN;
				case "object@interact": return oINTERACT;
				case "mouse@enter": return mENTER;
				case "mouse@leave": return mLEAVE;
				case "mouse@join": return mJOIN;
				case "mouse@interact": return mINTERACT;
			}
			return Event.NEVER;
		}
		public static String toString(Event state) {
			switch (state) {
				case NEVER: return "never";
				case ALWAYS: return "always";
				case TIMER: return "timer";
				case oENTER: return "object@enter";
				case oLEAVE: return "object@leave";
				case oJOIN: return "object@join";
				case oINTERACT: return "object@interact";
				case mENTER: return "mouse@enter";
				case mLEAVE: return "mouse@leave";
				case mJOIN: return "mouse@join";
				case mINTERACT: return "mouse@interact";
			}
			return "never";
		}
	}
	
	public String parent = "", target = "", attribute = "", attr_name = "", attr_value = "", text;
	public Event event;
	public int delay = 0;
	public List<Pair<Pair<String, String>, String>> script;
	
	public ScriptObject() {
		this("");
	}

	public ScriptObject(String name) {
		super(name);
		ScriptManager.put(this);
	}
	
	public ScriptObject(HashMap<String, String> data) {
		this(data.get("name"));
		
		// get attributes from data
		parent = data.get("parent");
		if (parent != null)
			SpriteManager.get(parent).scripts.put(this);
		
		target = data.get("target");
		event = Event.toEvent(data.get("event"));
		attribute = data.get("attribute");
		delay = Integer.valueOf(data.get("delay"));
		script = parse(data.get("script"));
		parse(attribute, attr_name, attr_value);
		
		// check validate
		if(Const.validate)
			try {
				validate(data.get("event"));
			} catch (ValidateException v){
				System.out.println("scripte validate error: " + data.get("event") + " is not a event");
			}
		ScriptManager.s_[event.get()].put(name, this);
	}
	
	public void run() {
		script.forEach((s)-> {
			String target = s.first.first, attr = s.first.second, rvalue = s.second;
			if (target.length() < 1)
				throw new ValidateException();
			SpriteObject obj = SpriteManager.get(target);
			
			if (obj == null)
				throw new ValidateException();
			
			if (!DataObject.isAttribute(Type.sprite, attr))
				throw new ValidateException();
			
			if (rvalue.indexOf(".") != -1)
				rvalue = SpriteManager.get(rvalue.substring(0, rvalue.indexOf("."))).get(rvalue.substring(rvalue.indexOf("." + 1)));
			
			obj.set(attr, rvalue);
		});
		
		if (event.equals(Event.ALWAYS)) 
			TimerEvent.put(this);
	}
	
	private static boolean validate(String event) {
		if (Event.toString(Event.toEvent(event)).equals(event))
			return true;
		throw new ValidateException();
	}
	
	private static void parse(String attribute, String attr_name, String attr_value) {
		int pos = attribute.indexOf('=');
		if (pos == -1)
			return;
		attr_name = attribute.substring(0, pos);
		attr_value = attribute.substring(pos + 1);
	}

	private static List<Pair<Pair<String, String>, String>> parse(String script)
	{
		List<Pair<Pair<String, String>, String>> ret = new ArrayList<Pair<Pair<String, String>, String>>();
		if (script.length() > 0)
			Arrays.asList(script.split("\n")).forEach((line)->{
				line = line.trim();
				String l = line.substring(0, line.indexOf("=")), r = line.substring(line.indexOf("=") + 1);
				String target = l.substring(0, l.indexOf(".")), attr = l.substring(l.indexOf(".")+1);
				if (Const.validate) {
					if (!NamedObject.names.contains(target)) {
						// error
						// can't find target
					}
					
					if (is.In(attr, Const.RESOURCE)) {
						ResourceManager.put(attr, r);
					}
				}
				ret.add(new Pair<Pair<String, String>, String>(new Pair<String, String>(target, attr), r));
			});
		return ret;
	}
}










