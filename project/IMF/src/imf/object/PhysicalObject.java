package imf.object;

import imf.data.DataObject;
import imf.utility.*;
import loot.graphics.DrawableObject3D;

public class PhysicalObject extends SpriteObject
{
	public double v_x = 0, v_y = 0, v_z = 0, a_x = 0, a_y = 0, a_z = 0;
	
	public PhysicalObject(int x, int y, int z, int width, int height)
	{
		super(x, y, z, width, height);
	}
	public PhysicalObject(DataObject e) 
	{
		super(e);
	}

	/**
	 * relative position <br>
	 * 
	 * example: (0,0).position(100,100) -> <False, False>
	 * @Param object
	 * @return relative position to Object (LEFT?, UP?)
	 */
	public Pair<Boolean, Boolean> position(SpriteObject o)
	{
		return new Pair<Boolean, Boolean>(distanceX(o) > 0, distanceY(o) > 0);
	}
	
	public double distanceX(SpriteObject o)
	{
		return (this.pos_x - o.pos_x);
	}
	
	public double distanceY(SpriteObject o)
	{
		return (this.pos_y - o.pos_y);
	}
	
	public double apertureX(SpriteObject o)
	{
		return Math.abs(distanceX(o)) - (this.radius_x + o.radius_x);
	}
	
	public double apertureY(SpriteObject o)
	{
		return Math.abs(distanceY(o)) - (this.radius_y + o.radius_y);
	}
	
	public boolean collision(SpriteObject o)
	{
		return false;
	}
}
