package toys;

public class Toy {
	private int numberOfHeads;
	private int numberOfArms;
	private int numberOfLegs;

	private static final int defaultOfHead = 1;
	private static final int defaultOfArms = 2;
	private static final int defaultOfLegs = 2;
	
	public Toy(int head, int arm, int leg)
	{
		numberOfHeads = head;
		numberOfArms = arm;
		numberOfLegs = leg;
	}
	public Toy() 
	{
		this(defaultOfHead,defaultOfArms,defaultOfLegs);
	}
	
	@Override
	public String toString()
	{
		return String.format("머리 %d개, 팔 %d개, 다리 %d개 달린 장난감", numberOfHeads, numberOfArms, numberOfLegs);
	}
	public boolean IsValid()
	{
		if(numberOfHeads == defaultOfHead &&
			numberOfArms == defaultOfArms &&
			numberOfLegs == defaultOfLegs)
		{
			return true;
		}
		return false;
	}
	public void Fix() 
	{
		if(IsValid() == false)
		{
			numberOfHeads = defaultOfHead;
			numberOfArms = defaultOfArms;
			numberOfLegs = defaultOfLegs;
		}
	}

}
