package imfg.object;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;

/**
 * Object Manager Class
 * 
 * HashMap object can contain Objects
 * 
 * @package imf.object
 * @author Maybe
 * @version 1.0.0
 */
public class ObjectManager<T> {
	public HashMap<String, T> objects = new HashMap<String, T>();

	public void insert(String name, T object) {
		objects.put(name, object);
	}

	public T get(String name) {
		return objects.get(name);
	}

	public void remove(String name) {
		objects.remove(name);
	}

	public void remove(T object) {
		objects.remove(object);
	}

	public void loop(Consumer<T> func) {
		for (Entry<String, T> e : objects.entrySet())
			func.accept(e.getValue());
	}
}