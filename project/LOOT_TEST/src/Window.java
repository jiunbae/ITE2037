
import java.util.ArrayList;
import java.util.List;

import imf.io.EventProcessor;
import imf.io.EventUtility;
import imf.io.Keyboard;
import imf.io.Processor;
import imf.io.Stage;
import imf.io.Keyboard.KEYBOARD;
import imf.object.*;
import imf.object.Character;
import loot.GameFrame;
import loot.GameFrameSettings;
import loot.graphics.Viewport;

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
			if(lParam == 0)
				return;
			
			switch (wParam)
			{
				case UP:
					me.pos_y +=10;
					break;
				case DOWN:
					me.pos_y -=10;
					break;
				case LEFT:
					me.pos_x -=10;
					break;
				case RIGHT:
					me.pos_x +=10;
					break;
				case JUMP:
					break;
				default:
					break;
			}
		}
	}

	Processor processor = new Processor();
	
	Stage stage = new Stage("stages/STAGE1.xml", 0);
	
	ObjectManager<Static> objects = new ObjectManager<Static>();
	
	Character me = new Character(0,0,0,100,100), you;
	
	Static block = new Static(0,0,0,1,100);
	Static glow = new Static(0,0,0,30,30);
	
	Viewport viewport;
	
	public Window(GameFrameSettings settings) 
	{
		super(settings);
		
		processor.install(new Keyboard(inputs, new KeyEvent()));
	}

	@Override
	public boolean Initialize() 
	{
		processor.Initilize();
		
		images.LoadImage("images/ball.png", "ball");
		images.LoadImage("images/ball2.png", "ball2");
		images.LoadImage("images/block_black.png", "block");
		images.LoadImage("images/glow.png", "glow");
		
		me.image = images.GetImage("ball");
		block.image = images.GetImage("block");
		glow.image = images.GetImage("glow");
		
		viewport = new Viewport(0, 0, settings.canvas_width, settings.canvas_height);
		viewport.pointOfView_z = 500;
		viewport.view_baseDistance = 500;
		viewport.view_minDistance = 0.1;
		viewport.view_maxDistance = 1000;
		viewport.view_width = settings.canvas_width;
		viewport.view_height = settings.canvas_height;
		
		viewport.children.add(me);
		//viewport.children.add(block);
		//viewport.children.add(glow);
		
		stage.loop((e)->{
			objects.insert(e.get("name"), new Static(e));
			images.LoadImage("images/" + e.attrs.get("texture"), e.attrs.get("name"));
			objects.get(e.attrs.get("name")).image = images.GetImage(e.attrs.get("name"));
		});
		objects.loop((e)->viewport.children.add(e));
		
	    return true;
	}
	
	@Override
	public boolean Update(long timeStamp) 
	{
		processor.Event();
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
