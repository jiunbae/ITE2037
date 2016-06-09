package imf.data;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;

import imf.object.ContainerObject;
import imf.object.PartnerObject;
import imf.object.PlayerObject;
import imf.object.SpriteObject;
import imf.object.TriggerObject;
import imf.processor.Interaction;

public class DataManager 
{
	static Interaction interaction;
	public static PlayerObject me;
	public static PartnerObject you;
	static HashMap<String, SpriteObject> sprites = new HashMap<String, SpriteObject>();
	static HashMap<String, ContainerObject> containers = new HashMap<String, ContainerObject>();

	public static void setAction(Interaction interaction)
	{
		DataManager.interaction = interaction;
	}
	public static Interaction action()
	{
		return DataManager.interaction;
	}
	
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
