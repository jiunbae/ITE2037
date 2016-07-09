package imf.processor;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import imf.object.SpriteObject;
import imf.utility.Pair;
import loot.InputManager;

public class Mouse implements IProcess<Pair<Integer, Integer>, SpriteObject> 
{
	ProcessManager manager;
	InputManager inputs;

	List<SpriteObject> buttons = new ArrayList<SpriteObject>();
	SpriteObject target;
	static int BIND_ID = 17;
	double x, y, width, height, ax, ay;
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
		manager.get("interaction").setter(new Pair<String, String>("click", o.name));
	}
	
	@SuppressWarnings("unchecked")
	public void hover(SpriteObject o)
	{
		if(o == null)
			return;
		manager.get("interaction").setter(new Pair<String, String>("hover", o.name));
	}

	@SuppressWarnings("unchecked")
	public void leave(SpriteObject o)
	{
		if(o == null)
			return;
		manager.get("interaction").setter(new Pair<String, String>("leave", o.name));
	}
	
	public SpriteObject findTarget()
	{
		for(SpriteObject o : buttons)
			if(o.box_bottom < y && o.box_top > y && o.box_left < x && o.box_right > x && o.trigger_hide == false && o.absolute.equals("false"))
				return o;
			else if(o.box_bottom < ay && o.box_top > ay && o.box_left < ax && o.box_right > ax && o.trigger_hide == false && o.absolute.equals("true"))
				return o;
		return null;
	}
	
	@Override
	public void initilize(@SuppressWarnings("rawtypes") IProcess manager) 
	{
		this.manager = (ProcessManager) manager;
		inputs.BindMouseButton(MouseEvent.BUTTON1, BIND_ID);
	}

	@Override
	public void setter(Pair<Integer ,Integer> object) 
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
		
		if (inputs.buttons[BIND_ID].isChanged && inputs.buttons[BIND_ID].isPressed)
			click(findTarget());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process() 
	{
		Pair<Double, Double> view = (Pair<Double, Double>) manager.get("scene").getter();
		
		ax = inputs.pos_mouseCursor.getX();
		ay = -inputs.pos_mouseCursor.getY();
		x = ax - width / 2 + view.first;
		y = ay + height / 2 + view.second;
		
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
		manager = null;
		inputs = null;
		buttons = null;
		target = null;
	}
}
