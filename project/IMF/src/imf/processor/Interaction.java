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
			// 상호작용 키를 눌렀을 때
			case 5:
				TriggerObject t = (TriggerObject) manager.get("physics").getter();
				if (t != null)
					t.trigger();
				
				break;

			// 마우스  hover
			case 9:
				TriggerObject c9 = (TriggerObject) manager.get("mouse").getter();
				if(c9 != null && c9.index == 0)
					c9.trigger();
				break;
			// 마우스  leave
			case 10:
				TriggerObject c0 = (TriggerObject) manager.get("mouse").getter();
				if(c0 != null && c0.index != 0)
					c0.trigger();
				break;
				
			// 마우스 클릭시
			case 11:
				break;
		}
	}

	@Override
	public Integer getter() 
	{
		return null;
	}
}
