
import java.util.ArrayList;
import java.util.List;

import imf.data.DataParser;
import imf.object.*;
import imf.object.CharacterObject;
import imf.processor.Keyboard;
import imf.processor.ProcessManager;
import imf.processor.ProcessEvent;
import imf.processor.ProcessUtility;
import imf.processor.Keyboard.KEYBOARD;

import loot.GameFrame;
import loot.GameFrameSettings;
import loot.graphics.Viewport;

public class Window extends GameFrame
{
	/**
	 * serialVersionUID 
	 */
	private static final long serialVersionUID = 2015004584L;
	
	public class KeyEvent implements ProcessUtility<KEYBOARD, Integer>
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

	Constant path;
	DataParser data;
	Viewport viewport;
	ProcessManager processor;
	
	ObjectManager<StaticObject> objects = new ObjectManager<StaticObject>();
	
	CharacterObject me = new CharacterObject(0,0,0,100,100), you;
	
	public Window(GameFrameSettings settings) 
	{
		super(settings);
		
		path = new Constant(settings);
		data = new DataParser(path.MAP + "stage1.xml", 0);
		processor = new ProcessManager();
		processor.install(new Keyboard(inputs, new KeyEvent()));
	}

	@Override
	public boolean Initialize() 
	{
		processor.Initilize();
		
		images.LoadImage(path.RES + "ball.png", "ball");
		
		me.image = images.GetImage("ball");
		
		viewport = new Viewport(0, 0, settings.canvas_width, settings.canvas_height);
		viewport.pointOfView_z = 500;
		viewport.view_baseDistance = 500;
		viewport.view_minDistance = 0.1;
		viewport.view_maxDistance = 1000;
		viewport.view_width = settings.canvas_width;
		viewport.view_height = settings.canvas_height;
		
		viewport.children.add(me);
		
		data.loop((e)->{
			objects.insert(e.get("name"), new StaticObject(e));
			images.LoadImage(path.RES + e.attrs.get("texture"), e.attrs.get("name"));
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
