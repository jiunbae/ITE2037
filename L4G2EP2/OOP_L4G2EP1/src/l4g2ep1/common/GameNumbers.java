package l4g2ep1.common;

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
public class GameNumbers
{
	public int lengthOfData;
	public int[] data;

	public GameNumbers(int numberOfGames)
	{
		lengthOfData = numberOfGames;
		data = new int[numberOfGames];
	}
	
	/**
	 * 주어진 파일에서 바이트 배열을 읽어 와 게임 번호 목록을 만듭니다.
	 * 같은 파일을 사용하면 항상 같은 목록이 생성됩니다.
	 * 주의: 파일의 내용이 충분히 길지 않은 경우 생성에 실패할 수 있습니다.
	 * 
	 * @param fileName_seed 게임 번호 목록 생성에 사용할 파일 이름입니다.
	 * @return 생성에 성공한 경우 true, 그렇지 않은 경우 false입니다.
	 */
	public boolean Create(String fileName_seed)
	{
		try
		{
			//Seed가 될 파일에서 글자들을 읽어와 조합 
			FileReader rs = new FileReader(fileName_seed);
			
			try
			{
				boolean isSameGameNumberFound;
				int newGameNumber;
				
				for ( int iNew = 0; iNew < lengthOfData; ++iNew )
				{
					isSameGameNumberFound = false;
					newGameNumber = rs.read();
					
					//목록을 모두 채우기 전에 파일의 끝에 도달한 경우 생성 실패
					if ( newGameNumber == -1 )
					{
						if ( rs != null )
							rs.close();
						rs = null;
						
						throw new Exception();
					}
					
					//방금 읽은 글자가 16비트 형식인 경우 다음 글자 하나와 조합하여 32비트 숫자 생성
					if ( newGameNumber > 0x100)
					{
						newGameNumber <<= 16;
						newGameNumber += rs.read();
					}
					//방금 읽은 글자가 8비트 형식인 경우 다음 글자 둘과 조합하여 24 ~ 32비트 숫자 생성 
					else
					{
						newGameNumber <<= 8;
						newGameNumber += rs.read();
						newGameNumber <<= 8;
						newGameNumber += rs.read();
					}
					
					//마지막으로, 생성된 숫자가 음수인 경우 양수로 변경
					if ( newGameNumber < 0 )
						newGameNumber += Integer.MAX_VALUE;
					
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
				System.err.println("게임 번호 목록을 생성할 수 없습니다. Seed 파일의 크기가 너무 작습니다.");
				e.printStackTrace();
				
				if ( rs != null )
					rs.close();
				
				return false;
			}

			rs.close();
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
	 * 
	 * @param seed 양수인 경우 임의의 게임 번호 목록을 만드는 seed로 사용됩니다. 그렇지 않은 경우 현재 시각 정보를 바탕으로 임의의 게임 번호 목록을 만듭니다.
	 * @return 생성에 성공한 경우 true, 그렇지 않은 경우 false입니다.
	 */
	public boolean Create(int seed)
	{
		java.util.Random rand;

		//seed가 양수인 경우 해당 seed값을 토대로 랜덤 요소 초기화
		if ( seed > 0 )
			rand = new Random(seed);
		//그렇지 않은 경우 현재 시각 정보를 바탕으로 랜덤 요소 초기화
		else
			rand = new Random();
		
		boolean isSameGameNumberFound;
		int newGameNumber;
		
		for ( int iNew = 0; iNew < lengthOfData; ++iNew )
		{
			isSameGameNumberFound = false;
			newGameNumber = rand.nextInt();
			
			if ( newGameNumber < 0 )
				newGameNumber += Integer.MAX_VALUE;
			
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
			
			for ( int gameNumber : data )
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
				
				data[i] = scanner.nextInt();
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
