
public class Toy {
	int numberOfHeads;
	int numberOfArms;
	int numberOfLegs;

	public Toy() {
		numberOfHeads = 1;
		numberOfArms = 2;
		numberOfLegs = 2;
	}
	
	@Override
	public String toString()
	{
		return String.format("�Ӹ� %d��, �� %d��, �ٸ� %d�� �޸� �峭��", numberOfHeads, numberOfArms, numberOfLegs);
	}

}