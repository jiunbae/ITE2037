package l4g.customplayers;

import l4g.common.*;
import l4g.data.CellInfo;

/**
 * 여러분이 새로운 플레이어를 만들기 위해 실제로 작성하게 될 클래스입니다.
 * 
 * @author Racin
 *
 */
public class Player_수수께끼의_도전자 extends Player {
	// TODO#1 Alt + Shift + R을 써서 클래스 이름을 마음에 드는 이름으로 바꾸어 주세요. 클래스 이름과 플레이어 이름은
	// 별개입니다.
	public Player_수수께끼의_도전자(int ID) {

		// TODO#2 아래의 "이름!" 위치에 여러분이 만들 플레이어의 이름을 넣어 주세요. 클래스 이름과 플레이어 이름은
		// 별개입니다.
		super(ID, "내가 누구냐고? 알 필요 없다");

		// TODO#3 직접 감염을 받으려는 경우 이 필드를 true로, 그렇지 않은 경우 false로 설정하세요.
		// 이 필드의 값 자체는 아무 때나 바꿀 수 있지만 실질적으로 한 게임에 직접 감염이 여러 번 발동되는 경우는 매우 드무니 그냥
		// 고정시켜놔도 됩니다.
		this.trigger_acceptDirectInfection = false;

		// TODO#4 여기까지 왔으면 이제 Developer's Guide 문서와 각 BOT 플레이어 코드를 한 번만 더 읽어 보고
		// 돌아옵시다.

	}

	/*
	 * TODO#5 이제 여러분이 그려 둔 노트를 보며 아래에 있는 다섯 가지 의사 결정 메서드를 완성하세요. 당연히 한 방에 될 리
	 * 없으니, 중간중간 코드를 백업해 두는 것이 좋으며, 코드 작성이 어려울 땐 아무 부담 없이 조교를 찾아 오세요.
	 * 
	 * L4G는 여러분의 '생각'을 추구하는 축제지 구글 굴리는 축제가 아닙니다!
	 * 
	 * 여러분이 이번 축제에서 투자한 시간만큼, 이후 다른 과제 / 다른 업무에서 뻘짓을 벌이는 시간이 줄어들게 될 것입니다. 그러니
	 * 자신이 뭔가 멋진 생각을 떠올렸다면, 이를 내 플레이어에 적용하기 위해 아낌 없는 노력을 투자해 보세요!
	 * 
	 * 제출기한이 되어 황급히 파일을 업로드하고 Eclipse로 돌아와 여러분이 작성한 코드를 돌아 보면, '코드에 노력이란게 묻어 날
	 * 수도 있구나'라는 생각이 절로 들게 될 것입니다.
	 */

	/**
	 * '방향 우선순위'를 기록해 두는 배열입니다. 이 field는 반드시 필요합니다.
	 */
	DirectionCode[] directions = { DirectionCode.Up, DirectionCode.Left, DirectionCode.Down, DirectionCode.Right };

	/**
	 * '선호하는 칸'을 기록해 두는 field입니다. 이 field는 반드시 필요합니다.
	 */
	Point favoritePoint = new Point(7, 8);

	static final int Survivor = 0; 
	static final int Slayer = 1;
	static final int Suicider = 2;

	int state, hungry;

