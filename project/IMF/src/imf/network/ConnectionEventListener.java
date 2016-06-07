package imf.network;

import java.util.function.Consumer;
import org.json.simple.JSONObject;

/**
 * ConnectionEventListener Class
 * 
 * @package	imf.network
 * @author Prev
 * @version 1.0.0
 */

public class ConnectionEventListener {
	protected String eventType;
	protected IConnectionReceiver iReceiver = null;
	protected Consumer<ConnectionEvent> lambdaReceiver = null;
	
	
	public ConnectionEventListener(String eventType, IConnectionReceiver iReceiver) {
		this.eventType = eventType;
		this.iReceiver = iReceiver;
	}
	
	
	public ConnectionEventListener(String eventType, Consumer<ConnectionEvent> lambdaReceiver) {
		this.eventType = eventType;
		this.lambdaReceiver = lambdaReceiver;
	}
	
	
	public void call(ConnectionEvent e) {
		if (eventType != null && !eventType.equals( e.type ) )
			return;
		
		if (iReceiver != null)
			iReceiver.onReceived(e);
		
		if (lambdaReceiver != null)
			lambdaReceiver.accept(e);
	}
	
}
