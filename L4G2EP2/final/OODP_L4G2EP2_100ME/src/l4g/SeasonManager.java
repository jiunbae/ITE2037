package l4g;

import l4g.common.Classroom_Settings;

/**
 * 정규 게임의 한 시즌을 진행하기 위한 요소들을 담고 있는 클래스입니다.
 * 
 * @author Racin
 *
 */
public class SeasonManager
{
	static final int NumberOfThreads = 8;
	
	int seasonNumber;
	
	long gameNumber_start;
	long gameNumber_end;
	long gameNumber_current;
	
	public Grader grader;
	
	Object lock;
	Thread[] thrs;
	
	int count_completedThreads;
	
	Classroom_Settings settings;
	
	public SeasonManager(Classroom_Settings settings, int seasonNumber)
	{
		this.seasonNumber = seasonNumber;
		
		int shipJari = seasonNumber / 10;
		int illJari = seasonNumber % 10;
		
		gameNumber_start = 2006000000L + shipJari * 1000000L + illJari * 10000L;
		gameNumber_end = gameNumber_start + 10000L;
		
		grader = new Grader();
		
		lock = new Object();
		thrs = new Thread[SeasonManager.NumberOfThreads];
		
		count_completedThreads = 0;
		
		this.settings = settings;
	}
	
	public void Initialize()
	{
		String logFileName = String.format("log_season_%d.txt", seasonNumber);
		
		if ( grader.LoadFromFile(logFileName) == true )
		{
			count_completedThreads = NumberOfThreads;
			return;
		}
		
		for ( int iThread = 0; iThread < NumberOfThreads; ++iThread )
			thrs[iThread] = new Thread(runner_thrs);
		
		gameNumber_current = gameNumber_start;
	}
	
	public void Start()
	{
		if ( count_completedThreads == NumberOfThreads )
			return;
		
		for ( int iThread = 0; iThread < NumberOfThreads; ++iThread )
			thrs[iThread].start();
	}
	
	public boolean IsCompleted()
	{
		return count_completedThreads == NumberOfThreads;
	}
	
	public void Log()
	{
		if ( IsCompleted() == false )
		{
			return;
		}
		
		String logFileName = String.format("log_season_%d.txt", seasonNumber);
		
		grader.SaveToFile(logFileName);
	}

	Runnable runner_thrs = new Runnable()
	{
		@Override
		public void run()
		{
			long gameNumberToRun;
			Classroom_Settings settings_local = new Classroom_Settings(settings);
			Classroom classroom;
			
			while ( count_completedThreads != NumberOfThreads )
			{
				synchronized ( lock )
				{
					if ( gameNumber_current == gameNumber_end )
					{
						++count_completedThreads;
						break;
					}
					
					gameNumberToRun = gameNumber_current;
					++gameNumber_current;
				}
				
				settings_local.game_number = gameNumberToRun;
				
				classroom = new Classroom(settings_local);
				classroom.Initialize();
				classroom.Start();
				
				synchronized ( lock )
				{
					grader.Update(classroom);
				}
			}
		}
	};
}