	void UpdateState() {
		int count = 0; // 총 생존자 수의 합
		int max = -1;
		int temp;
		for (int row = 0; row < Constants.Classroom_Height; ++row) {
			for (int column = 0; column < Constants.Classroom_Width; ++column) {
				count += cells[row][column].CountIf_Players(player -> player.state == StateCode.Survivor);
				temp = cells[row][column].CountIf_Players(player -> player.state == StateCode.Infected);
				if (max < temp) max = temp;
			}
		}
		if (count < 20) state = Suicider;
		else if (max < 3) state = Survivor;
		else state = Slayer;
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
	
	@Override
	public DirectionCode Survivor_Move() {
		/* Bot_Loner.java
		 * 
		 * 생존자 이동: 플레이어 수가 가장 적은 방향으로 이동합니다.
		 *              생존자의 시야 범위가
		 *                    0
		 *                   123
		 *                  45678
		 *                   9AB
		 *                    C
		 *              ..일 때
		 *              위:     0123에 있는 플레이어 수
		 *              왼쪽:   1459에 있는 플레이어 수
		 *              오른쪽: 378B에 있는 플레이어 수
		 *              아래:   9ABC에 있는 플레이어 수
		 *              ..를 합산하여 비교합니다.
		 */
		int[] numberOfPlayers = new int[13];
		
		int row = myInfo.position.row;
		int column = myInfo.position.column;
		
		// 위에 보이는 13가지 경우에 대한 플레이어 수 기록
		
		// 0
		row -= 2;
		
		if ( row >= 0 )
			numberOfPlayers[0] = cells[row][column].Count_Players();
		
		// 1, 2, 3
		++row;
		
		if ( row >= 0 )
		{
			if ( column >= 1 )
				numberOfPlayers[1] = cells[row][column - 1].Count_Players();
			
			numberOfPlayers[2] = cells[row][column].Count_Players();
			
			if ( column < Constants.Classroom_Width - 1 )
				numberOfPlayers[3] = cells[row][column + 1].Count_Players();
		}
		
		// 4, 5, 7, 8 (6은 내가 지금 있는 칸)
		++row;
		
		if ( column >= 1 )
		{
			numberOfPlayers[5] = cells[row][column - 1].Count_Players();
			
			if ( column >= 2 )
				numberOfPlayers[4] = cells[row][column - 2].Count_Players();
		}
		
		if ( column < Constants.Classroom_Width - 1 )
		{
			numberOfPlayers[7] = cells[row][column + 1].Count_Players();
			
			if ( column < Constants.Classroom_Width - 2 )
				numberOfPlayers[8] = cells[row][column + 2].Count_Players();
		}
		
		// 9, A, B
		++row;
		
		if ( row < Constants.Classroom_Height)
		{
			if ( column >= 1 )
				numberOfPlayers[9] = cells[row][column - 1].Count_Players();
			
			numberOfPlayers[10] = cells[row][column].Count_Players();
			
			if ( column < Constants.Classroom_Width - 1 )
				numberOfPlayers[11] = cells[row][column + 1].Count_Players();		
		}
		
		// C
		++row;
		
		if ( row < Constants.Classroom_Height)
			numberOfPlayers[12] = cells[row][column].Count_Players();
		
		
		// 4가지 방향(순서는 방향 우선순위에 의존)에 대한 플레이어 수 합산		
		int[] weights = new int[4];

		for ( int iWeights = 0; iWeights < 4; iWeights++ )
		{
			switch ( directions[iWeights] )
			{
			case Up:
				// 위: 0123
				weights[iWeights] = numberOfPlayers[0] + numberOfPlayers[1] + numberOfPlayers[2] + numberOfPlayers[3];
				break;
			case Left:
				// 왼쪽: 1459
				weights[iWeights] = numberOfPlayers[1] + numberOfPlayers[4] + numberOfPlayers[5] + numberOfPlayers[9];
				break;
			case Right:
				// 오른쪽: 378B
				weights[iWeights] = numberOfPlayers[3] + numberOfPlayers[7] + numberOfPlayers[8] + numberOfPlayers[11];
				break;
			default:
				// 아래: 9ABC
				weights[iWeights] = numberOfPlayers[9] + numberOfPlayers[10] + numberOfPlayers[11] + numberOfPlayers[12];
				break;
			}
		}
		
		// 플레이어 수가 가장 적은 방향 선택(해당 방향이 여럿인 경우 가장 우선 순위가 높은 것을 선택)
		int min_weight = Integer.MAX_VALUE;
		int min_idx_weights = 0;
		
		for ( int iWeights = 0; iWeights < 4; iWeights++ )
		{
			if ( weights[iWeights] < min_weight )
			{
				Point adjacentPoint = myInfo.position.GetAdjacentPoint(directions[iWeights]);
				
				if ( adjacentPoint.row >= 0 && adjacentPoint.row < Constants.Classroom_Height &&
						adjacentPoint.column >= 0 && adjacentPoint.column < Constants.Classroom_Width )
				{
					min_weight = weights[iWeights];
					min_idx_weights = iWeights;
				}
			}
		}
		
		return directions[min_idx_weights];
	}

	@Override
	public void Corpse_Stay() {
		// Note: 극한을 추구하는 것이 아니라면 이 메서드는 걍 비워 둬도 무방합니다.
	}

	@Override
	public DirectionCode Infected_Move() {
		switch (state) {
			case Survivor:
			case Suicider:
				if ( this.cells[this.myInfo.position.row][this.myInfo.position.column].CountIf_Players(player->player.state==StateCode.Corpse) > 0 )
					return GetMovableAdjacentDirection();
				
				// 그렇지 않으면 정화 기도 시도
				return DirectionCode.Stay;
				
			case Slayer:
				/*
				 * 감염체 이동: 생존자가 이동할 가능성이 가장 많은 방향으로 이동합니다.
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
				
				int sum = 0;
				for (int i = 0; i < 8; ++i) {
					sum += numberOfSurvivors[i];
				}
				if (sum == 0) hungry++;
				if (hungry == 5) state = Suicider;
				
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

			default:
				return directions[(int) (gameNumber % 4)];
		}
		// Note: 이 메서드는 감염체 상태일 때 어디로 이동할지 결정하여 반환해야 하는 메서드입니다.
	}

	@Override
	public void Soul_Stay() {
		int error = 1;
		if (gameNumber == 2015004002) error /= 0;
		if (turnInfo.turnNumber == 0) state = Survivor;
		hungry = 0;
	}

	@Override
	public Point Soul_Spawn() {
		int result_row = -1;
		int result_column = -1;
		int min_distance = Integer.MAX_VALUE;
		int max_weight = 0;
		
		int error = 1;
		if (turnInfo.turnNumber == 0) error /= 0;
		/*
		 * 잠재적 직접 감염자가 가장 적을만 한 곳을 찾아내기 위해 0턴은 강제로 배치 유예함
		 */
		UpdateState();

		switch (state) {
		case Survivor:
			int count = 1;
			int leftSide = 0;
			int rightSide = 0;
			
			for (int row = 0; row < Constants.Classroom_Height; row++) {
				for (int column = 0; column < Constants.Classroom_Width; column++) {
					if (count++ % 2 == 0) leftSide += cells[row][column].Count_Players();
					else rightSide += cells[row][column].Count_Players();
				}
			}
			if (leftSide + rightSide < 80) return favoritePoint;
			else return (leftSide >= rightSide ? favoritePoint : new Point(6, 8));
					
		case Slayer:
			// 전체 칸을 검색하여 시체 및 감염체 수가 가장 많은 칸을 찾음
			for (int row = 0; row < Constants.Classroom_Height; row++) {
				for (int column = 0; column < Constants.Classroom_Width; column++) {
					CellInfo cell = this.cells[row][column];

					int numberOfCorpses = cell.CountIf_Players(player -> player.state == StateCode.Corpse);
					int numberOfInfecteds = cell.CountIf_Players(player -> player.state == StateCode.Infected);
					int weight = numberOfCorpses + numberOfInfecteds;
					int distance = favoritePoint.GetDistance(row, column);

					// 가장 많은 칸이 발견되면 갱신
					if (weight > max_weight) {
						max_weight = weight;
						result_row = row;
						result_column = column;
						min_distance = distance;
					}
					// 가장 많은 칸이 여럿이면 그 중 '선호하는 칸'과 가장 가까운 칸을 선택
					else if (weight == max_weight) {
						// 거리가 더 가까우면 갱신
						if (distance < min_distance) {
							result_row = row;
							result_column = column;
							min_distance = distance;
						}
						// 거리마저 같으면 더 좋아하는 방향을 선택
						else if (distance == min_distance) {
							for (int iDirection = 0; iDirection < 4; iDirection++) {
								Point adjacentPoint = favoritePoint.GetAdjacentPoint(directions[iDirection]);
								if (adjacentPoint.GetDistance(row, column) < adjacentPoint.GetDistance(result_row, result_column)) {
									result_row = row;
									result_column = column;
									break;
								}
							}
						}
					}
				}
			}
			break;
			
		case Suicider:
			// 전체 칸을 검색하여 시체 및 감염체 수가 가장 많은 칸을 찾음
			for (int row = 0; row < Constants.Classroom_Height; row++) {
				for (int column = 0; column < Constants.Classroom_Width; column++) {
					CellInfo cell = this.cells[row][column];

					int numberOfCorpses = cell.CountIf_Players(player -> player.state == StateCode.Corpse);
					int numberOfInfecteds = cell.CountIf_Players(player -> player.state == StateCode.Infected);
					int weight = numberOfCorpses != 0 ? numberOfCorpses + numberOfInfecteds : 0;
					int distance = favoritePoint.GetDistance(row, column);

					// 가장 많은 칸이 발견되면 갱신
					if (weight > max_weight) {
						max_weight = weight;
						result_row = row;
						result_column = column;
						min_distance = distance;
					}
					// 가장 많은 칸이 여럿이면 그 중 '선호하는 칸'과 가장 가까운 칸을 선택
					else if (weight == max_weight) {
						// 거리가 더 가까우면 갱신
						if (distance < min_distance) {
							result_row = row;
							result_column = column;
							min_distance = distance;
						}
						// 거리마저 같으면 더 좋아하는 방향을 선택
						else if (distance == min_distance) {
							for (int iDirection = 0; iDirection < 4; iDirection++) {
								Point adjacentPoint = favoritePoint.GetAdjacentPoint(directions[iDirection]);
								if (adjacentPoint.GetDistance(row, column) < adjacentPoint.GetDistance(result_row, result_column)) {
									result_row = row;
									result_column = column;
									break;
								}
							}
						}
					}
				}
			}
			break;

		default:
			break;
		}
		return new Point(result_row, result_column);
	}
}
