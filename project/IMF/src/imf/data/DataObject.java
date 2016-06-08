package imf.data;

import java.util.ArrayList;
import java.util.List;

import imf.utility.HashMapDefault;

/**
 * Data Object Class
 * 
 * using with DataParser.<br>
 * Data object represent a object extends to SpriteObject -imf.object
 * 
 * @package	imf.data
 * @author MaybeS
 * @version 1.0.0
 */
public class DataObject 
{
	public String ID;
	static List<String> IDs = new ArrayList<String>();
	
	private HashMapDefault<String, String> attrs = new HashMapDefault<String, String>("0");
	private List<DataObject> childs = new ArrayList<DataObject>();
	
	public DataObject(String ID) 
	{
		this.ID = ID;
		//default parameter
		insert("name", "NULL");
		insert("x", "0");
		insert("y", "0");
		insert("z", "0");
		insert("w", "0");
		insert("h", "0");
		insert("texture", "");
		insert("type", "box");
		insert("trigger", "");
		insert("absolute", "false");
		insert("trigger_object", "");
		insert("collision", "true");
		insert("interval", "0");
		insert("visible", "true");
		if(ID.equals("me"))
			insert("name", "me");
	}
	public DataObject(String ID, DataObject o)
	{
		this(ID);
		// 아래의 속성들은 부모 컨테이너에서 상속받습니다.
		o.attrs.forEach((k, v)-> {
			if(k.equals("x") || k.equals("y") || k.equals("y") || k.equals("w") || k.equals("h") || k.equals("collision")|| k.equals("absolute"))
				insert(k,v);	
		});
		insert("name", "NULL");
	}
	
	public void rename(int index)
	{
		if (get("name").equals("NULL"))
		{
			String name = ID;
			int counter = 0;
			name += get("x");
			name += get("y");
			name += get("z");
			name += get("w");
			name += get("h");
			name += childs.size();
			name += counter + get("texture").substring(0, (get("texture").length() > 3 ? get("texture").length() -3 : get("texture").length()));
			name += get("type").charAt(0);
			name += get("collision").charAt(0);
			name += get("visible").charAt(0);
			name += index;
			insert("name", name);
		}
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
		o.rename(childs.size());
		childs.add(o);
	}
	
	public List<DataObject> getChild()
	{
		return childs;
	}
}
