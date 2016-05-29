package imf.processor;

import java.util.ArrayList;
import java.util.List;

import imf.object.*;

public class Physics implements ProcessEvent
{
	CharacterObject target;
	List<SpriteObject> objects = new ArrayList<SpriteObject>();
	
	public Physics(CharacterObject tar)
	{
		target = tar;
		target.a_y = -1;
	}
	
	public void install(SpriteObject po)
	{
		objects.add(po);
	}
	
	@Override
	public void Initilize() 
	{
		
	}

	@Override
	public void Event() 
	{
		double ox = target.pos_x, oy = target.pos_y;
		applyMove(target);
		for(SpriteObject o : objects)
		{
			if (target.apertureX(o) < 0)
				if (target.apertureY(o) < 0)
				{
					if(target.v_y < 0)
						target.v_y = 0;
				}
		}
	}

	private void applyMove(PhysicalObject o)
	{
		if(o==null)
			return;
		o.pos_x += o.v_x;
		o.pos_y += o.v_y;
		o.v_x += o.a_x;
		o.v_y += o.a_y;
	}
	
	@Override
	public void EventIterator() 
	{
		
	}
	
	@Override
	public void Finalize() 
	{
		
	}
}
