import java.awt.Image;

import org.json.simple.JSONObject;

import imf.network.IPlayerConnected;
import loot.graphics.DrawableObject;

public class Player extends DrawableObject implements IPlayerConnected {

	public Player(Image img) {
		super(200, 200, 100, 100, img);
	}
	
	@Override
	public JSONObject getDataForSending() {
		JSONObject obj = new JSONObject();
		obj.put("x", this.x);
		obj.put("y", this.y);
		
		return obj;
	}

}
