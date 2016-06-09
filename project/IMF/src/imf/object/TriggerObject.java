package imf.object;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;

import imf.data.DataManager;
import imf.data.DataObject;
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
	TriggerTask task;

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
			o.invisibleSup(trigger_hide);
		else if(!childs.isEmpty())
			o.invisibleSup(true);
		else if(childs.isEmpty())
			o.invisibleSup(false);
		
		childs.add(o);
	}
	
	@Override
	public void invisibleSup(boolean value)
	{
		trigger_hide = value;
		if(type.equals("box"))
			childs.forEach((o)->o.invisibleSup(value));
		else
			childs.get(index).invisibleSup(value);
		index = 0;
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
		/*for(int i = 0; i < childs.size(); ++i)
			if(childs.get(i).name.equals(name))
			{
				if(index != i)
					doNext(i);
				break;
			}*/
		if (trigger_hide != true)
		{
			if(name.equals(""))
				timer.schedule(task = new TriggerTask((e)->e.interval != 0), 0);
			else
				timer.schedule(task = new TriggerTask((e)->!e.name.equals(name) || e.interval != 0), 0);
		}
		else
			task.cancel();
	}
	
	/**
	 * trigger next child.
	 * if interval > 0, sleep while interval
	 */
	public void trigger()
	{	
		trigger("");
		/*if (trigger_hide != true)
			timer.schedule(task = new TriggerTask(), 0);
		else
			task.cancel();*/
	}
	
	public void doNext(int next)
	{
		if(index == next)
			return;
		childs.get(index).invisible(true);
		index = (next == childs.size() ? 0 : next);
		childs.get(index).invisible(false);
		/*if (!childs.get(index).trigger_object.equals(""))
		{
			if(!childs.get(index).trigger_object_target.equals(""))
				DataManager.action().setter(new Pair<String>("act_child", childs.get(index).trigger_object + "@" + childs.get(index).trigger_object_target));	
			else
				DataManager.action().setter(new Pair<String>("act_child", childs.get(index).trigger_object));
		}*/
	}

	public void stop()
	{
		timer.purge();
		timer = null;
		task = null;
	}
	
	public class TriggerTask extends TimerTask {
		Predicate<SpriteObject> test;
		public TriggerTask(Predicate<SpriteObject> test) {
			this.test = test;
		}
		@Override
		public void run() {
			doNext(index + 1);
			try {
				if (test.test(childs.get(index)))
					timer.schedule(task = new TriggerTask(test), childs.get(index).interval); 	
				else
				{
					task.cancel();
					task = null;
					timer.purge();
				}
			} catch (Exception e) {
			}
		}
	}
}