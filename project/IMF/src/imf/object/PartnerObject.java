package imf.object;

import org.json.simple.JSONObject;

import imf.data.DataObject;
import imf.network.ConnectionManager;
import imf.network.IPartnerConnected;
import imf.utility.Pair;

public class PartnerObject extends PhysicalObject implements IPartnerConnected {

	public PartnerObject(DataObject e) 
	{
		super(e);
	}
	
	@Override
	public void onInfoReceived(JSONObject info) 
	{
		pos_x = Double.parseDouble((String) (info.get("x")));
		pos_y = Double.parseDouble((String) (info.get("y")));
	}

}
