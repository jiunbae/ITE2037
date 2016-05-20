package toys;

import java.util.Random;

public class ToyFactory {
	public static Toy MakeOne() 
	{
		Toy ret = new Toy(1,2,2);
		Random rand = new Random();
		if (rand.nextInt(10) < 3)
		{
			do{
				ret = new Toy(rand.nextInt(4), rand.nextInt(8), rand.nextInt(8));
			} while (ret.IsValid());
			return ret;
		}
		return ret;
	}
}
