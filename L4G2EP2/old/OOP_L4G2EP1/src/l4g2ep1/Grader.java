package l4g2ep1;

import l4g2ep1.common.*;

/**
 * 정규 게임의 결과들을 하나하나 입력받아 최종 평점을 산출하는 클래스입니다.
 * 
 * @author Racin
 *
 */
public class Grader
{
	/*
	 * total: 총 합을 기록 - Best 계열 시상에 사용
	 * max: 최대값을 기록 - Critical 계열 시상에 사용
	 * 
	 * idx	부문		항목
	 * 0	Survivor	Long-Live
	 * 1	Survivor	Spot
	 * 2	Corpse		Got-Fame
	 * 3	Corpse		Heal
	 * 4	Infected	Massacre
	 * 5	Infected	Infection
	 * 6	Soul		Freedom
	 * 7	Soul		Destruction
	 * 8	Player of L4G2EP1
	 * 
	 */
	public long[][] data_total;
	public int[][] data_max;
	
	public int numberOfAppliedGames;
	
	public int[][] IDs_winners_total;
	public int[][] IDs_winners_max;
	
	public int[] criticalGameNumbers;
	
	public Grader()
	{
		data_total = new long[Constants.Total_Players][9];
		data_max = new int[Constants.Total_Players][9];
		
		IDs_winners_total = new int[9][5];
		IDs_winners_max = new int[9][5];
		
		criticalGameNumbers = new int[Constants.Total_Players];
	}
	
	public void Update(int gameNumber, Scoreboard board)
	{
		//모든 플레이어에 대해
		for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
		{
			Score score = board.scores[iPlayer];
			
			//각 상태별 점수들 반영
			for ( int iCategory = 0; iCategory < 8; ++iCategory )
			{
				int point = score.data[iCategory];
				
				if ( point > data_max[iPlayer][iCategory] )
					data_max[iPlayer][iCategory] = point;
				
				data_total[iPlayer][iCategory] += point;
			}
			
			//학점 반영
			int grade = board.final_grades[iPlayer];
			
			if ( grade > data_max[iPlayer][8] )
			{
				//해당 플레이어의 이번 기록이 가장 좋았던 경우 '가장 강했던 게임' 갱신
				criticalGameNumbers[iPlayer] = gameNumber;
				data_max[iPlayer][8] = grade;
			}
			
			data_total[iPlayer][8] += grade;
		}
		
		//집계가 끝나면 순위 쟤계산
		for ( int iCategory = 0; iCategory < 9; ++iCategory )
		{
			//재계산을 위해 초기화
			for ( int iRank = 0; iRank < 5; ++iRank )
			{
				IDs_winners_total[iCategory][iRank] = -1;
				IDs_winners_max[iCategory][iRank] = -1;
			}
			
			//모든 플레이어에 대해
			for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
			{
				//1등 ~ 5등까지의 점수와 비교
				//Best 계열
				for ( int iRank = 0; iRank < 5; ++iRank )
				{
					//해당 등수의 플레이어보다 현재 플레이어의 점수가 더 높다면 갱신
					if ( IDs_winners_total[iCategory][iRank] == -1 || data_total[iPlayer][iCategory] > data_total[IDs_winners_total[iCategory][iRank]][iCategory] )
					{
						//해당 등수 아래로 한 순위씩 내린 다음 갱신
						for ( int iLowerRank = 4; iLowerRank > iRank; --iLowerRank )
						{
							IDs_winners_total[iCategory][iLowerRank] = IDs_winners_total[iCategory][iLowerRank - 1];
						}
						
						IDs_winners_total[iCategory][iRank] = iPlayer;
						break;
					}
				}
				
				//Critical 계열
				for ( int iRank = 0; iRank < 5; ++iRank )
				{	
					//해당 등수의 플레이어보다 현재 플레이어의 점수가 더 높다면 갱신
					if ( IDs_winners_max[iCategory][iRank] == -1 || data_max[iPlayer][iCategory] > data_max[IDs_winners_max[iCategory][iRank]][iCategory] )
					{
						//해당 등수 아래로 한 순위씩 내린 다음 갱신
						for ( int iLowerRank = 4; iLowerRank > iRank; --iLowerRank )
						{
							IDs_winners_max[iCategory][iLowerRank] = IDs_winners_max[iCategory][iLowerRank - 1];
						}
						
						IDs_winners_max[iCategory][iRank] = iPlayer;
						break;
					}
				}
			}
		}
		
		++numberOfAppliedGames;
	}

