package imf.processor;

import java.util.ArrayList;
import java.util.List;

import imf.object.*;

/**
 * Constructor argument <br>
 * - target	 : charter object. <br>
 * - utility : ProcessUtility<SpriteObject, Integer> Callback Function.
 * @author Maybe
 *
 */
public class Physics implements IProcess<Integer, ContainerObject>
{
	ProcessManager manager;
	PhysicalObject target;
	List<ContainerObject> containers = new ArrayList<ContainerObject>();
	List<SpriteObject> sprites = new ArrayList<SpriteObject>();

	public boolean 	state_jump = false,
					state_do = false;
	/**
	* -1, 0, 1 - left, not move, right;
	*/
	public int 		state_move = 0;
	
	public Physics(PhysicalObject tar)
	{
		target = tar;
	}
	
	public void install(ContainerObject o)
	{
		containers.add(o);
	}
	public void install(SpriteObject o)
	{
		sprites.add(o);
	}
	private boolean doCollisionUtil(boolean jump, SpriteObject o, PhysicalObject next)
	{
		boolean state_next_jump = jump;
		
		if (!target.zPosition(o) || o.collision == false || o.trigger_hide == true || o == null)
			return state_next_jump;
		
		if (target.relativeY(o) == 0 && next.relativeY(o) == 0)
		{
			if (target.relativeX(o) != 0 && next.relativeX(o) == 0)
			{
				if (target.relativeX(o) > 0)
				{
					if (next.pos_x < o.box_right + target.radius_x)
						next.pos_x = o.box_right + target.radius_x;				
				}
				else if (target.relativeX(o) < 0)
				{
					if (next.pos_x > o.box_left - target.radius_x)
						next.pos_x = o.box_left - target.radius_x;
				}
			}
		}
			
		if (target.relativeX(o) == 0 && next.relativeX(o) == 0)
		{
			if (target.relativeY(o) != 0 && next.relativeY(o) == 0)
			{
				if (target.relativeY(o) > 0)
				{
					if (next.pos_y < o.box_top + target.radius_y)
						next.setPositionAbove(o);
					
					if (target.a_y < 0)
						state_next_jump = false;	
					
					if (next.v_y < 0)
						next.v_y = 0;
				}
				else if(target.relativeY(o) < 0)
				{
					if (next.pos_y > o.box_bottom - target.radius_y)
						next.setPositionBelow(o);
					
					if (target.a_y > 0)
						state_next_jump = false;
					
					if (next.v_y > 0)
						next.v_y = 0;
				}
			}
		}	
		return state_next_jump;
	}
	private void doCollision(PhysicalObject next)
	{
		boolean state_next_jump = true;
		boolean exc = false;
		
		for (SpriteObject o : sprites)
			state_next_jump = doCollisionUtil(state_next_jump, o, next);
		
		for (SpriteObject o : containers)
			state_next_jump = doCollisionUtil(state_next_jump, o, next);
		
		if (!exc)
		{
			target.doVelocity(next.nowVelocity());
			target.doPosition(next.nowPosition());
		}
		
		if(state_jump)
			state_jump = state_next_jump;
	}
	
	@Override
	public void initilize(@SuppressWarnings("rawtypes") IProcess manager) 
	{
		this.manager = (ProcessManager) manager;
	}

	@Override
	public void loop()
	{
		doCollision(target.newInstance().doMove());
	}
	
	@Override
	public void process() 
	{
		
	}
	
	@Override
	public void finalize() 
	{
		
	}
	
	@Override
	public void setter(Integer object) 
	{
		PhysicalObject next;
		switch (object)
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
				target.v_y = 15 * -((target.a_y) / Math.abs(target.a_y));
				break;
		}
	}

	@Override
	public ContainerObject getter() 
	{
		for (ContainerObject o : containers)
			if(target.relativeX(o) == 0 && target.relativeY(o) == 0 && o.type.equals("trigger"))
				return o;
		return null;
	}
}
