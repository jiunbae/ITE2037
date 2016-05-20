import java.awt.Color;

import loot.GameFrameSettings;

public class Assignment6 
{
	static final int HEIGHT = 600, WIDTH = 600, DEPTH = 500;
	
	public static void main(String[] args) 
	{	
	    GameFrameSettings settings = new GameFrameSettings();
	    
	    settings.canvas_height = HEIGHT;
	    settings.canvas_width = WIDTH;
	    settings.canvas_backgroundColor = Color.white;
	    settings.window_title = "pair";
	    settings.gameLoop_interval_ns = 1000;
	    
	    Window window = new Window(settings);
	    
	    window.setVisible(true);
	}

}
