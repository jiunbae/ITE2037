package imf.object;

import java.awt.event.KeyEvent;

import imf.data.DataObject;
import imf.utility.*;
import loot.graphics.DrawableObject3D;

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
	 * @param o
	 * @return o is in same z position
	 */
	public boolean zPosition(SpriteObject o)
	{
		return this.pos_z == o.pos_z;
	}
}
