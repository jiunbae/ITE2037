package imf.object;

import imf.data.DataObject;
import imf.utility.*;
import loot.graphics.DrawableObject3D;

public class PhysicalObject extends SpriteObject
{
	public double v_x = 0, v_y = 0, v_z = 0, a_x = 0, a_y = 0, a_z = 0;
	
	public PhysicalObject(double x, double y, double z, double width, double height)
	{
		super(x, y, z, width, height);
	}
	public PhysicalObject(DataObject e) 
	{
		super(e);
	}
	
	public PhysicalObject newInstance()
	{
		PhysicalObject object = new PhysicalObject(this.pos_x, this.pos_y, this.pos_z, width, height);
		object.a_x = this.a_x;
		object.a_y = this.a_y;
		object.a_z = this.a_z;
		object.v_x = this.v_x;
		object.v_y = this.v_y;
		object.v_z = this.v_z;
		object.radius_x = this.radius_x;
		object.radius_y = this.radius_y;
		object.trigger_hide = true;
		return object;
	}
	
	public void doMove()
	{
		doPosition(nextPosition());
		doVelocity(nextVelocity());
	}
	
	
	public void doPosition(double x, double y)
	{
		this.pos_x = x;
		this.pos_y = y;
	}
	public void doPosition(Pair<Double> pos)
	{
		doPosition(pos.first, pos.second);
	}
	public Pair<Double> nowPosition()
	{
		return new Pair<Double>(this.pos_x, this.pos_y);
	}
	public Pair<Double> nextPosition()
	{
		return new Pair<Double>(this.pos_x + this.v_x, this.pos_y + this.v_y);
	}
	
	public void doVelocity(double vx, double vy)
	{
		this.v_x = vx;
		this.v_y = vy;
	}
	public void doVelocity(Pair<Double> vel)
	{
		doVelocity(vel.first, vel.second);
	}
	public Pair<Double> nowVelocity()
	{
		return new Pair<Double>(this.v_x, this.v_y);
	}
	public Pair<Double> nextVelocity()
	{
		return new Pair<Double>(this.v_x + this.a_x, this.v_y + this.a_y);
	}
	
	/**
	 * left:	 x,
	 * in:		 0,
	 * right:	-x
	 * @param o
	 * @return relative X position to o
	 * @see 아 코딩하기싫다 ㅅㅂ
	 */
	public double relativeX(SpriteObject o)
	{
		return Math.abs(distanceX(o)) < (this.radius_x + o.radius_x) ? 0 : distanceX(o);
	}

	/**
	 * up:	 	 x,
	 * in:	 	 0,
	 * down:	-x
	 * @param o
	 * @return relative Y position to o
	 * @see 아 코딩하기싫다 ㅅㅂ
	 */
	public double relativeY(SpriteObject o)
	{
		return Math.abs(distanceY(o)) < (this.radius_y + o.radius_y) ? 0 : distanceY(o);
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
}
