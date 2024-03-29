import java.util.Arrays;
import java.util.Random;

public class Assignment2_3 {
	
	static int base;
	static boolean[] map = new boolean[1000];
	
	static int[] getVivid(int value) {
		int[] arg = new int[]{value / 100, (value / 10) - (value / 100) * 10 , value - (value / 10) * 10 };
		return arg;
	}
	
	static boolean isValid(int[] arg) {
		boolean[] number = new boolean[10];
		Arrays.fill(number, false);
		for (int e : arg)
			if (number[e] == true || e == 0)
				return false;
			else
				number[e] = true;
		return true;
	}
	
	static void setInit() {
		Arrays.fill(map, true);
		for (int i = 0; i < 1000; i++)
			if (!isValid(getVivid(i)))
				map[i] = false;
	}
	
	static int getNumber() {
		int ret = 0;
		Random rand = new Random();
		do {
			ret = rand.nextInt(1000);
		} while (!map[ret]);
		return ret;
	}
	
	static int[] getMatch(int first, int second) {
		int[] f = getVivid(first), s = getVivid(second), ret = new int[2];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (f[i] == s[j])
					if (i == j)
						ret[0]++;
					else
						ret[1]++;
		return ret;
	}
	
	static void doProcess(int pitch, int nStrike, int nBall) {
		int strike, ball;
		for (int i = 0; i < 1000; i++)
		{
			int[] sb = getMatch(i, pitch);
			strike = sb[0];
			ball = sb[1];
			if (nStrike != strike || nBall != ball)
				map[i] = false;
		}
	}
	
	public static void main(String[] arg) {
		double x = 0,y = 0;
		for(int j = 0; j < 100; j++)
		{
			int countTry = 0, totalTry = 0;
			for (int i = 0; i < 1000; i++) {
				if (isValid(getVivid(i))) {
					countTry++;
					setInit();
					while (++totalTry > 0) {
						int number = getNumber();
						int [] sb = getMatch(number, i);
						if (sb[0] == 3)
							break;
						doProcess(number, sb[0], sb[1]);
					}
				}
			}
			System.out.println(countTry + " Sample, Average try:" + (double)totalTry/countTry);
			x+= totalTry;
			y+= countTry;
		}
		System.out.println("Total Average try: "  + (double) x/y);
		
	}
}
