import java.awt.Color;
import java.util.Random;
import loot.GameFrame;
import loot.GameFrameSettings;
import loot.graphics.DrawableObject;
import loot.graphics.Layer;
import loot.graphics.TextBox;

public class Window extends GameFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Layer layer = new Layer(0,0,settings.canvas_width,settings.canvas_height);
	Random rand;
	TextBox tbox = new TextBox();
	long inner = 0, outter = 0;
	long total = 0;
	
	public class Dot extends DrawableObject {
		public Dot(int x, int y, boolean z) {
			super(x,y,14,14,images.GetImage(z ? "red" : "black"));
			++total;
		}
	}
	
	
	public Window(GameFrameSettings settings) {
		super(settings);

	}

	private double isDistance(int x, int y) {
		int centerX = (settings.canvas_width-10)/2;
		int centerY = (settings.canvas_height-10)/2;
		return Math.sqrt((x-centerX) *(x-centerX)  + (y-centerY)*(y-centerY));
	}
	private boolean isIn(int x, int y) {
		double dist = isDistance(x,y);
		return isDistance(x,y) <= (settings.canvas_height / 2);
	}
	
	@Override
	public boolean Initialize() {
		rand = new Random();
		images.LoadImage("images/dot_red.png", "red");
		images.LoadImage("images/dot_black.png", "black");
		tbox.x = 10;
		tbox.y = 10;
		tbox.height = 100;
		tbox.width = 100;
		tbox.background_color = Color.black;
		tbox.foreground_color = Color.white;
		
		layer.children.add(tbox);
		
		return true;
	}

	@Override
	public boolean Update(long timeStamp) {
		int nX = rand.nextInt(settings.canvas_width - 10);
		int nY = rand.nextInt(settings.canvas_height - 10);
		boolean in = isIn(nX,nY);
		if(in)
			++inner;
		else
			++outter;
		
		Dot dot = new Dot(nX, nY, isIn(nX, nY));
		layer.children.add(dot);
		

		if(total > 0)
		{
			tbox.text = "PI: " + 4* (inner / (double)total);
			
		}
		return true;
	}

	@Override
	public void Draw(long timeStamp) {
		BeginDraw();
		
		layer.Draw(g);
		
		EndDraw();

	}

}
