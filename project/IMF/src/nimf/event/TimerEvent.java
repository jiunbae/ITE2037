package nimf.event;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

import imf.utility.Pair;
import nimf.object.ScriptObject;

public class TimerEvent {

	public static Timer timer = new Timer();
	public static TimerEventTask task = new TimerEventTask();
	public static int counter = 0, incounter = 0;
	public static PriorityQueue<Pair<Integer, ScriptObject>> queue = new PriorityQueue<Pair<Integer, ScriptObject>>(new Comparator<Pair<Integer, ScriptObject>>() {
		@Override
		public int compare(Pair<Integer, ScriptObject> o1, Pair<Integer, ScriptObject> o2) {
			if (o1.first > o2.first) return 1;
			else if(o1.first < o2.first) return -1;
			return 0;
		}
	});
	
	static {
		timer.schedule(task, 1);
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
}
