package nimf.event;

import java.util.Map.Entry;

import imf.utility.Pair;
import nimf.manager.SpriteManager;
import nimf.object.SpriteObject;

public class InteractEvent {
	/**
	 * Pair<Double, Double> (pos, radius) to check interact
	 * @param first
	 * @param second
	 * @return is Interact?
	 */
	public static boolean isInteract(Pair<Double, Double> first, Pair<Double, Double> second) {
		return second.first - second.second <= first.first + first.second ||
				second.first + second.second >= first.first - first.second;
	}
	
	public static SpriteObject isInteract(SpriteObject s) {
		for (Entry<String, SpriteObject> o : SpriteManager.s.entrySet())
			if (isInteract(new Pair<Double, Double>(s.pos_x, s.radius_x), new Pair<Double, Double>(o.getValue().pos_x, o.getValue().radius_x)) && 
					isInteract(new Pair<Double, Double>(s.pos_y, s.radius_y), new Pair<Double, Double>(o.getValue().pos_y, o.getValue().radius_y)))
				return o.getValue();
		return null;
	}
	
	
}
