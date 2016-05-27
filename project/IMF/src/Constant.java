import loot.GameFrameSettings;

public class Constant 
{
	public String RES, MAP;
	
	public Constant(GameFrameSettings settings)
	{
		RES = settings.path.get("res");
		MAP = settings.path.get("map");
	}
}
