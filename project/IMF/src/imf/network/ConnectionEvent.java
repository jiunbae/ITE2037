package imf.network;

import org.json.simple.JSONObject;

/**
 * ConnectionEvent Class
 * 
 * @package	imf.network
 * @author Prev
 * @version 1.0.0
 */

public class ConnectionEvent {
	final static public String CONNECTED = "connected";
	final static public String DISCONNECTED = "disconnected";
	
	final static public String PARTNER_FOUND = "partner_found";
	final static public String PARTNER_DISCONNECTED = "partner_disconnected";
	
	final static public String PARTNER_SENT = "partner_sent";
	final static public String PARTNER_INFO_SENT = "partner_info_sent";
	
	
	public String type;
	public JSONObject data;
	public JSONObject rawData;
	
	public ConnectionEvent(JSONObject rawData) {
		this.type = (String) rawData.get("type");
		this.data = (JSONObject) rawData.get("data");
		this.rawData = rawData;
	}
	
	@SuppressWarnings("unchecked")
	public ConnectionEvent(String type, JSONObject data) {
		this.type = type;
		this.data = data;
		
		this.rawData = new JSONObject();
		this.rawData.put("type", type);
		this.rawData.put("data",	data);
	}
}
