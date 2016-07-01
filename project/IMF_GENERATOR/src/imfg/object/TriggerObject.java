package imfg.object;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;

import imfg.data.DataObject;

/**
 * Trigger Object class
 * 
 * It's container object and can trigger. trigger change visible of child and
 * callback interaction. Also can animate child's.
 * 
 * @package imf.object
 * @author Maybe
 * @version 1.0.0
 */
public class TriggerObject extends ContainerObject {
	public int index = 0;
	boolean inTask = false;
	Timer timer = new Timer();
	TriggerTask task;

	public boolean execute_forbidden = false;

	public TriggerObject(DataObject o) {
		super(o);
	}

	/**
	 * Override Container's add method to set child's visible. <br>
	 * box: inherit all child visible.<br>
	 * else: first child = visible true, else false.<br>
	 */
	@Override
	public void add(SpriteObject o) {
		if (o == null)
			return;

		if (type.equals("box"))
			o.invisibleSup(trigger_hide);
		else if (!childs.isEmpty())
			o.invisibleSup(true);
		else if (childs.isEmpty())
			o.invisibleSup(false);

		childs.add(o);
	}

	@Override
	public void invisibleSup(boolean value) {
		trigger_hide = value;
		if (type.equals("box"))
			childs.forEach((o) -> o.invisibleSup(value));
		else
			childs.get(index).invisibleSup(value);
		index = 0;
	}

	public void invisible(boolean value) {
		invisible(value, execute_forbidden);
	}

	/**
	 * Override invisible method to set child's visible.
	 */
	@Override
	public void invisible(boolean value, boolean trigger_forbiden) {
		trigger_hide = value;
		if (type.equals("box"))
			childs.forEach((o) -> {
				o.invisible(value, trigger_forbiden);
			});
		else {
			childs.get(index).invisible(value, trigger_forbiden);
		}
		index = 0;
	}

	/**
	 * trigger using name, trigger child who have 'name'
	 * 
	 * @param name
	 */
	public void trigger(String name, boolean execute_forbidden) {
		this.execute_forbidden = execute_forbidden;

		try {
			if (trigger_hide != true) {
				if (name.equals(""))
					timer.schedule(task = new TriggerTask((e) -> e.interval != 0), 0);
				else if (!childs.get(index).name.equals(name))
					timer.schedule(task = new TriggerTask((e) -> !e.name.equals(name)), 0);
			}
		} catch (Exception e) {

		}
	}

	/**
	 * trigger next child. if interval > 0, sleep while interval
	 */
	public void trigger() {
		trigger("", false);
	}

	public void trigger(boolean execute_forbidden) {
		trigger("", execute_forbidden);
	}

	public void trigger(String name) {
		trigger(name, false);
	}

	public void doNext(int next) {
		if (index == next)
			return;
		childs.get(index).invisible(true, execute_forbidden);
		index = (next == childs.size() ? 0 : next);
		childs.get(index).invisible(false, execute_forbidden);
	}

	public void stop() {
		if (task != null)
			task.cancel();
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
			} catch (Exception e) {
			}
		}
	}
}