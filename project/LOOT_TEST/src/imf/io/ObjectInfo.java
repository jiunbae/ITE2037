package imf.io;

import imf.utility.HashMapDefault;

public class ObjectInfo 
{
	public String name;
	public HashMapDefault<String, String> attrs = new HashMapDefault<String, String>("0");
	
	public ObjectInfo(String name) 
	{
		this.name = name;
	}
	
	protected void insert(String name, String option)
	{
		attrs.put(name, option);
	}
	public String get(String key)
	{
		return attrs.get(key);
	}
}
