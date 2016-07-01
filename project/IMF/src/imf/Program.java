package imf;
import java.awt.Color;

import loot.GameFrameSettings;

public class Program 
{
	static final int HEIGHT = 600, WIDTH = 1200, DEPTH = 500;
	
	public static void main(String[] args) 
	{	
	    GameFrameSettings settings = new GameFrameSettings();
	    
	    settings.canvas_height = HEIGHT;
	    settings.canvas_width = WIDTH;
	    settings.window_title = "IMF - It's (not) My Fault";
	    settings.canvas_backgroundColor = Color.white;
	    settings.numberOfButtons = 18;
	    settings.gameLoop_interval_ns = 1000000000 / 75;
	    
	    Window window = new Window(settings);
	    window.setVisible(true);
	}
}