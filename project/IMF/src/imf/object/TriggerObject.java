package imf.object;

import java.util.Timer;
import java.util.TimerTask;

import imf.data.DataObject;

public class TriggerObject extends ContainerObject 
{
	int index = 0;
	
	public TriggerObject(DataObject o) 
	{
		super(o);
	}
	
	@Override
	public void add(SpriteObject o)
	{
		if(o==null)
			return;
		if(!childs.isEmpty())
			o.visible(true);
		childs.add(o);
	}
	
	@Override
	public void visible(boolean value)
	{
		trigger_hide = value;
		childs.forEach((o) -> o.trigger_hide = value);
	}
	
	public void trigger()
	{
		next();
	}
	
	public void next()
	{
		childs.get(index).visible(true);
		index = (++index == childs.size() ? 0 : index);
		childs.get(index).visible(false);
		if(childs.get(index).interval != 0)
		{
			Timer timer = new Timer();
			timer.schedule(new WorkTask(), childs.get(index).interval);
		}
	}
	
	public class WorkTask extends TimerTask {
		@Override
		public void run() {
			next();
		}
	}
}