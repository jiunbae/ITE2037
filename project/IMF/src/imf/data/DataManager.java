package imf.data;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;

import imf.object.ContainerObject;
import imf.object.ObjectManager;
import imf.object.SpriteObject;

public class DataManager 
{
	static HashMap<String, SpriteObject> sprites = new HashMap<String, SpriteObject>();
	static HashMap<String, ContainerObject> containers = new HashMap<String, ContainerObject>();

	public static void add(String name, ContainerObject o)
	{
		containers.put(name, o);
	}
	public static void add(String name, SpriteObject o)
	{
		sprites.put(name, o);
	}
	public static void remove(String name)
	{
		containers.remove(name);
		sprites.remove(name);
	}
	public static void removeAll()
	{
		containers.clear();
		sprites.clear();
	}
	
	public static SpriteObject get_sprites(String name)
	{
		return sprites.get(name);
	}
	public static ContainerObject get_containers(String name)
	{
		return containers.get(name);
	}
	
	public static void loop_sprites(Consumer<SpriteObject> func)
	{
		for(Entry<String, SpriteObject> e : sprites.entrySet())
			func.accept(e.getValue());
	}
	public static void loop_containers(Consumer<ContainerObject> func)
	{
		for(Entry<String, ContainerObject> e : containers.entrySet())
			func.accept(e.getValue());
	}
}
