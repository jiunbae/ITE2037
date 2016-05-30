package imf.processor;

import java.util.ArrayList;
import java.util.List;

import imf.object.*;
import imf.utility.Pair;

public class Physics implements Process
{
	CharacterObject target;
	List<PhysicalObject> objectp = new ArrayList<PhysicalObject>();
	List<SpriteObject> objects = new ArrayList<SpriteObject>();
	
	public Physics(CharacterObject tar)
	{
		target = tar;
		target.a_y = -1;
	}
	
	public void install(PhysicalObject o)
	{
		objectp.add(o);
	}
	public void install(SpriteObject o)
	{
		objects.add(o);
	}
	
	@Override
	public void initilize() 
	{
	}

	@Override
	public void process() 
	{
		boolean exc = false;
		PhysicalObject next = target.newInstance();
		next.doMove();
		
		for(SpriteObject o : objects)
		{
			if(!target.zPosition(o) || o == null)
				continue;

			//
			if(target.relativeX(o) > 0)
			{
				target.pos_x = o.box_left - target.radius_y;
			}
			else if(target.relativeX(o) < 0)
			{
				target.pos_x = o.box_right + target.radius_y;
			}
			
			//
			if(target.relativeX(o) == 0 && next.relativeX(o) == 0)
			{
				if(target.relativeY(o) != 0 && next.relativeY(o) == 0)
				{
					if(target.relativeY(o) > 0)
					{
						target.pos_y = o.box_top + target.radius_y;
						if(target.v_y < 0)
							target.v_y = 0;
						target.state_jump = false;
					}
					else if(target.relativeY(o) < 0)
					{
						target.pos_y = o.box_bottom - target.radius_y;
						if(target.v_y > 0)
							target.v_y = 0;
					}
				}
			}
			
		}
		if(!exc)
			target.doMove();
	}
	
	@Override
	public void finalize() 
	{
		
	}
}
