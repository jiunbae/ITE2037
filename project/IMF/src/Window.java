
import imf.data.DataObject;
import imf.data.DataParser;
import imf.object.*;
import imf.processor.Keyboard;
import imf.processor.ProcessManager;
import imf.processor.Scene;
import imf.processor.Interaction;
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
	Interaction interaction;
	Keyboard keyboard;
	Physics physics;
	Scene scene;
	
	ObjectManager<SpriteObject> sprites;
	ObjectManager<ContainerObject> containers;
	
	CharacterObject me, you;
	
	public Window(GameFrameSettings settings) 
	{
		super(settings);
		
		//create new instance
		path = new Constant(settings);
		data = new DataParser(path.MAP + "stage1.xml", 0);
		
		sprites = new ObjectManager<SpriteObject>();
		containers = new ObjectManager<ContainerObject>();
		
		processor = new ProcessManager();

		viewport = new Viewport(0, 0, settings.canvas_width, settings.canvas_height);
		text = new TextBox();
	}
	
	@Override
	public boolean Initialize() 
	{
		// data load
		data.loop((e)->{
			if(e.ID.equals("me"))
			{
				me = new CharacterObject(e);
				images.LoadImage(path.RES + me.texture, "me");
				me.image = images.GetImage("me");
				viewport.children.add(me);
				me.a_y = -0.98;
			}
			else
			{
				switch (e.ID)
				{
					case "static":
						sprites.insert(e.get("name"), new SpriteObject(e));
						break;
					case "trigger":
						containers.insert(e.get("name"), new TriggerObject(e));
						for(DataObject o : e.getChild())
						{
							SpriteObject child = new SpriteObject(o);
							child.trigger_hide = true;
							sprites.insert(o.get("name"), child);
							containers.get(e.get("name")).add(child);
						}
						break;
				}
			}
		});
		
		// processor
		processor.install("keyboard", keyboard = new Keyboard(inputs));
		processor.install("physics", physics = new Physics(me));
		processor.install("scene", scene = new Scene(viewport));
		processor.install("interaction", interaction = new Interaction(me));
		processor.initilize(processor);

		// objects
		sprites.loop((e)->{
			physics.install(e);
			images.LoadImage(path.RES + e.texture, e.ID);
			e.image = images.GetImage(e.ID);
			viewport.children.add(e);
		});
		
		containers.loop((e)->{
			physics.install(e);
			images.LoadImage(path.RES + e.texture, e.ID);
			e.image = images.GetImage(e.ID);
			viewport.children.add(e);
		});
		
		scene.set(me);
		
		// viewport
		viewport.radius_x = 25;
		viewport.radius_y = 25;
		viewport.pointOfView_z = 500;
		viewport.view_baseDistance = 500;
		viewport.view_minDistance = 0.1;
		viewport.view_maxDistance = 1000;
		viewport.view_width = settings.canvas_width;
		viewport.view_height = settings.canvas_height;
		
		// text (for debug)
		text.height = 50;
		text.width = 50;
		text.x = 0;
		text.y = 0;
		
		viewport.children.add(me);
		viewport.children.add(text);
		
	    return true;
	}
	
	@Override
	public boolean Update(long timeStamp) 
	{
		processor.loop();
		text.text = "" + me.a_y;
		
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
}
