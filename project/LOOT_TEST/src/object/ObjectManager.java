package object;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Function;

public class ObjectManager<T> {
	HashMap<String, T> objects = new HashMap<String, T>();
	
	public void insert(String name, T object)
	{
		objects.put(name, object);
	}
	public T get(String name)
	{
		return objects.get(name);
	}
	public void remove(String name)
	{
		objects.remove(name);
	}
	public void remove(T object)
	{
		objects.remove(object);
	}
	
	public <R> void loop(Function<T, R> func)
	{
		for(Entry<String, T> e : objects.entrySet())
			func.apply(e.getValue());
	}
}
