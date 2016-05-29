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
		target.do_move();
		for(SpriteObject o : objects)
		{
			switch (target.position(o))
			{
				case 0:
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
					
			}
			if (target.apertureX(o) < 0)
				if (target.apertureY(o) < 0)
				{
					if(target.v_y < 0)
						target.v_y = 0;
					target.state_jump = false;
				}
		}
	}
	
	@Override
	public void finalize() 
	{
		
	}
}
