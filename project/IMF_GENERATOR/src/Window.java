import loot.GameFrame;
import loot.GameFrameSettings;

public class Window extends GameFrame {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 20150045840L;

	public Window(GameFrameSettings settings) {
		super(settings);
	}

	@Override
	public boolean Initialize() {
		return false;
	}

	@Override
	public boolean Update(long timeStamp) {
		return false;
	}

	@Override
	public void Draw(long timeStamp) {

	}

}
