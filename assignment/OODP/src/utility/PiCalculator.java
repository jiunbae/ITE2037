package utility;

import java.util.Random;

public class PiCalculator {
	Random rand; //복붙하고 Random의 m 오른쪽에 커서를 둔 다음 Ctrl + Space로 import부분 추가
	Double pi;
	
	private Thread thr_runner;        // Thread 그 자체
	private boolean isStarted;        // 내 주인에 의해 Start()가 호출되었는지를 나타냄
	private boolean isStopRequested;  // 내 주인에 의해 Stop()이 호출되었는지를 나타냄

	
	public PiCalculator() {
		rand = new Random();	
		
		thr_runner = new Thread(() -> {
			int x;
			int y;
			int count_in;
			
			count_in = 0;
			
			for ( int i = 0; isStopRequested == false; i++ )
			{
				x = rand.nextInt(Constants.Width);
				y = rand.nextInt(Constants.Width);
				
				x -= Constants.Center_X;
				x *= x;
				
				y -= Constants.Center_Y;
				y *= y;
				
				if ( x + y <= Constants.Radius_Square )
					++count_in;
				
				pi = (double)count_in / (i + 1) * 4.0;
			}
		});
	}
	
	public void Start()
	{
		if ( isStarted == false )
		{
			isStarted = true;
			
			isStopRequested = false;
			thr_runner.start();
		}
	}
		
	public void Print()
	{
		System.out.println("PI: " + pi + "\n");
	}

	public void Stop()
	{
		isStopRequested = true;
	}

}
