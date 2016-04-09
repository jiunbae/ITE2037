package l4g2ep1;

/**
 * 플레이어 한 명의 게임별 점수를 담는 클래스입니다.
 * 
 * @author Racin
 * 
 */
public class Score
{
	/*
	 * 이 필드는 여러분이 직접 볼 수 없으며 대신 아래에 정의된 getter 메서드들을 사용하여 내용을 읽어올 수 있습니다.
	 * ( 다시 말하면, 여러분이 이 필드의 내용을 수정할 수는 없습니다 )
	 */
	int[] data;

	/*
	 * 여러분이 직접 이 클래스의 인스턴스를 만들 수는 없습니다.
	 * 여러분의 현재 점수는 자동으로 제공됩니다.
	 */
	Score()
	{
		/*
		 * 기본 여섯 부문(각 상태별 둘씩) 이외에
		 * 잘못된 의사 결정 횟수와 런타임 예외 발생 횟수도 같이 집계
		 * -> 플레이어에게 보여주지는 않음
		 */
		data = new int[8];
	}

	/**
	 * 현재 기록중인 생존자 최대 생존 턴 수를 읽어옵니다.
	 */
	public int GetSurvivor_Max_Survived_Turns() { return data[0]; }

	/**
	 * 현재 기록중인 생존자 총 포착 횟수를 읽어옵니다.
	 */
	public int GetSurvivor_Total_Spots() { return data[1]; }

	/**
	 * 현재 기록중인 시체 최대 치유 플레이어 수를 읽어옵니다.
	 */
	public int GetCorpse_Max_Healed_Players() { return data[2]; }

	/**
	 * 현재 기록중인 시체 총 치유량을 읽어옵니다.
	 */
	public int GetCorpse_Total_Heals() { return data[3]; }

	/**
	 * 현재 기록중인 감염체 최대 처치 횟수를 읽어옵니다.
	 */
	public int GetInfected_Max_Kills() { return data[4]; }

	/**
	 * 현재 기록중인 감염체 총 감염량을 읽어옵니다.
	 */
	public int GetInfected_Total_Infects() { return data[5]; }

	@Override
	public String toString()
	{
		return String.format("%4d %4d %4d %4d %4d %4d", data[0], data[1], data[2], data[3], data[4], data[5]);
	}
}
