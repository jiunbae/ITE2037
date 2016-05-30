package imf.object;

import imf.data.DataObject;

public class CharacterObject extends PhysicalObject 
{
	public boolean 	state_jump = false,
					state_do = false;
	/**
	 * -1, 0, 1 - left, not move, right;
	 */
	public int 		state_move = 0;
	
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
	
	public void doJump()
	{
		if(state_jump)
			return;
		state_jump = true;
		this.v_y = 15;
	}
	public void doMove(int direction)
	{
		switch (direction)
		{
			case 0:
				break;
			case 1:
				break;
			case 2:
				this.pos_x -= 5;
				state_move = -1;
				break;
			case 3:
				this.pos_x += 5;
				state_move = 1;
				break;
		}
	}
}
