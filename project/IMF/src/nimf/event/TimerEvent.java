package nimf.event;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

import imf.utility.Pair;
import nimf.manager.IManager;
import nimf.object.ScriptObject;

public class TimerEvent implements IManager{

	public static Timer timer;
	public static TimerEventTask task;
	public static int counter = 0, incounter = 0;
	public static PriorityQueue<Pair<Integer, ScriptObject>> queue;
	
	static {
		clear();
	}
	
	public static void put(ScriptObject script) {
		queue.offer(new Pair<Integer, ScriptObject>(incounter + script.delay, script));
	}
	
	public static class TimerEventTask extends TimerTask {
		@Override
		public void run() {
			Pair<Integer, ScriptObject> now;
			++incounter;
			while ((now = queue.peek()).first <= counter) {
				now.second.run();
				queue.poll();
			}
			counter = incounter;
		}
	}
	
	public static void clear() {
		task = null;
		task = new TimerEventTask();
		timer = null;
		timer = new Timer();
		queue.clear();
		queue =  new PriorityQueue<Pair<Integer, ScriptObject>>(new Comparator<Pair<Integer, ScriptObject>>() {
			@Override
			public int compare(Pair<Integer, ScriptObject> o1, Pair<Integer, ScriptObject> o2) {
				if (o1.first > o2.first) return 1;
				else if(o1.first < o2.first) return -1;
				return 0;
			}
		});
		timer.schedule(task, 1);
	}
}
