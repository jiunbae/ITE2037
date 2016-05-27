
import java.util.ArrayList;
import java.util.List;

import imf.data.DataObject;
import imf.data.DataParser;
import imf.object.*;
import imf.object.CharacterObject;
import imf.processor.Keyboard;
import imf.processor.ProcessManager;
import imf.processor.ProcessEvent;
import imf.processor.ProcessUtility;
import imf.processor.Keyboard.KEYBOARD;
import imf.processor.Physics;
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
			if(lParam == 0 || lParam == 2)
				return;
			
			switch (wParam)
			{
				case UP:
					if(me.v_y < -9)
						me.v_y = 9;
					break;
				case DOWN:
					//me.pos_y -=1;
					break;
				case LEFT:
					me.pos_x -=3;
					break;
				case RIGHT:
					me.pos_x +=3;
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
	Keyboard keyboard;
	Physics physics;
	
	ObjectManager<PhysicalObject> objects = new ObjectManager<PhysicalObject>();
	
	PhysicalObject me, you;
	
	public Window(GameFrameSettings settings) 
	{
		super(settings);
		
		path = new Constant(settings);
		data = new DataParser(path.MAP + "stage1.xml", 0);
		
		images.LoadImage(path.RES + data.get("me").attrs.get("texture"), "me");
		me = new PhysicalObject(data.get("me"));
		me.image = images.GetImage("me");
		me.a_y = -0.3;
		
		processor = new ProcessManager();
		processor.install(keyboard = new Keyboard(inputs, new KeyEvent()));
		processor.install(physics = new Physics(me));
	}

	@Override
	public boolean Initialize() 
	{
		processor.Initilize();

		viewport = new Viewport(0, 0, settings.canvas_width, settings.canvas_height);
		viewport.pointOfView_z = 500;
		viewport.view_baseDistance = 500;
		viewport.view_minDistance = 0.1;
		viewport.view_maxDistance = 1000;
		viewport.view_width = settings.canvas_width;
		viewport.view_height = settings.canvas_height;
		
		viewport.children.add(me);
		
		data.loop((e)->{
			if(e.type().equals("me"))
				return;
			objects.insert(e.get("name"), new PhysicalObject(e));
			images.LoadImage(path.RES + e.attrs.get("texture"), e.attrs.get("name"));
			objects.get(e.attrs.get("name")).image = images.GetImage(e.attrs.get("name"));
			physics.install(objects.get(e.attrs.get("name")));
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
