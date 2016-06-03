package imf.processor;

import imf.object.SpriteObject;
import loot.graphics.Viewport;

public class Scene implements IProcess
{
	Viewport viewport;
	SpriteObject target;
	ProcessManager manager;
	
	public Scene(Viewport viewport, ProcessManager manager)
	{
		this.viewport = viewport;
		this.manager = manager;
	}
	
	public void set(SpriteObject o)
	{
		this.target = o;
	}
	
	@Override
	public void initilize() 
	{
		
	}

	@Override
	public void process() 
	{
		if(!(target.relativeX(viewport) == 0 && target.relativeY(viewport) == 0))
		{		
			viewport.pos_x += target.distanceX(viewport) / 50;
			viewport.pos_y += target.distanceY(viewport) / 50;
			viewport.x = (int) -viewport.pos_x;
			viewport.y = (int) viewport.pos_y;
		}
	}
	
	@Override
	public void finalize() 
	{
	
	}

	@Override
	public void utility(Integer arg) 
	{
		
	}
}
