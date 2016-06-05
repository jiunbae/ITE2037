package imf.processor;

import imf.object.*;

public class Interaction implements IProcess<Integer, Integer>
{
	CharacterObject target;
	ProcessManager manager;
	
	public Interaction(CharacterObject target)
	{
		this.target = target;
	}

	@Override
	public void initilize(@SuppressWarnings("rawtypes") IProcess manager) 
	{
		this.manager = (ProcessManager) manager;
	}

	@Override
	public void loop() 
	{
		
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
		switch (object)
		{
			case 5:
				TriggerObject o = (TriggerObject) manager.get("physics").getter();
				if (o != null)
					o.trigger();
				//target.a_y = -target.a_y;
				break;
		}
	}

	@Override
	public Integer getter() 
	{
		return null;
	}
}
