import toys.Toy;
import toys.ToyFactory;

public class Program
{
	public static void main(String[] args)
	{
		Toy[] toys = new Toy[10];
		
		
		System.out.print("장난감 생성...");
		
		for ( int i = 0; i < toys.length; i++ )
			toys[i] = ToyFactory.MakeOne();
		
		System.out.println("완료!");
		System.out.println();
		
		
		System.out.println("장난감 목록(수리 전):");
		
		for ( Toy toy : toys )
			System.out.println(toy);
		
		System.out.println();
		
		
		System.out.print("장난감 수리...");
		
		for ( Toy toy : toys )
			if ( toy.IsValid() == false)
				toy.Fix();
		
		System.out.println("완료!");
		System.out.println();
		
		
		System.out.println("장난감 목록(수리 후):");
		
		for ( Toy toy : toys )
			System.out.println(toy);
		
		System.out.println();
		
		
		System.out.println("적절한 완료 메시지");
	}
}
