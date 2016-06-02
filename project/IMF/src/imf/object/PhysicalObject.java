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
}
