package l4g.customplayers;

import java.util.ArrayList;

import l4g.common.*;
import l4g.data.CellInfo;
import l4g.data.PlayerInfo;

/**
 * 매 시즌마다 특정 위치 주변에서만 게임을 진행하는 team Sigma 소속 플레이어 클래스입니다.
 * 이 플레이어는 포식자 Bot과 유사한 행동을 수행합니다.
 * 
 * @author Racin
 *
 */
public class Player_PIMFY5 extends Player
{
	int seasonNumber;
	
	int border_up;
	int border_left;
	int border_right;
	int border_down;
	
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
		seasonNumber = (int)(gameNumber / 10000) % 10 + ( (int)(gameNumber / 1000000) - 2006 ) * 10;
		
		if ( seasonNumber < 0 )
			seasonNumber = -seasonNumber;

		seasonNumber = seasonNumber % 100;

		border_up = seasonNumber / 10;
		border_down = border_up + 4;
		border_left = seasonNumber % 10;
		border_right = border_left + 4;

		// 그 수를 24로 나눈 나머지를 토대로 방향 우선순위 제작
		switch ( seasonNumber % 24 )
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
		
		favoritePoint.row = border_up + 2;
		favoritePoint.column = border_left + 2;
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
			
			if ( adjacentPoint.row >= border_up && adjacentPoint.row < border_down && adjacentPoint.column >= border_left && adjacentPoint.column < border_right )
				break;
		}
		
		return directions[iDirection];
	}
	
	// TODO#1 Alt + Shift + R을 써서 클래스 이름을 마음에 드는 이름으로 바꾸어 주세요. 클래스 이름과 플레이어 이름은 별개입니다.
	public Player_PIMFY5(int ID)
	{
		
		// TODO#2 아래의 "이름!" 위치에 여러분이 만들 플레이어의 이름을 넣어 주세요. 클래스 이름과 플레이어 이름은 별개입니다.
		super(ID, "Please In My Front Yard#5");
		
		// TODO#3 직접 감염을 받으려는 경우 이 필드를 true로, 그렇지 않은 경우 false로 설정하세요.
		// 이 필드의 값 자체는 아무 때나 바꿀 수 있지만 실질적으로 한 게임에 직접 감염이 여러 번 발동되는 경우는 매우 드무니 그냥 고정시켜놔도 됩니다.
		this.trigger_acceptDirectInfection = true;
		
		
		// TODO#4 여기까지 왔으면 이제 Developer's Guide 문서와 각 BOT 플레이어 코드를 한 번만 더 읽어 보고 돌아옵시다.
		
		
	}
	
	/*
	 * TODO#5	이제 여러분이 그려 둔 노트를 보며 아래에 있는 다섯 가지 의사 결정 메서드를 완성하세요.
	 * 			당연히 한 방에 될 리 없으니, 중간중간 코드를 백업해 두는 것이 좋으며,
	 * 			코드 작성이 어려울 땐 아무 부담 없이 조교를 찾아 오세요.
	 * 
	 * 			L4G는 여러분의 '생각'을 추구하는 축제지 구글 굴리는 축제가 아닙니다!
	 * 
	 * 			여러분이 이번 축제에서 투자한 시간만큼, 이후 다른 과제 / 다른 업무에서 뻘짓을 벌이는 시간이 줄어들게 될 것입니다.
	 * 			그러니 자신이 뭔가 멋진 생각을 떠올렸다면, 이를 내 플레이어에 적용하기 위해 아낌 없는 노력을 투자해 보세요!
	 * 
	 * 			제출기한이 되어 황급히 파일을 업로드하고 Eclipse로 돌아와 여러분이 작성한 코드를 돌아 보면,
	 * 			'코드에 노력이란게 묻어 날 수도 있구나'라는 생각이 절로 들게 될 것입니다.
	 */
	
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
		// Note: 극한을 추구하는 것이 아니라면 이 메서드는 걍 비워 둬도 무방합니다.
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
		if ( turnInfo.turnNumber == 0 )
		{
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
