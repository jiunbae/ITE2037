import java.awt.Image;
import org.json.simple.JSONObject;
import imf.network.IPartnerConnected;
import loot.graphics.DrawableObject;

public class Partner extends DrawableObject implements IPartnerConnected {

	public Partner(Image img) {
		super(200, 200, 100, 100, img);
	}
	
	@Override
	public void onInfoReceived(JSONObject info) {
		this.x = Long.valueOf( (Long) info.get("x") ).intValue();
		this.y = Long.valueOf( (Long) info.get("y") ).intValue();
	}
	
}
