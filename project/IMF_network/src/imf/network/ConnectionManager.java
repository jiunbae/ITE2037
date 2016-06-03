package imf.network;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;
import org.json.simple.JSONObject;

/**
 * ConnectionManager Class
 * 
 * Communicate with server or partner (via server)
 * 
 * @package	imf.network
 * @author Prev
 * @version 1.0.0
 */

public class ConnectionManager {
	
	
	/**
	 * Server properties
	 * 
	 * serverAddress: domain or IP address
	 * serverPort: port number (integer)
	 */
	static public String serverAddress = "lab.prev.kr";
	static public int serverPort = 8282;
	
	
	
	/*
	 * Unique sessionID to distinct users in server
	 */
	static private String sessionID = null;
	
	/*
	 * Connection instance (standalone)
	 */
	static private Connection conn;
	
	/*
	 * Represent whether connected to server
	 */
	static private boolean connected = false;
	
	/*
	 * Partner's sessionID (if it is connected)
	 * If not connected, return null
	 */
	static private String partnerSessionID = null;
	
	
	
	/*
	 * Receivers objects implements IConnectionReceiver
	 * When data is received, call receivers[i].onReceived(JSONData data)
	 */
	static private ArrayList<IConnectionReceiver> receivers = new ArrayList<IConnectionReceiver>();
	
	
	
	/*
	 * On data receiving listener
	 */
	static private Consumer<JSONObject> receivedListener = (JSONObject data) -> {
		switch ((String)data.get("type")) {
			case "partner_found":
				partnerSessionID = (String) data.get("partner_sess_id");
				break;
	
			case "partner_disconnected":
				partnerSessionID = null;
				break;
		}
		
		
		for (int i=0; i<receivers.size(); i++)
			receivers.get(i).onReceived(data);
	};
	
	
	
	/**
	 * Get sessionID of me
	 * @return String
	 */
	static public String getSessionID() {
		return sessionID;
	}
	
	/**
	 * Return is connected to server
	 * @return Boolean
	 */
	static public boolean getIsConnected() {
		return connected;
	}
	
	/**
	 * Get partner's sessionID
	 * If partner is not connected, return null
	 * 
	 * @return String
	 */
	static public String getPartnerSessionID() {
		return partnerSessionID;
	}
	
	
	
	/**
	 * Connect to server with sessionID
	 * 
	 * If already connected to server, call is ignored.
	 * If not, new sessionID is generated and call connect request to server
	 * 
	 * Out of Connection Error of Connection Class is not called when first request is failed.
	 * You can check this result by return value
	 * 
	 * After Connected, you can access Connection class accessing by calling ConnectionManager.getConnection() method
	 * 
	 * 
	 *----------------------------------------------------------------------------------------
	 * Usage:
	 * if (ConnectionManager.connect()) {
	 *   	ConnectionManager.getConnection().addReceivedEvent((JSONObject data) -> {
	 *   		System.out.println( "Data received: " + data.toJSONString() );
	 *   	});
	 *   	
	 *   	ConnectionManager.getConnection().addOutOfConnectionEvent((Exception) -> {
	 *   		System.out.println( "Disconnected to server" );
	 *   	});
	 *   	
	 * }else {
	 * 		System.out.println( "Fail to connect to server" );
	 * }
	 *----------------------------------------------------------------------------------------
	 * 
	 * @return return if connected
	 */
	
	@SuppressWarnings("unchecked")
	static public boolean connect() {
		if (connected) return true;
		
		try {
			conn = new Connection(ConnectionManager.serverAddress, ConnectionManager.serverPort);
			
		}catch (Exception e) {
			System.out.println( e.toString() );
			return false;
		}
		
		
		
		Random rand = new Random();
		sessionID = Integer.toString( rand.nextInt(90000000) + 10000000 );
		
		JSONObject sendee = new JSONObject();
		sendee.put("session_id", sessionID);
		sendee.put("type", "connect");
		
		conn.addReceivedEvent(receivedListener);
		conn.addOutOfConnectionEvent((Exception e) -> {
			connected = false;
		});
		conn.send(sendee);
		
		
		connected = true;
		return true;
	}
	
	/**
	 * Get Connection Class Instance
	 * 
	 * If not connected to server, return null
	 * 
	 * @return Connection instance
	 */
	static public Connection getConnection() {
		if (!connected)
			return null;
		
		return conn;
	}
	
	/**
	 * Register instance that implements IConnectionReceiver
	 * When data is received, call onReceive(JSONObject data) method registered to this manager
	 * 
	 * @param receiver: instance that implements IConnectionReceiver
	 */
	static public void registerReceiver(IConnectionReceiver receiver) {
		receivers.add(receiver);
	}
	
	
	
	/**
	 * Send data to partner if partner is found
	 * 
	 * @param data: JSON data object to sent
	 * @param isInfoData: whether data is character's infoData (default: false)
	 */
	@SuppressWarnings("unchecked")
	static public boolean sendToPartner(JSONObject data, boolean isInfoData) {
		if (!connected || partnerSessionID == null) return false;
		
		JSONObject sendee = new JSONObject();
		sendee.put("type", "send_partner" + (isInfoData ? "_info" : ""));
		sendee.put("session_id", sessionID);
		sendee.put("data", data);
		
		conn.send(sendee);
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	static public boolean sendToPartner(JSONObject data) {
		return sendToPartner(data, false);
	}
	
}
