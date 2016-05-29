package imf.object;

import java.awt.event.KeyEvent;

import imf.data.DataObject;
import imf.utility.*;
import loot.graphics.DrawableObject3D;

public class SpriteObject extends DrawableObject3D
{
	public String ID;
	public int box_top = 0, box_bottom = 0, box_left = 0, box_right = 0;

	public SpriteObject(int x, int y, int z, int width, int height)
	{
		super(x, y, z, width, height);
		box_top = y + height/2;
		box_bottom = y - height/2;
		box_left = x - width/2;
		box_right = x + width/2;
	}
	public SpriteObject(DataObject oi)
	{
		this(Integer.valueOf(oi.get("x")), Integer.valueOf(oi.get("y")), Integer.valueOf(oi.get("z")), Integer.valueOf(oi.get("w")), Integer.valueOf(oi.get("h")));
		this.ID = oi.get("name");
	}
}
