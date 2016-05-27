package imf.processor;

import java.util.ArrayList;
import java.util.List;

import imf.object.PhysicalObject;

public class Physics implements ProcessEvent
{
	PhysicalObject target;
	List<PhysicalObject> objects = new ArrayList<PhysicalObject>();
	
	public Physics(PhysicalObject tar)
	{
		target = tar;
	}
	
	public void install(PhysicalObject po)
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
		for(PhysicalObject o : objects)
		{
			applyMove(o);
			double x = target.apertureY(o);
			if (target.apertureY(o) < 1)
			{
				target.pos_y = oy;
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
