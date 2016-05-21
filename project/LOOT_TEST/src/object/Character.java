package object;

public class Character extends Dynamic 
{
	public Character(int x, int y, int z)
	{
		super(x, y, z, -1, -1);
	}
	public Character(int x, int y, int z, int width, int height)
	{
		super(x, y, z, width, height);
	}
}
