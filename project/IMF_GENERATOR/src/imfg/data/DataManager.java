package imfg.data;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;

import imfg.object.ContainerObject;
import imfg.object.PartnerObject;
import imfg.object.PlayerObject;
import imfg.object.SpriteObject;
import imfg.object.TriggerObject;

public class DataManager 
{
	public static PlayerObject me;
	public static PartnerObject you;
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
		containers.forEach((key, o)->{
			if (o.type.equals("trigger"))
				((TriggerObject) o).stop();
			o = null;
		});
		containers.clear();
		sprites.forEach((key, o)-> o = null);
		sprites.clear();
	}
	public static SpriteObject get(String name)
	{
		SpriteObject ret = sprites.get(name);
		if(ret == null)
			ret = containers.get(name);
		return ret;
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
