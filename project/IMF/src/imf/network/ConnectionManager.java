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
 * @version 1.2.1
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
	static public ArrayList< ConnectionEventListener > listeners = new ArrayList< ConnectionEventListener >();
	
	
	
	/*
	 * On data receiving listener
	 */
	static private Consumer<JSONObject> receivedListener = (JSONObject rawData) -> {
		
		ConnectionEvent evt = new ConnectionEvent(rawData);
		
		switch (evt.type) {
			case ConnectionEvent.PARTNER_FOUND:
				partnerSessionID = (String) rawData.get("partner_sess_id");
				break;
	
			case ConnectionEvent.PARTNER_DISCONNECTED:
				partnerSessionID = null;
				break;
		}
		
		dispatchEventToListeners(evt);
	};
	
	
	/*
	 * callOutOfConnectionEvent listener
	 */
	@SuppressWarnings("unchecked")
	static private Consumer<Exception> outOfConnectionListener = (Exception e) -> {
		connected = false;
		
		JSONObject oocData = new JSONObject();
		oocData.put("exception", e);
		ConnectionEvent evt = new ConnectionEvent(ConnectionEvent.DISCONNECTED, oocData);
		
		dispatchEventToListeners(evt);
	};
	
	/*
	 * Dispatch event to listeners
	 */
	static private void dispatchEventToListeners(ConnectionEvent e) {
		for (int i=0; i<listeners.size(); i++)
			listeners.get(i).call(e);
	}
	
	
	
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
	 *   	ConnectionManager.registerCallback((ConnectionEvent e) -> {
	 *   		System.out.println( "Data received: " + e.data.toJSONString() );
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
			
			ConnectionEvent evt = new ConnectionEvent(ConnectionEvent.DISCONNECTED, null);
			dispatchEventToListeners(evt);
			
			return false;
		}
		
		Random rand = new Random();
		sessionID = Integer.toString( rand.nextInt(90000000) + 10000000 );
		
		JSONObject sendee = new JSONObject();
		sendee.put("session_id", sessionID);
		sendee.put("type", "connect");
		
		conn.addReceivedEvent(receivedListener);
		conn.addOutOfConnectionEvent(outOfConnectionListener);
		conn.send(sendee);
		
		
		connected = true;
		return true;
	}
	
	
	/**
	 * Close connection
	 */
	static public void disconnect() {
		conn.close();
		connected = false;
		partnerSessionID = null;
	}
	
	
	
	
	/**
	 * Get Connection Class Instance
	 * 
	 * If not connected to server, return null
	 * 
	 * @return Connection instance
	 */
	static public Connection getConnection() {
		return conn;
	}
	
	
	/**
	 * Functions of register event listener
	 * 
	 * Basic type: registerEventListener
	 * 		@param listener: listener instance
	 * 
	 * 
	 * Interface type: 
	 * 		@param eventType: EventType, constants are in ConnectionEvent
	 * 			If null, receive all types
	 * 		@param receiver: IConnectionReceiver instance
	 * 
	 * 
	 * Lambda type:
	 * 		@param eventType: EventType, constants are in ConnectionEvent
	 * 			If null, receive all types
	 * 		@param receiver: lambda instance
	 * 
	 */
	static public void registerEventListener(ConnectionEventListener listener) {
		listeners.add( listener );
	}
	
	static public void registerIReceiver(IConnectionReceiver receiver) {
		registerEventListener( new ConnectionEventListener(null, receiver) );
	}
	
	static public void registerIReceiver(String eventType, IConnectionReceiver receiver) {
		registerEventListener( new ConnectionEventListener(eventType, receiver) );
	}

	static public void registerCallback(Consumer<ConnectionEvent> receiver) {
		registerEventListener( new ConnectionEventListener(null, receiver) );
	}
	
	static public void registerCallback(String eventType, Consumer<ConnectionEvent> receiver) {
		registerEventListener( new ConnectionEventListener(eventType, receiver) );
	}
	
	
	/**
	 * Unregister event listener
	 */
	static public boolean unregisterEventListener(ConnectionEventListener listener) {
		for (int i=0; i<listeners.size(); i++) {
			if (listeners.get(i) == listener ) {
				listeners.remove(i);
				return true;
			}
		}
		return false;
	}
	
	static public boolean unregisterIReceiver(IConnectionReceiver receiver) {
		for (int i=0; i<listeners.size(); i++) {
			if (listeners.get(i).iReceiver == receiver ) {
				listeners.remove(i);
				return true;
			}
		}
		return false;
	}
	
	static public boolean unregisterCallback(Consumer<ConnectionEvent> receiver) {
		for (int i=0; i<listeners.size(); i++) {
			if (listeners.get(i).lambdaReceiver == receiver ) {
				listeners.remove(i);
				return true;
			}
		}
		return false;
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
	
	static public boolean sendToPartner(JSONObject data) {
		return sendToPartner(data, false);
	}
	
}
