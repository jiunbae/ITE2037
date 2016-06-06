package imf.object;

import java.util.ArrayList;
import java.util.List;

import imf.data.DataObject;

public class ContainerObject extends SpriteObject 
{
	public List<SpriteObject> childs = new ArrayList<SpriteObject>();
	
	public ContainerObject(DataObject o) 
	{
		super(o);
	}
	
	public void add(SpriteObject o)
	{
		childs.add(o);
		o.pos_x += pos_x;
		o.pos_y += pos_y;
		o.pos_z += pos_z;
		o.invisible(trigger_hide);
	}
}
