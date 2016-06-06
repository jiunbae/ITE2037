package imf.network;

import org.json.simple.JSONObject;

/**
 * IConnectionReceiver Interface
 * 
 * Registered by ConnectionManager.registerReceiver
 * 
 * ----------------------------------------------------------------
 * Usage:
 * class Test implements IConnectionReceiver {
 * 		int x;
 * 		int y;
 * 		@override public void onReceived(JSONObject data) {
 * 			this.x = (int) data.get("x");
 * 			this.y = (int) data.get("x");
 * 		}
 * }
 * 
 * Test t = new Test();
 * ConnectionManager.registerReceiver(t);
 * -----------------------------------------------------------------
 * 
 * @package	imf.network
 * @author Prev
 * 
 */

public interface IConnectionReceiver {
	
	/**
	 * On received data on server
	 * @param data: String data
	 */
	public void onReceived(JSONObject data);
}
