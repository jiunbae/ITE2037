
import imf.data.DataManager;
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
	
	PlayerObject me = null;
	PartnerObject you = null;
	SpriteObject stage;
	
	public Window(GameFrameSettings settings) 
	{
		super(settings);
		
		// load settings
		path = new Constant(settings);
		
		// create new instance
		processor = new ProcessManager(new Stage());
		viewport = new Viewport(0, 0, settings.canvas_width, settings.canvas_height);
		text = new TextBox();
		
		ConnectionManager.registerIReceiver(this);
	}
	
	
	@Override
	public void onReceived(ConnectionEvent e) 
	{
		switch (e.type) {
			
			case ConnectionEvent.CONNECTED:
			    ((TriggerObject)DataManager.get_containers("loading")).trigger("wait");
				break;

			case ConnectionEvent.PARTNER_DISCONNECTED :
				ConnectionManager.disconnect();
				
				state = GAME_STATE.SPLASH;
				Initialize();
				
				break;
				
			case ConnectionEvent.DISCONNECTED :
				if (state == GAME_STATE.FINDING)
					((TriggerObject)DataManager.get_containers("loading")).trigger("fail");
				else
				{
					state = GAME_STATE.SPLASH;
					Initialize();
				}
				break;
				
			case ConnectionEvent.PARTNER_FOUND:
				DataManager.get_containers("start").invisible(true);
				DataManager.get_containers("credit").invisible(true);
				DataManager.get_containers("loading").invisible(true);
				state = GAME_STATE.LOADING;
				Initialize();
				break;
		}
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
				
			case LOADING:
				data = new DataParser(path.MAP + "stage1.xml", 0);
				state = GAME_STATE.PLAY;
				break;
				
			default:
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
		processor.install("interaction", interaction = new Interaction(me));
		processor.install("keyboard", keyboard = new Keyboard(inputs));
		processor.install("physics", physics = new Physics(me));
		processor.install("scene", scene = new Scene(viewport, stage = newObject(data.stage)));
		processor.install("mouse", mouse = new Mouse(inputs, settings.canvas_width, settings.canvas_height));
		processor.initilize(processor);
		
		// install objects
		DataManager.loop_sprites((e)->objectInstall(e));
		DataManager.loop_containers((e)->objectInstall(e));
		
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
		DataManager.loop_sprites((o)->objectUninstall(o));
		DataManager.loop_containers((o)->objectUninstall(o));
		processor.finalize();
		processor.uninstall("interaction");
		processor.uninstall("keyboard");
		processor.uninstall("physics");
		processor.uninstall("mouse");
		processor.uninstall("scene");
		/**
		 * 리스너에서 삭제할수있어야함.
		 */
		
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
					if (state != GAME_STATE.SPLASH)
						break;
					
					state = GAME_STATE.FINDING;
					DataManager.get_containers("start").invisible(true);
					DataManager.get_containers("credit").invisible(true);
					((TriggerObject) DataManager.get_containers("loading")).invisible(false);
			    	((TriggerObject) DataManager.get_containers("loading")).trigger("connect");
					((TriggerObject) DataManager.get_containers("loadAni")).trigger();
					ConnectionManager.connect();
					
					break;
					
				case "credit":
					if (state != GAME_STATE.SPLASH)
						break;
					state = GAME_STATE.CREDIT;
					me.pos_x= 850;
					me.pos_y = 100;
					break;
					
				case "credit_cancel":
					if (state != GAME_STATE.CREDIT)
						break;
					state = GAME_STATE.SPLASH;
					me.pos_x= -250;
					me.pos_y = 100;
					break;
					
				case "cancel":
					if (state != GAME_STATE.FINDING)
						break;
					state = GAME_STATE.SPLASH;
					DataManager.get_containers("start").invisible(false);
					DataManager.get_containers("credit").invisible(false);
					((TriggerObject) DataManager.get_containers("loading")).invisible(true);
					((TriggerObject) DataManager.get_containers("loadAni")).trigger();
					me.pos_y = 50;
					
					ConnectionManager.disconnect();
					
					break;
			}
		}
		@Override
		public Integer getter() {
			return state.ordinal();
		}
	}
	
	
	private void objectInstall(SpriteObject o)
	{
		images.LoadImage(path.RES + o.texture, o.texture);
		o.image = images.GetImage(o.texture);
		physics.install(o);
		viewport.children.add(o);
		if (o.type.equals("button"))
			mouse.install(o);
	}
	
	private void objectInstall(ContainerObject o)
	{
		images.LoadImage(path.RES + o.texture, o.texture);
		o.image = images.GetImage(o.texture);
		physics.install(o);
		viewport.children.add(o);
		if (o.type.equals("button"))
			mouse.install(o);
	}
	
	private void objectUninstall(SpriteObject o)
	{
		physics.uninstall(o);
		viewport.children.remove(o);
		DataManager.remove(o.name);
		if (o.ID.equals("container"))
		{
			ContainerObject c = (ContainerObject) o;
			DataManager.remove(c.name);
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
				DataManager.add(e.get("name"), ret);
				break;
			case "container":
				switch (e.get("type"))
				{
					case "box":
						ret = new ContainerObject(e);
						DataManager.add(e.get("name"), (ContainerObject) ret);
					case "button":
					case "trigger":
						ret = new TriggerObject(e);
						DataManager.add(e.get("name"), (TriggerObject) ret);
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
