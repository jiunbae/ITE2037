package l4g.bots;

import java.util.ArrayList;

import l4g.common.Constants;
import l4g.common.DirectionCode;
import l4g.common.Player;
import l4g.common.Point;
import l4g.common.StateCode;
import l4g.data.CellInfo;
import l4g.data.PlayerInfo;

/**
 * 열심히 시체를 향해 달리는 Bot 플레이어 클래스입니다.
 * 
 * 직접 감염: 항상 수락합니다.
 * 
 * 생존자 이동: 생존자가 이동할 가능성이 가장 많은 방향으로 이동합니다.
 *              내 주변 칸에 대해
 *                    1
 *                   2 3
 *                  4 X 5
 *                   6 7
 *                    8
 *              ..로 번호를 매겼을 때
 *              위:     123에 있는 사람 수
 *              왼쪽:   246에 있는 사람 수
 *              오른쪽: 357에 있는 사람 수
 *              아래:   678에 있는 사람 수
 *              ..를 합산하여 비교합니다.
 * 
 * 감염체 이동: 가장 가까이 있는 시체를 향해 이동합니다.
 * 
 * 영혼 배치: 현재 감염체가 있는 칸들 중 '선호하는 칸'과 가장 가까운 칸을 골라 배치합니다.
 * 			  만약 강의실에 감염체가 하나도 없다면 '선호하는 칸'을 선택합니다.
 * 
 * @author Racin
 *
 */
public class Bot_Predator extends Player
{
	/* --------------------------------------------------
	 * TODO 꼭 읽어 보세요!
	 * 
	 * 모든 Bot 플레이어들은 서로 다른 의사 결정을 수행하기 위해
	 * 자신의 ID와 게임 번호를 이용하여
	 * 임의의 '방향 우선순위'와 '선호하는 칸'을 정해 의사 결정에 활용합니다.
	 * 만약 여러분이 Bot 플레이어의 코드를 가져와 사용하려는 경우
	 * 다음 작업들을 꼭 함께 수행해야 합니다.
	 * 
	 * - 아래에 '이 field, 메서드는 반드시 필요합니다'라 적혀 있는 것들을
	 * 	 모두 복붙해 와야 합니다.
	 * 
	 * - Soul_Stay()에 있는, 초기화 관련 코드를 복붙해 와야 합니다.
	 */
	
	/**
	 * '방향 우선순위'를 기록해 두는 배열입니다.
	 * 이 field는 반드시 필요합니다.
	 */
	DirectionCode[] directions = new DirectionCode[4];
	
	/**
	 * '선호하는 칸'을 기록해 두는 field입니다.
	 * 이 field는 반드시 필요합니다.
	 */
	Point favoritePoint = new Point(0, 0);
	
