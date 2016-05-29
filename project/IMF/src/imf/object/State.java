package imf.object;

import java.util.HashMap;

public class State 
{
	private HashMap<String, Integer> attrs = new HashMap<String,Integer>();
	public State()
	{
		
	}
	public void install(String name)
	{
		attrs.put(name, 0);
	}
	public Integer get(String name)
	{
		return attrs.get(name);
	}
	public void set(String name, Integer value)
	{
		attrs.replace(name, value);
	}
}
