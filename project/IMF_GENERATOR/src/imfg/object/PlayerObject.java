package imfg.object;

import imfg.data.DataObject;

/**
 * Player Object Class
 * 
 * It's PhysicalObject and implements IPlayerConnected send data to partner
 * 
 * @package imf.object
 * @author Maybe
 * @version 1.0.0
 */
public class PlayerObject extends PhysicalObject {
	public PlayerObject(DataObject e) {
		super(e);
	}
}