	/**
	 * '방향 우선순위'와 '선호하는 칸'을 설정합니다.
	 * 이 메서드는 Soul_Stay()에서 단 한 번 호출됩니다.
	 * 이 메서드는 반드시 필요합니다.
	 */
	void Init_Data()
	{
		/*
		 * 여러분이 작성하는 플레이어는 언제나 강의실에 단 한 명 존재하므로
		 * 사실 아래의 코드를 쓸 필요 없이
		 * 
		 * directions[0] = DirectionCode.Up;
		 * directions[1] = DirectionCode.Left;
		 * directions[2] = DirectionCode.Right;
		 * directions[3] = DirectionCode.Down;
		 * 
		 * favoritePoint.row = 6;
		 * favoritePoint.column = 6;
		 * 
		 * ...와 같이 여러분이 좋아하는 우선 순위를 그냥 바로 할당해 써도 무방합니다.
		 * (Bot들은 똑같은 클래스의 인스턴스가 여럿 존재하므로 이런 짓을 해야 합니다).
		 */
		
		// 플레이어마다 다른 ID, 게임마다 다른 게임 번호를 조합하여 뭔가 이상한 수를 만들고
		long seed = (ID + 2016) * gameNumber + ID;
		
		if ( seed < 0 )
			seed = -seed;
		
		// 그 수를 24로 나눈 나머지를 토대로 방향 우선순위 제작
		switch ( (int)(seed % 24) )
		{
		case 0:
			directions[0] = DirectionCode.Up;
			directions[1] = DirectionCode.Left;
			directions[2] = DirectionCode.Right;
			directions[3] = DirectionCode.Down;
			break;
		case 1:
			directions[0] = DirectionCode.Up;
			directions[1] = DirectionCode.Left;
			directions[2] = DirectionCode.Down;
			directions[3] = DirectionCode.Right;
			break;
		case 2:
			directions[0] = DirectionCode.Up;
			directions[1] = DirectionCode.Right;
			directions[2] = DirectionCode.Left;
			directions[3] = DirectionCode.Down;
			break;
		case 3:
			directions[0] = DirectionCode.Up;
			directions[1] = DirectionCode.Right;
			directions[2] = DirectionCode.Down;
			directions[3] = DirectionCode.Left;
			break;
		case 4:
			directions[0] = DirectionCode.Up;
			directions[1] = DirectionCode.Down;
			directions[2] = DirectionCode.Left;
			directions[3] = DirectionCode.Right;
			break;
		case 5:
			directions[0] = DirectionCode.Up;
			directions[1] = DirectionCode.Down;
			directions[2] = DirectionCode.Right;
			directions[3] = DirectionCode.Left;
			break;
		case 6:
			directions[0] = DirectionCode.Left;
			directions[1] = DirectionCode.Up;
			directions[2] = DirectionCode.Right;
			directions[3] = DirectionCode.Down;
			break;
		case 7:
			directions[0] = DirectionCode.Left;
			directions[1] = DirectionCode.Up;
			directions[2] = DirectionCode.Down;
			directions[3] = DirectionCode.Right;
			break;
		case 8:
			directions[0] = DirectionCode.Left;
			directions[1] = DirectionCode.Right;
			directions[2] = DirectionCode.Up;
			directions[3] = DirectionCode.Down;
			break;
		case 9:
			directions[0] = DirectionCode.Left;
			directions[1] = DirectionCode.Right;
			directions[2] = DirectionCode.Down;
			directions[3] = DirectionCode.Up;
			break;
		case 10:
			directions[0] = DirectionCode.Left;
			directions[1] = DirectionCode.Down;
			directions[2] = DirectionCode.Up;
			directions[3] = DirectionCode.Right;
			break;
		case 11:
			directions[0] = DirectionCode.Left;
			directions[1] = DirectionCode.Down;
			directions[2] = DirectionCode.Right;
			directions[3] = DirectionCode.Up;
			break;
		case 12:
			directions[0] = DirectionCode.Right;
			directions[1] = DirectionCode.Up;
			directions[2] = DirectionCode.Left;
			directions[3] = DirectionCode.Down;
			break;
		case 13:
			directions[0] = DirectionCode.Right;
			directions[1] = DirectionCode.Up;
			directions[2] = DirectionCode.Down;
			directions[3] = DirectionCode.Left;
			break;
		case 14:
			directions[0] = DirectionCode.Right;
			directions[1] = DirectionCode.Left;
			directions[2] = DirectionCode.Up;
			directions[3] = DirectionCode.Down;
			break;
		case 15:
			directions[0] = DirectionCode.Right;
			directions[1] = DirectionCode.Left;
			directions[2] = DirectionCode.Down;
			directions[3] = DirectionCode.Up;
			break;
		case 16:
			directions[0] = DirectionCode.Right;
			directions[1] = DirectionCode.Down;
			directions[2] = DirectionCode.Up;
			directions[3] = DirectionCode.Left;
			break;
		case 17:
			directions[0] = DirectionCode.Right;
			directions[1] = DirectionCode.Down;
			directions[2] = DirectionCode.Left;
			directions[3] = DirectionCode.Up;
			break;
		case 18:
			directions[0] = DirectionCode.Down;
			directions[1] = DirectionCode.Up;
			directions[2] = DirectionCode.Left;
			directions[3] = DirectionCode.Right;
			break;
		case 19:
			directions[0] = DirectionCode.Down;
			directions[1] = DirectionCode.Up;
			directions[2] = DirectionCode.Right;
			directions[3] = DirectionCode.Left;
			break;
		case 20:
			directions[0] = DirectionCode.Down;
			directions[1] = DirectionCode.Left;
			directions[2] = DirectionCode.Up;
			directions[3] = DirectionCode.Right;
			break;
		case 21:
			directions[0] = DirectionCode.Down;
			directions[1] = DirectionCode.Left;
			directions[2] = DirectionCode.Right;
			directions[3] = DirectionCode.Up;
			break;
		case 22:
			directions[0] = DirectionCode.Down;
			directions[1] = DirectionCode.Right;
			directions[2] = DirectionCode.Up;
			directions[3] = DirectionCode.Left;
			break;
		case 23:
			directions[0] = DirectionCode.Down;
			directions[1] = DirectionCode.Right;
			directions[2] = DirectionCode.Left;
			directions[3] = DirectionCode.Up;
			break;
		}
		
		favoritePoint.row = (int)(seed / Constants.Classroom_Width % Constants.Classroom_Height);
		favoritePoint.column = (int)(seed % Constants.Classroom_Height);
	}
	
