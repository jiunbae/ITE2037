package imf.network;

import java.util.function.Consumer;

import org.json.simple.JSONObject;

/**
 * CharacterInfoSyncher Class
 * 
 * Simplicated connection util with partner
 * 
 * @package	imf.network
 * @author Prev
 *
 */


public class CharacterInfoSyncher {
	
	/*
	 * Player and Partner instance
	 */
	static private IPlayerConnected player = null;
	static private IPartnerConnected partner = null;
	
	
	static private boolean isReceiverResgisterd = false;
	
	
	/*
	 * On data receiving listener
	 */
	static private Consumer<JSONObject> receivedListener = (JSONObject data) -> {
		if ((String)data.get("type") == "partner_info_sent") {
			partner.onInfoReceived( (JSONObject) data.get("data") );
		}
	};
	
	
	/**
	 * Register player
	 * When calling CharacterInfoSyncher.fetch(), player's data getting by player.getDataForSending() will be sent to partner
	 * 
	 * @param _player: IPlayerConnected instance
	 */
	static public void registerPlayer(IPlayerConnected _player) {
		player = _player;
		
		if (!ConnectionManager.getIsConnected())
			System.out.println("WARNING: CharacterInfoSyncher.registerPlayer SHOULD BE CALLED after ConnectionManager is connected");
		
		if (!isReceiverResgisterd)
			ConnectionManager.getConnection().addReceivedEvent(receivedListener);
	}
	
	
	/**
	 * Register partner
	 * When data received by partner's program, partner.onInfoReceived 
	 * 
	 * @param _partner: IPartnerConnected instance
	 */
	static public void registerPartner(IPartnerConnected _partner) {
		partner = _partner;
	}
	
	
	
	/**
	 * Fetch player's info data to partner's program
	 * 
	 * @return if player is null, return false.
	 * 			else, return true
	 */
	static public boolean fetch() {
		if (player == null) {
			System.out.println("WARNING: player object is NULL");
			return false;
		}
		
		ConnectionManager.sendToPartner(player.getDataForSending(), true);
		return true;
	}
	
}
