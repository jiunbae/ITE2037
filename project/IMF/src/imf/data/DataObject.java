package imf.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import imf.utility.HashMapDefault;

public class DataObject 
{
	public String ID;
	private HashMapDefault<String, String> attrs = new HashMapDefault<String, String>("0");
	private List<DataObject> childs = new ArrayList<DataObject>();
	
	public DataObject(String ID) 
	{
		this.ID = ID;
		//default parameter
		insert("name", hash(10));
		insert("x", "0");
		insert("y", "0");
		insert("z", "0");
		insert("w", "0");
		insert("h", "0");
		insert("texture", "");
		insert("type", "box");
		insert("collision", "true");
		if(ID.equals("me"))
			insert("name", "me");
	}
	public DataObject(String ID, DataObject o)
	{
		this.ID = ID;
		o.attrs.forEach((k, v)->insert(k, v));
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
	
	protected void insert(String name, String option)
	{
		attrs.put(name, option);
	}
	
	public String get(String key)
	{
		return attrs.get(key);
	}
	
	protected void insertChild(DataObject o)
	{
		childs.add(o);
	}
	
	public List<DataObject> getChild()
	{
		return childs;
	}
}