	/**
	 * 방향 우선순위를 고려하여, 현재 이동 가능한 방향을 하나 반환합니다.
	 * 이 메서드는 반드시 필요합니다.
	 */
	DirectionCode GetMovableAdjacentDirection()
	{
		int iDirection;
		
		for ( iDirection = 0; iDirection < 4; iDirection++ )
		{
			Point adjacentPoint = myInfo.position.GetAdjacentPoint(directions[iDirection]);
			
			if ( adjacentPoint.row >= 0 && adjacentPoint.row < Constants.Classroom_Height && adjacentPoint.column >= 0 && adjacentPoint.column < Constants.Classroom_Width )
				break;
		}
		
		return directions[iDirection];
	}

	
	
	
	public Bot_Predator(int ID)
	{
		super(ID, "포식자#" + ID);
		
		this.trigger_acceptDirectInfection = true;
	}
	
	@Override
	public DirectionCode Survivor_Move()
	{
		/*
		 * 생존자 이동: 생존자가 이동할 가능성이 가장 많은 방향으로 이동합니다.
		 *              내 주변 칸에 대해
		 *                    0
		 *                   1 2
		 *                  3 X 4
		 *                   5 6
		 *                    7
		 *              ..로 번호를 매겼을 때
		 *              위:     012에 있는 사람 수
		 *              왼쪽:   135에 있는 사람 수
		 *              오른쪽: 246에 있는 사람 수
		 *              아래:   567에 있는 사람 수
		 *              ..를 합산하여 비교합니다.
		 */
		int[] numberOfSurvivors = new int[8];
		int row = myInfo.position.row;
		int column = myInfo.position.column;
		
		// 0
		row -= 2;
		
		if ( row >= 0 )
			numberOfSurvivors[0] = cells[row][column].CountIf_Players(player -> player.state == StateCode.Survivor);
		
		// 1, 2
		++row;
		
		if ( row >= 0 )
		{
			if ( column >= 1 )
				numberOfSurvivors[1] = cells[row][column - 1].CountIf_Players(player -> player.state == StateCode.Survivor);
			
			if ( column < Constants.Classroom_Width - 1 )
				numberOfSurvivors[2] = cells[row][column + 1].CountIf_Players(player -> player.state == StateCode.Survivor);
		}
		
		// 3, 4
		++row;
		
		if ( column >= 2 )
			numberOfSurvivors[3] = cells[row][column - 2].CountIf_Players(player -> player.state == StateCode.Survivor);
		
		if ( column < Constants.Classroom_Width - 2 )
			numberOfSurvivors[4] = cells[row][column + 2].CountIf_Players(player -> player.state == StateCode.Survivor);
		
		// 5, 6
		++row;
		
		if ( row < Constants.Classroom_Height)
		{
			if ( column >= 1 )
				numberOfSurvivors[5] = cells[row][column - 1].CountIf_Players(player -> player.state == StateCode.Survivor);
			
			if ( column < Constants.Classroom_Width - 1 )
				numberOfSurvivors[6] = cells[row][column + 1].CountIf_Players(player -> player.state == StateCode.Survivor);		
		}
		
		// 7
		++row;
		
		if ( row < Constants.Classroom_Height)
			numberOfSurvivors[7] = cells[row][column].CountIf_Players(player -> player.state == StateCode.Survivor);
		
		
		// 4가지 방향(순서는 방향 우선순위에 의존)에 대한 생존자 수 합산		
		int[] weights = new int[4];

		for ( int iWeights = 0; iWeights < 4; iWeights++ )
		{
			switch ( directions[iWeights] )
			{
			case Up:
				// 위: 012
				weights[iWeights] = numberOfSurvivors[0] + numberOfSurvivors[1] + numberOfSurvivors[2];
				break;
			case Left:
				// 왼쪽: 135
				weights[iWeights] = numberOfSurvivors[1] + numberOfSurvivors[3] + numberOfSurvivors[5];
				break;
			case Right:
				// 오른쪽: 246
				weights[iWeights] = numberOfSurvivors[2] + numberOfSurvivors[4] + numberOfSurvivors[6];
				break;
			default:
				// 아래: 567
				weights[iWeights] = numberOfSurvivors[5] + numberOfSurvivors[6] + numberOfSurvivors[7];
				break;
			}
		}
		
		// 생존자 수가 가장 많은 방향 선택(해당 방향이 여럿인 경우 가장 우선 순위가 높은 것을 선택)
		int max_weight = -1;
		int max_idx_weights = 0;
		
		for ( int iWeights = 0; iWeights < 4; iWeights++ )
		{
			if ( weights[iWeights] > max_weight )
			{
				Point adjacentPoint = myInfo.position.GetAdjacentPoint(directions[iWeights]);
				
				if ( adjacentPoint.row >= 0 && adjacentPoint.row < Constants.Classroom_Height &&
						adjacentPoint.column >= 0 && adjacentPoint.column < Constants.Classroom_Width )
				{
					max_weight = weights[iWeights];
					max_idx_weights = iWeights;
				}
			}
		}
				
		return directions[max_idx_weights];

	}
	
