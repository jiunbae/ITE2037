package imf.processor;

import org.json.simple.JSONObject;

import imf.network.ConnectionEvent;
import imf.network.ConnectionManager;
import imf.object.*;
import imf.utility.Pair;

public class Interaction implements IProcess<Pair<String>, ContainerObject>
{
	PlayerObject target;
	ProcessManager manager;
	ObjectManager<ContainerObject> objects;
	ContainerObject ret;
	
	public Interaction(PlayerObject target, ObjectManager<ContainerObject> objects)
	{
		this.target = target;
		this.objects = objects;
	}

	@Override
	public void initilize(@SuppressWarnings("rawtypes") IProcess manager) 
	{
		this.manager = (ProcessManager) manager;
		if(ConnectionManager.getIsConnected() == true)
			ConnectionManager.addEventListener(ConnectionEvent.PARTNER_SENT, (JSONObject data) -> 
				setter(new Pair<String>("act_partner", (String) data.get("trigger")))
			);
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

	@SuppressWarnings("unchecked")
	@Override
	public void setter(Pair<String> object) 
	{
		TriggerObject t = (TriggerObject) objects.get(object.second);
		
		if (t == null)
			return;
		
		switch (object.first)
		{
			case "find":
				ret = t;
				break;
		
			// 상호작용 키를 눌렀을 때
			case "act":
				t.trigger();
				
				if (ConnectionManager.getIsConnected() == true)
				{
					JSONObject obj = new JSONObject();
					obj.put("trigger", t.name);	
					ConnectionManager.sendToPartner(obj);
				}
				break;
			
			// 상대방이 상호작용 했을 때
			case "act_partner":
				t.trigger();
				break;
				
			// 마우스  hover
			case "hover":
				if (t.index == 0)
					t.trigger();
				break;
			// 마우스  leave
			case "leave":
				if (t.index != 0)
					t.trigger();
				break;
				
			// 마우스 클릭시
			case "click":
				manager.property.setter(t.name);
				break;
		}
	}

	@Override
	public ContainerObject getter() 
	{
		return ret;
	}
}
