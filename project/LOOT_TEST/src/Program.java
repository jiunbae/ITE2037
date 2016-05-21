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
	    settings.canvas_backgroundColor = Color.black;
	    settings.window_title = "pair";
	    settings.gameLoop_interval_ns = 1000000000 / 60;
	    
	    Window window = new Window(settings);
	    
	    window.setVisible(true);
	}

}