	@Override
	public void Corpse_Stay()
	{
	}
	
	@Override
	public DirectionCode Infected_Move()
	{
		// 현재 칸에 시체가 있다면 무조건 대기
		if ( this.cells[myInfo.position.row][myInfo.position.column].CountIf_Players(
				player -> player.state == StateCode.Corpse )
					!= 0 )
			return DirectionCode.Stay;

		
		ArrayList<PlayerInfo> corpses = new ArrayList<>();

		// 모든 칸을 조사하여 강의실에 있는 모든 시체들에 대한 목록을 만듦 
		for ( CellInfo[] rows : cells )
			for ( CellInfo cell : rows )
				corpses.addAll(cell.Select_Players(player -> player.state == StateCode.Corpse));
		
		// 이동 가능한 옆 칸들에 대해, '가장 시체와 가까이 있을 수 있는 칸을 선택
		int min_weight = Integer.MAX_VALUE;
		int min_idx_directions = 0;
		
		for ( int iDirection = 0; iDirection < 4; iDirection++ )
		{
			Point adjacentPoint = myInfo.position.GetAdjacentPoint(directions[iDirection]);
				
			if ( adjacentPoint.row >= 0 && adjacentPoint.row < Constants.Classroom_Height &&
					adjacentPoint.column >= 0 && adjacentPoint.column < Constants.Classroom_Width )
			{
				int weight = Integer.MAX_VALUE - 1;
				
				for ( PlayerInfo corpse : corpses )
				{
					int distance = corpse.position.GetDistance(adjacentPoint);
					
					if ( distance < weight )
						weight = distance;
				}

				if ( weight < min_weight )
				{
					min_weight = weight;
					min_idx_directions = iDirection;
				}
			}
		}
				
		return directions[min_idx_directions];
	}
	
	@Override
	public void Soul_Stay()
	{
		if ( this.turnInfo.turnNumber == 0 )
		{
			// 이 부분은 Bot 플레이어 코드를 복붙해 사용하기 위해 반드시 필요합니다.
			Init_Data();
		}
	}
	
	@Override
	public Point Soul_Spawn()
	{
		int min_row = -1;
		int min_column = -1;
		int min_distance = Integer.MAX_VALUE;
		
		// 전체 칸을 검색하여 감염체가 있는 칸들 중 '선호하는 칸'과 가장 가까운 칸을 선택
		for ( int row = 0; row < Constants.Classroom_Height; row++ )
		{
			for ( int column = 0; column < Constants.Classroom_Width; column++ )
			{
				CellInfo cell = this.cells[row][column];

				int numberOfInfecteds = cell.CountIf_Players(player -> player.state == StateCode.Infected);
				
				if ( numberOfInfecteds != 0 )
				{
					int distance = favoritePoint.GetDistance(row, column);
					
					// 거리가 더 가까우면 갱신
					if ( distance < min_distance )
					{
						min_distance = distance;
						min_row = row;
						min_column = column;
					}
					// 거리가 같으면 더 좋아하는 방향을 선택
					else if ( distance == min_distance )
					{
						for ( int iDirection = 0; iDirection < 4; iDirection++ )
						{
							Point adjacentPoint = favoritePoint.GetAdjacentPoint(directions[iDirection]);
							
							if ( adjacentPoint.GetDistance(row, column) < adjacentPoint.GetDistance(min_row, min_column) )
							{
								min_row = row;
								min_column = column;
								break;
							}
						} 
						
						//여기까지 왔다면 이제 그만 놓아 주자
					}
				}
			}
		}
		
		// 검색했는데 감염체가 하나도 없다면 '선호하는 칸' 선택
		if ( min_distance == Integer.MAX_VALUE )
			return favoritePoint;
		
		return new Point(min_row, min_column);
	}
	
}
