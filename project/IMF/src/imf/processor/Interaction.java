package imf.processor;

import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONObject;

import imf.data.DataManager;
import imf.network.ConnectionEvent;
import imf.network.ConnectionManager;
import imf.network.IConnectionReceiver;
import imf.object.*;
import imf.utility.Pair;

public class Interaction implements IProcess<Pair<String>, ContainerObject>, IConnectionReceiver
{
	PlayerObject target;
	ProcessManager manager;
	ContainerObject ret;
	Timer timer = new Timer();
	InteractionTask task;
	
	public Interaction(PlayerObject target)
	{
		this.target = target;
	}

	@Override
	public void onReceived(ConnectionEvent e) {		
		setter( new Pair<String>("act_partner", (String) (e.data).get("trigger")) );
		setter( new Pair<String>("act_emotion_partner", (String) (e.data).get("emotion")) );
	}
	
	@Override
	public void initilize(@SuppressWarnings("rawtypes") IProcess manager) 
	{
		this.manager = (ProcessManager) manager;
		ConnectionManager.registerIReceiver(ConnectionEvent.PARTNER_SENT, this);
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
		ConnectionManager.unregisterIReceiver(this);
		manager = null;
		target = null;
		ret = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setter(Pair<String> object) 
	{
		if (object.second == null)
			return;
		
		String arg = "";
		
		if(object.first.equals("act_emotion_partner"))
		{
			arg = object.second.substring(object.second.indexOf("@")+1) + "_get";
			object.second = object.second.substring(0, object.second.indexOf("@")) + "_get";
		}
		else if (object.second.indexOf("@") !=-1)
		{
			arg = object.second.substring(object.second.indexOf("@")+1);
			object.second = object.second.substring(0, object.second.indexOf("@"));
		}
		
		TriggerObject t = (TriggerObject) DataManager.get_containers(object.second);
		if (t == null)
			return;
			
		switch (object.first)
		{
			case "find":
				ret = t;
				break;
			case "hover":
			case "leave":
				arg = object.second + "_" + object.first;
			case "act":
				if (object.first.equals("act") && ConnectionManager.getIsConnected() == true && t.name.indexOf("bg_switch") == -1)
				{
					JSONObject obj = new JSONObject();
					obj.put("trigger", t.name);
					ConnectionManager.sendToPartner(obj);
				}
			case "act_emotion":
				if (object.first.equals("act_emotion") && ConnectionManager.getIsConnected() == true)
				{
					JSONObject obj = new JSONObject();
					obj.put("emotion", t.name + "@" + arg);
					ConnectionManager.sendToPartner(obj);
				}
			case "act_emotion_partner":
				if (object.first.indexOf("emotion") != -1)
				{
					timer.purge();
					((TriggerObject) DataManager.get_containers(t.name+"_")).trigger(t.name);
					DataManager.get_containers(t.name).trigger_hide = false;
					timer.schedule(task = new InteractionTask(t.name+"_"), 1000);
				}
			case "dead":
			case "act_partner":
			case "act_child_only_animation":
			case "act_child":
				if (arg.equals(""))
					t.trigger();
				else
					t.trigger(arg, object.first.equals("act_child_only_animation"));
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

	public class InteractionTask extends TimerTask {
		String name;
		public InteractionTask(String name) {
			this.name = name;
		}
		@Override
		public void run() {
			((TriggerObject) DataManager.get_containers(name)).trigger(name + "0");
		}
	}
}
