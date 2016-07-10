package nimf.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.w3c.dom.Node;

import imf.Const;
import imf.utility.HashMapDefault;
import imf.utility.Pair;
import imf.utility.is;
import nimf.manager.ResourceManager;

public class DataObject {
	public static enum Type {
		none, stage, sprite, script;
		
		public static String toString(Type type) {
			switch (type) {
				case none: return "none";
				case stage: return "stage";
				case sprite: return "sprite";
				case script: return "script";
			}
			return "";
		}
		
		public static Type toType(String type) {
			switch (type) {
				case "none": return none;
				case "stage": return stage;
				case "sprite": return sprite;
				case "script": return script;
			}
			return none;
		}
	}
	
	public static List<Pair<String, HashSet<Pair<String, String>>>> attrs = new ArrayList<Pair<String, HashSet<Pair<String, String>>>>();
	public static HashMapDefault<Node, String> nodes = new HashMapDefault<Node, String>("");
	public HashMapDefault<String, String> s = new HashMapDefault<String, String>("");
	public Type type;
	
	static {
		attrs.add(new Pair<String, HashSet<Pair<String, String>>>("sprite", new HashSet<Pair<String, String>>()));
		attrs.add(new Pair<String, HashSet<Pair<String, String>>>("script", new HashSet<Pair<String, String>>()));
		attrs.get(0).second.add(new Pair<String, String>("x", "0"));
		attrs.get(0).second.add(new Pair<String, String>("y", "0"));
		attrs.get(0).second.add(new Pair<String, String>("z", "0"));
		attrs.get(0).second.add(new Pair<String, String>("w", "0"));
		attrs.get(0).second.add(new Pair<String, String>("h", "0"));
		attrs.get(0).second.add(new Pair<String, String>("vx", "0"));
		attrs.get(0).second.add(new Pair<String, String>("vy", "0"));
		attrs.get(0).second.add(new Pair<String, String>("ax", "0"));
		attrs.get(0).second.add(new Pair<String, String>("ay", "0"));
		attrs.get(0).second.add(new Pair<String, String>("enable", "true"));
		attrs.get(0).second.add(new Pair<String, String>("visible", "true"));
		attrs.get(0).second.add(new Pair<String, String>("collision", "true"));
		attrs.get(0).second.add(new Pair<String, String>("physics", "true"));
		attrs.get(0).second.add(new Pair<String, String>("absolute", "false"));
		attrs.get(0).second.add(new Pair<String, String>("texture", ""));
		attrs.get(0).second.add(new Pair<String, String>("script", ""));
		
		attrs.get(1).second.add(new Pair<String, String>("target", ""));
		attrs.get(1).second.add(new Pair<String, String>("event", ""));
		attrs.get(1).second.add(new Pair<String, String>("attribute", ""));
		attrs.get(1).second.add(new Pair<String, String>("script", ""));
		attrs.get(1).second.add(new Pair<String, String>("delay", "0"));
	}
	
	public DataObject(String type) {
		this(Type.toType(type));
	}
	
	public DataObject(Type type) {
		this.type = type;
		DataParser.datas.add(this);
		defaultData();
	}
	
	public void put(String name, String value) {
		if (is.In(name, Const.RESOURCE))
			ResourceManager.put(name, value);
		s.put(name, value);
	}
	
	public String get(String name) {
		return s.get(name);
	}
	
	public static boolean isAttribute(Type type, String attr) {
		for(Pair<String, HashSet<Pair<String, String>>> p : attrs) {
			if (p.first.equals(Type.toString(type))) 
				if (p.second.contains(attr))
					return true;
		}
		return false;
	}
	
	private void defaultData() {
		Type t = type.equals(Type.stage) ? Type.sprite : type;
		s.put("name", "");
		attrs.forEach((e)->{
			if(e.first.equals(Type.toString(t)))
				e.second.forEach((f)->s.put(f.first, f.second));
		});
	}
}
