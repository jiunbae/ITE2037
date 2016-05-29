import loot.GameFrameSettings;

public class Constant 
{
	public String RES, MAP;
	
	public final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
	
	public Constant(GameFrameSettings settings)
	{
		RES = settings.path.get("res");
		MAP = settings.path.get("map");
	}
}
