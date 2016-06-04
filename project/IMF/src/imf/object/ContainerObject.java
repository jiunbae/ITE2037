package imf.object;

import java.util.ArrayList;
import java.util.List;

import imf.data.DataObject;

public class ContainerObject extends SpriteObject 
{
	List<SpriteObject> childs = new ArrayList<SpriteObject>();
	
	public ContainerObject(DataObject o) 
	{
		super(o);
	}
	
	public void add(SpriteObject o)
	{
		if (childs.isEmpty())
			o.trigger_hide = false;
		childs.add(o);
		o.pos_x += pos_x;
		o.pos_y += pos_y;
	}
}
