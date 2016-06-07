
import imf.data.DataObject;
import imf.data.DataParser;
import imf.network.CharacterInfoSyncher;
import imf.network.ConnectionEvent;
import imf.network.ConnectionManager;
import imf.network.IConnectionReceiver;
import imf.object.*;
import imf.processor.Keyboard;
import imf.processor.Mouse;
import imf.processor.ProcessManager;
import imf.processor.Scene;
import imf.processor.IProcessProperty;
import imf.processor.Interaction;
import imf.processor.Physics;
import loot.GameFrame;
import loot.GameFrameSettings;
import loot.graphics.TextBox;
import loot.graphics.Viewport;

/**
 * LOOT Game Frame window
 * @author Maybe
 *
 */
public class Window extends GameFrame implements IConnectionReceiver
{
	/**
	 * serialVersionUID 
	 */
	private static final long serialVersionUID = 2015004584L;

	private enum GAME_STATE{
		SPLASH, FINDING, LOADING, CREDIT, PLAY, OVER
	}
	
	boolean isConnecting = false;
	static GAME_STATE state = GAME_STATE.SPLASH;
	
	int intervalHandle = 0;
	
	Constant path;
	DataParser data;
	
	Viewport viewport;
	TextBox text;
	
	/**
	 * processor(ProcessManager) manage processors.
	 * 
	 * @extends: IProcess
	 */
	ProcessManager processor;
	Interaction interaction;
	Keyboard keyboard;
	Physics physics;
	Mouse mouse;
	Scene scene;
	
	ObjectManager<SpriteObject> sprites;
	ObjectManager<ContainerObject> containers;
	PlayerObject me = null;
	PartnerObject you = null;
	SpriteObject stage;
	
	public Window(GameFrameSettings settings) 
	{
		super(settings);
		
		// load settings
		path = new Constant(settings);
		
		// create new instance
		sprites = new ObjectManager<SpriteObject>();
		containers = new ObjectManager<ContainerObject>();
		processor = new ProcessManager(new Stage());
		viewport = new Viewport(0, 0, settings.canvas_width, settings.canvas_height);
		text = new TextBox();
	}
	
	
	@Override
	public void onReceived(ConnectionEvent e) 
	{
		switch (e.type) {
			
			case ConnectionEvent.CONNECTED:
			    ((TriggerObject)containers.get("loading")).trigger("wait");
				break;

			case ConnectionEvent.PARTNER_DISCONNECTED :
			case ConnectionEvent.DISCONNECTED :
				if (state == GAME_STATE.FINDING)
					((TriggerObject)containers.get("loading")).trigger("fail");
				else
				{
					state = GAME_STATE.SPLASH;
					Initialize();
				}
				break;
				
			case ConnectionEvent.PARTNER_FOUND:
				containers.get("start").invisible(true);
				containers.get("credit").invisible(true);
				containers.get("loading").invisible(true);
				state = GAME_STATE.LOADING;
				Initialize();
				break;
		}
		isConnecting = false;
	}

	@Override
	public boolean Initialize() 
	{
		Destroy();
		switch (state)
		{
			// load data
			case SPLASH:
				data = new DataParser(path.MAP + "splash.xml", 0);
				break;
				
			case FINDING:
				break;
				
			case LOADING:
				data = new DataParser(path.MAP + "stage1.xml", 0);
				state = GAME_STATE.PLAY;
				break;
				
			case CREDIT:
				break;
				
			case PLAY:
				break;
				
			case OVER:
				break;
		}
		
		data.loop((e)->{
			if(e.ID.equals("me") && me == null)
			{
				me = new PlayerObject(e);
				images.LoadImage(path.RES + me.texture, "me");
				me.image = images.GetImage("me");
				viewport.children.add(me);
				me.a_y = -0.98;
			}
			else if (e.ID.equals("you") && you == null)
			{
				you = new PartnerObject(e);
				images.LoadImage(path.RES + you.texture, "you");
				you.image = images.GetImage("you");
				viewport.children.add(you);
				you.a_y = -0.98;
			}
			else
				newObject(e);
		});
		
		if(state == GAME_STATE.PLAY)
		{
			CharacterInfoSyncher.registerPlayer(me);
			CharacterInfoSyncher.registerPartner(you);
		}
		
		// install processor
		processor.install("interaction", interaction = new Interaction(me, containers));
		processor.install("keyboard", keyboard = new Keyboard(inputs));
		processor.install("physics", physics = new Physics(me));
		processor.install("scene", scene = new Scene(viewport, stage = newObject(data.stage)));
		processor.install("mouse", mouse = new Mouse(inputs, settings.canvas_width, settings.canvas_height));
		processor.initilize(processor);
		
		// install objects	
		sprites.loop((e)->{
			objectInstall(e);
		});
		
		containers.loop((e)->{
			objectInstall(e);
		});
		
		// scene setting
		scene.setter(me);

		// text (for debug)
		text.height = 50;
		text.width = 50;
		text.x = 0;
		text.y = 0;
		
		// viewport setting
		viewport.radius_x = 25;
		viewport.radius_y = 25;
		viewport.pointOfView_z = 10000;
		viewport.view_baseDistance = 10000;
		viewport.view_minDistance = 1000;
		viewport.view_maxDistance = 100000;
		viewport.view_width = settings.canvas_width;
		viewport.view_height = settings.canvas_height;
		
		if(me != null)
			viewport.children.add(me);
		//viewport.children.add(text);
		
		return true;
	}
	
