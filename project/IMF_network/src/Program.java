import java.io.IOException;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import imf.network.Connection;
import imf.network.ConnectionManager;
import imf.network.IConnectionReceiver;
import loot.GameFrameSettings;

public class Program {

	
	public static void main(String[] args) {
		
		GameFrameSettings settings = new GameFrameSettings();
	    settings.canvas_width = 1000;
	    settings.canvas_height = 1000;
	    
	    Window gWindow = new Window(settings);
	    gWindow.setVisible(true);
		
	    
	    if (ConnectionManager.connect()) {
	    	
	    	ConnectionManager.getConnection().addReceivedEvent((JSONObject data) -> {
	    		switch ((String)data.get("type")) {
					case "connected":
						gWindow.appendToTextBox("connected to server");
						gWindow.appendToTextBox("my sess id: " + ConnectionManager.getSessionID() );
						break;
	
					case "partner_found":
						gWindow.appendToTextBox( "partner found: " + (String)data.get("partner_sess_id") );
						
						gWindow.registerCharacters();
						break;
						
					case "partner_disconnected" :
						gWindow. appendToTextBox("partner disconnected");
						break;
				}
	    		
	    		//System.out.println( "data received: " + data.toJSONString() );
	    		
	    	});
	    	
	    	
	    	ConnectionManager.getConnection().addOutOfConnectionEvent((Exception) -> {
	    		gWindow.appendToTextBox("Disconnected to server");
	    	});
	    	
	    	
	    }else {
	    	gWindow.appendToTextBox("Fail to connect to server");
	    }

	}

}
