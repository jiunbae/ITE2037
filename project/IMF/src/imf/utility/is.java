package imf.utility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

public class is {
	
	public static <T> boolean In(T target, HashMap<T, ?> s) {
		if (s.containsKey(target))
			return true;
		return false;
	}
	
	public static <T> boolean In(T target, HashSet<T> s) {
		if (s.contains(target))
			return true;
		return false;
	}
	
	public static <T> boolean In(T target, List<T> s) {
		for (T t : s)
			if (t.equals(target))
				return true;
		return false;
	}
	
	public static <T> boolean In(T target, T[] s) {
		for(T t : s) 
			if (t.equals(target)) 
				return true;
		return false;
	}
	
	public static <T> boolean In(List<T> s, Function <T, Boolean> func) {
		return Get(s, func) != null;
	}
	
	public static <T> T Get(List<T> s, Function <T, Boolean> func) {
		for(T t : s)
			if(func.apply(t).booleanValue())
				return t;
		return null;
	}
}
