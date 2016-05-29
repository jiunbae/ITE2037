
import java.util.ArrayList;
import java.util.List;

import imf.data.DataObject;
import imf.data.DataParser;
import imf.object.*;
import imf.object.CharacterObject;
import imf.processor.Keyboard;
import imf.processor.ProcessManager;
import imf.processor.Process;
import imf.processor.ProcessUtility;
import imf.processor.Keyboard.KEYBOARD;
import imf.processor.Physics;
import loot.GameFrame;
import loot.GameFrameSettings;
import loot.graphics.TextBox;
import loot.graphics.Viewport;

public class Window extends GameFrame
{
	/**
	 * serialVersionUID 
	 */
	private static final long serialVersionUID = 2015004584L;

	Constant path;
	DataParser data;
	
	Viewport viewport;
	TextBox text;
	
	ProcessManager processor;
	Keyboard keyboard;
	Physics physics;
	
	ObjectManager<SpriteObject> objects;
	CharacterObject me, you;
	
	public Window(GameFrameSettings settings) 
	{
		super(settings);
		
		//create new instance
		path = new Constant(settings);
		data = new DataParser(path.MAP + "stage1.xml", 0);
		
		objects = new ObjectManager<SpriteObject>();
		
		processor = new ProcessManager();

		viewport = new Viewport(0, 0, settings.canvas_width, settings.canvas_height);
		text = new TextBox();
	}
	
	@Override
	public boolean Initialize() 
	{
		// data load
		data.loop((e)->{
			images.LoadImage(path.RES + e.get("texture"), e.get("name"));
			if(e.ID.equals("me"))
			{
				me = new CharacterObject(e);
				me.image = images.GetImage("me");
				viewport.children.add(me);
			}
			else
			{
				SpriteObject object = new SpriteObject(e);
				objects.insert(e.get("name"), object);
				objects.get(e.get("name")).image = images.GetImage(e.get("name"));
				viewport.children.add(object);
			}
		});
		
		// processor
		processor.install(keyboard = new Keyboard(inputs, new KeyEvent()));
		processor.install(physics = new Physics(me));
		processor.initilize();

		// objects
		objects.loop((e)->{
			physics.install(e);
		});
		
		// viewport
		viewport.pointOfView_z = 500;
		viewport.view_baseDistance = 500;
		viewport.view_minDistance = 0.1;
		viewport.view_maxDistance = 1000;
		viewport.view_width = settings.canvas_width;
		viewport.view_height = settings.canvas_height;
		
		// text (for debug)
		text.height = 100;
		text.width = 100;
		text.x = 0;
		text.y = 0;
		
		viewport.children.add(text);
		
	    return true;
	}
	
	@Override
	public boolean Update(long timeStamp) 
	{
		processor.process();
		
		return true;
	}

	@Override
	public void Draw(long timeStamp) 
	{
		BeginDraw();
		
		ClearScreen();
		
		viewport.Draw(g);
		
		EndDraw();
	}
	
	/**
	 * KeyEvent, callback function of Keyboard Process
	 * @author Maybe
	 */
	public class KeyEvent implements ProcessUtility<KEYBOARD, Integer>
	{
		@Override
		public void EventUtil(KEYBOARD wParam, Integer lParam) 
		{
			if(lParam == 0 || lParam == 2)
				return;
			
			switch (wParam)
			{
				case UP:
				case DOWN:
				case LEFT:
				case RIGHT:
					me.do_move(wParam.ordinal());
					break;
				case JUMP:
					me.do_jump();
					break;
				default:
					break;
			}
		}
	}
}
