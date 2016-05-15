import java.awt.event.KeyEvent;

import loot.GameFrame;
import loot.GameFrameSettings;
import loot.graphics.Viewport;
import object.*;

public class Window extends GameFrame 
{
	/**
	 * serialVersionUID 
	 */
	private static final long serialVersionUID = 2015004584L;
	
	private enum Keyboard { 
		UP(0), DOWN(1), LEFT(2), RIGHT(3),
		/**
		 * FINAL, count enum elements;
		 */
		FINAL(4);
		
		private final int key;
		
		Keyboard(int key) {
			this.key = key;
		}
		/**
		 * @return value of key by player (WHITE)
		 */
		int WHITE() {
			return this.key + 0;
		}
		/**
		 * @return value of key by player (BLACK)
		 */
		int BLACK() {
			return this.key + Keyboard.FINAL.key;
		}
	}
	
	Viewport viewport;
	ObjectStatic object, object2;
	
	public Window(GameFrameSettings settings) 
	{
		super(settings);
	}

	@Override
	public boolean Initialize() 
	{
		inputs.BindKey(KeyEvent.VK_W, Keyboard.UP.WHITE());
		inputs.BindKey(KeyEvent.VK_S, Keyboard.DOWN.WHITE());
		inputs.BindKey(KeyEvent.VK_A, Keyboard.LEFT.WHITE());
		inputs.BindKey(KeyEvent.VK_D, Keyboard.RIGHT.WHITE());
		
		images.LoadImage("images/ball.png", "ball");
		images.LoadImage("images/ball2.png", "ball2");
		
		viewport = new Viewport(0, 0, settings.canvas_width, settings.canvas_height);
		viewport.pointOfView_z = 500;
		viewport.view_baseDistance = 500;
		viewport.view_minDistance = 0.1;
		viewport.view_maxDistance = 1000;
		viewport.view_width = settings.canvas_width;
		viewport.view_height = settings.canvas_height;
		
		
		object= new ObjectStatic(100, 100);
		object.image = images.GetImage("ball");
		object.pos_x = 50;
		object2 = new ObjectStatic(160,160);
		object2.image = images.GetImage("ball2");
		viewport.children.add(object);
		viewport.children.add(object2);
	    return true;
	}

	@Override
	public boolean Update(long timeStamp) 
	{
		
		return true;

	}

	@Override
	public void Draw(long timeStamp) {
		BeginDraw();
		
		ClearScreen();
		
		viewport.Draw(g);
		
		EndDraw();
	}

}
