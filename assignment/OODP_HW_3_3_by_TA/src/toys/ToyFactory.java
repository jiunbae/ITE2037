package toys;

import java.util.Random;

/**
 * 새 장난감 instance를 만들기 위한 static method를 제공합니다.
 * 
 * @author Racin
 *
 */
public class ToyFactory
{
	private static Random rand = new Random();
	
	private ToyFactory()
	{
	}
	
	public static Toy MakeOne()
	{
		Toy newToy = new Toy();
		
		if ( rand.nextInt(10) < 3 )
			newToy.numberOfHeads = 3;
		
		return newToy;
	}
}
