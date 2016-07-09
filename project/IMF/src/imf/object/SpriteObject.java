package imf.object;

import imf.data.DataManager;
import imf.data.DataObject;
import imf.utility.Pair;
import loot.graphics.DrawableObject3D;
import loot.graphics.VisualObject3D;

/**
 * Sprite Object class
 * 
 * @property
 * name, ID, type, texture, collision, interval
 * 
 * @package	imf.object
 * @author Maybe
 * @version 1.0.0
 */
public class SpriteObject extends DrawableObject3D
{
	public String name, ID, type, texture, trigger_object, trigger_object_target, absolute;
	public boolean collision, execute_trigger;
	public int interval;
	public double box_top = 0, box_bottom = 0, box_left = 0, box_right = 0;

	public SpriteObject(double x, double y, double z, double width, double height)
	{
		super(x, y, z, width, height);
		box_top = y + this.radius_y;
		box_bottom = y - this.radius_y;
		box_left = x - this.radius_x;
		box_right = x + this.radius_x;
	}
	public SpriteObject(DataObject o)
	{
		this(Integer.valueOf(o.get("x")), Integer.valueOf(o.get("y")), Integer.valueOf(o.get("z")), Integer.valueOf(o.get("w")), Integer.valueOf(o.get("h")));
		this.collision = o.get("collision").equals("true");
		this.absolute = o.get("absolute");
		this.execute_trigger = o.get("execute_trigger").equals("true");
		this.interval = Integer.valueOf(o.get("interval"));
		this.texture = o.get("texture");
		this.type = o.get("type");
		this.name = o.get("name");
		this.trigger_object = o.get("trigger");
		this.trigger_object_target = o.get("trigger_object");
		this.ID = o.ID;
	}
	
	public void invisibleSup(boolean value)
	{
		trigger_hide = value;	
	}
	

	public void invisible(boolean value) {
		invisible(value, false);
	}
	
	public void invisible(boolean value, boolean trigger_forbiden)
	{
		invisibleSup(value);
		try {
			if (value == false && !trigger_object.equals("") && !trigger_forbiden)
			{
				if(!trigger_object_target.equals(""))
					DataManager.action().setter(new Pair<String, String>("act_child" + (execute_trigger == false ? "_only_animation" : ""), trigger_object + "@" + trigger_object_target));	
				else
					DataManager.action().setter(new Pair<String, String>("act_child" + (execute_trigger == false ? "_only_animation" : ""), trigger_object));
			}
		} catch (Exception e) {
			
		}
		
	}
	
	public void setPositionAbove(SpriteObject o)
	{
		pos_y = o.box_top + radius_y;
	}
	public void setPositionBelow(SpriteObject o)
	{
		pos_y = o.box_bottom - radius_y;
	}
	public void setPositionLeft(SpriteObject o)
	{
		pos_x = o.box_right + radius_x;
	}
	public void setPositionRight(SpriteObject o)
	{
		pos_x = o.box_left - radius_x;
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
	 * @return o is in same z position
	 */
	public boolean zPosition(VisualObject3D o)
	{
		return this.pos_z == o.pos_z;
	}
}
