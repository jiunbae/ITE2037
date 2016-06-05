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
	}
}
