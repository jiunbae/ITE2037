
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
	
	private SpriteObject newObject(DataObject e)
	{
		SpriteObject ret = null;
		switch (e.ID)
		{
			case "static":
				ret = new SpriteObject(e);
				sprites.insert(e.get("name"), ret);
				break;
			case "container":
				switch (e.get("type"))
				{
					case "box":
						ret = new ContainerObject(e);
						containers.insert(e.get("name"), (ContainerObject) ret);
					case "trigger":
						ret = new TriggerObject(e);
						containers.insert(e.get("name"), (TriggerObject) ret);
				}
				for (DataObject o : e.getChild())
					((ContainerObject) ret).add(newObject(o));
				break;
		}
		return ret;
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
				newObject(e);
		});
		
		// processor
		processor.install("interaction", interaction = new Interaction(me));
		processor.install("keyboard", keyboard = new Keyboard(inputs));
		processor.install("physics", physics = new Physics(me));
		processor.install("scene", scene = new Scene(viewport));
		processor.initilize(processor);

		// objects
		sprites.loop((e)->{
			physics.install(e);
			images.LoadImage(path.RES + e.texture, e.texture);
			e.image = images.GetImage(e.texture);
			viewport.children.add(e);
		});
		
		containers.loop((e)->{
			physics.install(e);
			images.LoadImage(path.RES + e.texture, e.texture);
			e.image = images.GetImage(e.texture);
			viewport.children.add(e);
		});
		
		scene.set(me);
		
		// viewport
		viewport.radius_x = 25;
		viewport.radius_y = 25;
		viewport.pointOfView_z = 10000;
		viewport.view_baseDistance = 10000;
		viewport.view_minDistance = 1000;
		viewport.view_maxDistance = 100000;
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
