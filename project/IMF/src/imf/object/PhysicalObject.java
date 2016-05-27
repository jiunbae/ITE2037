package imf.object;

import imf.data.DataObject;
import imf.utility.*;
import loot.graphics.DrawableObject3D;

public class PhysicalObject extends SpriteObject
{
	public double v_x = 0, v_y = 0, v_z = 0, a_x = 0, a_y = 0, a_z = 0;
	
	public PhysicalObject(int x, int y, int z)
	{
		super(x, y, z, -1, -1);
	}
	public PhysicalObject(int x, int y, int z, int width, int height)
	{
		super(x, y, z, width, height);
	}
	
	public PhysicalObject(DataObject e) {
		super(e);
	}
	public double distanceX(PhysicalObject po)
	{
		return (po.pos_x - this.pos_x);
	}
	
	public double distanceY(PhysicalObject po)
	{
		return (po.pos_y - this.pos_y);
	}
	
	public double apertureX(PhysicalObject po)
	{
		return Math.abs(distanceX(po)) - (this.radius_x + po.radius_x);
	}
	
	public double apertureY(PhysicalObject po)
	{
		return Math.abs(distanceY(po)) - (this.radius_y + po.radius_y);
	}
	
	public boolean collision(PhysicalObject po)
	{
		return false;
	}
	
	public Triple<Double, Double, Double> nextMove(Triple<Double, Double, Double> position)
	{
		position.first += this.pos_x;
		position.second += this.pos_y;
		position.third += this.pos_z;
		
		return position;
	}
	public Triple<Double, Double, Double> doMove(Triple<Double, Double, Double> position)
	{
		this.pos_x += position.first;
		this.pos_y += position.second;
		this.pos_z += position.third;
		
		return new Triple<Double, Double, Double>(this.pos_x, this.pos_y, this.pos_z);
	}
}
