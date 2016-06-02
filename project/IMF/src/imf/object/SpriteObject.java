package imf.object;

import java.awt.event.KeyEvent;

import imf.data.DataObject;
import imf.utility.*;
import loot.graphics.DrawableObject3D;
import loot.graphics.VisualObject3D;

public class SpriteObject extends DrawableObject3D
{
	public String ID;
	public double box_top = 0, box_bottom = 0, box_left = 0, box_right = 0;

	public SpriteObject(double x, double y, double z, double width, double height)
	{
		super(x, y, z, width, height);
		box_top = y + this.radius_y;
		box_bottom = y - this.radius_y;
		box_left = x - this.radius_x;
		box_right = x + this.radius_x;
	}
	public SpriteObject(DataObject oi)
	{
		this(Integer.valueOf(oi.get("x")), Integer.valueOf(oi.get("y")), Integer.valueOf(oi.get("z")), Integer.valueOf(oi.get("w")), Integer.valueOf(oi.get("h")));
		this.ID = oi.get("name");
	}
	
	/**
	 * left:	 x,
	 * in:		 0,
	 * right:	-x
	 * @param o
	 * @return relative X position to o
	 */
	public double relativeX(VisualObject3D o)
	{
		return Math.abs(distanceX(o)) < (this.radius_x + o.radius_x) ? 0 : distanceX(o);
	}

	/**
	 * up:	 	 x,
	 * in:	 	 0,
	 * down:	-x
	 * @param o
	 * @return relative Y position to o
	 */
	public double relativeY(VisualObject3D o)
	{
		return Math.abs(distanceY(o)) < (this.radius_y + o.radius_y) ? 0 : distanceY(o);
	}
	
	/**
	 * @param o
	 * @return distance X position to o
	 */
	public double distanceX(VisualObject3D o)
	{
		return (this.pos_x - o.pos_x);
	}
	
	/**
	 * @param o
	 * @return distance Y position to o
	 */
	public double distanceY(VisualObject3D o)
	{
		return (this.pos_y - o.pos_y);
	}
	
	/**
	 * @param o
	 * @return aperture X position to o
	 */
	public double apertureX(VisualObject3D o)
	{
		return Math.abs(distanceX(o)) - (this.radius_x + o.radius_x);
	}
	
	/**
	 * @param o
	 * @return aperture Y position to o
	 */
	public double apertureY(VisualObject3D o)
	{
		return Math.abs(distanceY(o)) - (this.radius_y + o.radius_y);
	}
	
	/**
	 * @param o
	 * @return o is in same z position
	 */
	public boolean zPosition(VisualObject3D o)
	{
		return this.pos_z == o.pos_z;
	}
}
