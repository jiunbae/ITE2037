package nimf.object;

import loot.graphics.DrawableObject3D;
import nimf.manager.ObjectManager;

public class Object extends DrawableObject3D{
	public String name;
	
	public Object() {
		this(ObjectManager.nextName());
	}
	
	public Object(String name) {
		this.name = name;
		ObjectManager.put(this);
	}
}
