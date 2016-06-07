package imf.object;

import org.json.simple.JSONObject;

import imf.data.DataObject;
import imf.network.IPartnerConnected;

/**
 * Partner Object Class
 * 
 * it is PhysicalObject & implements to IPartnerConnected.
 * get data from partner
 * 
 * @package	imf.object
 * @author Maybe
 * @version 1.0.0
 */
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
