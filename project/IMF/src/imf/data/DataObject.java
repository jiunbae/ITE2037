package imf.data;

import java.util.Random;

import imf.utility.HashMapDefault;

public class DataObject 
{
	public String ID;
	private HashMapDefault<String, String> attrs = new HashMapDefault<String, String>("0");
	
	public DataObject(String ID) 
	{
		this.ID = ID;
		insert("name", hash(10));
		if(ID.equals("me"))
			insert("name", "me");
	}
	
	public static String hash(int size) 
	{
		StringBuffer buffer = new StringBuffer();
		Random random = new Random();
				
		for (int i = 0; i < size; i++)
			buffer.append(Integer.toString(random.nextInt(10)));
		
		return buffer.toString();
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
