package imf.network;

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
 * 		@override public void onReceived(ConnectionEvent e) {
 * 			this.x = (int) e.data.get("x");
 * 			this.y = (int) e.data.get("x");
 * 		}
 * }
 * 
 * Test t = new Test();
 * ConnectionManager.registerIReceiver(t);
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
	public void onReceived(ConnectionEvent e);
}
