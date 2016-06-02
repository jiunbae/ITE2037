package imf.network;

import org.json.simple.JSONObject;

/**
 * IPartnerConnected Interface
 * 
 * @package	imf.network
 * @author Prev
 */


public interface IPartnerConnected {
	/**
	 * On info data received like position, velocity, acceleration, etc
	 * @return JSON Object
	 */
	public void onInfoReceived(JSONObject info);
}
