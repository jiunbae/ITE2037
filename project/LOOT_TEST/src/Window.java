import java.awt.event.KeyEvent;

import io.EventUtility;
import io.Keyboard;
import io.Keyboard.KEYBOARD;
import loot.GameFrame;
import loot.GameFrameSettings;
import loot.InputManager;
import loot.graphics.Viewport;
import object.*;
import utility.*;

public class Window extends GameFrame
{
	/**
	 * serialVersionUID 
	 */
	private static final long serialVersionUID = 2015004584L;
	
	public class KeyEvent implements EventUtility<KEYBOARD, Integer>
	{
		@Override
		public void EventUtil(KEYBOARD wParam, Integer lParam) 
		{
			switch (wParam)
			{
				case UP:
					break;
				case DOWN:
					break;
				case LEFT:
					break;
				case RIGHT:
					break;
				case JUMP:
					break;
				default:
					break;
			}	
		}
	}
	
	Viewport viewport;
	ObjectStatic object, object2;
	KeyEvent keyEvent;
	Keyboard keyboard = new Keyboard(inputs, keyEvent);
	
	public Window(GameFrameSettings settings) 
	{
		super(settings);
	}

	@Override
	public boolean Initialize() 
	{
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
