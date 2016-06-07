package imf.network;

import org.json.simple.JSONObject;

/**
 * IPlayerConnected Interface
 * 
 * @package	imf.network
 * @author Prev
 */


public interface IPlayerConnected {
	
	/**
	 * Get data for sending like position, velocity, acceleration, etc
	 * @return JSON Object
	 */
	public JSONObject getDataForSending();
	
}
