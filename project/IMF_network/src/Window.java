import java.awt.Font;

import loot.GameFrame;
import loot.GameFrameSettings;
import loot.graphics.TextBox;

public class Window extends GameFrame {
	
	private static final long serialVersionUID = 1L;
	public TextBox tb;
	
	
	public Window(GameFrameSettings settings) {
		super(settings);
	}

	
	@Override
	public boolean Initialize() {
		tb = new TextBox(10, 10, 900, 900);
		tb.text = "Waiting...\n";
		tb.font = new Font("Verdana", Font.PLAIN, 35);
		
		return true;
	}

	@Override
	public boolean Update(long timeStamp) {
		
		return true;
	}

	@Override
	public void Draw(long timeStamp) {
		BeginDraw();
		tb.Draw(g);
		EndDraw();
	}
	
}
