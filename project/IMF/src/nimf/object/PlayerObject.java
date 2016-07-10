package nimf.object;

import org.json.simple.JSONObject;

import imf.network.IPlayerConnected;

public class PlayerObject extends SpriteObject implements IPlayerConnected{

	@Override
	public JSONObject getDataForSending() {
		return null;
	}
}
