package imf.processor;

import java.util.ArrayList;
import java.util.List;

import imf.object.*;
import imf.processor.Keyboard.KEYBOARD;
import imf.utility.Pair;

/**
 * Constructor argument <br>
 * - target	 : charter object. <br>
 * - utility : ProcessUtility<SpriteObject, Integer> Callback Function.
 * @author Maybe
 *
 */
public class Physics implements IProcess
{
	ProcessManager manager;
	PhysicalObject target, next;
	List<PhysicalObject> objectp = new ArrayList<PhysicalObject>();
	List<SpriteObject> objects = new ArrayList<SpriteObject>();

	public boolean 	state_jump = false,
					state_do = false;
	/**
	* -1, 0, 1 - left, not move, right;
	*/
	public int 		state_move = 0;
	
	public Physics(PhysicalObject tar, ProcessManager manager)
	{
		target = tar;
		target.a_y = -1;
		this.manager = manager;
	}
	
	public void install(PhysicalObject o)
	{
		objectp.add(o);
	}
	public void install(SpriteObject o)
	{
		objects.add(o);
	}
	private void doCollision(PhysicalObject next)
	{
		boolean state_next_jump = true;
		boolean exc = false;
		for(SpriteObject o : objects)
		{
			if(!target.zPosition(o) || o == null)
				continue;
			
			if(target.relativeY(o) == 0 && next.relativeY(o) == 0)
			{
				if(target.relativeX(o) != 0 && next.relativeX(o) == 0)
				{
					if(target.relativeX(o) > 0)
					{
						if(next.pos_x < o.box_right + target.radius_x)
							next.pos_x = o.box_right + target.radius_x;				
					}
					else if(target.relativeX(o) < 0)
					{
						if(next.pos_x > o.box_left - target.radius_x)
							next.pos_x = o.box_left - target.radius_x;
					}
				}
			}
				
			if(target.relativeX(o) == 0 && next.relativeX(o) == 0)
			{
				if(target.relativeY(o) != 0 && next.relativeY(o) == 0)
				{
					if(target.relativeY(o) > 0)
					{
						if(next.pos_y < o.box_top + target.radius_y)
							next.pos_y = o.box_top + target.radius_y;
						state_next_jump = false;	
						
						if(next.v_y < 0)
							next.v_y = 0;
					}
					else if(target.relativeY(o) < 0)
					{
						if(next.pos_y > o.box_bottom - target.radius_y)
							next.pos_y = o.box_bottom - target.radius_y;
						
						if(next.v_y > 0)
							next.v_y = 0;
					}
				}
			}	
		}
		
		if(!exc)
		{
			target.doVelocity(next.nowVelocity());
			target.doPosition(next.nowPosition());
		}
		if(state_jump)
			state_jump = state_next_jump;
	}
	
	@Override
	public void initilize() 
	{
	}
	
	@Override
	public void process() 
	{
		next = target.newInstance();
		next.doMove();
		doCollision(next);
	}
	
	@Override
	public void finalize() 
	{
		
	}

	@Override
	public void utility(Integer arg) 
	{
		switch (arg)
		{
			case 0:
				break;
			case 1:
				break;
			case 2:
				next = target.newInstance();
				next.pos_x -= 5;
				doCollision(next);
				break;
			case 3:
				next = target.newInstance();
				next.pos_x += 5;
				doCollision(next);
				break;
			case 4:
				if(state_jump)
					return;
				state_jump = true;
				target.v_y = 15;
				break;
			case 5:
				state_jump = true;
				target.pos_x = 0;
				target.pos_y = 0;
				target.v_x = 0;
				target.v_y = 0;
				break;
		}
	}
}
