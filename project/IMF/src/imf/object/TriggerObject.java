package imf.object;

import imf.data.DataObject;

public class TriggerObject extends ContainerObject 
{
	int index = 0;
	
	public TriggerObject(DataObject o) 
	{
		super(o);
	}
	
	public void trigger()
	{
		childs.get(index).trigger_hide = true;
		index = (++index == childs.size() ? 0 : index);
		childs.get(index).trigger_hide = false;
	}
}