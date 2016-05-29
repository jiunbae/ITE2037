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
	
	public void do_jump()
	{
		if(state_jump)
			return;
		state_jump = true;
		this.v_y += 10;
	}
	public void do_move(int direction)
	{
		switch (direction)
		{
			case 0:
				break;
			case 1:
				break;
			case 2:
				this.pos_x -= 5;
				break;
			case 3:
				this.pos_x += 5;
				break;
			
		}
	}
}
