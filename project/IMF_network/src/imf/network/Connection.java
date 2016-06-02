package imf.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Connection Class
 * 
 * Connect to Game server and communicate
 * 
 * 
 * @package	imf.network
 * @author Prev
 *
 */

public class Connection extends Thread {
	
	
	/**
	 * Socket object
	 */
	private Socket socket;
	
	/**
	 * Data output stream for send data to server
	 */
	private DataOutputStream outStream;
	
	/**
	 * Data input stream for receive data from server 
	 */
	private BufferedReader inStream;
	
	/**
	 * received action listener
	 */
	private ArrayList< Consumer<JSONObject> > receiveListeners = new ArrayList< Consumer<JSONObject> >();
	
	/**
	 * Error listeners
	 */
	private ArrayList< Consumer<Exception> > outOfConnectionListener = new ArrayList< Consumer<Exception> >();
	
	
	/**
	 * Constructor called by Connection.getInstance() method
	 * @param serverAddress
	 * @param serverPort
	 */
	public Connection(String serverAddress, int serverPort) throws UnknownHostException, IOException {
		this.socket = new Socket(serverAddress, serverPort);
					
		inStream = new BufferedReader( new InputStreamReader( socket.getInputStream(), "UTF-8" ) );
		outStream = new DataOutputStream( socket.getOutputStream() );
		
		this.start();
	}
	
	
	
	/**
	 * Thread-run process
	 */
	public void run() {
		try {
			while (inStream != null) {
	    		if (inStream.read() == -1) {
	    			callOutOfConnectionEvent(null);
	    			this.stop();
	    			return;
	    		}
	    		String rawData = inStream.readLine();
	    		JSONParser jsonParser = new JSONParser();
	    		
	    		JSONObject data;
				try {
					data = (JSONObject) jsonParser.parse(rawData);
					
				}catch (ParseException e) {
					e.printStackTrace();
					return;
				}
	    		
	    		for (int i=0; i<receiveListeners.size(); i++)
	    			receiveListeners.get(i).accept(data);
			}
	    
		}catch(IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	
	/**
	 * Add received event listener
	 * @param receivedAction: Consumer class: JSONObject is received data
	 * 
	 * 
	 * Usage:
	 * conn.addReceivedEvent((JSONObject data)-> {
	 *		System.out.println("received: " + data);
	 * });
	 * 
	 */
	public void addReceivedEvent(Consumer<JSONObject> receivedAction) {
		receiveListeners.add( receivedAction );
	}
	
	
	/**
	 * Add out-of-connection event listener
	 * @param listener: Consumer class: Exception is not guaranteed
	 * 
	 * 
	 * Usage:
	 * conn.addOutOfConnectionEvent((Exception e)-> {
	 *		System.out.println("disconnected to server");
	 * });
	 * 
	 */
	public void addOutOfConnectionEvent(Consumer<Exception> listener) {
		outOfConnectionListener.add(listener);
	}
	
	
	/**
	 * Call out-of-connection event
	 * called in thread-process
	 * @param e: Exception, NOT guaranteed
	 */
	private void callOutOfConnectionEvent(Exception e) {
		for (int i=0; i<outOfConnectionListener.size(); i++)
			outOfConnectionListener.get(i).accept(e);
	}
	
	
	/**
	 * Send data to function
	 * @param data: JSObject data to send
	 */
	public void send(JSONObject data) {
		try {
			outStream.writeUTF(data.toJSONString());
			
		}catch (IOException e) {
			callOutOfConnectionEvent(e);
			e.printStackTrace();
		}
	}
	
}
