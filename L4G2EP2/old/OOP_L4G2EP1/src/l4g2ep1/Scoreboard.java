package l4g2ep1;

import l4g2ep1.common.*;

/**
 * 플레이어들의 점수를 기록하고 이를 토대로 학점을 산출하기 위한 모든 정보를 담는 클래스입니다.
 * 이 클래스는 강의실 내부에서만 사용되며 여러분은 이 클래스를 볼 수 없습니다.
 * 자신의 현재 점수를 확인하려면 this.myScore 필드를 참고하세요. 
 * 
 * @author Racin
 *
 */
class Scoreboard
{
	enum ScoreTypeCode
	{
		Survivor_Max,
		Survivor_Total,
		Corpse_Max,
		Corpse_Total,
		Infected_Max,
		Infected_Total,
		Soul_Freedom,
		Soul_Destruction
	}

	Score[] scores;

	int[][] grades;
	int[][] ranks;

	int[] final_grades;
	int[] final_ranks;

	/*
	 * 여러분은 이 클래스를 전혀 사용할 수 없습니다.
	 * 자신의 현재 점수를 확인하려면 this.myScore 필드를 참고하세요.
	 */
	Scoreboard()
	{
		scores = new Score[Constants.Total_Players];
		for ( int iScore = 0; iScore < Constants.Total_Players; ++iScore )
		{
			scores[iScore] = new Score();
		}

		grades = new int[Constants.Total_Players][8];
		ranks = new int[Constants.Total_Players][8];
		final_grades = new int[Constants.Total_Players];
		final_ranks = new int[Constants.Total_Players];
	}

	void Update(ScoreTypeCode type, int ID, int amount)
	{
		switch (type)
		{
		case Survivor_Max:
			if ( scores[ID].data[0] < amount )
			{
				scores[ID].data[0] = amount;
			}
			break;
		case Survivor_Total:
			scores[ID].data[1] += amount;
			break;
		case Corpse_Max:
			if ( scores[ID].data[2] < amount )
			{
				scores[ID].data[2] = amount;
			}
			break;
		case Corpse_Total:
			scores[ID].data[3] += amount;
			break;
		case Infected_Max:
			if ( scores[ID].data[4] < amount )
			{
				scores[ID].data[4] = amount;
			}
			break;
		case Infected_Total:
			scores[ID].data[5] += amount;
			break;
		case Soul_Freedom:
			scores[ID].data[6] += amount;
			break;
		case Soul_Destruction:
			scores[ID].data[7] += amount;
			break;
		}
	}

	void CalculateGrades()
	{
		int[] max = new int[6];
		int[] min = new int[6];

		// Max 계열은 최대값만 측정(최소값은 항상 0), Total 계열은 최대값과 최소값 모두 측정
		for ( int i = 0; i < 6; ++i )
		{
			if ( i % 2 == 1 )
				min[i] = Integer.MAX_VALUE;

			for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
			{
				int value = scores[iPlayer].data[i];
				if ( max[i] < value )
					max[i] = value;
				if ( min[i] > value )
					min[i] = value;
			}
		}

		// 측정한 값을 토대로 계열별 학점 및 순위 산출
		for ( int i = 0; i < 6; ++i )
		{
			// 해당 계열에 대한 모든 플레이어의 득점이 같다면 전부 1등
			if ( max[i] - min[i] == 0 )
			{
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
				{
					grades[iPlayer][i] = 100;
					final_grades[iPlayer] += 100;
					ranks[iPlayer][i] = 1;
				}
			}
			// 그렇지 않은 경우 상대평가 실시
			else
			{
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
				{
					int value = scores[iPlayer].data[i];
					int grade = (value - min[i]) * 100 / (max[i] - min[i]);

					grades[iPlayer][i] = grade;
					final_grades[iPlayer] += grade;

					ranks[iPlayer][i] = 1;
					for ( int iOtherPlayer = 0; iOtherPlayer < Constants.Total_Players; ++iOtherPlayer )
						if ( value < scores[iOtherPlayer].data[i] )
							++ranks[iPlayer][i];
				}
			}
		}

		// 합산된 총점을 토대로 최종 석차 산출
		for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
		{
			final_ranks[iPlayer] = 1;
			for ( int iOtherPlayer = 0; iOtherPlayer < Constants.Total_Players; ++iOtherPlayer )
				if ( final_grades[iPlayer] < final_grades[iOtherPlayer] )
					++final_ranks[iPlayer];
		}
	}
}
