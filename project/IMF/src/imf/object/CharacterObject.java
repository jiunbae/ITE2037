package imf.object;

public class CharacterObject extends DynamicObject 
{
	public CharacterObject(int x, int y, int z)
	{
		super(x, y, z, -1, -1);
	}
	public CharacterObject(int x, int y, int z, int width, int height)
	{
		super(x, y, z, width, height);
	}
}
