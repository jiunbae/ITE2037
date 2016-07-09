package nimf.object;	

import java.util.HashMap;
import java.util.function.Consumer;

import loot.graphics.DrawableObject3D;
import nimf.manager.SpriteManager;

public class SpriteObject extends DrawableObject3D {
	public enum Attribute {
		x, y, z, w, h, vx, vy, ax, ay, enable, visible, collision, physics, absolute, texture, script;
		
		public static void forEach(Consumer<String> func) {
			for (Attribute attr : Attribute.values())
				func.accept(attr.name());
		}
		
		public static Attribute toAttribute(String attribute) {
			for (Attribute attr : Attribute.values())
				if (attr.name().equals(attribute))
					return attr;
			return null;
		}
	}
	public double vx = 0, vy = 0, ax = 0, ay = 0;
	public boolean enable, visible, collision, physics;
	public String absolute, texture, script;
	
	public String parent;
	public ChildObject<SpriteObject> childs = new ChildObject<SpriteObject>();
	public ChildObject<ScriptObject> scripts = new ChildObject<ScriptObject>();
	
	public SpriteObject() {
		this("");	
	}
	
	public SpriteObject(String name) {
		super(name);
		SpriteManager.put(this);
	}
	
	public SpriteObject(HashMap<String, String> data) {
		this(data.get("name"));
		
		this.trigger_coordination = true;
		parent = data.get("parent");
		if (parent.length() > 0)
			SpriteManager.get(parent).childs.put(this);
		
		
		Attribute.forEach((s)->set(s, data.get(s)));
	}
	
	public void set(String attr, String value) {
		switch (Attribute.toAttribute(attr)) {
			case x: 		pos_x = x = Integer.valueOf(value); return;
			case y: 		pos_y = y = Integer.valueOf(value); return;
			case z: 		pos_z = Double.valueOf(value); return;
			case w: 		radius_x = Integer.valueOf(value); return;
			case h: 		radius_y = Integer.valueOf(value); return;
			case vx: 		vx = Double.valueOf(value); return;
			case vy: 		vy = Double.valueOf(value); return;
			case ax: 		ax = Double.valueOf(value); return;
			case ay: 		ay = Double.valueOf(value); return;
			case enable: 	enable = value.equals("true"); return;
			case visible: 	visible = value.equals("true"); trigger_hide = !visible; return;
			case collision: collision = value.equals("true"); return;
			case physics: 	physics = value.equals("true"); return;
			case absolute: 	absolute = value; return;
			case texture: 	texture = value; return;
			case script: 	script = value; return;
		}
	}
	
	public String get(String attr) {
		switch (Attribute.toAttribute(attr)) {
			case x: 		return Integer.toString(x);
			case y: 		return Integer.toString(y);
			case z: 		return Double.toString(pos_z);
			case w: 		return Double.toString(radius_x * 2);
			case h: 		return Double.toString(radius_y * 2);
			case vx: 		return Double.toString(vx);
			case vy: 		return Double.toString(vy);
			case ax: 		return Double.toString(ax);
			case ay: 		return Double.toString(ay);
			case enable: 	return Boolean.toString(enable);
			case visible: 	return Boolean.toString(visible);
			case collision: return Boolean.toString(collision);
			case physics: 	return Boolean.toString(physics);
			case absolute: 	return absolute;
			case texture: 	return texture;
			case script: 	return script;
		}
		return null;
	}
	
	public static boolean isAttribute (String attr) {
		if (Attribute.toAttribute(attr) != null)
			return true;
		return false;
	}
}
