package imf.data;

import java.util.Random;

import imf.utility.HashMapDefault;

public class DataObject 
{
	public String name;
	public HashMapDefault<String, String> attrs = new HashMapDefault<String, String>("0");
	
	public DataObject(String name) 
	{
		this.name = name;
		insert("name", hash(10));
	}
	
	public static String hash(int size) 
	{
		StringBuffer buffer = new StringBuffer();
		Random random = new Random();
				
		for (int i = 0; i < size; i++)
			buffer.append(Integer.toString(random.nextInt(10)));
		
		return buffer.toString();
	}
	
	public String type()
	{
		return name;
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
