package imf.object;

import java.util.Timer;
import java.util.TimerTask;

import imf.data.DataObject;

public class TriggerObject extends ContainerObject 
{
	public int index = 0;
	Timer timer = null;
	
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
		childs.add(o);
	}
	
	@Override
	public void invisible(boolean value)
	{
		trigger_hide = value;
		if (type.equals("box"))
			childs.forEach((o) -> o.invisible(value));
	}
	
	public void trigger()
	{
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