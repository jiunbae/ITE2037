package toys;

/**
 * 머리, 팔, 다리가 몇 개씩 달릴 수 있는
 * 장난감 하나를 나타내는 class입니다.
 * 
 * @author Racin
 *
 */
public class Toy
{
	int numberOfHeads;
	int numberOfArms;
	int numberOfLegs;
	
	Toy()
	{
		Fix();
	}
	
	public boolean IsValid()
	{
		return numberOfHeads == 1 &&
				numberOfArms == 2 &&
				numberOfLegs == 2;
	}
	
	public void Fix()
	{
		numberOfHeads = 1;
		numberOfArms = 2;
		numberOfLegs = 2;
	}
	
	@Override
	public String toString()
	{
		return String.format("머리 %d개, 팔 %d개, 다리 %d개 달린 장난감", 
				numberOfHeads, numberOfArms, numberOfLegs);
	}
}
