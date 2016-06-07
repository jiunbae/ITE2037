package imf.processor;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import imf.object.SpriteObject;
import imf.utility.Pair;
import loot.InputManager;

public class Mouse implements IProcess<Pair<Integer>, SpriteObject> 
{
	ProcessManager manager;
	InputManager inputs;

	List<SpriteObject> buttons = new ArrayList<SpriteObject>();
	SpriteObject target;
	double x, y, width, height;
	MOUSE state;
	
	public enum MOUSE {
		click, hover, leav
	}
	
	public Mouse(InputManager inputs, double width, double height)
	{
		this.inputs = inputs;
		this.width = width;
		this.height = height;
	}
	
	public void install(SpriteObject o)
	{
		buttons.add(o);
	}

	public void uninstall(SpriteObject o)
	{
		buttons.remove(o);
	}
	
	@SuppressWarnings("unchecked")
	public void click(SpriteObject o)
	{
		if(o == null)
			return;
		manager.get("interaction").setter(new Pair<String>("click", o.name));
	}
	
	@SuppressWarnings("unchecked")
	public void hover(SpriteObject o)
	{
		if(o == null)
			return;
		manager.get("interaction").setter(new Pair<String>("hover", o.name));
	}

	@SuppressWarnings("unchecked")
	public void leave(SpriteObject o)
	{
		if(o == null)
			return;
		manager.get("interaction").setter(new Pair<String>("leave", o.name));
	}
	
	public SpriteObject findTarget()
	{
		for(SpriteObject o : buttons)
			if(o.box_bottom < y && o.box_top > y && o.box_left < x && o.box_right > x && o.trigger_hide == false)
				return o;
		return null;
	}
	
	@Override
	public void initilize(@SuppressWarnings("rawtypes") IProcess manager) 
	{
		this.manager = (ProcessManager) manager;
		inputs.BindMouseButton(MouseEvent.BUTTON1, 7);
	}

	@Override
	public void setter(Pair<Integer> object) 
	{
		
	}

	@Override
	public SpriteObject getter() 
	{
		return target;
	}

	@Override
	public void loop() 
	{
		if (inputs.isMouseCursorMoved)
			process();
		
		if (inputs.buttons[7].isChanged && inputs.buttons[7].isPressed)
			click(findTarget());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process() 
	{
		Pair<Double> view = (Pair<Double>) manager.get("scene").getter();
		
		x = inputs.pos_mouseCursor.getX() - width/2 + view.first;
		y = -inputs.pos_mouseCursor.getY() + height/2 + view.second;
		
		SpriteObject target_next = findTarget();
		
		if(target != target_next)
		{
			leave(target);
			hover(target_next);
			target = target_next;
		}
	}

	@Override
	public void finalize()
	{
		
	}
}
