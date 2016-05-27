package imf.object;

import imf.data.DataObject;
import imf.utility.*;
import loot.graphics.DrawableObject3D;

public class SpriteObject extends DrawableObject3D
{
	public String ID;
	
	public SpriteObject(int x, int y, int z)
	{
		super(x, y, z, -1, -1);
	}
	public SpriteObject(int x, int y, int z, int width, int height)
	{
		super(x, y, z, width, height);
	}
	public SpriteObject(DataObject oi)
	{
		super(Integer.valueOf(oi.get("x")), Integer.valueOf(oi.get("y")), Integer.valueOf(oi.get("z")), Integer.valueOf(oi.get("w")), Integer.valueOf(oi.get("h")));
		this.ID = oi.get("name");
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
