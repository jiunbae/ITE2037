package imf.network;

import org.json.simple.JSONObject;

/**
 * IPartnerDelegate Interface
 * 
 * @author Prev
 */


public interface IPartnerDelegate {
	/**
	 * On info data received like position, velocity, acceleration, etc
	 * @return JSON Object
	 */
	public void onInfoReceived(JSONObject info);
}
