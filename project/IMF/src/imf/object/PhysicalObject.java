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

	public void do_move()
	{
		this.pos_x += this.v_x;
		this.pos_y += this.v_y;
		this.v_x += this.a_x;
		this.v_y += this.a_y;
	}

	/**
	 * up, down, left, right - 0,1,2,3
	 * 
	 * @param o
	 * @return relative position to o
	 * @see 아 코딩하기싫다 ㅅㅂ
	 */
	public int position(SpriteObject o)
	{
		return distanceY(o) > 0 ? 1 : 0 + distanceX(o) > 0 ? 2 : 0;
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
