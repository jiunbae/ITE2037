package imf.object;

import org.json.simple.JSONObject;

import imf.data.DataObject;
import imf.network.IPlayerConnected;

public class PlayerObject extends PhysicalObject implements IPlayerConnected
{
	public PlayerObject(DataObject e) 
	{
		super(e);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getDataForSending() 
	{
		JSONObject obj = new JSONObject();
		obj.put("x", "" + this.pos_x);
		obj.put("y", "" + this.pos_y);
		return obj;
	}

}
