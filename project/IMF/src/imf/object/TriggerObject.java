package imf.object;

import java.util.Timer;
import java.util.TimerTask;

import imf.data.DataObject;

public class TriggerObject extends ContainerObject 
{
	public int index = 0;
	Timer timer = null;
	WorkTask task = new WorkTask();
	
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
			o.invisible(true);
		
		if(type.equals("box"))
			o.invisible(false);
		
		childs.add(o);
	}
	
	@Override
	public void invisible(boolean value)
	{
		trigger_hide = value;
		childs.get(index).invisible(value);
		index = 0;
	}
	
	public void trigger()
	{
		if (task != null)
			task = null;
		if (timer != null)
		{
			timer.cancel();
			timer = null;
		}
		
		if (trigger_hide != true)
			next();
	}
	
	public void next()
	{
		childs.get(index).invisible(true);
		index = (++index == childs.size() ? 0 : index);
		childs.get(index).invisible(false);
		if(childs.get(index).interval != 0)
		{
			if (timer == null)
				timer = new Timer();
			timer.schedule(task = new WorkTask(), childs.get(index).interval);
		}
	}
	
	public class WorkTask extends TimerTask {
		@Override
		public void run() {
			trigger();
		}
	}
}