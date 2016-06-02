package imf.network;

import org.json.*;
import org.json.simple.JSONObject;

/**
 * IPlayerDelegate Interface
 * 
 * @author Prev
 */


public interface IPlayerDelegate {
	
	/**
	 * Get data for sending like position, velocity, acceleration, etc
	 * @return JSON Object
	 */
	public JSONObject getDataForSending();
	
}
