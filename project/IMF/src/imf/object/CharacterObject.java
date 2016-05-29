package imf.object;

import imf.data.DataObject;

public class CharacterObject extends PhysicalObject 
{
	public boolean 	state_jump = false, 
					state_do = false;
	
	public CharacterObject(int x, int y, int z)
	{
		super(x, y, z, -1, -1);
	}
	public CharacterObject(int x, int y, int z, int width, int height)
	{
		super(x, y, z, width, height);
	}
	public CharacterObject(DataObject e) 
	{
		super(e);
	}
}
