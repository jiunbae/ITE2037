package l4g.common;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

/**
 * 정규 게임에서 사용할 게임 번호 목록을 관리하는 클래스입니다.
 * 
 * @author Racin
 *
 */
public class GameNumberManager
{
	public int lengthOfData;
	public long[] data;
	
	Random rand;

	public GameNumberManager(int numberOfGames)
	{
		lengthOfData = numberOfGames;
		data = new long[numberOfGames];
	}
	
	/**
	 * 주어진 파일에서 바이트 배열을 읽어 와 게임 번호 목록을 만듭니다.
	 * 같은 파일을 사용하면 항상 같은 목록이 생성됩니다.
	 * 성공 여부를 반환합니다.<br>
	 * <br>
	 * <b>주의:</b> 파일의 내용이 충분히 길지 않은 경우 생성에 실패할 수 있습니다.
	 * 
	 * @param fileName_seed 게임 번호 목록 생성에 사용할 파일 이름입니다.
	 */
	public boolean Create(String fileName_seed)
	{
		try
		{
			//Seed가 될 파일에서 글자들을 읽어와 조합 
			FileReader rs = new FileReader(fileName_seed);
			
			long seed = 0;
			int input = 0;
			
			while ( input != -1 )
			{
				seed += input;
				input = rs.read();
			}			

			rs.close();

			rand = new Random(seed);
			
			boolean isSameGameNumberFound;
			long newGameNumber;
				
			for ( int iNew = 0; iNew < lengthOfData; ++iNew )
			{
				isSameGameNumberFound = false;
				
				newGameNumber = rand.nextLong();
				
				//겹치지 않는 게임 번호 조합을 만들기 위해 이전에 만든 게임 번호들과 비교
				for ( int iCurrent = 0; iCurrent < iNew; ++iCurrent )
				{
					if ( newGameNumber == data[iCurrent] )
					{
						isSameGameNumberFound = true;
						break;
					}
				}
				
				//겹치는 경우 이번 게임 번호를 다시 생성
				if ( isSameGameNumberFound == true )
					--iNew;
				//겹치지 않는 경우 게임 번호 목록에 등록
				else
					data[iNew] = newGameNumber;
			}
		}
		catch ( Exception e )
		{
			System.err.println("게임 번호 목록을 생성할 수 없습니다. 파일 이름이 잘못되었거나 해당 이름의 파일을 사용할 수 없습니다.");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/**
	 * 주어진 seed값이 양수인 경우 이를 토대로 임의의 게임 번호 목록을 만듭니다.
	 * seed값이 0 또는 음수인 경우 현재 시각 정보를 바탕으로 임의의 게임 번호 목록을 만듭니다.
	 * 성공 여부를 반환합니다.
	 * 
	 * @param seed 양수인 경우 임의의 게임 번호 목록을 만드는 seed로 사용됩니다. 그렇지 않은 경우 현재 시각 정보를 바탕으로 임의의 게임 번호 목록을 만듭니다.
	 */
	public boolean Create(int seed)
	{
		//seed가 양수인 경우 해당 seed값을 토대로 랜덤 요소 초기화
		if ( seed > 0 )
			rand = new Random(seed);
		//그렇지 않은 경우 현재 시각 정보를 바탕으로 랜덤 요소 초기화
		else
			rand = new Random();
		
		boolean isSameGameNumberFound;
		long newGameNumber;
		
		for ( int iNew = 0; iNew < lengthOfData; ++iNew )
		{
			isSameGameNumberFound = false;
			newGameNumber = rand.nextLong();
			
			if ( newGameNumber < 0 )
				newGameNumber += Long.MAX_VALUE;
			
			//겹치지 않는 게임 번호 조합을 만들기 위해 이전에 만든 게임 번호들과 비교
			for ( int iCurrent = 0; iCurrent < iNew; ++iCurrent )
			{
				if ( newGameNumber == data[iCurrent] )
				{
					isSameGameNumberFound = true;
					break;
				}
			}
			
			//겹치는 경우 이번 게임 번호를 다시 생성
			if ( isSameGameNumberFound == true )
				--iNew;
			//겹치지 않는 경우 게임 번호 목록에 등록
			else
				data[iNew] = newGameNumber;
		}
		
		return true;
	}

	/**
	 * 주어진 값에서 시작하여 1씩 증가하는 게임 번호 목록을 만듭니다.
	 * 주어진 값이 0 또는 음수인 경우 실패합니다.
	 * 성공 여부를 반환합니다.
	 * 
	 * @param startNumber 목록의 첫 번째에 해당하는 게임 번호입니다.
	 */
	public boolean Create_Sequence(long startNumber)
	{
		if ( startNumber <= 0 )
			return false;
		
		for ( int iData = 0; iData < lengthOfData; ++iData )
		{
			data[iData] = startNumber;
			++startNumber;
		}		
		
		return true;
	}

	/**
	 * 현재 게임 번호 목록을 주어진 파일에 저장합니다.
	 * 
	 * @param fileName_result 저장할 파일 이름입니다.
	 * @return 저장에 성공한 경우 true, 그렇지 않은 경우 false입니다.
	 */
	public boolean Save(String fileName_result)
	{
		try
		{
			PrintStream ws = new PrintStream(fileName_result);
			
			for ( long gameNumber : data )
				ws.println(gameNumber);
			
			ws.close();
		}
		catch ( Exception e )
		{
			System.err.println("게임 번호 목록을 저장할 수 없습니다. 파일 이름이 잘못되었거나 해당 이름의 파일을 사용할 수 없습니다.");
			e.printStackTrace();
			return false;			
		}
		
		return true;
	}
	
	/**
	 * 주어진 파일에서 게임 번호 목록을 불러옵니다.
	 * 
	 * @param fileName_savedNumbers 게임 번호 목록을 불러올 파일 이름입니다.
	 * @return 불러오기에 성공한 경우 true, 그렇지 않은 경우 false입니다.
	 */
	public boolean Load(String fileName_savedNumbers)
	{
		try
		{
			FileInputStream fs = new FileInputStream(fileName_savedNumbers);
			Scanner scanner = new Scanner(fs);
			
			for ( int i = 0; i < lengthOfData; ++i )
			{
				if ( scanner.hasNext() == false )
				{
					scanner.close();
					System.err.println("게임 번호 목록을 불러올 수 없습니다. 파일의 내용이 너무 짧습니다.");
					return false;
				}
				
				data[i] = scanner.nextLong();
			}
			
			scanner.close();
		}
		catch (Exception e)
		{
			System.err.println("게임 번호 목록을 불러올 수 없습니다. 파일 이름이 잘못되었거나 해당 이름의 파일을 사용할 수 없습니다.");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
