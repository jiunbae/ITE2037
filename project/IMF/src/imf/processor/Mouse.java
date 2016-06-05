package imf.processor;

import java.util.ArrayList;
import java.util.List;

import imf.object.SpriteObject;
import imf.utility.Pair;
import imf.utility.Swap;
import loot.InputManager;

public class Mouse implements IProcess<Pair<Integer>, SpriteObject> 
{
	ProcessManager manager;
	InputManager inputs;

	List<SpriteObject> buttons = new ArrayList<SpriteObject>();
	SpriteObject target, push;
	int x, y, width, height;
	MOUSE state;
	
	public enum MOUSE {
		click, hover, leav
	}
	
	public Mouse(InputManager inputs, Integer width, Integer height)
	{
		this.inputs = inputs;
		this.width = width;
		this.height = height;
	}
	
	public void install(SpriteObject object)
	{
		buttons.add(object);
	}

	@SuppressWarnings("unchecked")
	public void hover(SpriteObject o)
	{
		if(o == null)
			return;
		push = o;
		manager.get("interaction").setter(9);
	}

	@SuppressWarnings("unchecked")
	public void leave(SpriteObject o)
	{
		if(o == null)
			return;
		push = o;
		manager.get("interaction").setter(10);
	}
	
	@Override
	public void initilize(@SuppressWarnings("rawtypes") IProcess manager) 
	{
		this.manager = (ProcessManager) manager;
	}

	@Override
	public void setter(Pair<Integer> object) 
	{
		x = object.first - width/2;
		y = -object.second + height/2;

		SpriteObject target_next = null;
		for(SpriteObject o : buttons)
			if(o.box_bottom < y && o.box_top > y && o.box_left < x && o.box_right > x)
				target_next = o;
		
		if(target != target_next)
		{
			leave(target);
			hover(target_next);
			target = target_next;
		}
	}

	@Override
	public SpriteObject getter() 
	{
		return push;
	}

	@Override
	public void loop() 
	{
		if (inputs.isMouseCursorMoved)
		{
			setter(new Pair<Integer>((int) inputs.pos_mouseCursor.getX(), (int) inputs.pos_mouseCursor.getY()));
		}
	}

	@Override
	public void process() 
	{
		
	}

	@Override
	public void finalize()
	{
		
	}
}