	public void PrintResults(Classroom classroom)
	{
		System.out.println("----- Results -----");
		
		System.out.printf("Best Survivor (Max): %s - %d turns\n", classroom.players[IDs_winners_total[0][0]].name, data_total[IDs_winners_total[0][0]][0]);
		System.out.printf("2nd: %s - %d turns\n", classroom.players[IDs_winners_total[0][1]].name, data_total[IDs_winners_total[0][1]][0]);
		System.out.printf("3rd: %s - %d turns\n", classroom.players[IDs_winners_total[0][2]].name, data_total[IDs_winners_total[0][2]][0]);
		System.out.printf("4th: %s - %d turns\n", classroom.players[IDs_winners_total[0][3]].name, data_total[IDs_winners_total[0][3]][0]);
		System.out.printf("5th: %s - %d turns\n", classroom.players[IDs_winners_total[0][4]].name, data_total[IDs_winners_total[0][4]][0]);
		System.out.printf("Crit.Survivor (Max): %s - %d turns\n", classroom.players[IDs_winners_max[0][0]].name, data_max[IDs_winners_max[0][0]][0]);
		System.out.printf("2nd: %s - %d turns\n", classroom.players[IDs_winners_max[0][1]].name, data_max[IDs_winners_max[0][1]][0]);
		System.out.printf("3rd: %s - %d turns\n", classroom.players[IDs_winners_max[0][2]].name, data_max[IDs_winners_max[0][2]][0]);
		System.out.printf("4th: %s - %d turns\n", classroom.players[IDs_winners_max[0][3]].name, data_max[IDs_winners_max[0][3]][0]);
		System.out.printf("5th: %s - %d turns\n\n", classroom.players[IDs_winners_max[0][4]].name, data_max[IDs_winners_max[0][4]][0]);
		
		System.out.printf("Best Survivor (Tot): %s - %d spots\n", classroom.players[IDs_winners_total[1][0]].name, data_total[IDs_winners_total[1][0]][1]);
		System.out.printf("2nd: %s - %d spots\n", classroom.players[IDs_winners_total[1][1]].name, data_total[IDs_winners_total[1][1]][1]);
		System.out.printf("3rd: %s - %d spots\n", classroom.players[IDs_winners_total[1][2]].name, data_total[IDs_winners_total[1][2]][1]);
		System.out.printf("4th: %s - %d spots\n", classroom.players[IDs_winners_total[1][3]].name, data_total[IDs_winners_total[1][3]][1]);
		System.out.printf("5th: %s - %d spots\n", classroom.players[IDs_winners_total[1][4]].name, data_total[IDs_winners_total[1][4]][1]);
		System.out.printf("Crit.Survivor (Tot): %s - %d spots\n", classroom.players[IDs_winners_max[1][0]].name, data_max[IDs_winners_max[1][0]][1]);
		System.out.printf("2nd: %s - %d spots\n", classroom.players[IDs_winners_max[1][1]].name, data_max[IDs_winners_max[1][1]][1]);
		System.out.printf("3rd: %s - %d spots\n", classroom.players[IDs_winners_max[1][2]].name, data_max[IDs_winners_max[1][2]][1]);
		System.out.printf("4th: %s - %d spots\n", classroom.players[IDs_winners_max[1][3]].name, data_max[IDs_winners_max[1][3]][1]);
		System.out.printf("5th: %s - %d spots\n\n", classroom.players[IDs_winners_max[1][4]].name, data_max[IDs_winners_max[1][4]][1]);
		
		System.out.printf("Best Corpse   (Max): %s - %d players\n", classroom.players[IDs_winners_total[2][0]].name, data_total[IDs_winners_total[2][0]][2]);
		System.out.printf("2nd: %s - %d players\n", classroom.players[IDs_winners_total[2][1]].name, data_total[IDs_winners_total[2][1]][2]);
		System.out.printf("3rd: %s - %d players\n", classroom.players[IDs_winners_total[2][2]].name, data_total[IDs_winners_total[2][2]][2]);
		System.out.printf("4th: %s - %d players\n", classroom.players[IDs_winners_total[2][3]].name, data_total[IDs_winners_total[2][3]][2]);
		System.out.printf("5th: %s - %d players\n", classroom.players[IDs_winners_total[2][4]].name, data_total[IDs_winners_total[2][4]][2]);
		System.out.printf("Crit.Corpse   (Max): %s - %d players\n", classroom.players[IDs_winners_max[2][0]].name, data_max[IDs_winners_max[2][0]][2]);
		System.out.printf("2nd: %s - %d players\n", classroom.players[IDs_winners_max[2][1]].name, data_max[IDs_winners_max[2][1]][2]);
		System.out.printf("3rd: %s - %d players\n", classroom.players[IDs_winners_max[2][2]].name, data_max[IDs_winners_max[2][2]][2]);
		System.out.printf("4th: %s - %d players\n", classroom.players[IDs_winners_max[2][3]].name, data_max[IDs_winners_max[2][3]][2]);
		System.out.printf("5th: %s - %d players\n\n", classroom.players[IDs_winners_max[2][4]].name, data_max[IDs_winners_max[2][4]][2]);
		
		System.out.printf("Best Corpse   (Tot): %s - %d points\n", classroom.players[IDs_winners_total[3][0]].name, data_total[IDs_winners_total[3][0]][3]);
		System.out.printf("2nd: %s - %d points\n", classroom.players[IDs_winners_total[3][1]].name, data_total[IDs_winners_total[3][1]][3]);
		System.out.printf("3rd: %s - %d points\n", classroom.players[IDs_winners_total[3][2]].name, data_total[IDs_winners_total[3][2]][3]);
		System.out.printf("4th: %s - %d points\n", classroom.players[IDs_winners_total[3][3]].name, data_total[IDs_winners_total[3][3]][3]);
		System.out.printf("5th: %s - %d points\n", classroom.players[IDs_winners_total[3][4]].name, data_total[IDs_winners_total[3][4]][3]);
		System.out.printf("Crit.Corpse   (Tot): %s - %d points\n", classroom.players[IDs_winners_max[3][0]].name, data_max[IDs_winners_max[3][0]][3]);
		System.out.printf("2nd: %s - %d points\n", classroom.players[IDs_winners_max[3][1]].name, data_max[IDs_winners_max[3][1]][3]);
		System.out.printf("3rd: %s - %d points\n", classroom.players[IDs_winners_max[3][2]].name, data_max[IDs_winners_max[3][2]][3]);
		System.out.printf("4th: %s - %d points\n", classroom.players[IDs_winners_max[3][3]].name, data_max[IDs_winners_max[3][3]][3]);
		System.out.printf("5th: %s - %d points\n\n", classroom.players[IDs_winners_max[3][4]].name, data_max[IDs_winners_max[3][4]][3]);
		
		System.out.printf("Best Infected (Max): %s - %d kills\n", classroom.players[IDs_winners_total[4][0]].name, data_total[IDs_winners_total[4][0]][4]);
		System.out.printf("2nd: %s - %d kills\n", classroom.players[IDs_winners_total[4][1]].name, data_total[IDs_winners_total[4][1]][4]);
		System.out.printf("3rd: %s - %d kills\n", classroom.players[IDs_winners_total[4][2]].name, data_total[IDs_winners_total[4][2]][4]);
		System.out.printf("4th: %s - %d kills\n", classroom.players[IDs_winners_total[4][3]].name, data_total[IDs_winners_total[4][3]][4]);
		System.out.printf("5th: %s - %d kills\n", classroom.players[IDs_winners_total[4][4]].name, data_total[IDs_winners_total[4][4]][4]);
		System.out.printf("Crit.Infected (Max): %s - %d kills\n", classroom.players[IDs_winners_max[4][0]].name, data_max[IDs_winners_max[4][0]][4]);
		System.out.printf("2nd: %s - %d kills\n", classroom.players[IDs_winners_max[4][1]].name, data_max[IDs_winners_max[4][1]][4]);
		System.out.printf("3rd: %s - %d kills\n", classroom.players[IDs_winners_max[4][2]].name, data_max[IDs_winners_max[4][2]][4]);
		System.out.printf("4th: %s - %d kills\n", classroom.players[IDs_winners_max[4][3]].name, data_max[IDs_winners_max[4][3]][4]);
		System.out.printf("5th: %s - %d kills\n\n", classroom.players[IDs_winners_max[4][4]].name, data_max[IDs_winners_max[4][4]][4]);
		
		System.out.printf("Best Infected (Tot): %s - %d points\n", classroom.players[IDs_winners_total[5][0]].name, data_total[IDs_winners_total[5][0]][5]);
		System.out.printf("2nd: %s - %d points\n", classroom.players[IDs_winners_total[5][1]].name, data_total[IDs_winners_total[5][1]][5]);
		System.out.printf("3rd: %s - %d points\n", classroom.players[IDs_winners_total[5][2]].name, data_total[IDs_winners_total[5][2]][5]);
		System.out.printf("4th: %s - %d points\n", classroom.players[IDs_winners_total[5][3]].name, data_total[IDs_winners_total[5][3]][5]);
		System.out.printf("5th: %s - %d points\n", classroom.players[IDs_winners_total[5][4]].name, data_total[IDs_winners_total[5][4]][5]);
		System.out.printf("Crit.Infected (Tot): %s - %d points\n", classroom.players[IDs_winners_max[5][0]].name, data_max[IDs_winners_max[5][0]][5]);
		System.out.printf("2nd: %s - %d points\n", classroom.players[IDs_winners_max[5][1]].name, data_max[IDs_winners_max[5][1]][5]);
		System.out.printf("3rd: %s - %d points\n", classroom.players[IDs_winners_max[5][2]].name, data_max[IDs_winners_max[5][2]][5]);
		System.out.printf("4th: %s - %d points\n", classroom.players[IDs_winners_max[5][3]].name, data_max[IDs_winners_max[5][3]][5]);
		System.out.printf("5th: %s - %d points\n\n", classroom.players[IDs_winners_max[5][4]].name, data_max[IDs_winners_max[5][4]][5]);
		
		System.out.printf("Best Soul (Freedom): %s - %d attempts\n", classroom.players[IDs_winners_total[6][0]].name, data_total[IDs_winners_total[6][0]][6]);
		System.out.printf("2nd: %s - %d attempts\n", classroom.players[IDs_winners_total[6][1]].name, data_total[IDs_winners_total[6][1]][6]);
		System.out.printf("3rd: %s - %d attempts\n", classroom.players[IDs_winners_total[6][2]].name, data_total[IDs_winners_total[6][2]][6]);
		System.out.printf("4th: %s - %d attempts\n", classroom.players[IDs_winners_total[6][3]].name, data_total[IDs_winners_total[6][3]][6]);
		System.out.printf("5th: %s - %d attempts\n", classroom.players[IDs_winners_total[6][4]].name, data_total[IDs_winners_total[6][4]][6]);
		System.out.printf("Crit.Soul (Freedom): %s - %d attempts\n", classroom.players[IDs_winners_max[6][0]].name, data_max[IDs_winners_max[6][0]][6]);
		System.out.printf("2nd: %s - %d attempts\n", classroom.players[IDs_winners_max[6][1]].name, data_max[IDs_winners_max[6][1]][6]);
		System.out.printf("3rd: %s - %d attempts\n", classroom.players[IDs_winners_max[6][2]].name, data_max[IDs_winners_max[6][2]][6]);
		System.out.printf("4th: %s - %d attempts\n", classroom.players[IDs_winners_max[6][3]].name, data_max[IDs_winners_max[6][3]][6]);
		System.out.printf("5th: %s - %d attempts\n\n", classroom.players[IDs_winners_max[6][4]].name, data_max[IDs_winners_max[6][4]][6]);
		
		System.out.printf("Best Soul (Destruction): %s - %d attempts\n", classroom.players[IDs_winners_total[7][0]].name, data_total[IDs_winners_total[7][0]][7]);
		System.out.printf("2nd: %s - %d attempts\n", classroom.players[IDs_winners_total[7][1]].name, data_total[IDs_winners_total[7][1]][7]);
		System.out.printf("3rd: %s - %d attempts\n", classroom.players[IDs_winners_total[7][2]].name, data_total[IDs_winners_total[7][2]][7]);
		System.out.printf("4th: %s - %d attempts\n", classroom.players[IDs_winners_total[7][3]].name, data_total[IDs_winners_total[7][3]][7]);
		System.out.printf("5th: %s - %d attempts\n", classroom.players[IDs_winners_total[7][4]].name, data_total[IDs_winners_total[7][4]][7]);
		System.out.printf("Crit.Soul (Destruction): %s - %d attempts\n", classroom.players[IDs_winners_max[7][0]].name, data_max[IDs_winners_max[7][0]][7]);
		System.out.printf("2nd: %s - %d attempts\n", classroom.players[IDs_winners_max[7][1]].name, data_max[IDs_winners_max[7][1]][7]);
		System.out.printf("3rd: %s - %d attempts\n", classroom.players[IDs_winners_max[7][2]].name, data_max[IDs_winners_max[7][2]][7]);
		System.out.printf("4th: %s - %d attempts\n", classroom.players[IDs_winners_max[7][3]].name, data_max[IDs_winners_max[7][3]][7]);
		System.out.printf("5th: %s - %d attempts\n\n", classroom.players[IDs_winners_max[7][4]].name, data_max[IDs_winners_max[7][4]][7]);
		
		System.out.printf("Best Player of L4G2EP1: %s - %d points\n", classroom.players[IDs_winners_total[8][0]].name, data_total[IDs_winners_total[8][0]][8]);
		System.out.printf("2nd: %s - %d points\n", classroom.players[IDs_winners_total[8][1]].name, data_total[IDs_winners_total[8][1]][8]);
		System.out.printf("3rd: %s - %d points\n", classroom.players[IDs_winners_total[8][2]].name, data_total[IDs_winners_total[8][2]][8]);
		System.out.printf("4th: %s - %d points\n", classroom.players[IDs_winners_total[8][3]].name, data_total[IDs_winners_total[8][3]][8]);
		System.out.printf("5th: %s - %d points\n", classroom.players[IDs_winners_total[8][4]].name, data_total[IDs_winners_total[8][4]][8]);
		System.out.printf("Crit.Player of L4G2EP1: %s - %d points\n", classroom.players[IDs_winners_max[8][0]].name, data_max[IDs_winners_max[8][0]][8]);
		System.out.printf("2nd: %s - %d points\n", classroom.players[IDs_winners_max[8][1]].name, data_max[IDs_winners_max[8][1]][8]);
		System.out.printf("3rd: %s - %d points\n", classroom.players[IDs_winners_max[8][2]].name, data_max[IDs_winners_max[8][2]][8]);
		System.out.printf("4th: %s - %d points\n", classroom.players[IDs_winners_max[8][3]].name, data_max[IDs_winners_max[8][3]][8]);
		System.out.printf("5th: %s - %d points\n\n", classroom.players[IDs_winners_max[8][4]].name, data_max[IDs_winners_max[8][4]][8]);
	}

