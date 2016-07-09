package loot.graphics;

import java.util.HashSet;
import java.util.Random;

/**
 * 모든 오브젝트에 고유 이름을 부여합니다.
 * @author Maybe
 *
 */
public class NamedObject {
	public static final int nameSize = 8;
	public static HashSet<String> names = new HashSet<String>();
	private static Random random = new Random();
	public String name;
	
	public NamedObject() {
		this(nextName(""));
	}
	
	public NamedObject(String name) {
		this.name = nextName(name);
		names.add(name);
	}
	
	public static String nextName(String flag) {
		String name = flag;
		while(names.contains(name) || name.length() == 0)
			name = newName(flag);
		return name;
	}
	
	private static String newName(String flag) {
		String name = flag;
		for(int i = 0; i < nameSize; ++i)
			name += Integer.toString(random.nextInt(10));
		return name;
	}
	
	public static void clear() {
		names.clear();
	}
}
