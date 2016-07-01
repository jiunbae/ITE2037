package nimf.object;

import java.util.HashMap;

import nimf.manager.SpriteManager;

public class SpriteObject extends Object {
	double vx = 0, vy = 0, ax = 0, ay = 0;
	boolean enable, visible, collision, physics;
	String absolute, texture, script;
	
	public SpriteObject parent;
	public ChildObject<SpriteObject> childs;
	
	public SpriteObject() {
		super();
	}
	
	public SpriteObject(String name) {
		super(name);
	}
	
	public SpriteObject(HashMap<String, String> data) {
		parent = SpriteManager.get(data.get("parent"));
		parent.childs.put(this);
		
		this.x = Integer.valueOf(data.get("x"));
		this.y = Integer.valueOf(data.get("y"));
		this.pos_z = Integer.valueOf(data.get("z"));
		this.vx = Integer.valueOf(data.get("vx"));
		this.vy = Integer.valueOf(data.get("vy"));
		this.ax = Integer.valueOf(data.get("ax"));
		this.ay = Integer.valueOf(data.get("ay"));
		this.width = Integer.valueOf(data.get("width"));
		this.height = Integer.valueOf(data.get("height"));
		
		this.enable = data.get("enable").equals("true");
		this.visible = data.get("visible").equals("true");
		this.collision = data.get("collision").equals("true");
		this.physics = data.get("physics").equals("true");
		
		this.absolute = data.get("absolute");
		this.texture = data.get("texture");
		this.script = data.get("script");
	}
}
