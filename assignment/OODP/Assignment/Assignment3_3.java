import toys.*;

public class Assignment3_3 {
	
	private static final int NUM = 10;
	
	public static void main(String[] args) {
		Toy[] toys = new Toy[NUM];
		for (int i = 0; i < NUM; i++)
		{
			toys[i] = ToyFactory.MakeOne();
		}

		for (Toy toy : toys) {
			System.out.println(toy.toString());
		}
		
		System.out.println("Fixing===");
		for (Toy toy : toys) {
			if (!toy.IsValid())
				toy.Fix();
		}
		
		for (Toy toy : toys) {
			System.out.println(toy.toString());
		}
		
	}
}
