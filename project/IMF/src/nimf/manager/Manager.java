package nimf.manager;

import java.util.HashMap;
import java.util.Random;
import imf.Const;

public class Manager {

	private static Random random = new Random();
	
	public static <T> String nextName(HashMap<String, T> s) {
		String name;
		do {
			name = newName();
		} while(s.get(name) != null);
		return name;
	}
	
	private static String newName() {
		String name = "";
		for(int i = 0; i < Const.nameSize; ++i)
			name += Integer.toString(random.nextInt(10));
		return name;
	}
}
