package nimf.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import imf.utility.Pair;
import imf.utility.is;
import imf.validate.ValidateException;

public class ResourceManager implements IManager {
	private static List<Pair<String, List<String>>> s;
	
	static {
		clear();
	}
	
	public static void put(String name, String path) {
		if (path.length() < 1)
			return;
		
		if (path.charAt(0) == '\"' && path.charAt(path.length() - 1) == '\"')
			path = path.substring(1, path.length() - 1);
		
		if (!is.<Pair<String,List<String>>>In(s, (t)->{ return t.first.equals(name); }))
			s.add(new Pair<String, List<String>>(name, new ArrayList<String>()));
		
		if(validate(path)) {
			is.<Pair<String,List<String>>>Get(s, (t)-> { return t.first.equals(name); }).second.add(path);
		}
	}

	private static boolean validate(String path) {
		try {
			 if (!(new File(path)).isFile()) {
					throw new ValidateException();
			 }
		} catch (ValidateException v) {
		} finally {
		}
		return true;
	}
	
	public static List<String> get(String name) {
		return is.<Pair<String,List<String>>>Get(s, (t)-> { return t.first.equals(name); }).second;
	}
	
	public static void clear() {
		s = new ArrayList<Pair<String, List<String>>>();
	}
}
