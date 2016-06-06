package imf.object;

import java.util.Timer;
import java.util.TimerTask;

import imf.data.DataObject;

public class TriggerObject extends ContainerObject 
{
	public int index = 0;
	boolean inTask = false;
	Timer timer = new Timer();
	WorkTask task = new WorkTask();
	
	public TriggerObject(DataObject o) 
	{
		super(o);
	}
	
	@Override
	public void add(SpriteObject o)
	{
		if(o == null)
			return;
		
		if(type.equals("box"))
			o.invisible(trigger_hide);
		else if(!childs.isEmpty())
			o.invisible(true);
		else if(childs.isEmpty())
			o.invisible(false);
		
		childs.add(o);
	}
	
	@Override
	public void invisible(boolean value)
	{
		trigger_hide = value;
		if(type.equals("box"))
			childs.forEach((o)->o.invisible(value));
		else
			childs.get(index).invisible(value);
		index = 0;
	}
	
	public void trigger()
	{	
		if (trigger_hide != true)
			timer.schedule(task = new WorkTask(), 0);
		else
			task.cancel();
	}
	
	public void next()
	{
		childs.get(index).invisible(true);
		index = (++index == childs.size() ? 0 : index);
		childs.get(index).invisible(false);
	}
	
	public class WorkTask extends TimerTask {
		@Override
		public void run() {
			next();
			if (childs.get(index).interval != 0)
				timer.schedule(task = new WorkTask(), childs.get(index).interval);
			else
				this.cancel();
		}
	}
}