	public void Destroy()
	{
		sprites.loop((o)->objectUninstall(o));
		containers.loop((o)->objectUninstall(o));
		
		sprites = new ObjectManager<SpriteObject>();
		containers = new ObjectManager<ContainerObject>();
		processor = new ProcessManager(new Stage());
		viewport = new Viewport(0, 0, settings.canvas_width, settings.canvas_height);
		text = new TextBox();
		
		me = null;
		you = null;
	}
	
	@Override
	public boolean Update(long timeStamp) 
	{
		inputs.AcceptInputs();
		switch (state)
		{
			case FINDING:
				if (!isConnecting) 
				{
					if (!ConnectionManager.connect())
				    	((TriggerObject)containers.get("loading")).trigger("fail");
					isConnecting = true;
				}
				break;
			case PLAY:
				if (++intervalHandle == 5) {
					intervalHandle = 0;
					
					if (ConnectionManager.getPartnerSessionID() != null)
						CharacterInfoSyncher.fetch();
				}
				break;
			case OVER:
				break;
			default:
				break;
		}
		processor.loop();
		return true;
	}

	@Override
	public void Draw(long timeStamp) 
	{
		switch (state)
		{
			case SPLASH:
				break;
			case FINDING:
				break;
			case LOADING:
				break;
			case CREDIT:
				break;
			case PLAY:
				break;
			case OVER:
				break;
		}
		BeginDraw();
		ClearScreen();
		viewport.Draw(g);
		EndDraw();
	}
	
	public class Stage implements IProcessProperty<String, Integer> {
		@Override
		public void setter(String object) {
			switch (object) {
				case "start":
					if(state != GAME_STATE.SPLASH)
						break;
					state = GAME_STATE.FINDING;
					containers.get("start").invisible(true);
					containers.get("credit").invisible(true);
					((TriggerObject) containers.get("loading")).invisible(false);
			    	((TriggerObject) containers.get("loading")).trigger("connect");
					((TriggerObject) containers.get("loadAni")).trigger();
					connect();
					break;
				case "credit":
					if(state != GAME_STATE.SPLASH)
						break;
					state = GAME_STATE.CREDIT;
					me.pos_x= 850;
					me.pos_y = 0;
					break;
				case "credit_cancel":
					if(state != GAME_STATE.CREDIT)
						break;
					state = GAME_STATE.SPLASH;
					me.pos_x= -250;
					me.pos_y = 50;
					break;
				case "cancel":
					if(state != GAME_STATE.FINDING)
						break;
					state = GAME_STATE.SPLASH;
					containers.get("start").invisible(false);
					containers.get("credit").invisible(false);
					((TriggerObject) containers.get("loading")).invisible(true);
					((TriggerObject) containers.get("loadAni")).trigger();
					me.pos_y = 50;
					break;
			}
		}
		@Override
		public Integer getter() {
			return state.ordinal();
		}
	}
	
	private void connect()
	{
		ConnectionManager.connect();
		ConnectionManager.registerIReceiver(this);
	}
	
	private void objectInstall(SpriteObject o)
	{
		images.LoadImage(path.RES + o.texture, o.texture);
		o.image = images.GetImage(o.texture);
		physics.install(o);
		viewport.children.add(o);
		if(o.type.equals("button"))
			mouse.install(o);
	}
	
	private void objectInstall(ContainerObject o)
	{
		images.LoadImage(path.RES + o.texture, o.texture);
		o.image = images.GetImage(o.texture);
		physics.install(o);
		viewport.children.add(o);
		if(o.type.equals("button"))
			mouse.install(o);
	}
	
	private void objectUninstall(SpriteObject o)
	{
		physics.uninstall(o);
		viewport.children.remove(o);
		sprites.remove(o);
		if (o.ID.equals("container"))
		{
			ContainerObject c = (ContainerObject) o;
			containers.remove(c);
			for (SpriteObject s : c.childs)
				objectUninstall(s);
		}
		if(o.type.equals("button"))
			mouse.uninstall(o);
	}
	
	private SpriteObject newObject(DataObject e)
	{
		SpriteObject ret = null;
		switch (e.ID)
		{
			case "stage":
				ret = new SpriteObject(e);
				objectInstall(ret);
				break;
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
					case "button":
					case "trigger":
						ret = new TriggerObject(e);
						containers.insert(e.get("name"), (TriggerObject) ret);
				}
				for (DataObject o : e.getChild())
					((ContainerObject) ret).add(newObject(o));
				break;
		}
		if (e.get("visible").equals("false"))
			ret.invisible(true);
		return ret;
	}
}