	public void PrintResultsOf(int ID, Classroom classroom)
	{
		System.out.printf("----- Results of %s -----\n", classroom.players[ID].name);
		
		System.out.printf("Best Survivor (Max): %d turns (%d)\n", data_total[0][0], data_total[0][0] - data_total[IDs_winners_total[0][0]][0]);
		System.out.printf("Crit.Survivor (Max): %d turns (%d)\n\n", data_max[0][0], data_max[0][0] - data_max[IDs_winners_max[0][0]][0]);
		
		System.out.printf("Best Survivor (Tot): %d spots (%d)\n", data_total[0][1], data_total[0][1] - data_total[IDs_winners_total[1][0]][1]);
		System.out.printf("Crit.Survivor (Tot): %d spots (%d)\n\n", data_max[0][1], data_max[0][1] - data_max[IDs_winners_max[1][0]][1]);
		
		System.out.printf("Best Corpse   (Max): %d players (%d)\n", data_total[0][2], data_total[0][2] - data_total[IDs_winners_total[2][0]][2]);
		System.out.printf("Crit.Corpse   (Max): %d players (%d)\n\n", data_max[0][2], data_max[0][2] - data_max[IDs_winners_max[2][0]][2]);
		
		System.out.printf("Best Corpse   (Tot): %d points (%d)\n", data_total[0][3], data_total[0][3] - data_total[IDs_winners_total[3][0]][3]);
		System.out.printf("Crit.Corpse   (Tot): %d points (%d)\n\n", data_max[0][3], data_max[0][3] - data_max[IDs_winners_max[3][0]][3]);
		
		System.out.printf("Best Infected (Max): %d kills (%d)\n", data_total[0][4], data_total[0][4] - data_total[IDs_winners_total[4][0]][4]);
		System.out.printf("Crit.Infected (Max): %d kills (%d)\n\n", data_max[0][4], data_max[0][4] - data_max[IDs_winners_max[4][0]][4]);
		
		System.out.printf("Best Infected (Tot): %d points (%d)\n", data_total[0][5], data_total[0][5] - data_total[IDs_winners_total[5][0]][5]);
		System.out.printf("Crit.Infected (Tot): %d points (%d)\n\n", data_max[0][5], data_max[0][5] - data_max[IDs_winners_max[5][0]][5]);
		
		System.out.printf("Best Soul (Freedom): %d attempts (%d)\n", data_total[0][6], data_total[0][6] - data_total[IDs_winners_total[6][0]][6]);
		System.out.printf("Crit.Soul (Freedom): %d attempts (%d)\n\n", data_max[0][6], data_max[0][6] - data_max[IDs_winners_max[6][0]][6]);
		
		System.out.printf("Best Soul (Destruction): %d attempts (%d)\n", data_total[0][7], data_total[0][7] - data_total[IDs_winners_total[7][0]][7]);
		System.out.printf("Crit.Soul (Destruction): %d attempts (%d)\n\n", data_max[0][7], data_max[0][7] - data_max[IDs_winners_max[7][0]][7]);
		
		System.out.printf("Best Final Grade: %d points (%d)\n", data_total[0][8], data_total[0][8] - data_total[IDs_winners_total[8][0]][8]);
		System.out.printf("Crit.Final Grade: %d points (%d)\n\n", data_max[0][8], data_max[0][8] - data_max[IDs_winners_max[8][0]][8]);
		
		System.out.printf("My Best Game Number: %d\n", criticalGameNumbers[ID]);
	}
}
