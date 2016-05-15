package object;

import utility.*;
import loot.graphics.DrawableObject3D;

public class ObjectStatic extends DrawableObject3D
{
	private int width, height;
	public ObjectStatic(int width, int height)
	{
		super(0,0,0, width, height);
		this.width = width;
		this.height = height;
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
