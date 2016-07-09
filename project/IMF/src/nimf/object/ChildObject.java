package nimf.object;

import java.util.ArrayList;
import java.util.List;

import loot.graphics.NamedObject;

public class ChildObject<T extends NamedObject> {
	public List<T> s = new ArrayList<T>();
	
	public T get(String name) {
		for(T t : s)
			if(t.name.equals(name))
				return t;
		return null;
	}
	
	public void put(T t) {
		s.add(t);
	}
}
