import java.util.Random;

public class Assignment2_2 {
	int[] ary = { 1, 2, 3, 4, 5, 6, 7 };

	static void PrintData(int[] ary) {
		System.out.print("Array: ");
		for (int e : ary) {
			System.out.print(e + ",");
		}
		System.out.println("");
		System.out.println("Sum: " + GetSum(ary));
	}
	
	static int GetSum(int[] ary) {
		int ret = 0;
		for (int e : ary) {
			ret += e;
		}
		return ret;
	}

	static int[] SetNum(int[] ary, int value) {
		int[] ret = new int[ary.length];
		for (int i = 0; i < ary.length; i++) {
			ret[i] = value;
		}
		return ret;
	}

	static int[] SetRandom(int[] ary, int value) {
		int[] ret = new int[ary.length];
		Random rand = new Random();
		for (int i = 0; i < ary.length; i++) {
			ret[i] = rand.nextInt(value);
		}
		return ret;
	}

	static int[] MakeData(int length) {
		Random rand = new Random();
		int ret[] = new int[length];
		for (int i = 0; i < length; i++) {
			ret[i] = rand.nextInt(100);
		}
		return ret;
	}
	
	static int[] AddNum(int[] ary, int value) {
		int[] ret = new int[ary.length];
		for ( int i = 0; i < ary.length; i++ ) {
			ret[i] = ary[i] + value;
		}
		return ary;
	}
	
	static int[] AddOne(int[] ary) {
		int[] ret = new int[ary.length];
		for ( int i = 0; i < ary.length; i++ ) {
			ret[i] = ary[i] + 1;
		}
		return ret;
	}
	
	public static void main(String[] arg) {
		int[] ary = MakeData(10);
		PrintData(ary);
		ary = AddOne(ary);
		PrintData(ary);
	}

}
