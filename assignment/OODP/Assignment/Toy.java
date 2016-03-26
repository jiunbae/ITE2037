
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
		return String.format("머리 %d개, 팔 %d개, 다리 %d개 달린 장난감", numberOfHeads, numberOfArms, numberOfLegs);
	}

}
