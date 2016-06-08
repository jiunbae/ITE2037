package imf.object;

import java.util.Timer;
import java.util.TimerTask;

import imf.data.DataManager;
import imf.data.DataObject;
import imf.processor.Interaction;
import imf.utility.Pair;

/**
 * Trigger Object class
 * 
 * It's container object and can trigger.
 * trigger change visible of child and callback interaction.
 * Also can animate child's.
 * 
 * @package	imf.object
 * @author Maybe
 * @version 1.0.0
 */
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
	
	/**
	 * Override Container's add method to set child's visible.
	 * <br>
	 * box: inherit all child visible.<br> 
	 * else: first child = visible true, else false.<br>
	 */
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
	
	/**
	 * Override invisible method to set child's visible.
	 */
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
	
	/**
	 * trigger using name, trigger child who have 'name' 
	 * @param name
	 */
	public void trigger(String name)
	{
		for(int i = 0; i < childs.size(); ++i)
			if(childs.get(i).name.equals(name))
			{
				if(index != i)
					doNext(i);
				break;
			}
	}
	
	/**
	 * trigger next child.
	 * if interval > 0, sleep while interval
	 */
	public void trigger()
	{	
		if (trigger_hide != true)
			timer.schedule(task = new WorkTask(), 0);
		else
			task.cancel();
	}
	
	public void doNext(int next)
	{
		childs.get(index).invisible(true);
		index = (next == childs.size() ? 0 : next);
		childs.get(index).invisible(false);
		if (!childs.get(index).trigger_object.equals(""))
		{
			if(!childs.get(index).trigger_object_target.equals(""))
				DataManager.action().setter(new Pair<String>("act_child", childs.get(index).trigger_object + "@" + childs.get(index).trigger_object_target));	
			else
				DataManager.action().setter(new Pair<String>("act_child", childs.get(index).trigger_object));
		}
	}
	public class WorkTask extends TimerTask {
		@Override
		public void run() {
			doNext(index+1);
			if (childs.get(index).interval != 0)
				timer.schedule(task = new WorkTask(), childs.get(index).interval);
			else
				this.cancel();
		}
	}
}