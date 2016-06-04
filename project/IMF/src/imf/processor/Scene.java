package imf.processor;

import imf.object.SpriteObject;
import loot.graphics.Viewport;

public class Scene implements IProcess
{
	Viewport viewport;
	SpriteObject target;
	ProcessManager manager;
	
	public Scene(Viewport viewport)
	{
		this.viewport = viewport;
	}
	
	public void set(SpriteObject o)
	{
		this.target = o;
	}
	
	@Override
	public void initilize(@SuppressWarnings("rawtypes") IProcess manager) 
	{
		this.manager = (ProcessManager) manager;
	}

	@Override
	public void loop()
	{
		process();
	}
	
	@Override
	public void process() 
	{
		if ((Math.abs(viewport.pointOfView_x - target.pos_x) > 10) || Math.abs(viewport.pointOfView_y - target.pos_y) > 10)
		{
			viewport.pointOfView_x += (target.pos_x - viewport.pointOfView_x) * 0.05;
			viewport.pointOfView_y += (target.pos_y - viewport.pointOfView_y) * 0.05;
		}
	}
	
	@Override
	public void finalize() 
	{
	
	}
	
	@Override
	public void setter(Object object) 
	{
		
	}

	@Override
	public Object getter() 
	{
		return null;
	}
}
